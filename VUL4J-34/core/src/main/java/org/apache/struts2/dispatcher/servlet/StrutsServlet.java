
package org.apache.struts2.dispatcher.servlet;

import org.apache.struts2.dispatcher.Dispatcher;
import org.apache.struts2.dispatcher.mapper.ActionMapping;
import org.apache.struts2.dispatcher.ExecuteOperations;
import org.apache.struts2.dispatcher.InitOperations;
import org.apache.struts2.dispatcher.PrepareOperations;
import org.apache.struts2.dispatcher.servlet.ServletHostConfig;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class StrutsServlet extends HttpServlet {

    private PrepareOperations prepare;
    private ExecuteOperations execute;

    @Override
    public void init(ServletConfig filterConfig) throws ServletException {
        InitOperations init = new InitOperations();
        Dispatcher dispatcher = null;
        try {
            ServletHostConfig config = new ServletHostConfig(filterConfig);
            init.initLogging(config);
            dispatcher = init.initDispatcher(config);
            init.initStaticContentLoader(config, dispatcher);

            prepare = new PrepareOperations(dispatcher);
            execute = new ExecuteOperations(dispatcher);
        } finally {
            if (dispatcher != null) {
                dispatcher.cleanUpAfterInit();
            }
            init.cleanup();
        }
    }

    @Override
    public void service(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

        try {
            prepare.createActionContext(request, response);
            prepare.assignDispatcherToThread();
            prepare.setEncodingAndLocale(request, response);
            request = prepare.wrapRequest(request);
            ActionMapping mapping = prepare.findActionMapping(request, response);
            if (mapping == null) {
                boolean handled = execute.executeStaticResourceRequest(request, response);
                if (!handled)
                    throw new ServletException("Resource loading not supported, use the StrutsPrepareAndExecuteFilter instead.");
            } else {
                execute.executeAction(request, response, mapping);
            }
        } finally {
            prepare.cleanupRequest(request);
        }
    }

    @Override
    public void destroy() {
        prepare.cleanupDispatcher();
    }

}
