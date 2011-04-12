package org.kuali.rice.krms.framework.engine.result;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.EventObject;

import org.kuali.rice.krms.api.engine.ExecutionEnvironment;
import org.kuali.rice.krms.api.engine.ResultEvent;

public class BasicResult extends EventObject implements ResultEvent{
	private static final long serialVersionUID = -4124200802034785921L;
	
	protected String type;
	protected Date timestamp;
	protected ExecutionEnvironment environment;
	protected Boolean result = null;

	public BasicResult(String eventType, Object source, ExecutionEnvironment environment, boolean result) {
		this(eventType, source, environment);
		this.result = new Boolean(result);
	}

	public BasicResult(String eventType, Object source, ExecutionEnvironment environment) {
		super(source);
		this.type = eventType;
		this.timestamp = new Date(); 
		this.environment = environment;
	}

	@Override
	public String getType() {
		return type;
	}

	@Override
	public Date getTimestamp() {
		return new Date(timestamp.getTime()); // defensive copy
	}
	
	@Override
	public ExecutionEnvironment getEnvironment(){
		return environment;
	}
	
	@Override
	public Boolean getResult(){
		return result;
	}

	public String toString(){
		StringBuffer sb = new StringBuffer();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH.mm.ss.SSS");
		sb.append(df.format(this.getTimestamp()));
		sb.append(" EventType: "+ getType());
		sb.append(" ( "+ this.getSource().toString());
		if (this.getResult() != null){
			sb.append(" evaluated to: "+ this.getResult().toString());
		}
		sb.append(" )");
		return sb.toString();
	}

}
