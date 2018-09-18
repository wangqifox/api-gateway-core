package love.wangqi.filter;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: wangqi
 * @description:
 * @date: Created in 2018/9/3 下午6:02
 */
public class FilterRegistry {
    private static final FilterRegistry INSTANCE = new FilterRegistry();

    public static final FilterRegistry instance() {
        return INSTANCE;
    }

    private final ConcurrentHashMap<String, GatewayFilter> filters = new ConcurrentHashMap<>();

    private FilterRegistry() {
        put("sendErrorFilter", new SendErrorFilter());
        put("sendForwardFilter", new SendForwardFilter());
        put("sendResponseFilter", new SendResponseFilter());
    }

    public GatewayFilter remove(String key) {
        return this.filters.remove(key);
    }

    public GatewayFilter get(String key) {
        return this.filters.get(key);
    }

    public void put(String key, GatewayFilter filter) {
        this.filters.putIfAbsent(key, filter);
    }

    public int size() {
        return this.filters.size();
    }

    public Collection<GatewayFilter> getAllFilters() {
        return this.filters.values();
    }
}
