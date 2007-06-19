package edu.iu.uis.eden.messaging;

import java.io.Serializable;

public class MessagingTestObject implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8353712224372306350L;
	private String content;
	
	public MessagingTestObject(String content) {
		this.content = content;
	}
	
	public String getContent() {
		return this.content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
	
	
}
