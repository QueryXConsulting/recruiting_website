package com.queryx.recruiting_website.utils;

import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class IKAnalyzerUtil {
    public static List<String> cut(String keyword) {
        List<String> strings = new ArrayList<>();
        try (StringReader reader = new StringReader(keyword)) {
            IKSegmenter segment = new IKSegmenter(reader, true);
            Lexeme lexeme;
            while ((lexeme = segment.next()) != null) {
                strings.add(String.valueOf(lexeme));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return strings;
    }
}

