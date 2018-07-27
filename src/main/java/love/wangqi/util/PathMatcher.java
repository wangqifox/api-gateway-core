package love.wangqi.util;

import java.util.Map;

/**
 * @author: wangqi
 * @description:
 * @date: Created in 2018/5/30 上午9:55
 */
public interface PathMatcher {

    boolean match(String pattern, String path);

    Map<String, String> extractUriTemplateVariables(String pattern, String path);
}
