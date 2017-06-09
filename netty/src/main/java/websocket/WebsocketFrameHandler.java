package websocket;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.*;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggerFactory;

import javax.management.Attribute;

/**
 * Created by Administrator on 2017/5/14.
 */
public class WebsocketFrameHandler extends SimpleChannelInboundHandler<WebSocketFrame> {

    Logger logger = Logger.getLogger(WebsocketFrameHandler.class);

    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame frame) throws Exception {
        logger.info("ffffffffffffffffff");

        // Check for closing frame
        if (frame instanceof CloseWebSocketFrame) {
            logger.info("fram instanceof CloseWebSocketFrame ccccccccc");
            getHandshaker(ctx.channel()).close(ctx.channel(), (CloseWebSocketFrame) frame);
            return;
        }
        if (frame instanceof PingWebSocketFrame) {
            ctx.channel().writeAndFlush(new PongWebSocketFrame(frame.content()));
            return;
        }
        if (!(frame instanceof TextWebSocketFrame)) {
            throw new UnsupportedOperationException(
                    String.format("%s frame types not supported", frame.getClass().getName()));
        }

        // 处理数据 Send the uppercase string back.
        String request = ((TextWebSocketFrame) frame).text();
        logger.info(String.format("rrrrrrrr Channel %s received %s", ctx.channel(), request));
        ctx.channel().writeAndFlush(new TextWebSocketFrame(request.toUpperCase()));
    }

    protected WebSocketServerHandshaker getHandshaker(Channel channel) {
        io.netty.util.Attribute<WebSocketServerHandshaker> handshakerAttribute = channel.attr(ChannelConstant.handshakerAttributeKey);

        return handshakerAttribute.get();
    }
}
