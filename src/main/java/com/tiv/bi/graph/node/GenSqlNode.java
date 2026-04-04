package com.tiv.bi.graph.node;

import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.action.NodeAction;
import com.tiv.bi.common.Constants;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.preretrieval.query.transformation.RewriteQueryTransformer;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import reactor.core.publisher.Flux;

import java.util.Map;

/**
 * 生成sql节点
 */
@Slf4j
@AllArgsConstructor
public class GenSqlNode implements NodeAction {

    private ChatClient.Builder chatClientBuilder;

    private VectorStore vectorStore;

    @Override
    public Map<String, Object> apply(OverAllState state) throws Exception {
        // 1. 从状态机获取用户输入
        String userInput = state.value(Constants.USER_INPUT, "");

        // 2. 重写查询转换器
        RewriteQueryTransformer rewriteQueryTransformer = RewriteQueryTransformer.builder()
                .chatClientBuilder(chatClientBuilder)
                .build();
        
        // 3. RAG召回
        RetrievalAugmentationAdvisor retrievalAugmentationAdvisor = RetrievalAugmentationAdvisor
                .builder()
                .queryTransformers(rewriteQueryTransformer)
                // 指定文档检索器
                .documentRetriever(VectorStoreDocumentRetriever
                        .builder()
                        .vectorStore(vectorStore)
                        .build())
                .build();

        // 4. 调用大模型生成SQL
        Flux<String> content = chatClientBuilder
                .build()
                .prompt()
                .advisors(retrievalAugmentationAdvisor)
                .system("""
                        # 角色:
                        你是一位熟练的SQL专家,负责根据企业数据表结构生成SQL查询.
                        你能够通过向量数据库检索数据库表结构和表之间的关系.
                        用户将以自然语言提出数据检索需求,你需要为其生成可执行的SQL语句.
                        
                        # 要求:
                        1. 在生成SQL之前,先充分理解用户的需求和检索获取的表结构信息.
                        2. 不可凭空虚造数据,若数据不足,直接返回空字符串.
                        3. 根据表结构和关系选择合适的表和字段,生成可执行的SQL.
                        4. 如果有多种SQL查询方案,优先选择最简洁,易理解,性能较好的方案.
                        5. 仅生成可执行的SQL语句,不输出其他任何与SQL无关的文字和解释.
                        6. 禁止以Markdown格式输出,直接以纯文本格式输出.
                        7. 输出中严禁包含'```','SQL'等字符,这些字符不是SQL的一部分,会影响SQL的正常执行.
                        8. 输出中不要有换行符,回车符等特殊字符,这些字符会影响SQL的正常执行.
                        """)
                .user(userInput)
                .stream()
                .content();

        StringBuilder sb = new StringBuilder();
        content.doOnNext(sb::append)
                .blockLast();
        String genSql = sb.toString();
        log.info("GenSqlNode--apply--genSql: {}", genSql);

        return Map.of(Constants.GEN_SQL, genSql);
    }

}