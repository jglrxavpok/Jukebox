package org.jglrxavpok.jukebox;

import io.netty.bootstrap.*;
import io.netty.channel.*;
import io.netty.channel.nio.*;
import io.netty.channel.socket.*;
import io.netty.channel.socket.nio.*;
import io.netty.util.concurrent.*;
import io.netty.util.concurrent.Future;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import java.nio.*;
import java.util.*;
import java.util.concurrent.*;

import javax.imageio.*;
import javax.swing.*;
import javax.swing.event.*;

import javazoom.jl.decoder.*;
import javazoom.jl.player.*;
import javazoom.jl.player.advanced.*;

import org.jglrxavpok.jukebox.api.*;
import org.jglrxavpok.jukebox.api.music.*;
import org.jglrxavpok.jukebox.api.packets.*;
import org.jglrxavpok.jukebox.network.*;

public class DesktopJukebox implements IJukebox, IJukeboxPlayer, Runnable
{

    private JFrame                     frame;
    protected boolean                  running;
    private Channel                    serverChannel;
    private String                     name;
    private BufferedImage              icon;
    private float                      volume;
    private JLabel                     playingLabel;
    protected JavaSoundAudioDevice     device;
    protected AdvancedPlayer           player;
    private LinkedBlockingQueue<Music> musicQueue;
    private JList<Music>               musicListComp;
    private Music                      currentMusic;
    private Music                      music;
    private DefaultListModel<Music>    musicListModel;
    private ArrayList<Channel>         connected;
    private File                       cacheDir;

