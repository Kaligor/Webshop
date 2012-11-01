package se.black.webshop.model.exception;

public class NoSuchEntryException extends ManagerException {

	private static final long serialVersionUID = 2432377964808708807L;

	public NoSuchEntryException(String message) {
		super(message);
	}

	public NoSuchEntryException(String message, Exception e) {
		super(message, e);
	}


}
