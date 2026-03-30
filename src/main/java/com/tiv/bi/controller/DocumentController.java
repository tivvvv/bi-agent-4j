package com.tiv.bi.controller;

import com.tiv.bi.common.BusinessResponse;
import com.tiv.bi.service.DocumentService;
import com.tiv.bi.util.ResultUtils;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController("/document")
public class DocumentController {

    @Resource
    private DocumentService documentService;

    @PostMapping("/upload")
    public BusinessResponse<?> upload(MultipartFile file) {
        documentService.handleDocument(file);
        return ResultUtils.success();
    }

}