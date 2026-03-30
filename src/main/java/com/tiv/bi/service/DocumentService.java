package com.tiv.bi.service;

import org.springframework.web.multipart.MultipartFile;

public interface DocumentService {

    void handleDocument(MultipartFile multipartFile);

}
