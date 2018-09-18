package love.wangqi.handler;

import io.netty.handler.codec.http.FullHttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import love.wangqi.context.HttpRequestContext;
import love.wangqi.exception.GatewayException;
import love.wangqi.filter.FilterProcessor;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author: wangqi
 * @description:
 * @date: Created in 2018/9/4 上午9:01
 */
public class GatewayRunner {
    private FullHttpRequest httpRequest;
    private HttpRequestContext httpRequestContext = HttpRequestContext.getInstance();
    private final static Logger logger = LoggerFactory.getLogger(GatewayRunner.class);

    static abstract class AbstractDefaultThreadFactory implements ThreadFactory {
        protected static final AtomicInteger poolNumber = new AtomicInteger(1);
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);

        AbstractDefaultThreadFactory() {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() :
                    Thread.currentThread().getThreadGroup();
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r,
                    getNamePrefix() + threadNumber.getAndIncrement(),
                    0);
            if (t.isDaemon()) {
                t.setDaemon(false);
            }
            if (t.getPriority() != Thread.NORM_PRIORITY) {
                t.setPriority(Thread.NORM_PRIORITY);
            }
            return t;
        }

        /**
         * 线程名称前缀
         * @return
         */
        abstract String getNamePrefix();
    }

    static class PreRouteThreadFactory extends AbstractDefaultThreadFactory {

        @Override
        String getNamePrefix() {
            return "PreRoute-" + poolNumber.getAndIncrement() + "-thread-";
        }
    }

    static class RouteThreadFactory extends AbstractDefaultThreadFactory {

        @Override
        String getNamePrefix() {
            return "Route-" + poolNumber.getAndIncrement() + "-thread-";
        }
    }

    static class PostRouteThreadFactory extends AbstractDefaultThreadFactory {

        @Override
        String getNamePrefix() {
            return "PostRoute-" + poolNumber.getAndIncrement() + "-thread-";
        }
    }

    static class ErrorRouteThreadFactory extends AbstractDefaultThreadFactory {

        @Override
        String getNamePrefix() {
            return "ErrorRoute-" + poolNumber.getAndIncrement() + "-thread-";
        }
    }

    private static ExecutorService preRoutePool = new ThreadPoolExecutor(5, 20,
            30L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(), new PreRouteThreadFactory());

    private static ExecutorService routePool = new ThreadPoolExecutor(5, 20,
            30L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(), new RouteThreadFactory());

    private static ExecutorService postRoutePool = new ThreadPoolExecutor(5, 20,
            30L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(), new PostRouteThreadFactory());

    private static ExecutorService errorRoutePool = new ThreadPoolExecutor(5, 20,
            30L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(), new ErrorRouteThreadFactory());

    public GatewayRunner(FullHttpRequest httpRequest) {
        this.httpRequest = httpRequest;
    }

    public void run() {
        CompletableFuture.completedFuture(httpRequest)
                .thenAcceptAsync(httpRequest -> preRoute(), preRoutePool)
                .thenAcceptAsync(httpRequest -> route(), routePool)
                .thenAcceptAsync(httpRequest -> error(), errorRoutePool)
                .thenAcceptAsync(httpRequest -> postRoute(), postRoutePool)
                .exceptionally(throwable -> {
                    CompletableFuture.completedFuture(throwable)
                            .thenAcceptAsync(throwable1 -> {
                                httpRequestContext.setException(httpRequest, (Exception) throwable.getCause());
                                error();
                            }, errorRoutePool);
                    return null;
                });
    }

    private void preRoute() throws GatewayException {
        FilterProcessor.getInstance().preRoute(httpRequest);
    }

    private void route() throws GatewayException {
        FilterProcessor.getInstance().route(httpRequest);
    }

    private void postRoute() throws GatewayException {
        FilterProcessor.getInstance().postRoute(httpRequest);
    }

    public void error() {
        FilterProcessor.getInstance().error(httpRequest);
    }
}
