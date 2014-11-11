package org.jglrxavpok.jukebox;

import java.io.*;

import javax.sound.sampled.*;

import com.jcraft.jogg.*;
import com.jcraft.jorbis.*;

import org.jglrxavpok.jukebox.api.music.*;

public class ThreadPlayVorbis extends Thread
{
    private Packet         joggPacket      = new Packet();
    private Page           joggPage        = new Page();
    private StreamState    joggStreamState = new StreamState();
    private SyncState      joggSyncState   = new SyncState();

    private DspState       jorbisDspState  = new DspState();
    private Block          jorbisBlock     = new Block(jorbisDspState);
    private Comment        jorbisComment   = new Comment();
    private Info           jorbisInfo      = new Info();

    private InputStream    input;
    private boolean        stop;
    private boolean        pause;
    private boolean        oneLoop         = true;
    private SourceDataLine outputLine      = null;

    private float[][][]    pcmInfo;

    private int[]          pcmIndex;
    /**
     * We need a buffer, it's size, a count to know how many bytes we have read
     * and an index to keep track of where we are. This is standard networking
     * stuff used with read().
     */
    byte[]                 buffer          = null;
    int                    bufferSize      = 2048;
    int                    count           = 0;
    int                    index           = 0;
    private int            convertedBufferSize;
    private byte[]         convertedBuffer;
    private IMusicListener listener;
    private Music          music;

    /**
     * This method starts the sound system. It starts with initializing the
     * <code>DspState</code> object, after which it sets up the
     * <code>Block</code> object. Last but not least, it opens a line to the
     * source data line.
     * 
     * @return true if the sound system was successfully started, false
     *         otherwise
     */
    private boolean initializeSound()
    {

        // This buffer is used by the decoding method.
        convertedBufferSize = bufferSize * 2;
        convertedBuffer = new byte[convertedBufferSize];

        // Initializes the DSP synthesis.
        jorbisDspState.synthesis_init(jorbisInfo);

        // Make the Block object aware of the DSP.
        jorbisBlock.init(jorbisDspState);

        // Wee need to know the channels and rate.
        int channels = jorbisInfo.channels;
        int rate = jorbisInfo.rate;

        // Creates an AudioFormat object and a DataLine.Info object.
        AudioFormat audioFormat = new AudioFormat(rate, 16, channels, true,
                false);
        DataLine.Info datalineInfo = new DataLine.Info(SourceDataLine.class,
                audioFormat, AudioSystem.NOT_SPECIFIED);

        // Check if the line is supported.
        if(!AudioSystem.isLineSupported(datalineInfo))
        {
            System.err.println("Audio output line is not supported.");
            return false;
        }

        try
        {
            outputLine = (SourceDataLine) AudioSystem.getLine(datalineInfo);
            outputLine.open(audioFormat);
        }
        catch(LineUnavailableException exception)
        {
            System.out.println("The audio output line could not be opened due "
                    + "to resource restrictions.");
            System.err.println(exception);
            return false;
        }
        catch(IllegalStateException exception)
        {
            System.out.println("The audio output line is already open.");
            System.err.println(exception);
            return false;
        }
        catch(SecurityException exception)
        {
            System.out.println("The audio output line could not be opened due "
                    + "to security restrictions.");
            System.err.println(exception);
            return false;
        }

        outputLine.start();

        pcmInfo = new float[1][][];
        pcmIndex = new int[jorbisInfo.channels];

        return true;
    }

    /**
     * This method reads the entire stream body. Whenever it extracts a packet,
     * it will decode it by calling <code>decodeCurrentPacket()</code>.
     */
    private void readBody()
    {
        boolean needMoreData = true;

        while(needMoreData)
        {
            switch(joggSyncState.pageout(joggPage))
            {
                case -1:
                {
                    System.err.println("There is a hole in the data!");
                }

                //$FALL-THROUGH$
                case 0:
                {
                    break;
                }

                case 1:
                {
                    joggStreamState.pagein(joggPage);

                    if(joggPage.granulepos() == 0)
                    {
                        needMoreData = false;
                        break;
                    }

                    processPackets: while(true)
                    {
                        switch(joggStreamState.packetout(joggPacket))
                        {
                            case -1:
                            {

                            }
                            //$FALL-THROUGH$
                            case 0:
                            {
                                break processPackets;
                            }

                            case 1:
                            {
                                decodeCurrentPacket();
                            }
                        }
                    }

                    /*
                     * If the page is the end-of-stream, we don't need more
                     * data.
                     */
                    if(joggPage.eos() != 0)
                        needMoreData = false;
                }
            }

            // If we need more data
            if(needMoreData)
            {
                // We get the new index and an updated buffer.
                index = joggSyncState.buffer(bufferSize);
                buffer = joggSyncState.data;

                // Read from the InputStream.
                try
                {
                    count = input.read(buffer, index, bufferSize);
                }
                catch(Exception e)
                {
                    System.err.println(e);
                    return;
                }

                // We let SyncState know how many bytes we read.
                joggSyncState.wrote(count);

                // There's no more data in the stream.
                if(count == 0)
                    needMoreData = false;
            }
        }
    }

