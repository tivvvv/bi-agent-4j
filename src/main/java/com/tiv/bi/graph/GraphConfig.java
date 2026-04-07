package com.tiv.bi.graph;

import com.alibaba.cloud.ai.graph.CompiledGraph;
import com.alibaba.cloud.ai.graph.KeyStrategyFactory;
import com.alibaba.cloud.ai.graph.StateGraph;
import com.alibaba.cloud.ai.graph.action.AsyncNodeAction;
import com.alibaba.cloud.ai.graph.exception.GraphStateException;
import com.alibaba.cloud.ai.graph.state.strategy.ReplaceStrategy;
import com.tiv.bi.common.Constants;
import com.tiv.bi.common.NodeConstants;
import com.tiv.bi.graph.node.ExecSqlAndGenExcelNode;
import com.tiv.bi.graph.node.GenSqlNode;
import com.tiv.bi.graph.node.NotifyNode;
import com.tiv.bi.service.EmailService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Map;

/**
 * 图配置类
 */
@Slf4j
@Configuration
public class GraphConfig {

    @Resource
    private VectorStore vectorStore;

    @Resource
    private JdbcTemplate jdbcTemplate;

    @Resource
    private EmailService emailService;

    @Value("${notification.email.receiver}")
    private String receiver;

    @Bean
    public CompiledGraph graph(ChatClient.Builder chatClientBuilder) throws GraphStateException {
        StateGraph stateGraph = registerStateGraph();

        // 添加节点
        stateGraph.addNode(NodeConstants.GEN_SQL_NODE, AsyncNodeAction.node_async(new GenSqlNode(chatClientBuilder, vectorStore)));
        stateGraph.addNode(NodeConstants.EXEC_SQL_AND_GEN_EXCEL_NODE, AsyncNodeAction.node_async(new ExecSqlAndGenExcelNode(jdbcTemplate)));
        stateGraph.addNode(NodeConstants.NOTIFY_NODE, AsyncNodeAction.node_async(new NotifyNode(emailService, receiver)));

        // 添加边
        stateGraph.addEdge(StateGraph.START, NodeConstants.GEN_SQL_NODE);
        stateGraph.addEdge(NodeConstants.GEN_SQL_NODE, NodeConstants.EXEC_SQL_AND_GEN_EXCEL_NODE);
        stateGraph.addEdge(NodeConstants.EXEC_SQL_AND_GEN_EXCEL_NODE, NodeConstants.NOTIFY_NODE);
        stateGraph.addEdge(NodeConstants.NOTIFY_NODE, StateGraph.END);

        // 编译
        return stateGraph.compile();
    }

    private StateGraph registerStateGraph() {
        KeyStrategyFactory keyStrategyFactory = () -> Map.of(
                Constants.USER_INPUT, new ReplaceStrategy(),
                Constants.GEN_SQL, new ReplaceStrategy(),
                Constants.EXCEL, new ReplaceStrategy(),
                Constants.EVALUATE_RESULT, new ReplaceStrategy());
        return new StateGraph(Constants.BI_AGENT_GRAPH, keyStrategyFactory);
    }

}