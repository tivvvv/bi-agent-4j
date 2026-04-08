package com.tiv.bi.service.impl;

import com.tiv.bi.common.BusinessCodeEnum;
import com.tiv.bi.exception.BusinessException;
import com.tiv.bi.service.EmailService;
import jakarta.annotation.Resource;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;

@Slf4j
@Service
public class EmailServiceImpl implements EmailService {

    @Resource
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String sender;

    @Override
    public void sendEmail(String receiver, String content) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        log.info("sendEmail--start, receiver: {}", receiver);
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setFrom(sender);
            helper.setTo(receiver);
            helper.setSubject("BI 智能体通知");
            helper.setText(content);
            javaMailSender.send(mimeMessage);
            log.info("sendEmail--success, receiver: {}", receiver);
        } catch (Exception e) {
            log.error("sendEmail--fail, receiver: {}, content: {}", receiver, content, e);
            throw new BusinessException(BusinessCodeEnum.SYSTEM_ERROR, "邮件发送失败");
        }
    }

    @Override
    public void sendEmailWithAttachment(String receiver, String content, File file) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        log.info("sendEmailWithAttachment--start, receiver: {}", receiver);
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setFrom(sender);
            helper.setTo(receiver);
            helper.setSubject("BI 智能体通知");
            helper.setText(content);
            // 添加附件
            FileSystemResource fileSystemResource = new FileSystemResource(file);
            helper.addAttachment(file.getName(), fileSystemResource);
            javaMailSender.send(mimeMessage);
            log.info("sendEmailWithAttachment--success, receiver: {}", receiver);
        } catch (Exception e) {
            log.error("sendEmailWithAttachment--fail, receiver: {}, content: {}", receiver, content, e);
            throw new BusinessException(BusinessCodeEnum.SYSTEM_ERROR, "邮件发送失败");
        }
    }

}