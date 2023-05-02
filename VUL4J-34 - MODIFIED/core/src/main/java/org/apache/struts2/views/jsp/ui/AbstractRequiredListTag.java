

package org.apache.struts2.views.jsp.ui;


import org.apache.struts2.components.ListUIBean;


public abstract class AbstractRequiredListTag extends AbstractListTag {

    protected void populateParams() {
        super.populateParams();

        ListUIBean listUIBean = (ListUIBean) component;
        listUIBean.setThrowExceptionOnNullValueAttribute(true);
    }

}