    public DesktopJukebox(String name)
    {
        connected = new ArrayList<Channel>();
        musicQueue = new LinkedBlockingQueue<Music>();
        this.volume = 0.05f;
        this.name = name;
        running = true;
        frame = new JFrame("Jukebox - " + name);
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        final JSlider volumeSlider = new JSlider(0, 100, (int) (volume * 100f));
        volumeSlider.addChangeListener(new ChangeListener()
        {

            @Override
            public void stateChanged(ChangeEvent e)
            {
                setVolume((float) volumeSlider.getValue() / 100f);
            }
        });
        addSection("Volume", volumeSlider, panel);
        playingLabel = new JLabel("No music playing");
        addSection(null, playingLabel, panel);
        musicListModel = new DefaultListModel<Music>();
        musicListComp = new JList<Music>(musicListModel);
        musicListComp.setCellRenderer(new MusicCellRenderer<Music>());
        JScrollPane listView = new JScrollPane(musicListComp);
        addSection("Music queue", listView, panel);
        frame.add(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.addWindowListener(new WindowAdapter()
        {
            public void windowClosing(WindowEvent evt)
            {
                shutdown();
            }
        });
        PacketRegistry.init();
        new Thread(this, "Netty wrapper thread").start();
        frame.setVisible(true);

        try
        {
            icon = ImageIO.read(getClass().getResource("/assets/icons/default_icon.png"));
            frame.setIconImage(icon);
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    private void addSection(String title, Component component, JPanel container)
    {
        JPanel panel = new JPanel();
        if(title != null)
            panel.add(new JLabel(title));
        panel.add(component);

        container.add(panel);
    }

    protected void setVolume(float f)
    {
        volume = f;
        System.out.println("new volume: " + f);
        if(device != null)
        {
            player.pause();
            device.setLineGain(volume);
            player.resume();
        }
    }

    protected void shutdown()
    {
        System.out.println("Closing socket.");
        serverChannel.close().addListener(new GenericFutureListener<Future<? super Void>>()
        {

            @Override
            public void operationComplete(Future<? super Void> future) throws Exception
            {
                if(future.isDone())
                {
                    if(future.isSuccess())
                    {
                        System.out.println("Closed socket.");
                    }
                    else
                        future.cause().printStackTrace();
                }
            }
        });
    }

    @Override
    public EnumJukeboxEnd getType()
    {
        return EnumJukeboxEnd.PLAYER;
    }

    @Override
    public void run()
    {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try
        {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>()
                    {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception
                        {
                            ch.pipeline().addLast(new PacketDecoder()).addLast(new PacketEncoder()).addLast(new NettyChannelHandler(DesktopJukebox.this));
                        }
                    }).option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            // Bind and start to accept incoming connections.
            ChannelFuture f = b.bind(SOCKET_PORT).addListener(new GenericFutureListener<Future<? super Void>>()
            {
                @Override
                public void operationComplete(Future<? super Void> future) throws Exception
                {
                }
            }).sync();

            serverChannel = f.channel();
            serverChannel.closeFuture().sync();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public String getName()
    {
        return name;
    }

    public byte[] getImageData()
    {
        ByteBuffer buffer = ByteBuffer.allocate(icon.getWidth() * icon.getHeight() * 4);
        int[] pixels = icon.getRGB(0, 0, icon.getWidth(), icon.getHeight(), null, 0, icon.getWidth());
        for(int color : pixels)
        {
            int alpha = color >> 24 & 0xFF;
            int red = color >> 16 & 0xFF;
            int green = color >> 8 & 0xFF;
            int blue = color & 0xFF;
            buffer.put((byte) alpha);
            buffer.put((byte) red);
            buffer.put((byte) green);
            buffer.put((byte) blue);
        }
        buffer.flip();
        return buffer.array();
    }

    public void addToQueue(final Music music)
    {
        if(currentMusic == null)
        {
            play(music);
        }
        else
            musicQueue.add(music);
        updateMusicList();
    }

    public void setCurrentMusic(Music music)
    {
        this.music = music;
    }

    public Music getCurrentMusic()
    {
        return music;
    }

    public void play(final Music music)
    {
        currentMusic = music;
        playingLabel.setText("<html>Now playing: <u>" + music.getInfos().getTitle() + "</u></html>");
        frame.pack();
        switch(music.getInfos().getFormat())
        {
            case MP3:
                new Thread()
                {
                    public void run()
                    {
                        try
                        {
                            device = (JavaSoundAudioDevice) new JavaSoundAudioDeviceFactory().createAudioDevice();
                            player = new AdvancedPlayer(new ByteArrayInputStream(music.getFileData()), device);
                            device.setLineGain(volume);
                            player.setPlayBackListener(new JLayerListener(DesktopJukebox.this, music));
                            player.play();
                        }
                        catch(JavaLayerException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }.start();
                break;
            case OGG:
                new ThreadPlayVorbis(music, new JLayerListener(this, music)).start();
                break;
            default:
                throw new IllegalArgumentException("Other types than MP3 are not supported yet");
        }
    }

    @Override
    public void playNextInQueue()
    {
        currentMusic = null;
        if(!musicQueue.isEmpty())
        {
            play(pollMusic());
        }
    }

    private Music pollMusic()
    {
        Music m = musicQueue.poll();
        updateMusicList();
        return m;
    }

    private void updateMusicList()
    {
        musicListModel.clear();
        ArrayList<MusicInfos> musicInfos = new ArrayList<MusicInfos>();
        for(Music music : musicQueue)
        {
            musicInfos.add(music.getInfos());
            musicListModel.addElement(music);
        }
        sendToAll(new P1UpdateList(musicInfos));
        System.out.println("Updated list!");
    }

    public void onConnection(Channel channel)
    {
        connected.add(channel);
    }

    public void sendToAll(IPacket packet)
    {
        ArrayList<Channel> toRemove = new ArrayList<Channel>();
        for(Channel c : connected)
        {
            try
            {
                ChannelHelper.writeAndFlush(packet, c);
            }
            catch(IOException e)
            {
                e.printStackTrace();
                toRemove.add(c);
            }
        }
        connected.removeAll(toRemove);
    }

    public File getCacheDir()
    {
        if(cacheDir == null)
        {
            cacheDir = new File(System.getProperty("user.home"), "Jukebox");
            if(!cacheDir.exists())
                cacheDir.mkdirs();
        }
        return cacheDir;
    }

}
