package com.ude.sdp.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class FileNotFoundException extends RuntimeException {

	private static final long serialVersionUID = -1558447787699015829L;

	public FileNotFoundException(String message) {
		super(message);

	}

	public FileNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}
}