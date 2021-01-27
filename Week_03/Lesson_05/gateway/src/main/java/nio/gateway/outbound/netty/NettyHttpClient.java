package nio.gateway.outbound.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import nio.gateway.inbound.netty.NettyHttpInboundInitializer;

/**
 * 作业二：（可选）:使用netty实现后端http访问
 *
 * @author raw
 * @date 2021/1/23
 */
public class NettyHttpClient {

    public void connect(String host, int port) throws Exception {
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            Bootstrap b = new Bootstrap();
            b.group(workerGroup);
            b.channel(NioSocketChannel.class);
            b.option(ChannelOption.SO_KEEPALIVE, true);
            b.handler(new NettyHttpInboundInitializer());

            // Start the client.
            ChannelFuture f = b.connect(host, port).sync();

//            f.channel().write();
//            f.channel().flush();
            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        NettyHttpClient client = new NettyHttpClient();
        client.connect("127.0.0.1", 8801);
    }
}
