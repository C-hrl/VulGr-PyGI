

package org.apache.struts2.views;

import com.opensymphony.xwork2.util.ValueStack;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public interface TagLibraryModelProvider {

    
    Object getModels(ValueStack stack, HttpServletRequest req, HttpServletResponse res);

}
