import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.internal.logging.InternalLogLevel;
import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.util.internal.logging.Log4JLoggerFactory;
import org.apache.log4j.Logger;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.netty.util.Timer;
import io.netty.util.TimerTask;

import java.net.InetSocketAddress;
import java.nio.channels.Channels;
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

        EventLoopGroup bossGroup = new NioEventLoopGroup(2);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        //设置netty日志factory 用log4j输出日志
        InternalLoggerFactory.setDefaultFactory(new Log4JLoggerFactory());

        //连接超时处理的timer netty 4.0以后不需要自己设置timmer了
//        final Timer timer = new HashedWheelTimer();

        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        pipeline.addLast(new LoggingHandler(LogLevel.INFO));
//                        pipeline.addLast(new HttpRequestDecoder());
                        pipeline.addLast(new HttpServerCodec());
                        pipeline.addLast(new HttpObjectAggregator(64 * 1024));
                        pipeline.addLast(new ChunkedWriteHandler());
//                        pipeline.addLast(new HttpResponseEncoder());
//                        pipeline.addLast(new WebSocketServerProtocolHandler("/ws"));
                        pipeline.addLast(new IdleStateHandler(0, 0, 10, TimeUnit.SECONDS));
                        pipeline.addLast(new WebSocketServerHandler());
                        pipeline.addLast(new ChannelEventHandler());
                    }
                });

//        bootstrap.setOption();
//        bootstrap.setOption("", 128)
//                .childOption(ChannelOption.SO_KEEPALIVE, true)
//                .option(ChannelOption.TCP_NODELAY, true)
//                // 使用内存池
//                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
//                .option(ChannelOption.SO_SNDBUF, 16 * 1024)
//                // 设置socket 发送 buffer为 16K
//                .option(ChannelOption.SO_RCVBUF, 16 * 1024)
//                // 设置socket 接受 buffer为 16K
//                .option(ChannelOption.WRITE_BUFFER_LOW_WATER_MARK, 4 * 1024)
//                .option(ChannelOption.WRITE_BUFFER_HIGH_WATER_MARK, 8 * 1024);// 设置高低水位线
//        // 默认高水位是32K，低水位是16K

        logger.info("server listening at " + port);

        bootstrap.bind(new InetSocketAddress(port));

    }
}
