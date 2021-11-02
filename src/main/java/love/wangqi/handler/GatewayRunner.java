package love.wangqi.handler;

import io.netty.channel.Channel;
import love.wangqi.context.ContextUtil;
import love.wangqi.exception.GatewayException;
import love.wangqi.filter.FilterProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author: wangqi
 * @description:
 * @date: Created in 2018/9/4 上午9:01
 */
public class GatewayRunner {
    private final static Logger logger = LoggerFactory.getLogger(GatewayRunner.class);

    private final static GatewayRunner INSTANCE = new GatewayRunner();

    private GatewayRunner() {
    }

    public static GatewayRunner getInstance() {
        return INSTANCE;
    }

    static abstract class AbstractDefaultThreadFactory implements ThreadFactory {
        static final AtomicInteger poolNumber = new AtomicInteger(1);
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
         *
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

    private static ExecutorService preRoutePool = new ThreadPoolExecutor(5, 50,
            30L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(), new PreRouteThreadFactory());

    private static ExecutorService routePool = new ThreadPoolExecutor(5, 100,
            30L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(), new RouteThreadFactory());

    private static ExecutorService postRoutePool = new ThreadPoolExecutor(5, 20,
            30L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(), new PostRouteThreadFactory());

    private static ExecutorService errorRoutePool = new ThreadPoolExecutor(5, 10,
            30L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(), new ErrorRouteThreadFactory());

    private void errorAction(Channel channel, Exception e) {
        CompletableFuture.completedFuture(e)
                .thenAcceptAsync(throwable1 -> {
                    ContextUtil.setException(channel, e);
                    error(channel);
                }, errorRoutePool);
    }

    public void forwardAction(Channel channel) {
        CompletableFuture.completedFuture(channel)
                .thenApplyAsync(ch -> {
                    preRoute(ch);
                    return ch;
                }, preRoutePool)
                .thenApplyAsync(ch -> {
                    route(ch);
                    return ch;
                }, routePool)
                .exceptionally(throwable -> {
                    errorAction(channel, (Exception) throwable.getCause());
                    return null;
                });
    }

    public void errorAction(Channel channel) {
        CompletableFuture.completedFuture(channel)
                .thenApplyAsync(ch -> {
                    error(ch);
                    return ch;
                }, errorRoutePool)
                .exceptionally(throwable -> {
                    errorAction(channel, (Exception) throwable.getCause());
                    return null;
                });
    }

    public void postRoutAction(Channel channel) {
        CompletableFuture.completedFuture(channel)
                .thenApplyAsync(ch -> {
                    postRoute(ch);
                    return ch;
                }, postRoutePool)
                .exceptionally(throwable -> {
                    errorAction(channel, (Exception) throwable.getCause());
                    return null;
                });
    }

    private void preRoute(Channel channel) throws GatewayException {
        FilterProcessor.getInstance().preRoute(channel);
    }

    private void route(Channel channel) throws GatewayException {
        FilterProcessor.getInstance().route(channel);
    }

    private void postRoute(Channel channel) throws GatewayException {
        FilterProcessor.getInstance().postRoute(channel);
    }

    private void error(Channel channel) {
        FilterProcessor.getInstance().error(channel);
    }
}
