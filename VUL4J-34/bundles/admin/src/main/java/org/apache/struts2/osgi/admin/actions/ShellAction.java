

package org.apache.struts2.osgi.admin.actions;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.apache.struts2.osgi.DefaultBundleAccessor;
import org.apache.felix.shell.ShellService;
import org.osgi.framework.ServiceReference;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;


public class ShellAction extends ActionSupport {
    private String command;
    private String output;

    public String execute() {
        
        ByteArrayOutputStream outByteStream = new ByteArrayOutputStream();
        ByteArrayOutputStream errByteStream = new ByteArrayOutputStream();
        PrintStream outStream = new PrintStream(outByteStream);
        PrintStream errStream = new PrintStream(errByteStream);

        String outString = null;
        String errString = null;
        try {
            executeCommand(command, outStream, errStream);
            outString = outByteStream.toString().trim();
            errString = errByteStream.toString().trim();
        } catch (Exception e) {
            errString = e.getMessage();
        } finally {
            outStream.close();
            errStream.close();
        }

        output = errString != null && errString.length() > 0 ? errString : outString;
        return Action.SUCCESS;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getOutput() {
        return output;
    }

    public void executeCommand(String commandLine, PrintStream out, PrintStream err) throws Exception {
        ShellService shellService = getShellService();
        if (shellService != null)
            shellService.executeCommand(commandLine, out, err);
        else
            err.println("Apache Felix Shell service is not installed");
    }

    private ShellService getShellService() {
        
        DefaultBundleAccessor bundleAcessor = DefaultBundleAccessor.getInstance();
        ServiceReference ref = bundleAcessor.getServiceReference(ShellService.class.getName());
        return (ShellService) bundleAcessor.getService(ref);
    }
}
