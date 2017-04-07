import org.apache.log4j.Logger;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.*;

/**
 * Created by Administrator on 2017/3/27.
 */
public class StringClient extends SimpleChannelUpstreamHandler {

    private int count = 0;

    protected static Logger logger = Logger.getLogger(StringClient.class.getName());

    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {

        String message = "aaaaaaaaaaxxxxxxxxxxxxxxxx";

        e.getChannel().write(ChannelBuffers.wrappedBuffer(message.getBytes()));

    }

    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {

        String message = (String)e.getMessage();

        logger.info("收到回复:\t" + message);

        Thread.sleep(2000);

        String input = "now count is:\t" + count;

        ChannelBuffer channelBuffer = ChannelBuffers.wrappedBuffer(input.getBytes());

        e.getChannel().write(channelBuffer);

        count++;
    }

    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {

        e.getCause().printStackTrace();

        e.getChannel().close();

    }



}
