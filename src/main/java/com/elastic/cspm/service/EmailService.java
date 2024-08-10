package com.elastic.cspm.service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final Random random = new Random();

    @Async
    public String sendEmailNotice(String email){
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            mimeMessageHelper.setTo(email); // 메일 수신자
            mimeMessageHelper.setSubject("이메일 인증"); // 메일 제목
            // 무작위 숫자 6개 생성
            String verificationCode = generateRandomCode(6);
            mimeMessageHelper.setText("<h1>인증 코드: " + verificationCode + "</h1>", true); // HTML 여부 설정

            javaMailSender.send(mimeMessage);

            log.info("Succeeded to send Email");
            return verificationCode;
        } catch (Exception e) {
            log.info("Failed to send Email");
            throw new RuntimeException(e);
        }
    }

    public String todayDate(){
        ZonedDateTime todayDate = LocalDateTime.now(ZoneId.of("Asia/Seoul")).atZone(ZoneId.of("Asia/Seoul"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M월 d일");
        return todayDate.format(formatter);
    }

    private String generateRandomCode(int length) {
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < length; i++) {
            code.append(random.nextInt(10)); // 0부터 9까지의 숫자 중 무작위 선택
        }
        return code.toString();
    }
}
