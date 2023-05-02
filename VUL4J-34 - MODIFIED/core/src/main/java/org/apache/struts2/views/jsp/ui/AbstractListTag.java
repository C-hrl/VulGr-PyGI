

package org.apache.struts2.views.jsp.ui;

import org.apache.struts2.components.ListUIBean;


public abstract class AbstractListTag extends AbstractUITag {
    protected String list;
    protected String listKey;
    protected String listValue;
    protected String listValueKey;
    protected String listLabelKey;
    protected String listCssClass;
    protected String listCssStyle;
    protected String listTitle;

    protected void populateParams() {
        super.populateParams();

        ListUIBean listUIBean = ((ListUIBean) component);
        listUIBean.setList(list);
        listUIBean.setListKey(listKey);
        listUIBean.setListValue(listValue);
        listUIBean.setListValueKey(listValueKey);
        listUIBean.setListLabelKey(listLabelKey);
        listUIBean.setListCssClass(listCssClass);
        listUIBean.setListCssStyle(listCssStyle);
        listUIBean.setListTitle(listTitle);
    }

    public void setList(String list) {
        this.list = list;
    }

    public void setListKey(String listKey) {
        this.listKey = listKey;
    }

    public void setListValue(String listValue) {
        this.listValue = listValue;
    }

    public void setListValueKey(String listValueKey) {
        this.listValueKey = listValueKey;
    }

    public void setListLabelKey(String listLabelKey) {
        this.listLabelKey = listLabelKey;
    }

    public void setListCssClass(String listCssClass) {
        this.listCssClass = listCssClass;
    }

    public void setListCssStyle(String listCssStyle) {
        this.listCssStyle = listCssStyle;
    }

    public void setListTitle(String listTitle) {
        this.listTitle = listTitle;
    }
}
