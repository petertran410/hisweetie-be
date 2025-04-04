package com.enterprise.backend.config;

import com.enterprise.backend.model.error.ErrorCode;
import com.enterprise.backend.model.response.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class SecretKeyInterceptor implements HandlerInterceptor {

    @Value("${security.secret-key}")
    private String expectedSecretKey;

    private final ObjectMapper mapper;

    public SecretKeyInterceptor(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // Lấy secret key từ header của yêu cầu
        String secretKey = request.getHeader("X-Force-Signature");

        // Kiểm tra secret key, nếu không hợp lệ trả về lỗi 403
        if (secretKey == null || !secretKey.equals(expectedSecretKey)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json");
            mapper.writeValue(
                    response.getOutputStream(),
                    new ErrorResponse(ErrorCode.UNAUTHORIZED, "Invalid secret key"));
            return false; // Dừng quá trình xử lý request
        }

        // Nếu secret key hợp lệ, cho phép tiếp tục xử lý yêu cầu
        return true;
    }
}
