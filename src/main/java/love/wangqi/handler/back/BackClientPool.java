package love.wangqi.handler.back;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.pool.AbstractChannelPoolMap;
import io.netty.channel.pool.ChannelPoolMap;
import io.netty.channel.pool.FixedChannelPool;
import io.netty.channel.pool.SimpleChannelPool;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.multipart.HttpPostRequestEncoder;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import love.wangqi.codec.RequestHolder;
import love.wangqi.context.Attributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;


/**
 * @author: wangqi
 * @description:
 * @date: Created in 2018-11-27 16:14
 */
public class BackClientPool {
    private final static Logger logger = LoggerFactory.getLogger(BackClientPool.class);

    private final EventLoopGroup group = new NioEventLoopGroup(8 * 4);
    private final Bootstrap bootstrap = new Bootstrap();
    private ChannelPoolMap<RequestHolder, SimpleChannelPool> poolMap;

    public static final BackClientPool INSTANCE = new BackClientPool();

    private BackClientPool() {
        bootstrap
                .group(group)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 500)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_KEEPALIVE, true);

        poolMap = new AbstractChannelPoolMap<RequestHolder, SimpleChannelPool>() {
            @Override
            protected SimpleChannelPool newPool(RequestHolder requestHolder) {
                return new FixedChannelPool(bootstrap.remoteAddress(requestHolder.getSocketAddress()), new BackPoolHandler(requestHolder), 50);
            }
        };
    }

    public synchronized void request(RequestHolder requestHolder, Channel serverChannel) throws InterruptedException {
        final SimpleChannelPool pool = poolMap.get(requestHolder);
        Future<Channel> f = pool.acquire().sync();
        f.addListener((FutureListener<Channel>) future -> {
            if (future.isSuccess()) {
                HttpRequest request = requestHolder.request;
                HttpPostRequestEncoder bodyRequestEncoder = requestHolder.bodyRequestEncoder;

                Channel clientChannel = future.getNow();

                // 添加读写超时控制器
                clientChannel.pipeline().addFirst("ReadTimeoutHandler",
                        new ReadTimeoutHandler(requestHolder.route.getTimeoutInMilliseconds(), TimeUnit.MILLISECONDS));
                clientChannel.pipeline().addFirst("WriteTimeoutHandler",
                        new WriteTimeoutHandler(500, TimeUnit.MILLISECONDS));

                clientChannel.attr(Attributes.SERVER_CHANNEL).set(serverChannel);
                clientChannel.attr(Attributes.CLIENT_POOL).set(pool);

                clientChannel.write(request);
                if (bodyRequestEncoder != null && bodyRequestEncoder.isChunked()) {
                    clientChannel.write(bodyRequestEncoder);
                }
                clientChannel.flush();
            }
        });
    }
}
