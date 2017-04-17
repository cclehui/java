import org.apache.log4j.Logger;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.codec.http.HttpChunkAggregator;
import org.jboss.netty.handler.codec.http.HttpContentCompressor;
import org.jboss.netty.handler.codec.http.HttpRequestDecoder;
import org.jboss.netty.handler.codec.http.HttpResponseEncoder;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 2017/4/7.
 */
public class HttpServer {

    protected static Logger logger = Logger.getLogger(HttpServer.class.getName());

    static final boolean SSL = System.getProperty("ssl") != null;

    public static void main(String[] args) {

        logger.info("before start http server");

        logger.info(System.currentTimeMillis());

//        System.exit(0);

        Integer port = new Integer(8500);

        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.setFactory(new NioServerSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool()));

        bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
            public ChannelPipeline getPipeline() throws Exception {
                ChannelPipeline pipeline = Channels.pipeline();
                pipeline.addLast("decoder", new HttpRequestDecoder());
                pipeline.addLast("aggrator", new HttpChunkAggregator(1048576));
                pipeline.addLast("encoder", new HttpResponseEncoder());
                pipeline.addLast("compressor", new HttpContentCompressor());
                pipeline.addLast("server_handler", new HttpServerHandler());

                return pipeline;
            }
        });

//        bootstrap.setOption();

        bootstrap.bind(new InetSocketAddress(port));

    }
}
