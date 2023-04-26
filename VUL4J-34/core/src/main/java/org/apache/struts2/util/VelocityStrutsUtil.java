

package org.apache.struts2.util;

import java.io.CharArrayWriter;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;

import com.opensymphony.xwork2.util.ValueStack;



public class VelocityStrutsUtil extends StrutsUtil {

    private Context ctx;
    private VelocityEngine velocityEngine;

    public VelocityStrutsUtil(VelocityEngine engine, Context ctx, ValueStack stack, HttpServletRequest request, HttpServletResponse response) {
        super(stack, request, response);
        this.ctx = ctx;
        this.velocityEngine = engine;
    }

    public String evaluate(String expression) throws IOException, ResourceNotFoundException, MethodInvocationException, ParseErrorException {
        CharArrayWriter writer = new CharArrayWriter();
        velocityEngine.evaluate(ctx, writer, "Error parsing " + expression, expression);

        return writer.toString();
    }

}
