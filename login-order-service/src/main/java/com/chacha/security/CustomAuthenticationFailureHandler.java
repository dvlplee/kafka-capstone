package com.chacha.security;

import com.chacha.util.ClientInfoUtils;
import com.chacha.event.LoginEvent;
import com.chacha.service.KafkaMessageService;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private final KafkaMessageService kafkaMessageService;
    private final Gson gson;

    public CustomAuthenticationFailureHandler(KafkaMessageService kafkaMessageService, Gson gson) {
        this.kafkaMessageService = kafkaMessageService;
        this.gson = gson;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)
            throws IOException, ServletException {

        String attemptedEmail = request.getParameter("username"); // 시도된 이메일
        String ipAddress = ClientInfoUtils.getClientIp(request);
        String location = ClientInfoUtils.getClientLocation(ipAddress);
        String userAgent = request.getHeader("User-Agent"); // User-Agent 가져오기

        // LoginEvent 생성
        LoginEvent event = new LoginEvent();
        event.setEventType("LOGIN_FAILURE");
        event.setTimeStamp(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").format(new Date()));
        event.setEmail(attemptedEmail);
        event.setIpAddress(ipAddress);
        event.setLocation(location);
        event.setUserAgent(userAgent);
        event.setOs(ClientInfoUtils.detectOS(userAgent));
        event.setBrowser(ClientInfoUtils.detectBrowser(userAgent));

        String reason;
        if (exception instanceof UsernameNotFoundException) {
            reason = "Email not registered: " + attemptedEmail; // 이메일 미등록 메시지
        } else if (exception instanceof BadCredentialsException) {
            reason = "Invalid credentials"; // 잘못된 비밀번호
        } else {
            reason = "Authentication failed: " + exception.getMessage();
        }
        event.setReason(reason);
        System.out.println("Login failure reason: " + reason);

        // 스키마 형태로 로그를 커넥트에 전송
        String payLoad = gson.toJson(event);
        String schema = "{\n" +
                "  \"type\": \"struct\",\n" +
                "  \"fields\": [\n" +
                "    {\"type\": \"string\", \"field\": \"eventType\"},\n" +
                "    {\"type\": \"string\", \"field\": \"timeStamp\"},\n" +
                "    {\"type\": \"string\", \"field\": \"email\"},\n" +
                "    {\"type\": \"string\", \"field\": \"ipAddress\"},\n" +
                "    {\"type\": \"string\", \"field\": \"location\"},\n" +
                "    {\"type\": \"string\", \"field\": \"userAgent\"},\n" +
                "    {\"type\": \"string\", \"field\": \"os\"},\n" +
                "    {\"type\": \"string\", \"field\": \"browser\"},\n" +
                "    {\"type\": \"string\", \"field\": \"reason\"}\n" +
                "  ]\n" +
                "}";
        String schemaWithPayload = "{\n" +
                "  \"schema\": " + schema + ",\n" +
                "  \"payload\": " + payLoad + "\n" +
                "}";

        kafkaMessageService.sendLoginEvent(schemaWithPayload);

        // 로그인 실패 메시지를 URL 파라미터로 전달
        response.sendRedirect("/login?error=true&message=" + URLEncoder.encode(reason, "UTF-8"));

    }
}
