package edu.iu.uis.eden.edl.components;

/**
 * Represents an EDocLite Validation that can be executed.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class EDLValidation {

	private String type;
	private String expression;
	private String message;
	private String key;

	public String getExpression() {
		return expression;
	}
	public void setExpression(String expression) {
		this.expression = expression;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}

}
