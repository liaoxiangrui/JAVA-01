package nio.gateway.inbound.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpRequestEncoder;
import io.netty.handler.codec.http.HttpResponseDecoder;
import nio.gateway.outbound.netty.NettyHttpClientOutboundHandler;

/**
 * @author raw
 * @date 2021/1/23
 */
public class NettyHttpInboundInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) {
        ChannelPipeline cp = ch.pipeline();
        // 客户端接收到的是httpResponse响应，所以要使用HttpResponseDecoder进行解码
        cp.addLast(new HttpResponseDecoder());
        // 客户端发送的是httprequest，所以要使用HttpRequestEncoder进行编码
        cp.addLast(new HttpRequestEncoder());
        cp.addLast(new NettyHttpClientOutboundHandler());
    }
}
