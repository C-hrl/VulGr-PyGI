
package org.apache.struts2.showcase.exception;




public class UpdateException extends StorageException {

	private static final long serialVersionUID = -4728238600375630452L;


	public UpdateException(String message) {
		super(message);
	}

	public UpdateException(Throwable cause) {
		super(cause);
	}

	public UpdateException(String message, Throwable cause) {
		super(message, cause);
	}
}
