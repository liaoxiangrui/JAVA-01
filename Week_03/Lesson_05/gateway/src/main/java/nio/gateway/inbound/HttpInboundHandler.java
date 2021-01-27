package nio.gateway.inbound;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.util.ReferenceCountUtil;
import nio.gateway.filter.HttpFilter;
import nio.gateway.filter.impl.HttpFilterImpl;
import nio.gateway.outbound.okhttp.OkhttpOutboundHandler;

import java.util.List;

/**
 * @author raw
 * @date 2021/1/23
 */
public class HttpInboundHandler extends ChannelInboundHandlerAdapter {

    private final OkhttpOutboundHandler handler;
    private final HttpFilter filter = new HttpFilterImpl();

    public HttpInboundHandler(List<String> proxyServers) {
        this.handler = new OkhttpOutboundHandler(proxyServers);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {
            FullHttpRequest fullRequest = (FullHttpRequest) msg;
            handler.handle(fullRequest, ctx, filter);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }
}
