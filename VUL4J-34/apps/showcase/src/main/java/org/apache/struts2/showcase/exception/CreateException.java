
package org.apache.struts2.showcase.exception;



public class CreateException extends StorageException {

	private static final long serialVersionUID = 6734349565111633783L;

	public CreateException(String message) {
		super(message);
	}

	public CreateException(String message, Throwable cause) {
		super(message, cause);
	}

	public CreateException(Throwable cause) {
		super(cause);
	}
}
