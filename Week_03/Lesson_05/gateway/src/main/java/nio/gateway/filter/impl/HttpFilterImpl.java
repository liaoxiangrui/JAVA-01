package nio.gateway.filter.impl;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.util.CharsetUtil;
import nio.gateway.filter.HttpFilter;

/**
 * 作业三：（必做）实现过滤器 ~
 *
 * @author raw
 * @date 2021/1/23
 */
public class HttpFilterImpl implements HttpFilter {

    @Override
    public void reqFilter(FullHttpRequest fullRequest, ChannelHandlerContext ctx) {
        System.out.println("前置过滤器执行~");
    }

    @Override
    public FullHttpResponse resFilter(FullHttpResponse response) {
        String res = response.content().toString(CharsetUtil.UTF_8);
        String appendRes = res.concat("后置过滤器执行啦！");
        response = response.replace(Unpooled.wrappedBuffer(appendRes.getBytes(CharsetUtil.UTF_8)));
        response.headers().setInt("Content-Length", response.content().readableBytes());
        return response;
    }
}
