import org.apache.log4j.Logger;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.codec.string.StringDecoder;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 2017/3/25.
 */
public class SimpleServer {

    protected static Logger logger = Logger.getLogger(SimpleServer.class.getName());

    public static void main(String[] args) {

        logger.info("before start server");

        Integer port = new Integer(8500);

        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.setFactory(new NioServerSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool()));

        bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
            public ChannelPipeline getPipeline() throws Exception {
                ChannelPipeline pipeline = Channels.pipeline();
                pipeline.addLast(StringDecoder.class.getName(), new StringDecoder());
                pipeline.addLast(StringHandler.class.getName(), new StringHandler());

                return pipeline;
            }
        });

        bootstrap.bind(new InetSocketAddress(port));


    }
}
