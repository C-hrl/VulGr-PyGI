

package org.apache.struts2.components;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.LocaleProvider;
import com.opensymphony.xwork2.TextProvider;
import com.opensymphony.xwork2.TextProviderFactory;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.util.LocalizedTextUtil;
import com.opensymphony.xwork2.util.ValueStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.StrutsException;
import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.views.annotations.StrutsTagAttribute;

import java.io.Writer;
import java.util.Locale;
import java.util.ResourceBundle;


@StrutsTag(name="i18n", tldTagClass="org.apache.struts2.views.jsp.I18nTag", description="Get a resource bundle" +
                " and place it on the value stack")
public class I18n extends Component {

    private static final Logger LOG = LogManager.getLogger(I18n.class);

    protected boolean pushed;
    protected String name;
    protected Container container;
    private TextProvider textProvider;

    public I18n(ValueStack stack) {
        super(stack);
    }
    
    @Inject
    public void setContainer(Container container) {
        this.container = container;
    }

    public boolean start(Writer writer) {
        boolean result = super.start(writer);

        try {
            String name = this.findString(this.name, "name", "Resource bundle name is required. Example: foo or foo_en");
            ResourceBundle bundle = (ResourceBundle) findValue("getTexts('" + name + "')");

            if (bundle == null) {
                bundle = LocalizedTextUtil.findResourceBundle(name, (Locale) getStack().getContext().get(ActionContext.LOCALE));
            }

            if (bundle != null) {
                final Locale locale = (Locale) getStack().getContext().get(ActionContext.LOCALE);
                TextProviderFactory tpf = new TextProviderFactory();
                container.inject(tpf);
                textProvider = tpf.createInstance(bundle, new LocaleProvider() {
                    public Locale getLocale() {
                        return locale;
                    }
                });
                getStack().push(textProvider);
                pushed = true;
            }
        } catch (Exception e) {
            throw new StrutsException("Could not find the bundle " + name, e);
        }

        return result;
    }

    public boolean end(Writer writer, String body) throws StrutsException {
        if (pushed) {
            Object o = getStack().pop();
            if ((o == null) || (!o.equals(textProvider))) {
                LOG.error("A closing i18n tag attempted to pop its own TextProvider from the top of the ValueStack but popped an unexpected object ("+(o != null ? o.getClass() : "null")+"). " +
                            "Refactor the page within the i18n tags to ensure no objects are pushed onto the ValueStack without popping them prior to the closing tag. " +
                            "If you see this message it's likely that the i18n's TextProvider is still on the stack and will continue to provide message resources after the closing tag.");
                throw new StrutsException("A closing i18n tag attempted to pop its TextProvider from the top of the ValueStack but popped an unexpected object ("+(o != null ? o.getClass() : "null")+")");
            }
        }

        return super.end(writer, body);
    }

    @StrutsTagAttribute(description="Name of resource bundle to use (eg foo/bar/customBundle)", required=true, defaultValue="String")
    public void setName(String name) {
        this.name = name;
    }
}
