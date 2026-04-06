package com.tiv.bi.service;

import java.io.File;

public interface EmailService {

    void sendEmail(String receiver, String content);

    void sendEmailWithAttachment(String receiver, String content, File file);

}