
package com.opensymphony.xwork2.util;

import org.apache.commons.lang3.ArrayUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class AnnotationUtils {

    private static final Pattern SETTER_PATTERN = Pattern.compile("set([A-Z][A-Za-z0-9]*)$");
    private static final Pattern GETTER_PATTERN = Pattern.compile("(get|is|has)([A-Z][A-Za-z0-9]*)$");

    
    public static void addAllFields(Class<? extends Annotation> annotationClass, Class clazz, List<Field> allFields) {

        if (clazz == null) {
            return;
        }

        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            Annotation ann = field.getAnnotation(annotationClass);
            if (ann!=null) {
                allFields.add(field);
            }
        }
        addAllFields(annotationClass, clazz.getSuperclass(), allFields);
    }

    
    public static void addAllMethods(Class<? extends Annotation> annotationClass, Class clazz, List<Method> allMethods) {

        if (clazz == null) {
            return;
        }

        Method[] methods = clazz.getDeclaredMethods();

        for (Method method : methods) {
            Annotation ann = method.getAnnotation(annotationClass);
            if (ann!=null) {
                allMethods.add(method);
            }
        }
        addAllMethods(annotationClass, clazz.getSuperclass(), allMethods);
    }

    
    public static void addAllInterfaces(Class clazz, List<Class> allInterfaces) {
        if (clazz == null) {
            return;
        }

        Class[] interfaces = clazz.getInterfaces();
        allInterfaces.addAll(Arrays.asList(interfaces));
        addAllInterfaces(clazz.getSuperclass(), allInterfaces);
    }

	
	public static Collection<Method> getAnnotatedMethods(Class clazz, Class<? extends Annotation>... annotation){
        Collection<Method> toReturn = new HashSet<>();

        for (Method m : clazz.getMethods()) {
            if (ArrayUtils.isNotEmpty(annotation) && isAnnotatedBy(m, annotation)) {
                toReturn.add(m);
            } else if (ArrayUtils.isEmpty(annotation) && ArrayUtils.isNotEmpty(m.getAnnotations())) {
                toReturn.add(m);
            }
		}
		
		return toReturn;
	}

	
	public static boolean isAnnotatedBy(AnnotatedElement annotatedElement, Class<? extends Annotation>... annotation) {
        if (ArrayUtils.isEmpty(annotation)) {
            return false;
        }

		for( Class<? extends Annotation> c : annotation ){
			if( annotatedElement.isAnnotationPresent(c) ) return true;
		}

		return false;
	}

    
    public static String resolvePropertyName(Method method) {

        Matcher matcher = SETTER_PATTERN.matcher(method.getName());
        if (matcher.matches() && method.getParameterTypes().length == 1) {
            String raw = matcher.group(1);
            return raw.substring(0, 1).toLowerCase() + raw.substring(1);
        }

        matcher = GETTER_PATTERN.matcher(method.getName());
        if (matcher.matches() && method.getParameterTypes().length == 0) {
            String raw = matcher.group(2);
            return raw.substring(0, 1).toLowerCase() + raw.substring(1);
        }

        return null;
    }

    
    public static <T extends Annotation> T findAnnotation(Class<?> clazz, Class<T> annotationClass) {
        T ann = clazz.getAnnotation(annotationClass);
        while (ann == null && clazz != null) {
            ann = clazz.getAnnotation(annotationClass);
            if (ann == null) {
                ann = clazz.getPackage().getAnnotation(annotationClass);
            }
            if (ann == null) {
                clazz = clazz.getSuperclass();
                if (clazz != null) {
                    ann = clazz.getAnnotation(annotationClass);
                }
            }
        }

        return ann;
    }
}
