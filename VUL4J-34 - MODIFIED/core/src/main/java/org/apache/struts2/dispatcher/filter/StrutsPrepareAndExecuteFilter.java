
package org.apache.struts2.dispatcher.filter;

import org.apache.struts2.StrutsStatics;
import org.apache.struts2.dispatcher.Dispatcher;
import org.apache.struts2.dispatcher.mapper.ActionMapping;
import org.apache.struts2.dispatcher.ExecuteOperations;
import org.apache.struts2.dispatcher.InitOperations;
import org.apache.struts2.dispatcher.PrepareOperations;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;


public class StrutsPrepareAndExecuteFilter implements StrutsStatics, Filter {
    protected PrepareOperations prepare;
    protected ExecuteOperations execute;
    protected List<Pattern> excludedPatterns = null;

    public void init(FilterConfig filterConfig) throws ServletException {
        InitOperations init = new InitOperations();
        Dispatcher dispatcher = null;
        try {
            FilterHostConfig config = new FilterHostConfig(filterConfig);
            init.initLogging(config);
            dispatcher = init.initDispatcher(config);
            init.initStaticContentLoader(config, dispatcher);

            prepare = new PrepareOperations(dispatcher);
            execute = new ExecuteOperations(dispatcher);
            this.excludedPatterns = init.buildExcludedPatternsList(dispatcher);

            postInit(dispatcher, filterConfig);
        } finally {
            if (dispatcher != null) {
                dispatcher.cleanUpAfterInit();
            }
            init.cleanup();
        }
    }

    
    protected void postInit(Dispatcher dispatcher, FilterConfig filterConfig) {
    }

    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        try {
            if (excludedPatterns != null && prepare.isUrlExcluded(request, excludedPatterns)) {
                chain.doFilter(request, response);
            } else {
                prepare.setEncodingAndLocale(request, response);
                prepare.createActionContext(request, response);
                prepare.assignDispatcherToThread();
                request = prepare.wrapRequest(request);
                ActionMapping mapping = prepare.findActionMapping(request, response, true);
                if (mapping == null) {
                    boolean handled = execute.executeStaticResourceRequest(request, response);
                    if (!handled) {
                        chain.doFilter(request, response);
                    }
                } else {
                    execute.executeAction(request, response, mapping);
                }
            }
        } finally {
            prepare.cleanupRequest(request);
        }
    }

    public void destroy() {
        prepare.cleanupDispatcher();
    }

}
