package se.black.webshop.model.exception;

public class ManagerException extends Exception{

	private static final long serialVersionUID = -6623435894475612205L;

	public ManagerException(String message) {
		super(message);
	}

	public ManagerException(String message, Exception e) {
		super(message, e);
	}

}
