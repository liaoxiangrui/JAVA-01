package nio.gateway.outbound.netty;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultLastHttpContent;
import io.netty.util.CharsetUtil;

/**
 * @author raw
 * @date 2021/1/23
 */
public class NettyHttpClientOutboundHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        DefaultLastHttpContent httpContent = (DefaultLastHttpContent) msg;
        System.out.println("服务端消息：" + httpContent.content().toString(CharsetUtil.UTF_8));
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        String msg = "hello,i am client";
        //发送数据
        ctx.writeAndFlush(Unpooled.wrappedBuffer(msg.getBytes(CharsetUtil.UTF_8)));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
