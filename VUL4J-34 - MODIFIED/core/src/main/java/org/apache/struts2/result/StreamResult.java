

package org.apache.struts2.result;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.util.ValueStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.OutputStream;


public class StreamResult extends StrutsResultSupport {

    private static final long serialVersionUID = -1468409635999059850L;

    protected static final Logger LOG = LogManager.getLogger(StreamResult.class);

    public static final String DEFAULT_PARAM = "inputName";

    protected String contentType = "text/plain";
    protected String contentLength;
    protected String contentDisposition = "inline";
    protected String contentCharSet ;
    protected String inputName = "inputStream";
    protected InputStream inputStream;
    protected int bufferSize = 1024;
    protected boolean allowCaching = true;

    public StreamResult() {
        super();
    }

    public StreamResult(InputStream in) {
        this.inputStream = in;
    }

     
    public boolean getAllowCaching() {
        return allowCaching;
    }

    
    public void setAllowCaching(boolean allowCaching) {
        this.allowCaching = allowCaching;
    }


    
    public int getBufferSize() {
        return (bufferSize);
    }

    
    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    
    public String getContentType() {
        return (contentType);
    }

    
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    
    public String getContentLength() {
        return contentLength;
    }

    
    public void setContentLength(String contentLength) {
        this.contentLength = contentLength;
    }

    
    public String getContentDisposition() {
        return contentDisposition;
    }

    
    public void setContentDisposition(String contentDisposition) {
        this.contentDisposition = contentDisposition;
    }

    
    public String getContentCharSet() {
        return contentCharSet;
    }

    
    public void setContentCharSet(String contentCharSet) {
        this.contentCharSet = contentCharSet;
    }

    
    public String getInputName() {
        return (inputName);
    }

    
    public void setInputName(String inputName) {
        this.inputName = inputName;
    }

    
    protected void doExecute(String finalLocation, ActionInvocation invocation) throws Exception {

        
        resolveParamsFromStack(invocation.getStack(), invocation);

        
        HttpServletResponse oResponse = (HttpServletResponse) invocation.getInvocationContext().get(HTTP_RESPONSE);
        try (OutputStream oOutput = oResponse.getOutputStream()) {
            if (inputStream == null) {
                
                inputStream = (InputStream) invocation.getStack().findValue(conditionalParse(inputName, invocation));
            }

            if (inputStream == null) {
                String msg = ("Can not find a java.io.InputStream with the name [" + inputName + "] in the invocation stack. " +
                    "Check the <param name=\"inputName\"> tag specified for this action.");
                LOG.error(msg);
                throw new IllegalArgumentException(msg);
            }

            
            if (contentCharSet != null && ! contentCharSet.equals("")) {
                oResponse.setContentType(conditionalParse(contentType, invocation)+";charset="+contentCharSet);
            }
            else {
                oResponse.setContentType(conditionalParse(contentType, invocation));
            }

            
            if (contentLength != null) {
                String _contentLength = conditionalParse(contentLength, invocation);
                int _contentLengthAsInt = -1;
                try {
                    _contentLengthAsInt = Integer.parseInt(_contentLength);
                    if (_contentLengthAsInt >= 0) {
                        oResponse.setContentLength(_contentLengthAsInt);
                    }
                }
                catch(NumberFormatException e) {
                    LOG.warn("failed to recognize {} as a number, contentLength header will not be set", _contentLength, e);
                }
            }

            
            if (contentDisposition != null) {
                oResponse.addHeader("Content-Disposition", conditionalParse(contentDisposition, invocation));
            }

            
            if (!allowCaching) {
                oResponse.addHeader("Pragma", "no-cache");
                oResponse.addHeader("Cache-Control", "no-cache");
            }

            LOG.debug("Streaming result [{}] type=[{}] length=[{}] content-disposition=[{}] charset=[{}]",
                    inputName, contentType, contentLength, contentDisposition, contentCharSet);

            
        	LOG.debug("Streaming to output buffer +++ START +++");
            byte[] oBuff = new byte[bufferSize];
            int iSize;
            while (-1 != (iSize = inputStream.read(oBuff))) {
                LOG.debug("Sending stream ... {}", iSize);
                oOutput.write(oBuff, 0, iSize);
            }
        	LOG.debug("Streaming to output buffer +++ END +++");

            
            oOutput.flush();
        }
    }

    
    protected void resolveParamsFromStack(ValueStack stack, ActionInvocation invocation) {
        String disposition = stack.findString("contentDisposition");
        if (disposition != null) {
            setContentDisposition(disposition);
        }

        String contentType = stack.findString("contentType");
        if (contentType != null) {
            setContentType(contentType);
        }

        String inputName = stack.findString("inputName");
        if (inputName != null) {
            setInputName(inputName);
        }

        String contentLength = stack.findString("contentLength");
        if (contentLength != null) {
            setContentLength(contentLength);
        }

        Integer bufferSize = (Integer) stack.findValue("bufferSize", Integer.class);
        if (bufferSize != null) {
            setBufferSize(bufferSize);
        }

        if (contentCharSet != null ) {
            contentCharSet = conditionalParse(contentCharSet, invocation);
        }
        else {
            contentCharSet = stack.findString("contentCharSet");
        }
    }

}
