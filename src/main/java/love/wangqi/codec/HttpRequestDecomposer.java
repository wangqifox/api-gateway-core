package love.wangqi.codec;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author: wangqi
 * @description:
 * @date: Created in 2018/5/29 下午4:57
 */
public class HttpRequestDecomposer {
    private static final Logger logger = LoggerFactory.getLogger(HttpRequestDecomposer.class);

    private FullHttpRequest request;
    private ObjectMapper objectMapper = new ObjectMapper();

    public HttpRequestDecomposer(FullHttpRequest request) {
        this.request = request;
    }

    /**
     * 获取请求的uri（包含?后面的参数部分）
     *
     * @return
     */
    public String getUri() {
        return request.uri();
    }

    /**
     * 获取请求路径（不包含?后面的参数部分）
     *
     * @return
     */
    public String getPath() {
        QueryStringDecoder stringDecoder = new QueryStringDecoder(getUri(), StandardCharsets.UTF_8);
        return stringDecoder.path();
    }

    /**
     * 获取请求参数
     *
     * @return
     */
    public Map<String, List<String>> getParams() {
        QueryStringDecoder stringDecoder = new QueryStringDecoder(getUri(), StandardCharsets.UTF_8);
        return stringDecoder.parameters();
    }

    /**
     * 获取content-type
     *
     * @return
     */
    public String getContentType() {
        return request.headers().get(HttpHeaderNames.CONTENT_TYPE);
    }

    /**
     * 获取请求头
     *
     * @return
     */
    public Map<String, List<String>> getHeaders() {
        Map<String, List<String>> headers = new HashMap<>();
        Iterator<Map.Entry<String, String>> iterator = request.headers().iteratorAsString();
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            String key = entry.getKey();
            String value = entry.getValue();
            List<String> values = headers.get(key);
            if (values == null) {
                values = new ArrayList<>(1);
                headers.put(key, values);
            }
            values.add(value);
        }
        return headers;
    }

    /**
     * 如果content-type为application/json，将内容转换成JsonNode
     *
     * @return
     */
    public JsonNode getContentJson() {
        return getContentJson(JsonNode.class);
    }

    /**
     * 如果content-type为application/json，以字符串形式返回请求体
     *
     * @return
     */
    public String getContentJsonAsString() {
        return getContentAsString();
    }

    /**
     * 如果content-type为application/json，将内容转换成相应的类型
     *
     * @return
     */
    public <T> T getContentJson(Class<T> valueType) {
        String content = request.content().toString(StandardCharsets.UTF_8);
        try {
            return objectMapper.readValue(content, valueType);
        } catch (IOException e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    /**
     * 如果content-type为application/x-www-form-urlencoded，将内容转换成map
     *
     * @return
     */
    public Map<String, List<String>> getContentFormUrlEncoded() {
        String content = request.content().toString(StandardCharsets.UTF_8);
        QueryStringDecoder stringDecoder = new QueryStringDecoder("?" + content, StandardCharsets.UTF_8);
        return stringDecoder.parameters();
    }

    /**
     * 如果content-type为multipart/form-data，获取内容列表
     *
     * @return
     */
    public List<InterfaceHttpData> getContentFormdata() {
        HttpPostRequestDecoder postRequestDecoder = new HttpPostRequestDecoder(request);
        return postRequestDecoder.getBodyHttpDatas();
    }

    /**
     * 其他类型的content-type则直接返回相应的ByteBuf
     *
     * @return
     */
    public ByteBuf getContentOther() {
        return request.content();
    }

    /**
     * 以字符串形式返回请求体
     *
     * @return
     */
    public String getContentAsString() {
        return request.content().toString(StandardCharsets.UTF_8);
    }

}
