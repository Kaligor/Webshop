package se.black.webshop.model.exception;

public class DuplicateEntryException extends ManagerException {

	private static final long serialVersionUID = 8329196467200585161L;

	public DuplicateEntryException(String message) {
		super(message);
	}

	public DuplicateEntryException(String message, Exception e) {
		super(message, e);
	}

}
