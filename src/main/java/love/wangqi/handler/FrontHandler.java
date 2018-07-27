package love.wangqi.handler;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.multipart.HttpPostRequestEncoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import love.wangqi.codec.DefaultHttpRequestBuilder;
import love.wangqi.codec.HttpRequestBuilder;
import love.wangqi.exception.handler.DefaultExceptionHandler;
import love.wangqi.filter.HttpRequestFilter;
import love.wangqi.server.GatewayServer;

import java.net.URL;


/**
 * @author: wangqi
 * @description:
 * @date: Created in 2018/5/26 21:58
 */
public class FrontHandler extends ChannelInboundHandlerAdapter {
    private final Logger logger = LoggerFactory.getLogger(FrontHandler.class);
    private final DefaultExceptionHandler defaultExceptionHandler = new DefaultExceptionHandler();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!(msg instanceof FullHttpRequest)) {
            GatewayServer.config.getExceptionHandler().handle(ctx, new Exception("未知请求"));
            return;
        }

        FullHttpRequest httpRequest = (FullHttpRequest) msg;
        try {
            for (HttpRequestFilter httpRequestFilter : GatewayServer.config.getHttpRequestFilterList()) {
                httpRequestFilter.filter(GatewayServer.config, ctx, httpRequest);
            }

            HttpRequestBuilder httpRequestBuilder = GatewayServer.config.getHttpRequestBuilder()
                    .setRouteMapper(GatewayServer.config.getRouteMapper())
                    .setOriginRequest(httpRequest);

            DefaultHttpRequestBuilder.RequestHolder requestHolder = httpRequestBuilder.build();
            forward(ctx.channel(), requestHolder.url, requestHolder.request, requestHolder.bodyRequestEncoder);
        } catch (Exception e) {
            logger.error(e.toString());
            if (GatewayServer.config.getExceptionHandler() != null) {
                GatewayServer.config.getExceptionHandler().handle(ctx, e);
            } else {
                defaultExceptionHandler.handle(ctx, e);
            }
        } finally {
            httpRequest.release();
        }
    }

    private void forward(Channel inboundChannel, URL url, HttpRequest request, HttpPostRequestEncoder bodyRequestEncoder) throws Exception {
        String protocol = url.getProtocol() == null ? "http" : url.getProtocol();
        String host = url.getHost() == null ? "127.0.0.1" : url.getHost();
        int port = url.getPort();
        if (port == -1) {
            if ("http".equalsIgnoreCase(protocol)) {
                port = 80;
            } else if ("https".equalsIgnoreCase(protocol)) {
                port = 443;
            }
        }

        if (!"http".equalsIgnoreCase(protocol) && !"https".equalsIgnoreCase(protocol)) {
            System.err.println("Only HTTP(S) is supported.");
            return;
        }

        // Configure SSL context if necessary.
        final boolean ssl = "https".equalsIgnoreCase(protocol);
        final SslContext sslCtx;
        if (ssl) {
            sslCtx = SslContextBuilder.forClient()
                    .trustManager(InsecureTrustManagerFactory.INSTANCE).build();
        } else {
            sslCtx = null;
        }

        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new BackendFilter(sslCtx, inboundChannel));

            Channel ch = b.connect(host, port).sync().channel();

            ch.write(request);
            if (bodyRequestEncoder != null && bodyRequestEncoder.isChunked()) {
                ch.write(bodyRequestEncoder);
            }
            ch.flush();
            ch.closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.info("连接的客户端地址：{}", ctx.channel().remoteAddress());
        super.channelActive(ctx);
    }
}
