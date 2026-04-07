package com.tiv.bi.graph.node;

import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.action.NodeAction;
import com.tiv.bi.common.Constants;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import reactor.core.publisher.Flux;

import java.util.Map;

/**
 * 评估SQL节点
 */
@Slf4j
@AllArgsConstructor
public class EvaluateSqlNode implements NodeAction {

    private ChatClient.Builder chatClientBuilder;

    private VectorStore vectorStore;

    @Override
    public Map<String, Object> apply(OverAllState state) throws Exception {
        // 1. 从状态机获取SQL
        String sql = state.value(Constants.GEN_SQL, "");
        String user_input = state.value(Constants.USER_INPUT, "");

        // 2. 向量文档检索器
        RetrievalAugmentationAdvisor retrievalAugmentationAdvisor = RetrievalAugmentationAdvisor
                .builder()
                .documentRetriever(VectorStoreDocumentRetriever.builder()
                        .vectorStore(vectorStore)
                        .build())
                .build();

        // 3. 调用大模型评估SQL
        Flux<String> content = chatClientBuilder.build()
                .prompt()
                .advisors(retrievalAugmentationAdvisor)
                .system(s -> s.text("""
                                # 角色
                                你是一位专业的SQL评审专家,负责评估SQL的正确性和安全性.
                                
                                # 评估标准
                                1. 语法正确性
                                - SQL的语法是否符合MYSQL8规范.
                                - 关键字,函数名,别名使用是否正确.
                                - 括号匹配,引号使用是否正确.
                                
                                2. Schema匹配度
                                - 检查SQL中的表名,列名是否存在.
                                - 检查数据类型是否匹配,例如: 字符串函数不应该用于数字列.
                                - 校验JOIN条件中关联的字段是否匹配.
                                
                                3. 语义准确性
                                - SQL逻辑是否准确反映了用户意图: {user_input}.
                                - WHERE,GROUP BY,HAVING等子句使用是否合理.
                                - 聚合函数使用是否恰当.
                                
                                4. 执行安全性
                                - SQL是否只包含查询操作,禁止出现INSERT,UPDATE,DELETE,DROP,TRUNCATE等破坏性操作.
                                - 是否存在SQL注入风险.
                                - 查询复杂度是否合理.
                                
                                5. 性能考虑
                                - 是否存在明显的性能问题,例如: 笛卡尔积,缺少关键过滤条件.
                                - 子查询使用是否合理.
                                
                                # 输出要求
                                - 如果SQL评估通过则仅返回PASS字符串,禁止输出多余内容.
                                - 如果SQL评估不通过则返回FAIL,并给出不通过的原因.
                                """)
                        .param("user_input", user_input))
                .user(u -> u.text("需要评估的SQL: {sql}")
                        .param("sql", sql))
                .stream()
                .content();

        StringBuilder sb = new StringBuilder();
        content.doOnNext(sb::append)
                .blockLast();
        String result = sb.toString();
        log.info("EvaluateNode--apply--content: {}", result);
        return Map.of(Constants.EVALUATE_RESULT, result);
    }

}