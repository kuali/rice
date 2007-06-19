package edu.iu.uis.eden.messaging;

import java.io.Serializable;

public class ClientAppServiceSharedPayloadObj implements Serializable {

	private static final long serialVersionUID = -4106213467989020586L;
	private boolean throwException;
	private String messageContents;
	
	public ClientAppServiceSharedPayloadObj() {
		// default constructor
	}
	
	public ClientAppServiceSharedPayloadObj(String messageContents, boolean throwException) {
		this.messageContents = messageContents;
		this.throwException = throwException;
	}
	
	public String getMessageContents() {
		return this.messageContents;
	}
	public void setMessageContents(String messageContents) {
		this.messageContents = messageContents;
	}
	public boolean isThrowException() {
		return this.throwException;
	}
	public void setThrowException(boolean throwException) {
		this.throwException = throwException;
	}	
}