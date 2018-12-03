package love.wangqi.context;

import io.netty.channel.Channel;
import io.netty.channel.pool.SimpleChannelPool;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.util.AttributeKey;
import love.wangqi.codec.RequestHolder;

/**
 * @author: wangqi
 * @description:
 * @date: Created in 2018-11-27 11:30
 */
public interface Attributes {
    AttributeKey<FullHttpRequest> REQUEST = AttributeKey.newInstance("httpRequest");
    AttributeKey<Exception> EXCEPTION = AttributeKey.newInstance("exception");
    AttributeKey<FullHttpResponse> RESPONSE = AttributeKey.newInstance("response");
    AttributeKey<Boolean> KEEPALIVE = AttributeKey.newInstance("keepAlive");
    AttributeKey<Channel> SERVER_CHANNEL = AttributeKey.newInstance("serverChannel");
    AttributeKey<SimpleChannelPool> CLIENT_POOL = AttributeKey.newInstance("clientPool");
    AttributeKey<RequestHolder> REQUEST_HOLDER = AttributeKey.newInstance("requestHolder");
}
