
package org.apache.struts2.showcase.token;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

import java.util.Date;


public class TokenAction extends ActionSupport {

	private static final long serialVersionUID = 616150375751184884L;

	private int amount;

	public String execute() throws Exception {
		

		Integer balSource = (Integer) ActionContext.getContext().getSession().get("balanceSource");
		Integer balDest = (Integer) ActionContext.getContext().getSession().get("balanceDestination");

		Integer newSource = new Integer(balSource.intValue() - amount);
		Integer newDest = new Integer(balDest.intValue() + amount);

		ActionContext.getContext().getSession().put("balanceSource", newSource);
		ActionContext.getContext().getSession().put("balanceDestination", newDest);
		ActionContext.getContext().getSession().put("time", new Date());

		Thread.sleep(2000); 

		return SUCCESS;
	}

	public String input() throws Exception {
		
		Integer balSource = (Integer) ActionContext.getContext().getSession().get("balanceSource");
		Integer balDest = (Integer) ActionContext.getContext().getSession().get("balanceDestination");

		if (balSource == null) {
			
			balSource = new Integer(1200);
			ActionContext.getContext().getSession().put("balanceSource", balSource);
		}

		if (balDest == null) {
			
			balDest = new Integer(2500);
			ActionContext.getContext().getSession().put("balanceDestination", balDest);
		}

		return INPUT;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

}
