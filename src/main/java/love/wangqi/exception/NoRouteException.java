package love.wangqi.exception;

/**
 * @author: wangqi
 * @description:
 * @date: Created in 2018/6/5 下午7:26
 */
public class NoRouteException extends Exception {
    public NoRouteException() {
        super("no route found");
    }
}
