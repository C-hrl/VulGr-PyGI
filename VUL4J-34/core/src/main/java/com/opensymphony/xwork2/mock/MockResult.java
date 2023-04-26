
package com.opensymphony.xwork2.mock;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.Result;


public class MockResult implements Result {

    public static final String DEFAULT_PARAM = "foo";

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        return o instanceof MockResult;
    }

    public void execute(ActionInvocation invocation) throws Exception {
        
    }

    @Override
    public int hashCode() {
        return 10;
    }

    public void setFoo(String foo) {
        
    }

}
