import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import org.apache.log4j.Logger;

import java.net.InetSocketAddress;
import java.nio.channels.Channels;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 2017/3/27.
 */
public class SimpleClient {

    protected static Logger logger = Logger.getLogger(SimpleClient.class.getName());

    public static void main(String[] args) {

        Bootstrap bootstrap = new Bootstrap();

        EventLoopGroup workerGroup = new NioEventLoopGroup();

        bootstrap.group( workerGroup)
                .channel(NioServerSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        pipeline.addLast(new StringDecoder());
                        pipeline.addLast(new StringClient());
                    }
                });

        ChannelFuture future = bootstrap.connect(new InetSocketAddress("127.0.0.1", 8500));

        try {
            future.channel().closeFuture().await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
        }

    }
}
