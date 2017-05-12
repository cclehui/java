import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.log4j.Logger;

import java.util.Date;

/**
 * Created by Administrator on 2017/3/25.
 */
public class StringHandler extends ChannelInboundHandlerAdapter {

    protected static Logger logger = Logger.getLogger(StringHandler.class.getName());

    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        String input = (String)msg;

        logger.info("输入:\t" + input);

        String output = new StringBuilder(input).reverse().toString();

        ctx.channel().write(output);
        ctx.channel().write("now time is " + new Date());



    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.channel().close();
    }

}
