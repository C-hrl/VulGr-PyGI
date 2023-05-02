

package org.apache.struts2.util;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.util.LocalizedTextUtil;
import com.opensymphony.xwork2.util.ValueStack;
import com.opensymphony.xwork2.util.ValueStackFactory;
import org.apache.struts2.dispatcher.Dispatcher;
import org.apache.struts2.dispatcher.DispatcherErrorHandler;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;


public class StrutsTestCaseHelper {
    
    
    public static void setUp() throws Exception {
        LocalizedTextUtil.clearDefaultResourceBundles();
    }
    
    public static Dispatcher initDispatcher(ServletContext ctx, Map<String,String> params) {
        if (params == null) {
            params = new HashMap<>();
        }
        Dispatcher du = new DispatcherWrapper(ctx, params);
        du.init();
        Dispatcher.setInstance(du);

        
        Container container = du.getContainer();
        ValueStack stack = container.getInstance(ValueStackFactory.class).createValueStack();
        stack.getContext().put(ActionContext.CONTAINER, container);
        ActionContext.setContext(new ActionContext(stack.getContext()));
        
        return du;
    }

    public static void tearDown() throws Exception {
        Dispatcher.setInstance(null);
        ActionContext.setContext(null);
    }

    private static class DispatcherWrapper extends Dispatcher {

        public DispatcherWrapper(ServletContext ctx, Map<String, String> params) {
            super(ctx, params);
            super.setDispatcherErrorHandler(new MockErrorHandler());
        }

        @Override
        public void setDispatcherErrorHandler(DispatcherErrorHandler errorHandler) {
            
        }
    }

    private static class MockErrorHandler implements DispatcherErrorHandler {
        public void init(ServletContext ctx) {
            
        }

        public void handleError(HttpServletRequest request, HttpServletResponse response, int code, Exception e) {
            System.out.println("Dispatcher#sendError: " + code);
            e.printStackTrace(System.out);
        }
    }

}
