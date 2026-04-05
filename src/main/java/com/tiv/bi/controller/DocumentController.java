package com.tiv.bi.controller;

import com.tiv.bi.common.BusinessResponse;
import com.tiv.bi.service.DocumentService;
import com.tiv.bi.util.ResultUtils;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/document")
public class DocumentController {

    @Resource
    private DocumentService documentService;

    @PostMapping("/upload")
    public BusinessResponse<?> upload(@RequestPart("multipartFile") MultipartFile multipartFile) {
        documentService.handleDocument(multipartFile);
        return ResultUtils.success();
    }

}