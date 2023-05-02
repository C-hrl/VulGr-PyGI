
package com.opensymphony.xwork2.interceptor;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.util.CompoundRoot;
import com.opensymphony.xwork2.util.ValueStack;



public class ModelDrivenInterceptor extends AbstractInterceptor {

    protected boolean refreshModelBeforeResult = false;

    public void setRefreshModelBeforeResult(boolean val) {
        this.refreshModelBeforeResult = val;
    }

    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        Object action = invocation.getAction();

        if (action instanceof ModelDriven) {
            ModelDriven modelDriven = (ModelDriven) action;
            ValueStack stack = invocation.getStack();
            Object model = modelDriven.getModel();
            if (model !=  null) {
            	stack.push(model);
            }
            if (refreshModelBeforeResult) {
                invocation.addPreResultListener(new RefreshModelBeforeResult(modelDriven, model));
            }
        }
        return invocation.invoke();
    }

    
    protected static class RefreshModelBeforeResult implements PreResultListener {
        private Object originalModel = null;
        protected ModelDriven action;


        public RefreshModelBeforeResult(ModelDriven action, Object model) {
            this.originalModel = model;
            this.action = action;
        }

        public void beforeResult(ActionInvocation invocation, String resultCode) {
            ValueStack stack = invocation.getStack();
            CompoundRoot root = stack.getRoot();

            boolean needsRefresh = true;
            Object newModel = action.getModel();

            
            for (Object item : root) {
                if (item.equals(newModel)) {
                    needsRefresh = false;
                    break;
                }
            }

            
            if (needsRefresh) {

                
                if (originalModel != null) {
                    root.remove(originalModel);
                }
                if (newModel != null) {
                    stack.push(newModel);
                }
            }
        }
    }
}
