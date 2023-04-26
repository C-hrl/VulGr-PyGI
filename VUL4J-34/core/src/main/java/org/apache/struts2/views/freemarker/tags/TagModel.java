

package org.apache.struts2.views.freemarker.tags;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.util.ValueStack;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import freemarker.ext.beans.BeansWrapper;
import freemarker.template.SimpleNumber;
import freemarker.template.SimpleSequence;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateTransformModel;
import org.apache.struts2.components.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public abstract class TagModel implements TemplateTransformModel {
    private static final Logger LOG = LogManager.getLogger(TagModel.class);

    protected ValueStack stack;
    protected HttpServletRequest req;
    protected HttpServletResponse res;

    public TagModel(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        this.stack = stack;
        this.req = req;
        this.res = res;
    }

    public Writer getWriter(Writer writer, Map params)
        throws TemplateModelException, IOException {
        Component bean = getBean();
        Container container = (Container) stack.getContext().get(ActionContext.CONTAINER);
        container.inject(bean);

        Map unwrappedParameters = unwrapParameters(params);
        bean.copyParams(unwrappedParameters);

        return new CallbackWriter(bean, writer);
    }

    protected abstract Component getBean();

    protected Map unwrapParameters(Map params) {
        Map map = new HashMap(params.size());
        BeansWrapper objectWrapper = BeansWrapper.getDefaultInstance();
        for (Iterator iterator = params.entrySet().iterator(); iterator.hasNext();) {
            Map.Entry entry = (Map.Entry) iterator.next();

            Object value = entry.getValue();

            if (value != null) {
                
                if (value instanceof TemplateModel) {
                    try {
                        map.put(entry.getKey(), objectWrapper.unwrap((TemplateModel) value));
                    } catch (TemplateModelException e) {
                        LOG.error("failed to unwrap [{}] it will be ignored", value.toString(), e);
                    }
                }
                
                else {
                    map.put(entry.getKey(), value.toString());
                }
            }
        }
        return map;
    }

    protected Map convertParams(Map params) {
        HashMap map = new HashMap(params.size());
        for (Iterator iterator = params.entrySet().iterator(); iterator.hasNext();) {
            Map.Entry entry = (Map.Entry) iterator.next();
            Object value = entry.getValue();
            if (value != null && !complexType(value)) {
                map.put(entry.getKey(), value.toString());
            }
        }
        return map;
    }

    protected Map getComplexParams(Map params) {
        HashMap map = new HashMap(params.size());
        for (Iterator iterator = params.entrySet().iterator(); iterator.hasNext();) {
            Map.Entry entry = (Map.Entry) iterator.next();
            Object value = entry.getValue();
            if (value != null && complexType(value)) {
                if (value instanceof freemarker.ext.beans.BeanModel) {
                    map.put(entry.getKey(), ((freemarker.ext.beans.BeanModel) value).getWrappedObject());
                } else if (value instanceof SimpleNumber) {
                    map.put(entry.getKey(), ((SimpleNumber) value).getAsNumber());
                } else if (value instanceof SimpleSequence) {
                    try {
                        map.put(entry.getKey(), ((SimpleSequence) value).toList());
                    } catch (TemplateModelException e) {
                        if (LOG.isErrorEnabled()) {
                            LOG.error("There was a problem converting a SimpleSequence to a list", e);
                        }
                    }
                }
            }
        }
        return map;
    }

    protected boolean complexType(Object value) {
        return value instanceof freemarker.ext.beans.BeanModel
                || value instanceof SimpleNumber
                || value instanceof SimpleSequence;
    }
}
