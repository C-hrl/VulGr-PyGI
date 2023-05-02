package com.opensymphony.xwork2;


public class ProxyInvocationAction extends ActionSupport implements ProxyInvocationInterface {
    public String show() {
        return "proxyResult";
    }
}