    public ThreadPlayVorbis(Music music, IMusicListener listener)
    {
        super("ThreadPlayOggVorbis");
        this.music = music;
        this.listener = listener;
        this.input = new ByteArrayInputStream(music.getFileData());
        oneLoop = true;
    }

    @Override
    public void run()
    {
        input.mark(Integer.MAX_VALUE);
        listener.onStart(music);
        initializeJOrbis();
        if(readHeader())
        {
            if(initializeSound())
            {
                readBody();
            }

        }
        listener.onStop(music);
        cleanUp();
        if(!oneLoop)
        {
            try
            {
                input.reset();
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void initializeJOrbis()
    {
        bufferSize = 2048;
        // Initialize SyncState
        joggSyncState.init();

        // Prepare the to SyncState internal buffer
        joggSyncState.buffer(bufferSize);

        /*
         * Fill the buffer with the data from SyncState's internal buffer. Note
         * how the size of this new buffer is different from bufferSize.
         */
        buffer = joggSyncState.data;
    }

    @Override
    public void interrupt()
    {
        stop = true;
    }

    public void stopSound()
    {
        interrupt();
    }

    public void pauseSound()
    {
        pause = true;
    }

    public void resumeSound()
    {
        pause = false;
    }

    public boolean readHeader()
    {
        /*
         * Variable used in loops below. While we need more data, we will
         * continue to read from the InputStream.
         */
        boolean needMoreData = true;

        /*
         * We will read the first three packets of the header. We start off by
         * defining packet = 1 and increment that value whenever we have
         * successfully read another packet.
         */
        int packet = 1;

        /*
         * While we need more data (which we do until we have read the three
         * header packets), this loop reads from the stream and has a big
         * <code>switch</code> statement which does what it's supposed to do in
         * regards to the current packet.
         */
        while(needMoreData)
        {
            // Read from the InputStream.
            try
            {
                count = input.read(buffer, index, bufferSize);
            }
            catch(IOException exception)
            {
                System.err.println("Could not read from the input stream.");
                System.err.println(exception);
            }

            // We let SyncState know how many bytes we read.
            joggSyncState.wrote(count);

            /*
             * We want to read the first three packets. For the first packet, we
             * need to initialize the StreamState object and a couple of other
             * things. For packet two and three, the procedure is the same: we
             * take out a page, and then we take out the packet.
             */
            switch(packet)
            {
            // The first packet.
                case 1:
                {
                    // We take out a page.
                    switch(joggSyncState.pageout(joggPage))
                    {
                    // If there is a hole in the data, we must exit.
                        case -1:
                        {
                            System.err.println("There is a hole in the first "
                                    + "packet data.");
                            return false;
                        }

                        // If we need more data, we break to get it.
                        case 0:
                        {
                            break;
                        }

                        /*
                         * We got where we wanted. We have successfully read the
                         * first packet, and we will now initialize and reset
                         * StreamState, and initialize the Info and Comment
                         * objects. Afterwards we will check that the page
                         * doesn't contain any errors, that the packet doesn't
                         * contain any errors and that it's Vorbis data.
                         */
                        case 1:
                        {
                            // Initializes and resets StreamState.
                            joggStreamState.init(joggPage.serialno());
                            joggStreamState.reset();

                            // Initializes the Info and Comment objects.
                            jorbisInfo.init();
                            jorbisComment.init();

                            // Check the page (serial number and stuff).
                            if(joggStreamState.pagein(joggPage) == -1)
                            {
                                System.err.println("We got an error while "
                                        + "reading the first header page.");
                                return false;
                            }

                            /*
                             * Try to extract a packet. All other return values
                             * than "1" indicates there's something wrong.
                             */
                            if(joggStreamState.packetout(joggPacket) != 1)
                            {
                                System.err.println("We got an error while "
                                        + "reading the first header packet.");
                                return false;
                            }

                            /*
                             * We give the packet to the Info object, so that it
                             * can extract the Comment-related information,
                             * among other things. If this fails, it's not
                             * Vorbis data.
                             */
                            if(jorbisInfo.synthesis_headerin(jorbisComment,
                                    joggPacket) < 0)
                            {
                                System.err.println("We got an error while "
                                        + "interpreting the first packet. "
                                        + "Apparantly, it's not Vorbis data.");
                                return false;
                            }

                            // We're done here, let's increment "packet".
                            packet++ ;
                            break;
                        }
                    }

                    /*
                     * Note how we are NOT breaking here if we have proceeded to
                     * the second packet. We don't want to read from the input
                     * stream again if it's not necessary.
                     */
                    if(packet == 1)
                        break;
                }

                // The code for the second and third packets follow.
                //$FALL-THROUGH$
                case 2:
                case 3:
                {
                    // Try to get a new page again.
                    switch(joggSyncState.pageout(joggPage))
                    {
                    // If there is a hole in the data, we must exit.
                        case -1:
                        {
                            System.err.println("There is a hole in the second "
                                    + "or third packet data.");
                            return false;
                        }

                        // If we need more data, we break to get it.
                        case 0:
                        {
                            break;
                        }

                        /*
                         * Here is where we take the page, extract a packet and
                         * and (if everything goes well) give the information to
                         * the Info and Comment objects like we did above.
                         */
                        case 1:
                        {
                            // Share the page with the StreamState object.
                            joggStreamState.pagein(joggPage);

                            /*
                             * Just like the switch(...packetout...) lines
                             * above.
                             */
                            switch(joggStreamState.packetout(joggPacket))
                            {
                            // If there is a hole in the data, we must exit.
                                case -1:
                                {
                                    System.err
                                            .println("There is a hole in the first"
                                                    + "packet data.");
                                    return false;
                                }

                                // If we need more data, we break to get it.
                                case 0:
                                {
                                    break;
                                }

                                // We got a packet, let's process it.
                                case 1:
                                {
                                    /*
                                     * Like above, we give the packet to the
                                     * Info and Comment objects.
                                     */
                                    jorbisInfo.synthesis_headerin(
                                            jorbisComment, joggPacket);

                                    // Increment packet.
                                    packet++ ;

                                    if(packet == 4)
                                    {
                                        /*
                                         * There is no fourth packet, so we will
                                         * just end the loop here.
                                         */
                                        needMoreData = false;
                                    }

                                    break;
                                }
                            }

                            break;
                        }
                    }

                    break;
                }
            }

            // We get the new index and an updated buffer.
            index = joggSyncState.buffer(bufferSize);
            buffer = joggSyncState.data;

            /*
             * If we need more data but can't get it, the stream doesn't contain
             * enough information.
             */
            if(count == 0 && needMoreData)
            {
                System.err.println("Not enough header data was supplied.");
                return false;
            }
        }

        return true;
    }

    /**
     * A clean-up method, called when everything is finished. Clears the
     * JOgg/JOrbis objects and closes the <code>InputStream</code>.
     */
    private void cleanUp()
    {

        // Clear the necessary JOgg/JOrbis objects.
        joggStreamState.clear();
        jorbisBlock.clear();
        jorbisDspState.clear();
        if(jorbisInfo != null)
            jorbisInfo.clear();
        joggSyncState.clear();

        // Closes the stream.
        /*
         * try { if(i != null) i.close(); } catch(Exception e) { }
         */

    }

    /**
     * Decodes the current packet and sends it to the audio output line.
     */
    private void decodeCurrentPacket()
    {
        while(pause)
        {
            try
            {
                Thread.sleep(1);
            }
            catch(InterruptedException e)
            {
                e.printStackTrace();
            }
        }
        if(stop)
        {
            cleanUp();
            super.interrupt();
        }
        int samples;

        // Check that the packet is a audio data packet etc.
        if(jorbisBlock.synthesis(joggPacket) == 0)
        {
            // Give the block to the DspState object.
            jorbisDspState.synthesis_blockin(jorbisBlock);
        }

        // We need to know how many samples to process.
        int range;

        /*
         * Get the PCM information and count the samples. And while these
         * samples are more than zero...
         */
        while((samples = jorbisDspState.synthesis_pcmout(pcmInfo, pcmIndex)) > 0)
        {
            // We need to know for how many samples we are going to process.
            if(samples < convertedBufferSize)
            {
                range = samples;
            }
            else
            {
                range = convertedBufferSize;
            }

            // For each channel...
            for(int i1 = 0; i1 < jorbisInfo.channels; i1++ )
            {
                int sampleIndex = i1 * 2;

                // For every sample in our range...
                for(int j = 0; j < range; j++ )
                {
                    /*
                     * Get the PCM value for the channel at the correct
                     * position.
                     */
                    int value = (int) (pcmInfo[0][i1][pcmIndex[i1] + j] * 32767);

                    /*
                     * We make sure our value doesn't exceed or falls below
                     * +-32767.
                     */
                    if(value > 32767)
                    {
                        value = 32767;
                    }
                    if(value < -32768)
                    {
                        value = -32768;
                    }

                    /*
                     * It the value is less than zero, we bitwise-or it with
                     * 32768 (which is 1000000000000000 = 10^15).
                     */
                    if(value < 0)
                        value = value | 32768;

                    /*
                     * Take our value and split it into two, one with the last
                     * byte and one with the first byte.
                     */
                    convertedBuffer[sampleIndex] = (byte) (value);
                    convertedBuffer[sampleIndex + 1] = (byte) (value >>> 8);

                    /*
                     * Move the sample index forward by two (since that's how
                     * many values we get at once) times the number of channels.
                     */
                    sampleIndex += 2 * (jorbisInfo.channels);
                }
            }

            // Write the buffer to the audio output line.
            outputLine.write(convertedBuffer, 0, 2 * jorbisInfo.channels
                    * range);

            // Update the DspState object.
            jorbisDspState.synthesis_read(range);
        }
    }
}
