
package org.apache.struts2.showcase.conversion;

import com.opensymphony.xwork2.ActionSupport;

import java.util.LinkedHashSet;
import java.util.Set;


public class AddressAction extends ActionSupport {

	private Set<Address> addresses = new LinkedHashSet<Address>();

	public String input() throws Exception {
		return SUCCESS;
	}

	public String submit() throws Exception {
		System.out.println(addresses);
		return SUCCESS;
	}

	public Set<Address> getAddresses() {
		return addresses;
	}

	public void setAddresses(Set<Address> addresses) {
		this.addresses = addresses;
	}
}
