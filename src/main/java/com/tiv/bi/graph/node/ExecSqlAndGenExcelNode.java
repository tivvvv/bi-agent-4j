package com.tiv.bi.graph.node;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.action.NodeAction;
import com.tiv.bi.common.Constants;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 执行SQL并生成Excel文件节点
 */
@Slf4j
@AllArgsConstructor
public class ExecSqlAndGenExcelNode implements NodeAction {

    private JdbcTemplate jdbcTemplate;

    @Override
    public Map<String, Object> apply(OverAllState state) throws Exception {
        // 1. 从状态机获取SQL
        String sql = state.value(Constants.GEN_SQL, "");

        // 2. jdbcTemplate执行查询SQL
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
        log.info("ExecSqlAndCreateExcelNode--apply--rows: {}", JSONUtil.toJsonStr(rows));

        // 3. 生成Excel
        File excel = genExcelFile(rows);
        return Map.of(Constants.EXCEL, excel);
    }

    private File genExcelFile(List<Map<String, Object>> rows) throws IOException {
        if (CollectionUtil.isEmpty(rows)) {
            throw new IllegalArgumentException("SQL查询结果为空,无法生成Excel文件");
        }
        XSSFWorkbook workbook = new XSSFWorkbook();
        // 1. 创建Sheet页
        XSSFSheet sheet = workbook.createSheet("sheet1");

        // 2. 创建表头
        XSSFRow header = sheet.createRow(0);
        List<String> columnNames = new ArrayList<>(rows.get(0).keySet());
        for (int i = 0; i < columnNames.size(); i++) {
            header.createCell(i).setCellValue(columnNames.get(i));
        }

        // 3. 填充内容
        for (int i = 0; i < rows.size(); i++) {
            Map<String, Object> columnName2Value = rows.get(i);
            XSSFRow row = sheet.createRow(i + 1);
            for (int j = 0; j < columnNames.size(); j++) {
                Object value = columnName2Value.get(columnNames.get(j));
                row.createCell(j).setCellValue(value != null ? value.toString() : "");
            }
        }

        // 4. 创建临时文件
        File file = File.createTempFile("bi_excel", ".xlsx");
        try (FileOutputStream out = new FileOutputStream(file)) {
            workbook.write(out);
        }
        return file;
    }

}