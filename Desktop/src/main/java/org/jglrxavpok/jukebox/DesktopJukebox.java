package org.jglrxavpok.jukebox;

import io.netty.bootstrap.*;
import io.netty.channel.*;
import io.netty.channel.nio.*;
import io.netty.channel.socket.*;
import io.netty.channel.socket.nio.*;
import io.netty.util.concurrent.*;

import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import java.nio.*;

import javax.imageio.*;
import javax.swing.*;

import org.jglrxavpok.jukebox.api.*;
import org.jglrxavpok.jukebox.api.packets.*;
import org.jglrxavpok.jukebox.network.*;

public class DesktopJukebox implements IJukebox, Runnable
{

    private JFrame        frame;
    protected boolean     running;
    private Channel       serverChannel;
    private String        name;
    private BufferedImage icon;

    public DesktopJukebox(String name)
    {
        this.name = name;
        running = true;
        frame = new JFrame("Jukebox - " + name);
        JLabel tmp = new JLabel("TMP");
        frame.add(tmp);
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
        }
        catch(IOException e)
        {
            e.printStackTrace();
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

}
