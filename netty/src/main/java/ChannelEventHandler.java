import org.apache.log4j.Logger;
import org.jboss.netty.channel.ChannelEvent;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.timeout.IdleState;
import org.jboss.netty.handler.timeout.IdleStateEvent;

/**
 * Created by Administrator on 2017/5/9.
 */
public class ChannelEventHandler extends SimpleChannelUpstreamHandler {

    protected static Logger logger = Logger.getLogger(ChannelEventHandler.class.getName());

    public void handleUpstream(ChannelHandlerContext ctx, ChannelEvent e) throws Exception {
        if (e instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent)e;

            if (event.getState() == IdleState.ALL_IDLE) {
                logger.info("连接timeout 被关闭\t" + e.getChannel());
                e.getChannel().close();
            }
        }
    }
}
