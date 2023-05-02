

package org.apache.struts2.components;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.views.annotations.StrutsTagAttribute;
import org.apache.struts2.dispatcher.mapper.ActionMapper;
import org.apache.struts2.dispatcher.mapper.ActionMapping;

import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.inject.Inject;


public abstract class FormButton extends ClosingUIBean {

    static final String BUTTONTYPE_INPUT = "input";
    static final String BUTTONTYPE_BUTTON = "button";
    static final String BUTTONTYPE_IMAGE = "image";

    protected String action;
    protected String method;
    protected String type;

    public FormButton(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    
    public void evaluateExtraParams() {
        super.evaluateExtraParams();

        String submitType = BUTTONTYPE_INPUT;
        if (type != null && (BUTTONTYPE_BUTTON.equalsIgnoreCase(type) || (supportsImageType() && BUTTONTYPE_IMAGE.equalsIgnoreCase(type))))
        {
            submitType = type;
        }

        

        addParameter("type", submitType);

        if (!BUTTONTYPE_INPUT.equals(submitType) && (label == null)) {
            addParameter("label", getParameters().get("nameValue"));
        }

        if (action != null || method != null) {
            String name;

            if (action != null) {
                ActionMapping mapping = new ActionMapping();
                mapping.setName(findString(action));
                if (method != null) {
                    mapping.setMethod(findString(method));
                }
                mapping.setExtension("");
                name = "action:" + actionMapper.getUriFromActionMapping(mapping);
            } else {
                name = "method:" + findString(method);
            }

            addParameter("name", name);
        }

    }

    
    protected void populateComponentHtmlId(Form form) {
        String _tmp_id = "";
        if (id != null) {
            
        	_tmp_id = findStringIfAltSyntax(id);
        }
        else {
            if (form != null && form.getParameters().get("id") != null) {
                _tmp_id = _tmp_id + form.getParameters().get("id").toString() + "_";
            }
            if (name != null) {
                _tmp_id = _tmp_id + escape(name);
            } else if (action != null || method != null){
                if (action != null) {
                    _tmp_id = _tmp_id + escape(action);
                }
                if (method != null) {
                    _tmp_id = _tmp_id + "_" + escape(method);
                }
            } else {
                
                
                if (form != null) {
                    _tmp_id = _tmp_id + form.getSequence();
                }
            }
        }
        addParameter("id", _tmp_id);
    }

    
    protected abstract boolean supportsImageType();

    @Inject
    public void setActionMapper(ActionMapper mapper) {
        this.actionMapper = mapper;
    }

    @StrutsTagAttribute(description="Set action attribute.")
    public void setAction(String action) {
        this.action = action;
    }

    @StrutsTagAttribute(description="Set method attribute.")
    public void setMethod(String method) {
        this.method = method;
    }


    @StrutsTagAttribute(description="The type of submit to use. Valid values are <i>input</i>, " +
                "<i>button</i> and <i>image</i>.", defaultValue="input")
    public void setType(String type) {
        this.type = type;
    }
}
