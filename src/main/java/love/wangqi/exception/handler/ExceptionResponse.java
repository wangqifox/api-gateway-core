package love.wangqi.exception.handler;

import io.netty.handler.codec.http.HttpResponseStatus;

/**
 * @author: wangqi
 * @description:
 * @date: Created in 2018/6/5 下午5:30
 */
public class ExceptionResponse {
    private HttpResponseStatus status;
    private String contentType;
    private String content;

    public HttpResponseStatus getStatus() {
        return status;
    }

    public void setStatus(HttpResponseStatus status) {
        this.status = status;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
