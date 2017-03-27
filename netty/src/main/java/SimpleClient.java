import org.apache.log4j.Logger;
import org.jboss.netty.bootstrap.Bootstrap;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.codec.string.StringDecoder;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 2017/3/27.
 */
public class SimpleClient {

    protected static Logger logger = Logger.getLogger(SimpleClient.class.getName());

    public static void main(String[] args) {
        ClientBootstrap bootstrap = new ClientBootstrap(new NioClientSocketChannelFactory(
                Executors.newCachedThreadPool(),
                Executors.newCachedThreadPool()
        ));

        bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
            public ChannelPipeline getPipeline() throws Exception {
                 ChannelPipeline pipeline = Channels.pipeline();
                pipeline.addLast(StringDecoder.class.getName(), new StringDecoder());
                pipeline.addLast(StringClient.class.getName(), new StringClient());

                return pipeline;
            }
        });

        ChannelFuture future = bootstrap.connect(new InetSocketAddress("127.0.0.1", 8500));

        try {
            future.getChannel().getCloseFuture().await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bootstrap.releaseExternalResources();
        }

    }
}
