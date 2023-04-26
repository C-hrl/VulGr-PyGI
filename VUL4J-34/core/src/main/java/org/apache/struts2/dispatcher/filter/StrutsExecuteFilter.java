
package org.apache.struts2.dispatcher.filter;

import org.apache.struts2.StrutsStatics;
import org.apache.struts2.dispatcher.Dispatcher;
import org.apache.struts2.dispatcher.mapper.ActionMapping;
import org.apache.struts2.dispatcher.ExecuteOperations;
import org.apache.struts2.dispatcher.InitOperations;
import org.apache.struts2.dispatcher.PrepareOperations;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class StrutsExecuteFilter implements StrutsStatics, Filter {
    protected PrepareOperations prepare;
    protected ExecuteOperations execute;

    protected FilterConfig filterConfig;

    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
    }

    protected synchronized void lazyInit() {
        if (execute == null) {
            InitOperations init = new InitOperations();
            Dispatcher dispatcher = init.findDispatcherOnThread();
            init.initStaticContentLoader(new FilterHostConfig(filterConfig), dispatcher);

            prepare = new PrepareOperations(dispatcher);
            execute = new ExecuteOperations(dispatcher);
        }

    }

    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        if (excludeUrl(request)) {
            chain.doFilter(request, response);
            return;
        }

        
        if (execute == null) {
            lazyInit();
        }

        ActionMapping mapping = prepare.findActionMapping(request, response);

        
        
        Integer recursionCounter = (Integer) request.getAttribute(PrepareOperations.CLEANUP_RECURSION_COUNTER);

        if (mapping == null || recursionCounter > 1) {
            boolean handled = execute.executeStaticResourceRequest(request, response);
            if (!handled) {
                chain.doFilter(request, response);
            }
        } else {
            execute.executeAction(request, response, mapping);
        }
    }

    private boolean excludeUrl(HttpServletRequest request) {
        return request.getAttribute(StrutsPrepareFilter.REQUEST_EXCLUDED_FROM_ACTION_MAPPING) != null;
    }

    public void destroy() {
        prepare = null;
        execute = null;
        filterConfig = null;
    }

}
