
package org.apache.struts2.showcase.exception;



public class StorageException extends Exception {

	private static final long serialVersionUID = -2528721270540362905L;

	public StorageException(String message) {
		super(message);
	}

	public StorageException(Throwable cause) {
		super(cause);
	}

	public StorageException(String message, Throwable cause) {
		super(message, cause);
	}

}
