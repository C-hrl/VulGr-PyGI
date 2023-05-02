

package org.apache.struts2.components;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.views.annotations.StrutsTag;
import org.apache.struts2.util.TokenHelper;

import com.opensymphony.xwork2.util.ValueStack;


@StrutsTag(name="token", tldTagClass="org.apache.struts2.views.jsp.ui.TokenTag", description="Stop double-submission of forms")
public class Token extends UIBean {

    public static final String TEMPLATE = "token";

    public Token(ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
    }

    protected String getDefaultTemplate() {
        return TEMPLATE;
    }

    
    protected void evaluateExtraParams() {
        super.evaluateExtraParams();

        String tokenName;
        Map parameters = getParameters();

        if (parameters.containsKey("name")) {
            tokenName = (String) parameters.get("name");
        } else {
            if (name == null) {
                tokenName = TokenHelper.DEFAULT_TOKEN_NAME;
            } else {
                tokenName = findString(name);

                if (tokenName == null) {
                    tokenName = name;
                }
            }

            addParameter("name", tokenName);
        }

        String token = buildToken(tokenName);
        addParameter("token", token);
        addParameter("tokenNameField", TokenHelper.TOKEN_NAME_FIELD);
    }

    private String buildToken(String name) {
        Map context = stack.getContext();
        Object myToken = context.get(name);

        if (myToken == null) {
            myToken = TokenHelper.setToken(name);
            context.put(name, myToken);
        }

        return myToken.toString();
    }
}
