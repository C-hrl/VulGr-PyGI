package org.apache.struts2.dispatcher;

import com.opensymphony.xwork2.inject.Container;


class ContainerHolder {

    private static ThreadLocal<Container> instance = new ThreadLocal<>();

    public static void store(Container instance) {
        ContainerHolder.instance.set(instance);
    }

    public static Container get() {
        return ContainerHolder.instance.get();
    }

    public static void clear() {
        ContainerHolder.instance.remove();
    }

}
