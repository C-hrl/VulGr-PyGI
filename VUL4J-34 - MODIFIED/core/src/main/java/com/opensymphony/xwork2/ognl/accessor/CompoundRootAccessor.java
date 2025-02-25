
package com.opensymphony.xwork2.ognl.accessor;

import com.opensymphony.xwork2.XWorkConstants;
import com.opensymphony.xwork2.XWorkException;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.ognl.OgnlValueStack;
import com.opensymphony.xwork2.util.CompoundRoot;
import com.opensymphony.xwork2.util.ValueStack;
import ognl.*;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.String.format;
import static org.apache.commons.lang3.BooleanUtils.toBoolean;


public class CompoundRootAccessor implements PropertyAccessor, MethodAccessor, ClassResolver {

    
    public String getSourceAccessor(OgnlContext context, Object target, Object index) {
        return null;
    }

    
    public String getSourceSetter(OgnlContext context, Object target, Object index) {
        return null;
    }

    private final static Logger LOG = LogManager.getLogger(CompoundRootAccessor.class);
    private final static Class[] EMPTY_CLASS_ARRAY = new Class[0];
    private static Map<MethodCall, Boolean> invalidMethods = new ConcurrentHashMap<>();
    private boolean devMode = false;

    @Inject(XWorkConstants.DEV_MODE)
    public void setDevMode(String mode) {
        this.devMode = BooleanUtils.toBoolean(mode);
    }

    public void setProperty(Map context, Object target, Object name, Object value) throws OgnlException {
        CompoundRoot root = (CompoundRoot) target;
        OgnlContext ognlContext = (OgnlContext) context;

        for (Object o : root) {
            if (o == null) {
                continue;
            }

            try {
                if (OgnlRuntime.hasSetProperty(ognlContext, o, name)) {
                    OgnlRuntime.setProperty(ognlContext, o, name, value);

                    return;
                } else if (o instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<Object, Object> map = (Map<Object, Object>) o;
                    try {
                        map.put(name, value);
                        return;
                    } catch (UnsupportedOperationException e) {
                        
                    }
                }






            } catch (IntrospectionException e) {
                
            }
        }

        boolean reportError = toBoolean((Boolean) context.get(ValueStack.REPORT_ERRORS_ON_NO_PROP));

        if (reportError || devMode) {
            final String msg = format("No object in the CompoundRoot has a publicly accessible property named '%s' " +
                    "(no setter could be found).", name);
            if (reportError) {
                throw new XWorkException(msg);
            } else {
                LOG.warn(msg);
            }
        }
    }

    public Object getProperty(Map context, Object target, Object name) throws OgnlException {
        CompoundRoot root = (CompoundRoot) target;
        OgnlContext ognlContext = (OgnlContext) context;

        if (name instanceof Integer) {
            Integer index = (Integer) name;
            return root.cutStack(index);
        } else if (name instanceof String) {
            if ("top".equals(name)) {
                if (root.size() > 0) {
                    return root.get(0);
                } else {
                    return null;
                }
            }

            for (Object o : root) {
                if (o == null) {
                    continue;
                }

                try {
                    if ((OgnlRuntime.hasGetProperty(ognlContext, o, name)) || ((o instanceof Map) && ((Map) o).containsKey(name))) {
                        return OgnlRuntime.getProperty(ognlContext, o, name);
                    }
                } catch (OgnlException e) {
                    if (e.getReason() != null) {
                        final String msg = "Caught an Ognl exception while getting property " + name;
                        throw new XWorkException(msg, e);
                    }
                } catch (IntrospectionException e) {
                    
                }
            }

            
            if (context.containsKey(OgnlValueStack.THROW_EXCEPTION_ON_FAILURE))
                throw new NoSuchPropertyException(target, name);
            else
                return null;
        } else {
            return null;
        }
    }

