package edu.iu.uis.eden.web;

import java.util.ArrayList;
import java.util.List;

public class AuthorizationResult {

	private boolean authorized;
	private List<String> messages;

	public AuthorizationResult(boolean authorized, List<String> messages) {
		this.authorized = authorized;
		this.messages = messages;
	}

	public AuthorizationResult(boolean authorized) {
		this(authorized, new ArrayList<String>());
	}

	public AuthorizationResult(boolean authorized, String message) {
		this(authorized);
		getMessages().add(message);
	}

	public boolean isAuthorized() {
		return authorized;
	}

	public List<String> getMessages() {
		return messages;
	}

}
