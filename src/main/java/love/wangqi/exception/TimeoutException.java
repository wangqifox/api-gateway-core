package love.wangqi.exception;

/**
 * @author: wangqi
 * @description:
 * @date: Created in 2018/7/28 09:37
 */
public class TimeoutException extends Exception {
    public TimeoutException() {
        super("timeout");
    }
}
