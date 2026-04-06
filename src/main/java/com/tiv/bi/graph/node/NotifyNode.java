package com.tiv.bi.graph.node;

import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.action.NodeAction;
import com.tiv.bi.common.Constants;
import com.tiv.bi.service.EmailService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.Map;
import java.util.Optional;

/**
 * 通知节点
 */
@Slf4j
@AllArgsConstructor
public class NotifyNode implements NodeAction {

    private final EmailService emailService;

    private final String receiver;

    @Override
    public Map<String, Object> apply(OverAllState state) throws Exception {
        Optional<Object> excel = state.value(Constants.EXCEL);
        if (excel.isPresent()) {
            // 发送含附件的文件
            File file = (File) excel.get();
            emailService.sendEmailWithAttachment(receiver, "查询数据", file);
        } else {
            // 发送纯文本文件
            emailService.sendEmail(receiver, "查询数据不存在");
        }
        return Map.of();
    }

}