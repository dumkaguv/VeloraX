package me.didk.common.response;

import me.didk.common.exception.ApiErrorResponse;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@RestControllerAdvice
public class GlobalResponseBodyAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(
            Object body,
            MethodParameter returnType,
            MediaType selectedContentType,
            Class<? extends HttpMessageConverter<?>> selectedConverterType,
            ServerHttpRequest request,
            ServerHttpResponse response
    ) {
        if (isDocumentationEndpoint(request) || body instanceof ApiEnvelope<?> || body instanceof ApiErrorResponse || body instanceof String) {
            return body;
        }

        if (body == null) {
            return ApiEnvelope.success("Success", null);
        }

        if (body instanceof Page<?> page) {
            return ApiEnvelope.paginated(
                    "Success",
                    page.getContent(),
                    page.getTotalElements(),
                    page.getNumber() + 1,
                    page.getSize()
            );
        }

        return ApiEnvelope.success("Success", body);
    }

    private boolean isDocumentationEndpoint(ServerHttpRequest request) {
        if (!(request instanceof ServletServerHttpRequest servletRequest)) {
            return false;
        }
        String path = servletRequest.getServletRequest().getRequestURI();
        return path.startsWith("/swagger-ui") || path.startsWith("/api-docs") || path.startsWith("/v3/api-docs");
    }
}
