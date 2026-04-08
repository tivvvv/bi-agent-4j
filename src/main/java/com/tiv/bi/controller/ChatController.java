package com.tiv.bi.controller;

import com.alibaba.cloud.ai.graph.CompiledGraph;
import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.RunnableConfig;
import com.tiv.bi.common.BusinessResponse;
import com.tiv.bi.common.Constants;
import com.tiv.bi.util.ResultUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/chat")
public class ChatController {

    @Resource
    private CompiledGraph compiledGraph;

    @GetMapping("/genSql")
    public BusinessResponse<Map<String, Object>> genSql(@RequestParam String userInput, @RequestParam String userId) {
        RunnableConfig runnableConfig = RunnableConfig.builder()
                .threadId(Constants.THREAD_PREFIX + userId)
                .build();
        OverAllState overAllState = compiledGraph.call(Map.of(
                                Constants.USER_INPUT, userInput,
                                Constants.LOOP_COUNT, 0),
                        runnableConfig)
                .get();
        return ResultUtils.success(overAllState.data());
    }

}