package com.queryx.recruiting_website.utils;

import lombok.extern.slf4j.Slf4j;
import org.wltea.analyzer.core.Lexeme;
import org.wltea.analyzer.core.IKSegmenter;

import java.util.List;
import java.io.IOException;
import java.util.ArrayList;
import java.io.StringReader;

@Slf4j
public class IKAnalyzerUtil {
    public static List<String> cut(String keyword) {
        List<String> strings = new ArrayList<>();
        try (StringReader reader = new StringReader(keyword)) {
            IKSegmenter segment = new IKSegmenter(reader, true);
            Lexeme lexeme;
            while ((lexeme = segment.next()) != null) {
                strings.add(lexeme.getLexemeText());
            }
        } catch (IOException e) {
            log.error("IK分词异常，", e);
        }
        return strings;
    }
}

