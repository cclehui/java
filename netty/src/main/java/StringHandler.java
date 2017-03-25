import org.apache.log4j.Logger;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;

/**
 * Created by Administrator on 2017/3/25.
 */
public class StringHandler extends SimpleChannelHandler {

    protected static Logger logger = Logger.getLogger(StringHandler.class.getName());

    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {

        String input = (String)e.getMessage();

        logger.info("输入:\t" + input);

        String output = new StringBuilder(input).reverse().toString();

        ChannelBuffer channelBuffer = ChannelBuffers.wrappedBuffer(output.getBytes());

        e.getChannel().write(channelBuffer);



    }

    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
        e.getCause().printStackTrace();

        ctx.getChannel().close();
    }
}
