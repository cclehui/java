import org.apache.log4j.Logger;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.codec.http.HttpChunkAggregator;
import org.jboss.netty.handler.codec.http.HttpRequestDecoder;
import org.jboss.netty.handler.codec.http.HttpResponseEncoder;
import org.jboss.netty.handler.codec.string.StringEncoder;
import org.jboss.netty.handler.logging.LoggingHandler;
import org.jboss.netty.handler.timeout.IdleStateHandler;
import org.jboss.netty.logging.InternalLogLevel;
import org.jboss.netty.logging.InternalLoggerFactory;
import org.jboss.netty.logging.Log4JLoggerFactory;
import org.jboss.netty.util.HashedWheelTimer;
import org.jboss.netty.util.Timeout;
import org.jboss.netty.util.Timer;
import org.jboss.netty.util.TimerTask;

import java.net.InetSocketAddress;
import java.text.SimpleDateFormat;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2017/4/7.
 */
public class WebSocketServer {

    protected static Logger logger = Logger.getLogger(WebSocketServer.class.getName());

    static final boolean SSL = System.getProperty("ssl") != null;

    public static void main(String[] args) {

        logger.info("before start websocket server");

        logger.info(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis()));

//        System.exit(0);

        Integer port = new Integer(8500);

        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.setFactory(new NioServerSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool()));

        //设置netty日志factory 用log4j输出日志
        InternalLoggerFactory.setDefaultFactory(new Log4JLoggerFactory());

        //连接超时处理的timer
        final Timer timer = new HashedWheelTimer();

        bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
            public ChannelPipeline getPipeline() throws Exception {
                ChannelPipeline pipeline = Channels.pipeline();
                pipeline.addLast("logging", new LoggingHandler(InternalLogLevel.INFO));
                pipeline.addLast("decoder", new HttpRequestDecoder());
                pipeline.addLast("aggrator", new HttpChunkAggregator(65536));
                pipeline.addLast("encoder", new HttpResponseEncoder());
                pipeline.addLast("idle", new IdleStateHandler(timer, 0, 0, 30, TimeUnit.SECONDS));
                pipeline.addLast("server_handler", new WebSocketServerHandler());
                pipeline.addLast("event_handler", new ChannelEventHandler());

                return pipeline;
            }
        });

//        bootstrap.setOption();

        bootstrap.bind(new InetSocketAddress(port));

    }
}
