package love.wangqi.filter.command;

import com.netflix.hystrix.*;
import com.netflix.hystrix.exception.HystrixTimeoutException;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.multipart.HttpPostRequestEncoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import love.wangqi.codec.RequestHolder;
import love.wangqi.context.HttpRequestContext;
import love.wangqi.exception.GatewayTimeoutException;
import love.wangqi.handler.BackendFilter;

import java.net.URL;

/**
 * @author: wangqi
 * @description:
 * @date: Created in 2018/7/27 下午3:10
 */
public class ForwardCommand extends HystrixCommand<Void> {
    private ChannelHandlerContext ctx;
    private RequestHolder requestHolder;
    private HttpRequestContext httpRequestContext = HttpRequestContext.getInstance();
    private final static String HTTP = "http";
    private final static String HTTPS = "https";
    private Channel ch;
    private final static EventLoopGroup eventExecutors = new NioEventLoopGroup(8 * 8);

    private final static Logger logger = LoggerFactory.getLogger(ForwardCommand.class);

    public ForwardCommand(ChannelHandlerContext ctx, RequestHolder requestHolder) {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("ForwardCommandGroup"))
                        .andCommandKey(HystrixCommandKey.Factory.asKey(requestHolder.route.getId() + requestHolder.route.getPath()))
                        // 熔断器配置
                        .andCommandPropertiesDefaults(HystrixCommandProperties.Setter().withCircuitBreakerEnabled(true))
                        // command配置
                        .andCommandPropertiesDefaults(
                            HystrixCommandProperties.Setter()
                                .withExecutionTimeoutInMilliseconds(1000)
                                .withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.SEMAPHORE)
                                .withFallbackIsolationSemaphoreMaxConcurrentRequests(10000)
                        )

        );
        this.ctx = ctx;
        this.requestHolder = requestHolder;
    }


    @Override
    protected Void run() throws Exception {
        forward();
        return null;
    }

    @Override
    protected Void getFallback() {
        Exception exception = getExceptionFromThrowable(getExecutionException());
        if (exception instanceof HystrixTimeoutException) {
            logger.error("time out");
            exception = new GatewayTimeoutException();
        }
        httpRequestContext.setException(ctx.channel(), exception);
        HystrixCircuitBreaker breaker = HystrixCircuitBreaker.Factory.getInstance(
                HystrixCommandKey.Factory.asKey(requestHolder.route.getId() + requestHolder.route.getPath())
        );
        logger.info("断路器状态： " + breaker.isOpen());
        return null;
    }

    class UrlMetadata {
        String protocol;
        String host;
        int port;

        public UrlMetadata(String protocol, String host, int port) {
            this.protocol = protocol;
            this.host = host;
            this.port = port;
        }
    }

    private UrlMetadata getProtocol(URL url) {
        String protocol = url.getProtocol() == null ? HTTP : url.getProtocol();
        if (url.getHost() == null) {
            throw new RuntimeException("no host found");
        }
        String host = url.getHost();
        int port = url.getPort();
        if (port == -1) {
            if (HTTP.equalsIgnoreCase(protocol)) {
                port = 80;
            } else if (HTTPS.equalsIgnoreCase(protocol)) {
                port = 443;
            }
        }

        if (!HTTP.equalsIgnoreCase(protocol) && !HTTPS.equalsIgnoreCase(protocol)) {
            throw new RuntimeException("Only HTTP(S) is supported.");
        }
        return new UrlMetadata(protocol, host, port);
    }

    private void forward() throws Exception {
        URL url = requestHolder.url;
        HttpRequest request = requestHolder.request;
        HttpPostRequestEncoder bodyRequestEncoder = requestHolder.bodyRequestEncoder;
        UrlMetadata urlMetadata = getProtocol(url);
        // Configure SSL context if necessary.
        final boolean ssl = HTTPS.equalsIgnoreCase(urlMetadata.protocol);
        final SslContext sslCtx;
        if (ssl) {
            sslCtx = SslContextBuilder.forClient()
                    .trustManager(InsecureTrustManagerFactory.INSTANCE).build();
        } else {
            sslCtx = null;
        }

        Bootstrap bootstrap = new Bootstrap();
        bootstrap
                .group(eventExecutors)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 500)
                .channel(NioSocketChannel.class)
                .handler(new BackendFilter(sslCtx, ctx, requestHolder.route.getTimeoutInMilliseconds()));

        bootstrap.connect(urlMetadata.host, urlMetadata.port).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                ch = future.channel();
                ch.write(request);
                if (bodyRequestEncoder != null && bodyRequestEncoder.isChunked()) {
                    ch.write(bodyRequestEncoder);
                }
                ch.flush();
            }
        });
    }
}
