package com.cnpc;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.util.ReferenceCountUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * outbound obj handler
 * @author whoami
 * @since 2022-06-01
 */
public class DnHdl extends ChannelOutboundHandlerAdapter {

    private static final Logger LOGGER = LogManager.getLogger();

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
        if (!(msg instanceof String)) {
            return;
        }
        final String ack = (String) msg;
        LOGGER.debug("dn_msg, {}",ack);
        LOGGER.info("wrt_ctx_hex_stt, {}", ack);
        Server.ctx = ctx;
        Server.channel = ctx.channel();
        CtxUtl.wrtCtx(ack, ctx);
        LOGGER.info("wrt_ctx_hex_fin, {}", ack);

//        ctx.channel().close();
//        LOGGER.info("{} channel closed", fu.getTid());
//        ReferenceCountUtil.release(ack);
//        ReferenceCountUtil.release(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        LOGGER.error("error", cause);
        ctx.channel().close();
    }
}
