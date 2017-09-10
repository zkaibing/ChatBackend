package models;

public class ResponseEntity {
	//private int idCreated;
	private String message;

	public ResponseEntity(String message) {
		//this.idCreated = idCreated;
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
}