package websocket;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.apache.log4j.Logger;

/**
 * Created by Administrator on 2017/5/9.
 */
public class ChannelEventHandler extends ChannelInboundHandlerAdapter {

    protected static Logger logger = Logger.getLogger(ChannelEventHandler.class.getName());

    public void channelActive(final ChannelHandlerContext ctx) {
        logger.info("新连接\t" + ctx.channel());
    }

    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            //连接超时处理
            IdleStateEvent event = (IdleStateEvent)evt;

            if (event.state() == IdleState.ALL_IDLE) {
                logger.info("连接timeout 被关闭\t" + ctx.channel());
                ctx.channel().close();
            }
        }
    }
}
