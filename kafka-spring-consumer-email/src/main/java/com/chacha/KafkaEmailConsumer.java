package com.chacha;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class KafkaEmailConsumer {
    private final JavaMailSender mailSender;

    private KafkaEmailConsumer(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @KafkaListener(topics = "security-alert", groupId = "email-group")
    public void consume(String message) {
        System.out.println("받은 메시지: " + message);
        sendMail(message);
    }

    public void sendMail(String message) {
        SimpleMailMessage email = new SimpleMailMessage();

        // prefix와 suffix로 로그의 이메일 부분 추출
        String prefix = "Security Alert: ";
        String suffix = "이(가)";
        String emailAddr = "";
        int start = message.indexOf(prefix) + prefix.length(); // prefix 바로 뒤에서 추출 시작
        int end = message.indexOf(suffix); // 추출을 멈출 지점
        if (start != -1 && end != -1) { // 둘다 인덱스 null이 아니면
            emailAddr =  message.substring(start, end).trim();
        }

        // 이상 감지 알림 본문 추출
        String text = message.replace("Security Alert: ", "");

        email.setTo(emailAddr); // 이메일주소 설정
        email.setSubject("[라멘쟁이] 비정상적인 로그인 시도가 감지됐습니다."); // 제목 설정
        email.setText(text); // 내용 설정
        mailSender.send(email);

        System.out.println("이메일 전송 성공!");
    }
}
