
package com.opensymphony.xwork2.mock;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.Interceptor;
import junit.framework.Assert;



public class MockInterceptor implements Interceptor {

    private static final long serialVersionUID = 2692551676567227756L;
    
    public static final String DEFAULT_FOO_VALUE = "fooDefault";


    private String expectedFoo = DEFAULT_FOO_VALUE;
    private String foo = DEFAULT_FOO_VALUE;
    private boolean executed = false;


    public boolean isExecuted() {
        return executed;
    }

    public void setExpectedFoo(String expectedFoo) {
        this.expectedFoo = expectedFoo;
    }

    public String getExpectedFoo() {
        return expectedFoo;
    }

    public void setFoo(String foo) {
        this.foo = foo;
    }

    public String getFoo() {
        return foo;
    }

    
    public void destroy() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof MockInterceptor)) {
            return false;
        }

        final MockInterceptor testInterceptor = (MockInterceptor) o;

        if (executed != testInterceptor.executed) {
            return false;
        }

        if ((expectedFoo != null) ? (!expectedFoo.equals(testInterceptor.expectedFoo)) : (testInterceptor.expectedFoo != null))
        {
            return false;
        }

        if ((foo != null) ? (!foo.equals(testInterceptor.foo)) : (testInterceptor.foo != null)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        result = ((expectedFoo != null) ? expectedFoo.hashCode() : 0);
        result = (29 * result) + ((foo != null) ? foo.hashCode() : 0);
        result = (29 * result) + (executed ? 1 : 0);

        return result;
    }

    
    public void init() {
    }

    
    public String intercept(ActionInvocation invocation) throws Exception {
        executed = true;
        Assert.assertNotSame(DEFAULT_FOO_VALUE, foo);
        Assert.assertEquals(expectedFoo, foo);

        return invocation.invoke();
    }
}
