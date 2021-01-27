package nio.gateway.filter;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

public interface HttpFilter {

    void reqFilter(FullHttpRequest fullRequest, ChannelHandlerContext ctx);

    FullHttpResponse resFilter(FullHttpResponse response);
}
