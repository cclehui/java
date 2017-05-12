import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.apache.log4j.Logger;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 2017/3/25.
 */
public class SimpleServer {

    protected static Logger logger = Logger.getLogger(SimpleServer.class.getName());

    public static void main(String[] args) {

        logger.info("before start server");

        logger.info(System.currentTimeMillis());

//        System.exit(0);

        Integer port = new Integer(8500);

        ServerBootstrap bootstrap = new ServerBootstrap();
        EventLoopGroup bossGroup = new NioEventLoopGroup(2);
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        pipeline.addLast(new StringDecoder());
                        pipeline.addLast(new StringEncoder());
                        pipeline.addLast(new StringHandler());
                    }
                });

//        bootstrap.setOption();

        bootstrap.bind(new InetSocketAddress(port));


    }
}
