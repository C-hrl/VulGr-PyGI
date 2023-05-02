

package org.apache.struts2.components.template;

import java.io.Writer;
import java.util.Map;

import org.apache.struts2.components.UIBean;

import com.opensymphony.xwork2.util.ValueStack;


public class TemplateRenderingContext {
    Template template;
    ValueStack stack;
    Map parameters;
    UIBean tag;
    Writer writer;

    
    public TemplateRenderingContext(Template template, Writer writer, ValueStack stack, Map params, UIBean tag) {
        this.template = template;
        this.writer = writer;
        this.stack = stack;
        this.parameters = params;
        this.tag = tag;
    }

    public Template getTemplate() {
        return template;
    }

    public ValueStack getStack() {
        return stack;
    }

    public Map getParameters() {
        return parameters;
    }

    public UIBean getTag() {
        return tag;
    }

    public Writer getWriter() {
        return writer;
    }
}
