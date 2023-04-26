

package org.apache.struts2.views.jsp.ui;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.components.Component;
import org.apache.struts2.components.UpDownSelect;

import com.opensymphony.xwork2.util.ValueStack;


public class UpDownSelectTag extends SelectTag {

    private static final long serialVersionUID = -8136573053799541353L;

    protected String allowMoveUp;
    protected String allowMoveDown;
    protected String allowSelectAll;

    protected String moveUpLabel;
    protected String moveDownLabel;
    protected String selectAllLabel;


    public Component getBean(ValueStack stack, HttpServletRequest req, HttpServletResponse res) {
        return new UpDownSelect(stack, req, res);
    }

    protected void populateParams() {
        super.populateParams();

        UpDownSelect c = (UpDownSelect) component;

        c.setAllowMoveUp(allowMoveUp);
        c.setAllowMoveDown(allowMoveDown);
        c.setAllowSelectAll(allowSelectAll);

        c.setMoveUpLabel(moveUpLabel);
        c.setMoveDownLabel(moveDownLabel);
        c.setSelectAllLabel(selectAllLabel);

    }


    public String getAllowMoveUp() {
        return allowMoveUp;
    }

    public void setAllowMoveUp(String allowMoveUp) {
        this.allowMoveUp = allowMoveUp;
    }



    public String getAllowMoveDown() {
        return allowMoveDown;
    }

    public void setAllowMoveDown(String allowMoveDown) {
        this.allowMoveDown = allowMoveDown;
    }



    public String getAllowSelectAll() {
        return allowSelectAll;
    }

    public void setAllowSelectAll(String allowSelectAll) {
        this.allowSelectAll = allowSelectAll;
    }


    public String getMoveUpLabel() {
        return moveUpLabel;
    }

    public void setMoveUpLabel(String moveUpLabel) {
        this.moveUpLabel = moveUpLabel;
    }



    public String getMoveDownLabel() {
        return moveDownLabel;
    }

    public void setMoveDownLabel(String moveDownLabel) {
        this.moveDownLabel = moveDownLabel;
    }



    public String getSelectAllLabel() {
        return selectAllLabel;
    }

    public void setSelectAllLabel(String selectAllLabel) {
        this.selectAllLabel = selectAllLabel;
    }
}
