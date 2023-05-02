

package org.apache.struts2.views.jsp.ui;

import org.apache.struts2.components.ClosingUIBean;


public abstract class AbstractClosingTag extends AbstractUITag {
    protected String openTemplate;

    protected void populateParams() {
        super.populateParams();

        ((ClosingUIBean) component).setOpenTemplate(openTemplate);
    }

    public void setOpenTemplate(String openTemplate) {
        this.openTemplate = openTemplate;
    }
}
