package com.cnpc;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.GenericFutureListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * {@link ChannelHandlerContext} operation
 */
public class CtxUtl {

    private static final Logger LOGGER = LogManager.getLogger();

    public static boolean wrtCtx(final String msg, final ChannelHandlerContext ctx) {
        if (null == ctx) {
            LOGGER.error("wrt_ctx_hex_ctx_null_err");
            return false;
        }
        final Channel ch = ctx.channel();
        LOGGER.debug(
            "ctx removed {}, channel active {}, open {}, registered {}, writable {}",
            ctx.isRemoved(), ch.isActive(), ch.isOpen(), ch.isRegistered(), ch.isWritable()
        );
        final String ack = String.format(
            "server get msg from [%s], msg=%s",
            ctx.channel().remoteAddress(), msg
        );
        final ByteBuf buf = Unpooled.copiedBuffer(ack.getBytes());
        LOGGER.info("wrt_ctx_hex_stt, {}", ack);
        try {
            ctx.writeAndFlush(buf).addListeners((GenericFutureListener) future -> {
                if (future.isSuccess()) {
                    LOGGER.info("write dt success.");
                } else {
                    Throwable cause = future.cause();
                    LOGGER.error("write dt err.", cause);
                }
            });
        } catch (Exception ex) {
            LOGGER.error("wrt_ctx_hex_err, {}", msg);
            return false;
        }
        LOGGER.info("wrt_ctx_hex_fin, {}", msg);
        return true;
    }
}
