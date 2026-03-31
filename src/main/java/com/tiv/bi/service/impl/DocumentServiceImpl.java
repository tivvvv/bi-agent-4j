package com.tiv.bi.service.impl;

import com.tiv.bi.service.DocumentService;
import com.tiv.bi.splitter.NumberTitleTextSplitter;
import jakarta.annotation.Resource;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class DocumentServiceImpl implements DocumentService {

    @Resource
    private VectorStore vectorStore;

    @Override
    public void handleDocument(MultipartFile multipartFile) {
        // 1. 读取文档
        TikaDocumentReader tikaDocumentReader = new TikaDocumentReader(multipartFile.getResource());
        List<Document> documents = tikaDocumentReader.get();

        // 2. 文档分割
        NumberTitleTextSplitter numberTitleTextSplitter = new NumberTitleTextSplitter();
        List<Document> splits = numberTitleTextSplitter.split(documents);

        // 3. 保存到向量数据库
        vectorStore.add(splits);
    }

}
