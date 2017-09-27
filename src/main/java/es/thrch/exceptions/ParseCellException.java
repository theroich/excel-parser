package es.thrch.exceptions;

import lombok.Data;

@Data
public class ParseCellException extends Exception {

	private String message;
	private Object object;

	public ParseCellException(String message, Object object) {
		super(message);
		this.object = object;
		this.message = message;

	}
}
