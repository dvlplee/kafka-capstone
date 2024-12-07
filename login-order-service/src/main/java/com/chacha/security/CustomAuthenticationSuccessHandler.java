package com.chacha.security;

import com.chacha.util.ClientInfoUtils;
import com.chacha.event.LoginEvent;
import com.chacha.service.KafkaMessageService;
import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component // 로그인에 성공했을 때 수행할 작업
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final KafkaMessageService kafkaMessageService;
    private final Gson gson;

    public CustomAuthenticationSuccessHandler(KafkaMessageService kafkaMessageService, Gson gson) {
        this.kafkaMessageService = kafkaMessageService;
        this.gson = gson;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException {

        // Authentication 객체에서 UserDetails(즉, User)를 가져옴
        String email = authentication.getName();  // 로그인한 사용자의 이메일 가져오기
        String ipAddress = ClientInfoUtils.getClientIp(request);
        String location = ClientInfoUtils.getClientLocation(ipAddress);
        String userAgent = request.getHeader("User-Agent"); // User-Agent 가져오기

        // Login 이벤트 구성
        LoginEvent event = new LoginEvent();
        event.setEventType("LOGIN_SUCCESS");
        event.setTimeStamp(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZ").format(new Date()));
        event.setEmail(email);
        event.setIpAddress(ipAddress);
        event.setLocation(location);
        event.setUserAgent(userAgent);
        event.setOs(ClientInfoUtils.detectOS(userAgent));
        event.setBrowser(ClientInfoUtils.detectBrowser(userAgent));
        event.setReason(null); // 성공 시 reason은 null

        // 스키마 형태로 로그를 커넥트에 전송
        String payLoad = gson.toJson(event); // LoginEvent 객체를 JSON 형태 문자열로 변환
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
                "    {\"type\": \"string\", \"field\": \"browser\"}\n" +
                "  ]\n" +
                "}";
        String schemaWithPayload = "{\n" +
                "  \"schema\": " + schema + ",\n" +
                "  \"payload\": " + payLoad + "\n" +
                "}";

        kafkaMessageService.sendLoginEvent(schemaWithPayload);

        response.sendRedirect("/menu"); // 로그인 성공하면 /menu로 리다이렉트
    }
}
