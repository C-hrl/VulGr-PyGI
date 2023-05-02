package com.opensymphony.xwork2;

import java.util.ArrayList;


public class UnknownHandlerManagerMock extends DefaultUnknownHandlerManager {
    public void addUnknownHandler(UnknownHandler uh) {
        if (this.unknownHandlers == null)
            this.unknownHandlers = new ArrayList<>();
        this.unknownHandlers.add(uh);
    }
}
