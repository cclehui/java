import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.log4j.Logger;

/**
 * Created by Administrator on 2017/3/27.
 */
public class StringClient extends ChannelInboundHandlerAdapter {

    private int count = 0;

    protected static Logger logger = Logger.getLogger(StringClient.class.getName());

    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {

        String message = "aaaaaaaaaaxxxxxxxxxxxxxxxx";

        ctx.channel().write(message);

    }

    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        String message = (String)msg;

        logger.info("收到回复:\t" + message);

        Thread.sleep(2000);

        String input = "now count is:\t" + count;

        ctx.channel().write(input);

        count++;
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.channel().close();
    }
}
