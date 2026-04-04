package com.tiv.bi.graph;

import com.alibaba.cloud.ai.graph.CompiledGraph;
import com.alibaba.cloud.ai.graph.KeyStrategyFactory;
import com.alibaba.cloud.ai.graph.StateGraph;
import com.alibaba.cloud.ai.graph.action.AsyncNodeAction;
import com.alibaba.cloud.ai.graph.exception.GraphStateException;
import com.alibaba.cloud.ai.graph.state.strategy.ReplaceStrategy;
import com.tiv.bi.common.Constants;
import com.tiv.bi.common.NodeConstants;
import com.tiv.bi.graph.node.GenSqlNode;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * 图配置类
 */
@Slf4j
@Configuration
public class GraphConfig {

    @Resource
    private VectorStore vectorStore;

    @Bean
    public CompiledGraph graph(ChatClient.Builder chatClientBuilder) throws GraphStateException {
        StateGraph stateGraph = registerStateGraph();

        // 添加节点
        stateGraph.addNode(NodeConstants.GEN_SQL_NODE, AsyncNodeAction.node_async(new GenSqlNode(chatClientBuilder, vectorStore)));

        // 添加边
        stateGraph.addEdge(StateGraph.START, NodeConstants.GEN_SQL_NODE);
        stateGraph.addEdge(NodeConstants.GEN_SQL_NODE, StateGraph.END);

        // 编译
        return stateGraph.compile();
    }

    private StateGraph registerStateGraph() {
        KeyStrategyFactory keyStrategyFactory = () -> Map.of(
                Constants.USER_INPUT, new ReplaceStrategy(),
                Constants.GEN_SQL, new ReplaceStrategy());
        return new StateGraph(Constants.BI_AGENT_GRAPH, keyStrategyFactory);
    }

}