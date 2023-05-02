

package org.apache.struts2.util;

import com.opensymphony.xwork2.TextProvider;
import com.opensymphony.xwork2.util.ValueStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.List;


public class TextProviderHelper {

    private static final Logger LOG = LogManager.getLogger(TextProviderHelper.class);

     
    public static String getText(String key, String defaultMessage, List<Object> args, ValueStack stack) {
        return getText(key, defaultMessage, args, stack, true);
    }

    
    public static String getText(String key, String defaultMessage, List<Object> args, ValueStack stack, boolean searchStack) {
        String msg = null;
        TextProvider tp = null;

        for (Object o : stack.getRoot()) {
            if (o instanceof TextProvider) {
                tp = (TextProvider) o;
                msg = tp.getText(key, null, args, stack);

                break;
            }
        }

        if (msg == null) {
            
            if (searchStack)
                msg = stack.findString(defaultMessage);
            
            if (msg == null) {
                
                msg = defaultMessage;
            }

            if (LOG.isWarnEnabled()) {
                if (tp != null) {
                    LOG.warn("The first TextProvider in the ValueStack ({}) could not locate the message resource with key '{}'", tp.getClass().getName(), key);
                } else {
                    LOG.warn("Could not locate the message resource '{}' as there is no TextProvider in the ValueStack.", key);
                }
                if (defaultMessage.equals(msg)) {
                    LOG.warn("The default value expression '{}' was evaluated and did not match a property. The literal value '{}' will be used.", defaultMessage, defaultMessage);
                } else {
                    LOG.warn("The default value expression '{}' evaluated to '{}'", defaultMessage, msg);
                }
            }
        }
        return msg;
    }

    
    public static String getText(String key, String defaultMessage, ValueStack stack) {
        return getText(key, defaultMessage, Collections.emptyList(), stack);
    }
}
