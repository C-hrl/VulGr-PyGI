

package org.apache.struts2.components;

import com.opensymphony.xwork2.ObjectFactory;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.ValueStack;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import com.opensymphony.xwork2.util.reflection.ReflectionProvider;
import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.views.annotations.StrutsTagAttribute;

import java.io.Writer;


@StrutsTag(name = "bean", tldTagClass = "org.apache.struts2.views.jsp.BeanTag",
        description = "Instantiate a JavaBean and place it in the context")
public class Bean extends ContextBean {
    protected static final Logger LOG = LogManager.getLogger(Bean.class);

    protected Object bean;
    protected String name;
    protected ObjectFactory objectFactory;
    protected ReflectionProvider reflectionProvider;

    public Bean(ValueStack stack) {
        super(stack);
    }

    @Inject
    public void setObjectFactory(ObjectFactory objectFactory) {
        this.objectFactory = objectFactory;
    }

    @Inject
    public void setReflectionProvider(ReflectionProvider prov) {
        this.reflectionProvider = prov;
    }

    public boolean start(Writer writer) {
        boolean result = super.start(writer);

        ValueStack stack = getStack();

        try {
            String beanName = findString(name, "name", "Bean name is required. Example: com.acme.FooBean or proper Spring bean ID");
            bean = objectFactory.buildBean(beanName, stack.getContext(), false);
        } catch (Exception e) {
            LOG.error("Could not instantiate bean", e);
            return false;
        }

        
        stack.push(bean);

        
        putInContext(bean);

        return result;
    }

    public boolean end(Writer writer, String body) {
        ValueStack stack = getStack();
        stack.pop();

        return super.end(writer, body);
    }

    public void addParameter(String key, Object value) {
        reflectionProvider.setProperty(key, value, bean, getStack().getContext());
    }

    @StrutsTagAttribute(description = "The class name of the bean to be instantiated (must respect JavaBean specification)", required = true)
    public void setName(String name) {
        this.name = name;
    }

}
