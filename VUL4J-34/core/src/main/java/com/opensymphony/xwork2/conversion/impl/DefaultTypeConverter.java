





























package com.opensymphony.xwork2.conversion.impl;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.LocaleProvider;
import com.opensymphony.xwork2.conversion.TypeConverter;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.ognl.XWorkTypeConverterWrapper;

import java.lang.reflect.Array;
import java.lang.reflect.Member;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public abstract class DefaultTypeConverter implements TypeConverter {

    protected static String MILLISECOND_FORMAT = ".SSS";

    private static final String NULL_STRING = "null";

    private static final Map<Class, Object> primitiveDefaults;

    private Container container;

    static {
        Map<Class, Object> map = new HashMap<>();
        map.put(Boolean.TYPE, Boolean.FALSE);
        map.put(Byte.TYPE, Byte.valueOf((byte) 0));
        map.put(Short.TYPE, Short.valueOf((short) 0));
        map.put(Character.TYPE, new Character((char) 0));
        map.put(Integer.TYPE, Integer.valueOf(0));
        map.put(Long.TYPE, Long.valueOf(0L));
        map.put(Float.TYPE, new Float(0.0f));
        map.put(Double.TYPE, new Double(0.0));
        map.put(BigInteger.class, new BigInteger("0"));
        map.put(BigDecimal.class, new BigDecimal(0.0));
        primitiveDefaults = Collections.unmodifiableMap(map);
    }

    @Inject
    public void setContainer(Container container) {
        this.container = container;
    }

    public Object convertValue(Map<String, Object> context, Object value, Class toType) {
        return convertValue(value, toType);
    }

    public Object convertValue(Map<String, Object> context, Object target, Member member,
            String propertyName, Object value, Class toType) {
        return convertValue(context, value, toType);
    }
    
    public TypeConverter getTypeConverter( Map<String, Object> context )
    {
        Object obj = context.get(TypeConverter.TYPE_CONVERTER_CONTEXT_KEY);
        if (obj instanceof TypeConverter) {
            return (TypeConverter) obj;
            
        
        } else if (obj instanceof ognl.TypeConverter) {
            return new XWorkTypeConverterWrapper((ognl.TypeConverter) obj);
        }
        return null; 
    }

    
    public Object convertValue(Object value, Class toType) {
        Object result = null;

        if (value != null) {
            
            if (value.getClass().isArray() && toType.isArray()) {
                Class componentType = toType.getComponentType();

                result = Array.newInstance(componentType, Array
                        .getLength(value));
                for (int i = 0, icount = Array.getLength(value); i < icount; i++) {
                    Array.set(result, i, convertValue(Array.get(value, i),
                            componentType));
                }
            } else {
                if ((toType == Integer.class) || (toType == Integer.TYPE))
                    result = (int) longValue(value);
                if ((toType == Double.class) || (toType == Double.TYPE))
                    result = doubleValue(value);
                if ((toType == Boolean.class) || (toType == Boolean.TYPE))
                    result = booleanValue(value) ? Boolean.TRUE : Boolean.FALSE;
                if ((toType == Byte.class) || (toType == Byte.TYPE))
                    result = (byte) longValue(value);
                if ((toType == Character.class) || (toType == Character.TYPE))
                    result = (char) longValue(value);
                if ((toType == Short.class) || (toType == Short.TYPE))
                    result = (short) longValue(value);
                if ((toType == Long.class) || (toType == Long.TYPE))
                    result = longValue(value);
                if ((toType == Float.class) || (toType == Float.TYPE))
                    result = new Float(doubleValue(value));
                if (toType == BigInteger.class)
                    result = bigIntValue(value);
                if (toType == BigDecimal.class)
                    result = bigDecValue(value);
                if (toType == String.class)
                    result = stringValue(value);
                if (Enum.class.isAssignableFrom(toType))
                    result = enumValue(toType, value);
            }
        } else {
            if (toType.isPrimitive()) {
                result = primitiveDefaults.get(toType);
            }
        }
        return result;
    }

    
    public static boolean booleanValue(Object value) {
        if (value == null)
            return false;
        Class c = value.getClass();
        if (c == Boolean.class)
            return (Boolean) value;
        
        
        if (c == Character.class)
            return (Character) value != 0;
        if (value instanceof Number)
            return ((Number) value).doubleValue() != 0;
        return true; 
    }
    
    public Enum<?> enumValue(Class toClass, Object o) {
        Enum<?> result = null;
        if (o == null) {
            result = null;
        } else if (o instanceof String[]) {
            result = Enum.valueOf(toClass, ((String[]) o)[0]);
        } else if (o instanceof String) {
            result = Enum.valueOf(toClass, (String) o);
        }
        return result;
    }

    
    public static long longValue(Object value) throws NumberFormatException {
        if (value == null)
            return 0L;
        Class c = value.getClass();
        if (c.getSuperclass() == Number.class)
            return ((Number) value).longValue();
        if (c == Boolean.class)
            return (Boolean) value ? 1 : 0;
        if (c == Character.class)
            return (Character) value;
        return Long.parseLong(stringValue(value, true));
    }

    
    public static double doubleValue(Object value) throws NumberFormatException {
        if (value == null)
            return 0.0;
        Class c = value.getClass();
        if (c.getSuperclass() == Number.class)
            return ((Number) value).doubleValue();
        if (c == Boolean.class)
            return (Boolean) value ? 1 : 0;
        if (c == Character.class)
            return (Character) value;
        String s = stringValue(value, true);

        return (s.length() == 0) ? 0.0 : Double.parseDouble(s);
        
        
    }

    
    public static BigInteger bigIntValue(Object value)
            throws NumberFormatException {
        if (value == null)
            return BigInteger.valueOf(0L);
        Class c = value.getClass();
        if (c == BigInteger.class)
            return (BigInteger) value;
        if (c == BigDecimal.class)
            return ((BigDecimal) value).toBigInteger();
        if (c.getSuperclass() == Number.class)
            return BigInteger.valueOf(((Number) value).longValue());
        if (c == Boolean.class)
            return BigInteger.valueOf((Boolean) value ? 1 : 0);
        if (c == Character.class)
            return BigInteger.valueOf(((Character) value).charValue());
        return new BigInteger(stringValue(value, true));
    }

    
    public static BigDecimal bigDecValue(Object value)
            throws NumberFormatException {
        if (value == null)
            return BigDecimal.valueOf(0L);
        Class c = value.getClass();
        if (c == BigDecimal.class)
            return (BigDecimal) value;
        if (c == BigInteger.class)
            return new BigDecimal((BigInteger) value);
        if (c.getSuperclass() == Number.class)
            return new BigDecimal(((Number) value).doubleValue());
        if (c == Boolean.class)
            return BigDecimal.valueOf((Boolean) value ? 1 : 0);
        if (c == Character.class)
            return BigDecimal.valueOf(((Character) value).charValue());
        return new BigDecimal(stringValue(value, true));
    }

    
    public static String stringValue(Object value, boolean trim) {
        String result;

        if (value == null) {
            result = NULL_STRING;
        } else {
            result = value.toString();
            if (trim) {
                result = result.trim();
            }
        }
        return result;
    }

    
    public static String stringValue(Object value) {
        return stringValue(value, false);
    }

    protected Locale getLocale(Map<String, Object> context) {
        Locale locale = null;
        if (context != null) {
            locale = (Locale) context.get(ActionContext.LOCALE);
        }
        if (locale == null) {
            locale = container.getInstance(LocaleProvider.class).getLocale();
        }
        return locale;
    }

}
