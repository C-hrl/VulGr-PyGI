
package com.opensymphony.xwork2.ognl.accessor;

import com.opensymphony.xwork2.conversion.impl.XWorkConverter;
import com.opensymphony.xwork2.util.reflection.ReflectionContextState;
import ognl.ObjectPropertyAccessor;
import ognl.OgnlException;

import java.util.Map;


public class XWorkObjectPropertyAccessor extends ObjectPropertyAccessor {
    @Override
    public Object getProperty(Map context, Object target, Object oname)
            throws OgnlException {
        
        
        
        
        context.put(XWorkConverter.LAST_BEAN_CLASS_ACCESSED, target.getClass());
        context.put(XWorkConverter.LAST_BEAN_PROPERTY_ACCESSED, oname.toString());
        ReflectionContextState.updateCurrentPropertyPath(context, oname);
        return super.getProperty(context, target, oname);
    }
}
