package com.tiv.bi.graph.edge;

import cn.hutool.core.util.StrUtil;
import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.StateGraph;
import com.alibaba.cloud.ai.graph.action.EdgeAction;
import com.tiv.bi.common.Constants;
import com.tiv.bi.common.NodeConstants;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EvaluateSqlEdge implements EdgeAction {

    @Override
    public String apply(OverAllState state) throws Exception {
        // 1. 从状态机获取SQL评估结果和循环次数
        String evaluateResult = state.value(Constants.EVALUATE_RESULT, "");
        int loopCount = state.value(Constants.LOOP_COUNT, 0);
        log.info("EvaluateSqlEdge--apply--evaluateResult: {}, loopCount: {}", evaluateResult, loopCount);
        // 2. 超过循环上限则直接结束
        if (loopCount > Constants.MAX_LOOP_COUNT) {
            return StateGraph.END;
        }
        // 3. SQL评估通过则进入执行SQL节点
        if (StrUtil.isNotBlank(evaluateResult) && evaluateResult.contains(Constants.PASS)) {
            return NodeConstants.EXEC_SQL_AND_GEN_EXCEL_NODE;
        }
        // 4. 否则重新进入生成SQL节点
        return NodeConstants.GEN_SQL_NODE;
    }

}