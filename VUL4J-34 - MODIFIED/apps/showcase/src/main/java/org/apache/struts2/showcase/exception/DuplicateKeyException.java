
package org.apache.struts2.showcase.exception;



public class DuplicateKeyException extends CreateException {

	private static final long serialVersionUID = 989620752592415898L;

	public DuplicateKeyException(String message) {
		super(message);
	}

	public DuplicateKeyException(Throwable cause) {
		super(cause);
	}

	public DuplicateKeyException(String message, Throwable cause) {
		super(message, cause);
	}

}
