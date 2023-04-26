

package org.apache.struts2.components;

import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.ValueStack;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.StrutsException;
import org.apache.struts2.components.template.Template;
import org.apache.struts2.components.template.TemplateEngine;
import org.apache.struts2.components.template.TemplateEngineManager;
import org.apache.struts2.components.template.TemplateRenderingContext;
import org.apache.struts2.util.TextProviderHelper;
import org.apache.struts2.views.annotations.StrutsTagAttribute;
import org.apache.struts2.views.util.ContextUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Writer;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public abstract class UIBean extends Component {
    private static final Logger LOG = LogManager.getLogger(UIBean.class);

    protected HttpServletRequest request;
    protected HttpServletResponse response;

    public UIBean(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack);
        this.request = request;
        this.response = response;
        this.templateSuffix = ContextUtil.getTemplateSuffix(stack.getContext());
    }

    
    protected String templateSuffix;

    
    protected String template;

    
    protected String templateDir;
    protected String theme;

    
    protected String key;

    protected String id;
    protected String cssClass;
    protected String cssStyle;
    protected String cssErrorClass;
    protected String cssErrorStyle;
    protected String disabled;
    protected String label;
    protected String labelPosition;
    protected String labelSeparator;
    protected String requiredPosition;
    protected String errorPosition;
    protected String name;
    protected String requiredLabel;
    protected String tabindex;
    protected String value;
    protected String title;

    
    protected String onclick;
    protected String ondblclick;
    protected String onmousedown;
    protected String onmouseup;
    protected String onmouseover;
    protected String onmousemove;
    protected String onmouseout;
    protected String onfocus;
    protected String onblur;
    protected String onkeypress;
    protected String onkeydown;
    protected String onkeyup;
    protected String onselect;
    protected String onchange;

    
    protected String accesskey;

    
    protected String tooltip;
    protected String tooltipConfig;
    protected String javascriptTooltip;
    protected String tooltipDelay;
    protected String tooltipCssClass;
    protected String tooltipIconPath;

    
    protected Map<String, Object> dynamicAttributes = new HashMap<>();

    protected String defaultTemplateDir;
    protected String defaultUITheme;
    protected String uiThemeExpansionToken;
    protected TemplateEngineManager templateEngineManager;

    @Inject(StrutsConstants.STRUTS_UI_TEMPLATEDIR)
    public void setDefaultTemplateDir(String dir) {
        this.defaultTemplateDir = dir;
    }

    @Inject(StrutsConstants.STRUTS_UI_THEME)
    public void setDefaultUITheme(String theme) {
        this.defaultUITheme = theme;
    }

    @Inject(StrutsConstants.STRUTS_UI_THEME_EXPANSION_TOKEN)
    public void setUIThemeExpansionToken(String uiThemeExpansionToken) {
        this.uiThemeExpansionToken = uiThemeExpansionToken;
    }

    @Inject
    public void setTemplateEngineManager(TemplateEngineManager mgr) {
        this.templateEngineManager = mgr;
    }

    public boolean end(Writer writer, String body) {
        evaluateParams();
        try {
            super.end(writer, body, false);
            mergeTemplate(writer, buildTemplateName(template, getDefaultTemplate()));
        } catch (Exception e) {
            throw new StrutsException(e);
        }
        finally {
            popComponentStack();
        }

        return false;
    }

    
    protected abstract String getDefaultTemplate();

    protected Template buildTemplateName(String myTemplate, String myDefaultTemplate) {
        String template = myDefaultTemplate;

        if (myTemplate != null) {
            template = findString(myTemplate);
        }

        String templateDir = getTemplateDir();
        String theme = getTheme();

        return new Template(templateDir, theme, template);

    }

    protected void mergeTemplate(Writer writer, Template template) throws Exception {
        final TemplateEngine engine = templateEngineManager.getTemplateEngine(template, templateSuffix);
        if (engine == null) {
            throw new ConfigurationException("Unable to find a TemplateEngine for template " + template);
        }

        LOG.debug("Rendering template {}", template);

        final TemplateRenderingContext context = new TemplateRenderingContext(template, writer, getStack(), getParameters(), this);
        engine.renderTemplate(context);
    }

    public String getTemplateDir() {
        String templateDir = null;

        if (this.templateDir != null) {
            templateDir = findString(this.templateDir);
        }

        
        
        if (StringUtils.isBlank(templateDir)) {
            templateDir = stack.findString("#attr.templateDir");
        }

        
        if (StringUtils.isBlank(templateDir)) {
            templateDir = defaultTemplateDir;
        }

        
        if (StringUtils.isBlank(templateDir)) {
            templateDir = "template";
        }

        return templateDir;
    }

    public String getTheme() {
        String theme = null;

        if (this.theme != null) {
            theme = findString(this.theme);
        }

        if (StringUtils.isBlank(theme)) {
            Form form = (Form) findAncestor(Form.class);
            if (form != null) {
                theme = form.getTheme();
            }
        }

        
        
        if (StringUtils.isBlank(theme)) {
            theme = stack.findString("#attr.theme");
        }

        
        if (StringUtils.isBlank(theme)) {
            theme = defaultUITheme;
        }

        return theme;
    }

    public void evaluateParams() {
        String templateDir = getTemplateDir();
        String theme = getTheme();
        
        addParameter("templateDir", templateDir);
        addParameter("theme", theme);
        addParameter("template", template != null ? findString(template) : getDefaultTemplate());
        addParameter("dynamicAttributes", dynamicAttributes);
        addParameter("themeExpansionToken", uiThemeExpansionToken);
        addParameter("expandTheme", uiThemeExpansionToken + theme);

        String name = null;
        String providedLabel = null;

        if (this.key != null) {

            if(this.name == null) {
                this.name = key;
            }

            if(this.label == null) {
                
                providedLabel = TextProviderHelper.getText(key, key, stack);
            }
        }

        if (this.name != null) {
            name = findString(this.name);
            addParameter("name", name);
        }

        if (label != null) {
            addParameter("label", findString(label));
        } else {
            if (providedLabel != null) {
                
                addParameter("label", providedLabel);
            }
        }

        if (labelSeparator != null) {
            addParameter("labelseparator", findString(labelSeparator));
        }

        if (labelPosition != null) {
            addParameter("labelposition", findString(labelPosition));
        }

        if (requiredPosition != null) {
            addParameter("requiredPosition", findString(requiredPosition));
        }

        if (errorPosition != null) {
            addParameter("errorposition", findString(errorPosition));
        }
        
        if (requiredLabel != null) {
            addParameter("required", findValue(requiredLabel, Boolean.class));
        }

        if (disabled != null) {
            addParameter("disabled", findValue(disabled, Boolean.class));
        }

        if (tabindex != null) {
            addParameter("tabindex", findString(tabindex));
        }

        if (onclick != null) {
            addParameter("onclick", findString(onclick));
        }

        if (ondblclick != null) {
            addParameter("ondblclick", findString(ondblclick));
        }

        if (onmousedown != null) {
            addParameter("onmousedown", findString(onmousedown));
        }

        if (onmouseup != null) {
            addParameter("onmouseup", findString(onmouseup));
        }

        if (onmouseover != null) {
            addParameter("onmouseover", findString(onmouseover));
        }

        if (onmousemove != null) {
            addParameter("onmousemove", findString(onmousemove));
        }

        if (onmouseout != null) {
            addParameter("onmouseout", findString(onmouseout));
        }

        if (onfocus != null) {
            addParameter("onfocus", findString(onfocus));
        }

        if (onblur != null) {
            addParameter("onblur", findString(onblur));
        }

        if (onkeypress != null) {
            addParameter("onkeypress", findString(onkeypress));
        }

        if (onkeydown != null) {
            addParameter("onkeydown", findString(onkeydown));
        }

        if (onkeyup != null) {
            addParameter("onkeyup", findString(onkeyup));
        }

        if (onselect != null) {
            addParameter("onselect", findString(onselect));
        }

        if (onchange != null) {
            addParameter("onchange", findString(onchange));
        }

        if (accesskey != null) {
            addParameter("accesskey", findString(accesskey));
        }

        if (cssClass != null) {
            addParameter("cssClass", findString(cssClass));
        }

        if (cssStyle != null) {
            addParameter("cssStyle", findString(cssStyle));
        }

        if (cssErrorClass != null) {
            addParameter("cssErrorClass", findString(cssErrorClass));
        }

        if (cssErrorStyle != null) {
            addParameter("cssErrorStyle", findString(cssErrorStyle));
        }

        if (title != null) {
            addParameter("title", findString(title));
        }


        
        if (parameters.containsKey("value")) {
            parameters.put("nameValue", parameters.get("value"));
        } else {
            if (evaluateNameValue()) {
                final Class valueClazz = getValueClassType();

                if (valueClazz != null) {
                    if (value != null) {
                        addParameter("nameValue", findValue(value, valueClazz));
                    } else if (name != null) {
                        String expr = completeExpressionIfAltSyntax(name);

                        addParameter("nameValue", findValue(expr, valueClazz));
                    }
                } else {
                    if (value != null) {
                        addParameter("nameValue", findValue(value));
                    } else if (name != null) {
                        addParameter("nameValue", findValue(name));
                    }
                }
            }
        }

        final Form form = (Form) findAncestor(Form.class);

        
        populateComponentHtmlId(form);

        if (form != null ) {
            addParameter("form", form.getParameters());

            if ( name != null ) {
                
                List<String> tags = (List<String>) form.getParameters().get("tagNames");
                tags.add(name);
            }
        }


        
        if (tooltipConfig != null) {
            addParameter("tooltipConfig", findValue(tooltipConfig));
        }
        if (tooltip != null) {
            addParameter("tooltip", findString(tooltip));

            Map tooltipConfigMap = getTooltipConfig(this);

            if (form != null) { 
                form.addParameter("hasTooltip", Boolean.TRUE);

                
                
                Map overallTooltipConfigMap = getTooltipConfig(form);
                overallTooltipConfigMap.putAll(tooltipConfigMap); 

                for (Object o : overallTooltipConfigMap.entrySet()) {
                    Map.Entry entry = (Map.Entry) o;
                    addParameter((String) entry.getKey(), entry.getValue());
                }
            }
            else {
                LOG.warn("No ancestor Form found, javascript based tooltip will not work, however standard HTML tooltip using alt and title attribute will still work");
            }

            
            String  jsTooltipEnabled = (String) getParameters().get("jsTooltipEnabled");
            if (jsTooltipEnabled != null)
                this.javascriptTooltip = jsTooltipEnabled;

            
            String tooltipIcon = (String) getParameters().get("tooltipIcon");
            if (tooltipIcon != null)
                this.addParameter("tooltipIconPath", tooltipIcon);
            if (this.tooltipIconPath != null)
                this.addParameter("tooltipIconPath", findString(this.tooltipIconPath));

            
            String tooltipDelayParam = (String) getParameters().get("tooltipDelay");
            if (tooltipDelayParam != null)
                this.addParameter("tooltipDelay", tooltipDelayParam);
            if (this.tooltipDelay != null)
                this.addParameter("tooltipDelay", findString(this.tooltipDelay));

            if (this.javascriptTooltip != null) {
                Boolean jsTooltips = (Boolean) findValue(this.javascriptTooltip, Boolean.class);
                
                this.addParameter("jsTooltipEnabled", jsTooltips.toString());

                if (form != null)
                    form.addParameter("hasTooltip", jsTooltips);
                if (this.tooltipCssClass != null)
                    this.addParameter("tooltipCssClass", findString(this.tooltipCssClass));
            }
        }

        evaluateExtraParams();
    }

	protected String escape(String name) {
        
        if (name != null) {
            return name.replaceAll("[\\/\\.\\[\\]]", "_");
        } else {
            return null;
        }
    }

    
    protected String ensureAttributeSafelyNotEscaped(String val) {
        if (val != null) {
            return val.replaceAll("\"", "&#34;");
        } else {
            return null;
        }
    }

    protected void evaluateExtraParams() {
    }

    protected boolean evaluateNameValue() {
        return true;
    }

    protected Class getValueClassType() {
        return String.class;
    }

    public void addFormParameter(String key, Object value) {
        Form form = (Form) findAncestor(Form.class);
        if (form != null) {
            form.addParameter(key, value);
        }
    }

    protected void enableAncestorFormCustomOnsubmit() {
        Form form = (Form) findAncestor(Form.class);
        if (form != null) {
            form.addParameter("customOnsubmitEnabled", Boolean.TRUE);
        } else {
            if (LOG.isWarnEnabled()) {
        	LOG.warn("Cannot find an Ancestor form, custom onsubmit is NOT enabled");
            }
        }
    }

    protected Map getTooltipConfig(UIBean component) {
        Object tooltipConfigObj = component.getParameters().get("tooltipConfig");
        Map<String, String> tooltipConfig = new LinkedHashMap<>();

        if (tooltipConfigObj instanceof Map) {
            
            
            

            tooltipConfig = new LinkedHashMap<>((Map) tooltipConfigObj);
        } else if (tooltipConfigObj instanceof String) {

            
            
            String tooltipConfigStr = (String) tooltipConfigObj;
            String[] tooltipConfigArray = tooltipConfigStr.split("\\|");

            for (String aTooltipConfigArray : tooltipConfigArray) {
                String[] configEntry = aTooltipConfigArray.trim().split("=");
                String key = configEntry[0].trim();
                String value;
                if (configEntry.length > 1) {
                    value = configEntry[1].trim();
                    tooltipConfig.put(key, value);
                } else {
                    LOG.warn("component {} tooltip config param {} has no value defined, skipped", component, key);
                }
            }
        }
        if (component.javascriptTooltip != null)
            tooltipConfig.put("jsTooltipEnabled", component.javascriptTooltip);
        if (component.tooltipIconPath != null)
            tooltipConfig.put("tooltipIcon", component.tooltipIconPath);
        if (component.tooltipDelay != null)
            tooltipConfig.put("tooltipDelay", component.tooltipDelay);
        return tooltipConfig;
    }

    
    protected void populateComponentHtmlId(Form form) {
        String tryId;
        String generatedId;
        if (id != null) {
            
            tryId = findStringIfAltSyntax(id);
        } else if (null == (generatedId = escape(name != null ? findString(name) : null))) {
            LOG.debug("Cannot determine id attribute for [{}], consider defining id, name or key attribute!", this);
            tryId = null;
        } else if (form != null) {
            tryId = form.getParameters().get("id") + "_" + generatedId;
        } else {
            tryId = generatedId;
        }
        
        
        
        if (tryId != null) {
          addParameter("id", tryId);
          addParameter("escapedId", escape(tryId));
        }
    }

    
    public String getId() {
        return id;
    }

    @StrutsTagAttribute(description="HTML id attribute")
    public void setId(String id) {
        if (id != null) {
            this.id = findString(id);
        }
    }

    @StrutsTagAttribute(description="The template directory.")
    public void setTemplateDir(String templateDir) {
        this.templateDir = templateDir;
    }

    @StrutsTagAttribute(description="The theme (other than default) to use for rendering the element")
    public void setTheme(String theme) {
        this.theme = theme;
    }

    public String getTemplate() {
        return template;
    }

    @StrutsTagAttribute(description="The template (other than default) to use for rendering the element")
    public void setTemplate(String template) {
        this.template = template;
    }

    @StrutsTagAttribute(description="The css class to use for element")
    public void setCssClass(String cssClass) {
        this.cssClass = cssClass;
    }

    @StrutsTagAttribute(description="The css class to use for element - it's an alias of cssClass attribute.")
    public void setClass(String cssClass) {
        this.cssClass = cssClass;
    }

    @StrutsTagAttribute(description="The css style definitions for element to use")
    public void setCssStyle(String cssStyle) {
        this.cssStyle = cssStyle;
    }

    @StrutsTagAttribute(description="The css style definitions for element to use - it's an alias of cssStyle attribute.")
    public void setStyle(String cssStyle) {
        this.cssStyle = cssStyle;
    }

    @StrutsTagAttribute(description="The css error class to use for element")
    public void setCssErrorClass(String cssErrorClass) {
        this.cssErrorClass = cssErrorClass;
    }

    @StrutsTagAttribute(description="The css error style definitions for element to use")
    public void setCssErrorStyle(String cssErrorStyle) {
        this.cssErrorStyle = cssErrorStyle;
    }

    @StrutsTagAttribute(description="Set the html title attribute on rendered html element")
    public void setTitle(String title) {
        this.title = title;
    }

    @StrutsTagAttribute(description="Set the html disabled attribute on rendered html element")
    public void setDisabled(String disabled) {
        this.disabled = disabled;
    }

    @StrutsTagAttribute(description="Label expression used for rendering an element specific label")
    public void setLabel(String label) {
        this.label = label;
    }

    @StrutsTagAttribute(description="String that will be appended to the label", defaultValue=":")
    public void setLabelSeparator(String labelseparator) {
        this.labelSeparator = labelseparator;
    }

    @StrutsTagAttribute(description="Define label position of form element (top/left)")
    public void setLabelposition(String labelPosition) {
        this.labelPosition = labelPosition;
    }

    @StrutsTagAttribute(description="Define required position of required form element (left|right)")
    public void setRequiredPosition(String requiredPosition) {
        this.requiredPosition = requiredPosition;
    }

    @StrutsTagAttribute(description="Define error position of form element (top|bottom)")
    public void setErrorPosition(String errorPosition) {
        this.errorPosition = errorPosition;
    }
    
    @StrutsTagAttribute(description="The name to set for element")
    public void setName(String name) {
        this.name = name;
    }

    @StrutsTagAttribute(description="If set to true, the rendered element will indicate that input is required", type="Boolean", defaultValue="false")
    public void setRequiredLabel(String requiredLabel) {
        this.requiredLabel = requiredLabel;
    }

    @StrutsTagAttribute(description="Set the html tabindex attribute on rendered html element")
    public void setTabindex(String tabindex) {
        this.tabindex = tabindex;
    }

    @StrutsTagAttribute(description="Preset the value of input element.")
    public void setValue(String value) {
        this.value = value;
    }

    @StrutsTagAttribute(description="Set the html onclick attribute on rendered html element")
    public void setOnclick(String onclick) {
        this.onclick = onclick;
    }

    @StrutsTagAttribute(description="Set the html ondblclick attribute on rendered html element")
    public void setOndblclick(String ondblclick) {
        this.ondblclick = ondblclick;
    }

    @StrutsTagAttribute(description="Set the html onmousedown attribute on rendered html element")
    public void setOnmousedown(String onmousedown) {
        this.onmousedown = onmousedown;
    }

    @StrutsTagAttribute(description="Set the html onmouseup attribute on rendered html element")
    public void setOnmouseup(String onmouseup) {
        this.onmouseup = onmouseup;
    }

    @StrutsTagAttribute(description="Set the html onmouseover attribute on rendered html element")
    public void setOnmouseover(String onmouseover) {
        this.onmouseover = onmouseover;
    }

    @StrutsTagAttribute(description="Set the html onmousemove attribute on rendered html element")
    public void setOnmousemove(String onmousemove) {
        this.onmousemove = onmousemove;
    }

    @StrutsTagAttribute(description="Set the html onmouseout attribute on rendered html element")
    public void setOnmouseout(String onmouseout) {
        this.onmouseout = onmouseout;
    }

    @StrutsTagAttribute(description="Set the html onfocus attribute on rendered html element")
    public void setOnfocus(String onfocus) {
        this.onfocus = onfocus;
    }

    @StrutsTagAttribute(description=" Set the html onblur attribute on rendered html element")
    public void setOnblur(String onblur) {
        this.onblur = onblur;
    }

    @StrutsTagAttribute(description="Set the html onkeypress attribute on rendered html element")
    public void setOnkeypress(String onkeypress) {
        this.onkeypress = onkeypress;
    }

    @StrutsTagAttribute(description="Set the html onkeydown attribute on rendered html element")
    public void setOnkeydown(String onkeydown) {
        this.onkeydown = onkeydown;
    }

    @StrutsTagAttribute(description="Set the html onkeyup attribute on rendered html element")
    public void setOnkeyup(String onkeyup) {
        this.onkeyup = onkeyup;
    }

    @StrutsTagAttribute(description="Set the html onselect attribute on rendered html element")
    public void setOnselect(String onselect) {
        this.onselect = onselect;
    }

    @StrutsTagAttribute(description="Set the html onchange attribute on rendered html element")
    public void setOnchange(String onchange) {
        this.onchange = onchange;
    }

    @StrutsTagAttribute(description="Set the html accesskey attribute on rendered html element")
    public void setAccesskey(String accesskey) {
        this.accesskey = accesskey;
    }

    @StrutsTagAttribute(description="Set the tooltip of this particular component")
    public void setTooltip(String tooltip) {
        this.tooltip = tooltip;
    }

    @StrutsTagAttribute(description="Deprecated. Use individual tooltip configuration attributes instead.")
    public void setTooltipConfig(String tooltipConfig) {
        this.tooltipConfig = tooltipConfig;
    }

    @StrutsTagAttribute(description="Set the key (name, value, label) for this particular component")
    public void setKey(String key) {
        this.key = key;
    }

    @StrutsTagAttribute(description="Use JavaScript to generate tooltips", type="Boolean", defaultValue="false")
    public void setJavascriptTooltip(String javascriptTooltip) {
        this.javascriptTooltip = javascriptTooltip;
    }

    @StrutsTagAttribute(description="CSS class applied to JavaScrip tooltips", defaultValue="StrutsTTClassic")
    public void setTooltipCssClass(String tooltipCssClass) {
        this.tooltipCssClass = tooltipCssClass;
    }

    @StrutsTagAttribute(description="Delay in milliseconds, before showing JavaScript tooltips ",
        defaultValue="Classic")
    public void setTooltipDelay(String tooltipDelay) {
        this.tooltipDelay = tooltipDelay;
    }

    @StrutsTagAttribute(description="Icon path used for image that will have the tooltip")
    public void setTooltipIconPath(String tooltipIconPath) {
        this.tooltipIconPath = tooltipIconPath;
    }

	public void setDynamicAttributes(Map<String, Object> tagDynamicAttributes) {
        for (String key : tagDynamicAttributes.keySet()) {
            if (!isValidTagAttribute(key)) {
                dynamicAttributes.put(key, tagDynamicAttributes.get(key));
            }
        }
    }

	@Override
	
    public void copyParams(Map params) {
        super.copyParams(params);
        for (Object o : params.entrySet()) {
            Map.Entry entry = (Map.Entry) o;
            String key = (String) entry.getKey();
            if(!isValidTagAttribute(key) && !key.equals("dynamicAttributes"))
                dynamicAttributes.put(key, entry.getValue());
        }
    }

}
