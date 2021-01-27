package nio.gateway.outbound.okhttp;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpUtil;
import nio.gateway.filter.HttpFilter;
import nio.gateway.router.HttpEndpointRouter;
import nio.gateway.router.impl.HttpEndpointRouterImpl;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static io.netty.handler.codec.http.HttpResponseStatus.NO_CONTENT;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * 作业一：（必做）整合你上次作业的httpclient/okhttp；
 *
 * @author raw
 * @date 2021/1/23
 */
public class OkhttpOutboundHandler {

    private final List<String> backendUrls;
    private final OkHttpClient okHttpClient = new OkHttpClient();
    private final HttpEndpointRouter router = new HttpEndpointRouterImpl();

    public OkhttpOutboundHandler(List<String> backendUrls) {
        this.backendUrls = backendUrls.stream().map(this::formatUrl).collect(Collectors.toList());
    }

    private String formatUrl(String backendUrl) {
        return backendUrl.endsWith("/") ? backendUrl.substring(0, backendUrl.length() - 1) : backendUrl;
    }

    public void handle(final FullHttpRequest fullRequest, final ChannelHandlerContext ctx, final HttpFilter filter) {
        final String url = router.randomRoute(backendUrls) + fullRequest.uri();
        filter.reqFilter(fullRequest, ctx);
        fetchGet(fullRequest, ctx, url, filter);
    }

    private void fetchGet(final FullHttpRequest fullRequest, final ChannelHandlerContext ctx, final String url, final HttpFilter filter) {
        final Request request = new Request.Builder().url(url).build();
        final Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull final Call call, @NotNull final IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull final Call call, @NotNull final Response response) {
                handleResponse(fullRequest, ctx, response, filter);
            }
        });
    }

    private void handleResponse(final FullHttpRequest fullRequest, final ChannelHandlerContext ctx, final Response endpointResponse, final HttpFilter filter) {
        FullHttpResponse response = null;
        try {
            byte[] body = Objects.requireNonNull(endpointResponse.body()).bytes();
            response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(body));
            response.headers().set("Content-Type", "application/json");
            response.headers().setInt("Content-Length", response.content().readableBytes());
            response = filter.resFilter(response);
        } catch (Exception e) {
            e.printStackTrace();
            response = new DefaultFullHttpResponse(HTTP_1_1, NO_CONTENT);
            exceptionCaught(ctx, e);
        } finally {
            if (fullRequest != null) {
                if (!HttpUtil.isKeepAlive(fullRequest)) {
                    ctx.write(response).addListener(ChannelFutureListener.CLOSE);
                } else {
                    ctx.write(response);
                }
            }
            ctx.flush();
        }
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
