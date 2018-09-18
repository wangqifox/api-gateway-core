package love.wangqi.filter;

/**
 * @author: wangqi
 * @description:
 * @date: Created in 2018/6/4 下午7:47
 */
public abstract class GatewayFilter implements IGatewayFilter, Comparable<GatewayFilter> {

    abstract public String filterType();

    abstract public int filterOrder();

    @Override
    public int compareTo(GatewayFilter filter) {
        return Integer.compare(this.filterOrder(), filter.filterOrder());
    }
}
