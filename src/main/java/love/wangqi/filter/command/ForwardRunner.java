package love.wangqi.filter.command;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.multipart.HttpPostRequestEncoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import love.wangqi.codec.RequestHolder;
import love.wangqi.handler.BackendFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;

/**
 * @author: wangqi
 * @description:
 * @date: Created in 2018/11/22 下午3:42
 */
public class ForwardRunner {
    private Channel serverChannel;
    private RequestHolder requestHolder;
    private final static String HTTP = "http";
    private final static String HTTPS = "https";
    private final static EventLoopGroup eventExecutors = new NioEventLoopGroup(8 * 8);


    private final static Logger logger = LoggerFactory.getLogger(ForwardRunner.class);

    public ForwardRunner(Channel serverChannel, RequestHolder requestHolder) {
        this.serverChannel = serverChannel;
        this.requestHolder = requestHolder;
    }

    public void execute() throws Exception {
        forward();
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

    private void forward2() throws Exception {
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
                .handler(new BackendFilter(sslCtx, serverChannel, requestHolder.route.getTimeoutInMilliseconds()));

        bootstrap.connect(urlMetadata.host, urlMetadata.port).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                Channel ch = future.channel();
                ch.write(request);
                if (bodyRequestEncoder != null && bodyRequestEncoder.isChunked()) {
                    ch.write(bodyRequestEncoder);
                }
                ch.flush();
            }
        });
    }

    private void forward() throws Exception {
        HttpClientPool.INSTANCE.request(requestHolder, serverChannel);
    }
}
