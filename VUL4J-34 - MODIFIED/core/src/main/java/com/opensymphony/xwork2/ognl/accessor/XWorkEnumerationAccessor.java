
package com.opensymphony.xwork2.ognl.accessor;

import ognl.EnumerationPropertyAccessor;
import ognl.ObjectPropertyAccessor;
import ognl.OgnlException;

import java.util.Map;



public class XWorkEnumerationAccessor extends EnumerationPropertyAccessor {

    ObjectPropertyAccessor opa = new ObjectPropertyAccessor();

    @Override
    public void setProperty(Map context, Object target, Object name, Object value) throws OgnlException {
        opa.setProperty(context, target, name, value);
    }
}
