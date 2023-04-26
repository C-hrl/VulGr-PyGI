package com.opensymphony.xwork2.util;


public interface TextParser {

    Object evaluate(char[] openChars, String expression, TextParseUtil.ParsedValueEvaluator evaluator, int maxLoopCount);

}
