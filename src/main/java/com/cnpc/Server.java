package com.cnpc;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

import java.util.Map;


/**
 * TCP server
 * @author whoami
 * @since 2024-04-01
 */
public class Server {

    private static final Logger LOGGER = LogManager.getLogger();

    private static final int PL_SIZE = 1; //Runtime.getRuntime().availableProcessors();
    private static final EventLoopGroup BOS_GRP = new NioEventLoopGroup(PL_SIZE);
    public static final EventLoopGroup WRK_GRP = new NioEventLoopGroup(PL_SIZE * 2);

    public static ChannelHandlerContext ctx;

    public static Channel channel;

    public void start(final int port) {
        ServerBootstrap b = new ServerBootstrap();
        b.channel(NioServerSocketChannel.class)
            .option(ChannelOption.SO_BACKLOG, 1024)
            .childOption(ChannelOption.RCVBUF_ALLOCATOR,
                new FixedRecvByteBufAllocator(1024))
            .childOption(ChannelOption.SO_KEEPALIVE, true)
            .childOption(ChannelOption.TCP_NODELAY, true)
            .group(Server.BOS_GRP, Server.WRK_GRP);
        b.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline()
                    .addLast("in", new UpHdl())     // in
                    .addLast("out", new DnHdl())      // out
                ;
            }
        });
        try {
            ChannelFuture future = b.bind(port).sync();
            LOGGER.info("server started, listen {}", port);
            future.channel().closeFuture().sync();
        } catch (Exception ex) {
            LOGGER.error("error", ex);
        } finally {
            Server.shutdown();
        }
    }

    protected static void shutdown() {
        Server.WRK_GRP.shutdownGracefully();
        Server.BOS_GRP.shutdownGracefully();
    }

    public static void main(String[] args) {
        Thread.currentThread().setName("boot");
        Configurator.initialize("Log4j2", "./config/log4j2.xml");
        Thread t = new Thread(new Task());
        t.start();
        Server srv = new Server();
        srv.start(8081);
    }
}