    public Object callMethod(Map context, Object target, String name, Object[] objects) throws MethodFailedException {
        CompoundRoot root = (CompoundRoot) target;

        if ("describe".equals(name)) {
            Object v;
            if (objects != null && objects.length == 1) {
                v = objects[0];
            } else {
                v = root.get(0);
            }


            if (v instanceof Collection || v instanceof Map || v.getClass().isArray()) {
                return v.toString();
            }

            try {
                Map<String, PropertyDescriptor> descriptors = OgnlRuntime.getPropertyDescriptors(v.getClass());

                int maxSize = 0;
                for (String pdName : descriptors.keySet()) {
                    if (pdName.length() > maxSize) {
                        maxSize = pdName.length();
                    }
                }

                SortedSet<String> set = new TreeSet<>();
                StringBuffer sb = new StringBuffer();
                for (PropertyDescriptor pd : descriptors.values()) {

                    sb.append(pd.getName()).append(": ");
                    int padding = maxSize - pd.getName().length();
                    for (int i = 0; i < padding; i++) {
                        sb.append(" ");
                    }
                    sb.append(pd.getPropertyType().getName());
                    set.add(sb.toString());

                    sb = new StringBuffer();
                }

                sb = new StringBuffer();
                for (Object aSet : set) {
                    String s = (String) aSet;
                    sb.append(s).append("\n");
                }

                return sb.toString();
            } catch (IntrospectionException | OgnlException e) {
                LOG.debug("Got exception in callMethod", e);
            }
            return null;
        }

        for (Object o : root) {
            if (o == null) {
                continue;
            }

            Class clazz = o.getClass();
            Class[] argTypes = getArgTypes(objects);

            MethodCall mc = null;

            if (argTypes != null) {
                mc = new MethodCall(clazz, name, argTypes);
            }

            if ((argTypes == null) || !invalidMethods.containsKey(mc)) {
                try {
                    Object value = OgnlRuntime.callMethod((OgnlContext) context, o, name, objects);

                    if (value != null) {
                        return value;
                    }
                } catch (OgnlException e) {
                    
                    Throwable reason = e.getReason();

                    if (!context.containsKey(OgnlValueStack.THROW_EXCEPTION_ON_FAILURE) && (mc != null) && (reason != null) && (reason.getClass() == NoSuchMethodException.class)) {
                        invalidMethods.put(mc, Boolean.TRUE);
                    } else if (reason != null) {
                        throw new MethodFailedException(o, name, e.getReason());
                    }
                }
            }
        }

        return null;
    }

    public Object callStaticMethod(Map transientVars, Class aClass, String s, Object[] objects) throws MethodFailedException {
        return null;
    }

    public Class classForName(String className, Map context) throws ClassNotFoundException {
        Object root = Ognl.getRoot(context);

        try {
            if (root instanceof CompoundRoot) {
                if (className.startsWith("vs")) {
                    CompoundRoot compoundRoot = (CompoundRoot) root;

                    if ("vs".equals(className)) {
                        return compoundRoot.peek().getClass();
                    }

                    int index = Integer.parseInt(className.substring(2));

                    return compoundRoot.get(index - 1).getClass();
                }
            }
        } catch (Exception e) {
            LOG.debug("Got exception when tried to get class for name [{}]", className, e);
        }

        return Thread.currentThread().getContextClassLoader().loadClass(className);
    }

    private Class[] getArgTypes(Object[] args) {
        if (args == null) {
            return EMPTY_CLASS_ARRAY;
        }

        Class[] classes = new Class[args.length];

        for (int i = 0; i < args.length; i++) {
            Object arg = args[i];
            classes[i] = (arg != null) ? arg.getClass() : Object.class;
        }

        return classes;
    }


    static class MethodCall {
        Class clazz;
        String name;
        Class[] args;
        int hash;

        public MethodCall(Class clazz, String name, Class[] args) {
            this.clazz = clazz;
            this.name = name;
            this.args = args;
            this.hash = clazz.hashCode() + name.hashCode();

            for (Class arg : args) {
                hash += arg.hashCode();
            }
        }

        @Override
        public boolean equals(Object obj) {
            MethodCall mc = (CompoundRootAccessor.MethodCall) obj;

            return (mc.clazz.equals(clazz) && mc.name.equals(name) && Arrays.equals(mc.args, args));
        }

        @Override
        public int hashCode() {
            return hash;
        }
    }
}
