

package org.apache.struts2.util;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.util.LocalizedTextUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Map;
import java.util.Random;


public class TokenHelper {

	
	public static final String TOKEN_NAMESPACE = "struts.tokens";

	
    public static final String DEFAULT_TOKEN_NAME = "token";

    
    public static final String TOKEN_NAME_FIELD = "struts.token.name";
    private static final Logger LOG = LogManager.getLogger(TokenHelper.class);
    private static final Random RANDOM = new SecureRandom();


    
    public static String setToken() {
        return setToken(DEFAULT_TOKEN_NAME);
    }

	
	public static String setToken( String tokenName ) {
		String token = generateGUID();
		setSessionToken(tokenName, token);
		return token;
	}

	
	public static void setSessionToken( String tokenName, String token ) {
		Map<String, Object> session = ActionContext.getContext().getSession();
		try {
			session.put(buildTokenSessionAttributeName(tokenName), token);
		} catch ( IllegalStateException e ) {
			
            String msg = "Error creating HttpSession due response is committed to client. You can use the CreateSessionInterceptor or create the HttpSession from your action before the result is rendered to the client: " + e.getMessage();
            LOG.error(msg, e);
            throw new IllegalArgumentException(msg);
		}
	}


	
	public static String buildTokenSessionAttributeName( String tokenName ) {
		return TOKEN_NAMESPACE + "." + tokenName;
	}

	
    public static String getToken() {
        return getToken(DEFAULT_TOKEN_NAME);
    }

    
    public static String getToken(String tokenName) {
        if (tokenName == null ) {
            return null;
        }
        Map params = ActionContext.getContext().getParameters();
        String[] tokens = (String[]) params.get(tokenName);
        String token;

        if ((tokens == null) || (tokens.length < 1)) {
            LOG.warn("Could not find token mapped to token name: {}", tokenName);
            return null;
        }

        token = tokens[0];
        return token;
    }

    
    public static String getTokenName() {
        Map params = ActionContext.getContext().getParameters();

        if (!params.containsKey(TOKEN_NAME_FIELD)) {
        	LOG.warn("Could not find token name in params.");
            return null;
        }

        String[] tokenNames = (String[]) params.get(TOKEN_NAME_FIELD);
        String tokenName;

        if ((tokenNames == null) || (tokenNames.length < 1)) {
        	LOG.warn("Got a null or empty token name.");
            return null;
        }

        tokenName = tokenNames[0];
        return tokenName;
    }

    
    public static boolean validToken() {
        String tokenName = getTokenName();

        if (tokenName == null) {
            LOG.debug("No token name found -> Invalid token ");
            return false;
        }

        String token = getToken(tokenName);

        if (token == null) {
            LOG.debug("No token found for token name {} -> Invalid token ", tokenName);
            return false;
        }

        Map session = ActionContext.getContext().getSession();
		String tokenSessionName = buildTokenSessionAttributeName(tokenName);
        String sessionToken = (String) session.get(tokenSessionName);

        if (!token.equals(sessionToken)) {
            if (LOG.isWarnEnabled()) {
                LOG.warn(LocalizedTextUtil.findText(TokenHelper.class, "struts.internal.invalid.token", ActionContext.getContext().getLocale(), "Form token {0} does not match the session token {1}.", new Object[]{
                        token, sessionToken
                }));
            }

            return false;
        }

        
        session.remove(tokenSessionName);

        return true;
    }

    public static String generateGUID() {
        return new BigInteger(165, RANDOM).toString(36).toUpperCase();
    }
}
