
package com.opensymphony.xwork2.util;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.conversion.impl.XWorkConverter;
import com.opensymphony.xwork2.inject.Container;

import java.util.*;



public class TextParseUtil {

    private static final int MAX_RECURSION = 1;

    
    public static String translateVariables(String expression, ValueStack stack) {
        return translateVariables(new char[]{'$', '%'}, expression, stack, String.class, null).toString();
    }


    
    public static String translateVariables(String expression, ValueStack stack, ParsedValueEvaluator evaluator) {
    	return translateVariables(new char[]{'$', '%'}, expression, stack, String.class, evaluator).toString();
    }

    
    public static String translateVariables(char open, String expression, ValueStack stack) {
        return translateVariables(open, expression, stack, String.class, null).toString();
    }

    
    public static Object translateVariables(char open, String expression, ValueStack stack, Class asType) {
    	return translateVariables(open, expression, stack, asType, null);
    }

    
    public static Object translateVariables(char open, String expression, ValueStack stack, Class asType, ParsedValueEvaluator evaluator) {
        return translateVariables(new char[]{open} , expression, stack, asType, evaluator, MAX_RECURSION);
    }

    
    public static Object translateVariables(char[] openChars, String expression, ValueStack stack, Class asType, ParsedValueEvaluator evaluator) {
        return translateVariables(openChars, expression, stack, asType, evaluator, MAX_RECURSION);
    }

    
    public static Object translateVariables(char open, String expression, ValueStack stack, Class asType, ParsedValueEvaluator evaluator, int maxLoopCount) {
        return translateVariables(new char[]{open}, expression, stack, asType, evaluator, maxLoopCount);
    }

    
    public static Object translateVariables(char[] openChars, String expression, final ValueStack stack, final Class asType, final ParsedValueEvaluator evaluator, int maxLoopCount) {

        ParsedValueEvaluator ognlEval = new ParsedValueEvaluator() {
            public Object evaluate(String parsedValue) {
                Object o = stack.findValue(parsedValue, asType);
                if (evaluator != null && o != null) {
                    o = evaluator.evaluate(o.toString());
                }
                return o;
            }
        };

        TextParser parser = ((Container)stack.getContext().get(ActionContext.CONTAINER)).getInstance(TextParser.class);

        return parser.evaluate(openChars, expression, ognlEval, maxLoopCount);
    }

    
    public static Collection<String>  translateVariablesCollection(String expression, ValueStack stack, boolean excludeEmptyElements, ParsedValueEvaluator evaluator) {
        return translateVariablesCollection(new char[]{'$', '%'}, expression, stack, excludeEmptyElements, evaluator, MAX_RECURSION);
    }

    
    public static Collection<String> translateVariablesCollection(
            char[] openChars, String expression, final ValueStack stack, boolean excludeEmptyElements,
            final ParsedValueEvaluator evaluator, int maxLoopCount) {

        ParsedValueEvaluator ognlEval = new ParsedValueEvaluator() {
            public Object evaluate(String parsedValue) {
                return stack.findValue(parsedValue); 
            }
        };

        Map<String, Object> context = stack.getContext();
        TextParser parser = ((Container)context.get(ActionContext.CONTAINER)).getInstance(TextParser.class);

        Object result = parser.evaluate(openChars, expression, ognlEval, maxLoopCount);

        Collection<String> resultCol;
        if (result instanceof Collection) {
            @SuppressWarnings("unchecked")
            Collection<Object> casted = (Collection<Object>)result;
            resultCol = new ArrayList<>();

            XWorkConverter conv = ((Container)context.get(ActionContext.CONTAINER)).getInstance(XWorkConverter.class);

            for (Object element : casted) {
                String stringElement = (String)conv.convertValue(context, element, String.class);
                if (shallBeIncluded(stringElement, excludeEmptyElements)) {
                    if (evaluator != null) {
                        stringElement = evaluator.evaluate(stringElement).toString();
                    }
                    resultCol.add(stringElement);
                }
            }
        } else {
            resultCol = new ArrayList<>();
            String resultStr = translateVariables(expression, stack, evaluator);
            if (shallBeIncluded(resultStr, excludeEmptyElements)) {
                resultCol.add(resultStr);
            }
        }

        return resultCol;
    }

    
    private static boolean shallBeIncluded(String str, boolean excludeEmptyElements) {
        return !excludeEmptyElements || ((str != null) && (str.length() > 0));
    }

    
    public static Set<String> commaDelimitedStringToSet(String s) {
        Set<String> set = new HashSet<>();
        String[] split = s.split(",");
        for (String aSplit : split) {
            String trimmed = aSplit.trim();
            if (trimmed.length() > 0)
                set.add(trimmed);
        }
        return set;
    }


    
    public static interface ParsedValueEvaluator {

    	
    	Object evaluate(String parsedValue);
    }
}
