

package org.apache.struts2.views.velocity.components;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.components.Component;
import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.directive.Directive;
import org.apache.velocity.runtime.parser.node.Node;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.util.ValueStack;

public abstract class AbstractDirective extends Directive {
    public String getName() {
        return "s" + getBeanName();
    }

    public abstract String getBeanName();

    
    public int getType() {
        return LINE;
    }

    protected abstract Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res);

    public boolean render(InternalContextAdapter ctx, Writer writer, Node node) throws IOException, ResourceNotFoundException, ParseErrorException, MethodInvocationException {
        
        ValueStack stack = (ValueStack) ctx.get("stack");
        HttpServletRequest req = (HttpServletRequest) stack.getContext().get(ServletActionContext.HTTP_REQUEST);
        HttpServletResponse res = (HttpServletResponse) stack.getContext().get(ServletActionContext.HTTP_RESPONSE);
        Component bean = getBean(stack, req, res);
        Container container = (Container) stack.getContext().get(ActionContext.CONTAINER);
        container.inject(bean);
        
        Map params = createPropertyMap(ctx, node);
        bean.copyParams(params);
        
        bean.start(writer);

        if (getType() == BLOCK) {
            Node body = node.jjtGetChild(node.jjtGetNumChildren() - 1);
            body.render(ctx, writer);
        }

        bean.end(writer, "");
        return true;
    }

    
    protected Map createPropertyMap(InternalContextAdapter contextAdapter, Node node) throws ParseErrorException, MethodInvocationException {
        Map propertyMap;

        int children = node.jjtGetNumChildren();
        if (getType() == BLOCK) {
            children--;
        }

        
        
        
        
        
        
        
        
        Node firstChild = null;
        Object firstValue = null;
        if(children == 1
           && null != (firstChild = node.jjtGetChild(0))
           && null != (firstValue = firstChild.value(contextAdapter))
           && firstValue instanceof Map) {
            propertyMap = (Map)firstValue;
        } else {
            propertyMap = new HashMap();

            for (int index = 0, length = children; index < length; index++) {
                this.putProperty(propertyMap, contextAdapter, node.jjtGetChild(index));
            }
        }

        return propertyMap;
    }

    
    protected void putProperty(Map propertyMap, InternalContextAdapter contextAdapter, Node node) throws ParseErrorException, MethodInvocationException {
        
        String param = node.value(contextAdapter).toString();

        int idx = param.indexOf("=");

        if (idx != -1) {
            String property = param.substring(0, idx);

            String value = param.substring(idx + 1);
            propertyMap.put(property, value);
        } else {
            throw new ParseErrorException("#" + this.getName() + " arguments must include an assignment operator!  For example #tag( Component \"template=mytemplate\" ).  #tag( TextField \"mytemplate\" ) is illegal!");
        }
    }
}
