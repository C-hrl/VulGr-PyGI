
package com.opensymphony.xwork2;


public class VoidResult implements Result {

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        return o instanceof VoidResult;
    }

    public void execute(ActionInvocation invocation) throws Exception {
    }

    @Override
    public int hashCode() {
        return 42;
    }
}
