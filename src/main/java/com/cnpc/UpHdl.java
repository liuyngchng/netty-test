package com.cnpc;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.UnsupportedEncodingException;
import java.util.List;

public class UpHdl extends ByteToMessageDecoder {

    private static final Logger LOGGER = LogManager.getLogger();

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws UnsupportedEncodingException {
        final byte[] bytes = new byte[in.readableBytes()];
        in.readBytes(bytes);
        final String msg = new String(bytes, "UTF-8");
        LOGGER.info("{}, rcv_msg, {}", ctx.channel().remoteAddress(), msg);
        out.add(msg);
        ctx.channel().writeAndFlush(msg);
    }
}
