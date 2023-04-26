

package org.apache.struts2.views.freemarker;

import com.opensymphony.xwork2.util.ValueStack;
import freemarker.template.ObjectWrapper;
import freemarker.template.SimpleHash;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;



public class ScopesHashModel extends SimpleHash implements TemplateModel {

    private static final long serialVersionUID = 5551686380141886764L;

    private HttpServletRequest request;
    private ServletContext servletContext;
    private ValueStack stack;
    private final Map<String, TemplateModel> unlistedModels = new HashMap<>();
    private volatile Object parametersCache;

    public ScopesHashModel(ObjectWrapper objectWrapper, ServletContext context, HttpServletRequest request, ValueStack stack) {
        super(objectWrapper);
        this.servletContext = context;
        this.request = request;
        this.stack = stack;
    }

    
    public ScopesHashModel(ObjectWrapper objectWrapper, ServletContext context, HttpServletRequest request) {
         super(objectWrapper);
         this.servletContext = context;
         this.request = request;
    }

    
    public void putUnlistedModel(String key, TemplateModel model) {
        unlistedModels.put(key, model);
    }

    public TemplateModel get(String key) throws TemplateModelException {
        
        TemplateModel model = super.get(key);

        if (model != null) {
            return model;
        }


        if (stack != null) {
            Object obj = findValueOnStack(key);

            if (obj != null) {
                return wrap(obj);
            }

            
            obj = stack.getContext().get(key);
            if (obj != null) {
                return wrap(obj);
            }
        }

        if (request != null) {
            
            Object obj = request.getAttribute(key);

            if (obj != null) {
                return wrap(obj);
            }

            
            HttpSession session = request.getSession(false);

            if (session != null) {
                obj = session.getAttribute(key);

                if (obj != null) {
                    return wrap(obj);
                }
            }
        }

        if (servletContext != null) {
            
            Object obj = servletContext.getAttribute(key);

            if (obj != null) {
                return wrap(obj);
            }
        }

        
        model = unlistedModels.get(key);
        if(model != null) {
            return wrap(model);
        }


        return null;
    }

    private Object findValueOnStack(final String key) {
        if ("parameters".equals(key)) {
            if (parametersCache != null) {
                return parametersCache;
            }
            Object parametersLocal = stack.findValue(key);
            parametersCache = parametersLocal;
            return parametersLocal;
        }
        return stack.findValue(key);
    }

    public void put(String string, boolean b) {
        super.put(string, b);
    }

    public void put(String string, Object object) {
        super.put(string, object);
    }
}
