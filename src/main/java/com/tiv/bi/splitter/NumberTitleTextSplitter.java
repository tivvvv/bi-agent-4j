package com.tiv.bi.splitter;

import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.transformer.splitter.TextSplitter;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 数字标题文本分割器
 */
public class NumberTitleTextSplitter extends TextSplitter {

    /**
     * 数字标题正则, 匹配1. 简介, 1.1 背景, 1.1.1 详情等
     */
    private static final Pattern NUMBER_TITLE_PATTERN = Pattern.compile("^\\d+(?:\\.\\d+)*\\s+.*$", Pattern.MULTILINE);

    @Override
    protected List<String> splitText(String text) {
        List<String> blocks = new ArrayList<>();
        if (StringUtils.isBlank(text)) {
            return blocks;
        }

        Matcher matcher = NUMBER_TITLE_PATTERN.matcher(text);
        List<Integer> startIndices = new ArrayList<>();
        while (matcher.find()) {
            startIndices.add(matcher.start());
        }

        if (startIndices.isEmpty()) {
            blocks.add(text.trim());
            return blocks;
        }

        Integer firstStart = startIndices.get(0);
        // 如果第一个数字标题不是文本开头, 则将开头和第一个数字标题间的前言作为第一个块
        if (firstStart > 0) {
            String introduction = text.substring(0, firstStart).trim();
            if (StringUtils.isNotBlank(introduction)) {
                blocks.add(introduction);
            }
        }

        for (int i = 0; i < startIndices.size(); i++) {
            int start = startIndices.get(i);
            int end = i + 1 < startIndices.size() ? startIndices.get(i + 1) : text.length();
            String block = text.substring(start, end).trim();
            if (StringUtils.isNotBlank(block)) {
                blocks.add(block);
            }
        }
        return blocks;
    }

}