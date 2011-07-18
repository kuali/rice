package org.kuali.rice.krms.framework.engine.result;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.EventObject;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.kuali.rice.krms.api.engine.ExecutionEnvironment;
import org.kuali.rice.krms.api.engine.ResultEvent;

public class BasicResult extends EventObject implements ResultEvent {
	private static final long serialVersionUID = -4124200802034785921L;
	
	protected String type;
	protected DateTime timestamp;
	protected ExecutionEnvironment environment;
	protected Boolean result = null;
	protected String description;
	protected Map<String, ?> resultDetails;

    public BasicResult(Map<String, ?> resultDetails, String eventType, Object source, ExecutionEnvironment environment, boolean result) {
        this(resultDetails, null, eventType, source, environment, result);
    }
	
    public BasicResult(Map<String, ?> resultDetails, String description, String eventType, Object source, ExecutionEnvironment environment, boolean result) {
        this(eventType, source, environment);
        this.resultDetails = resultDetails;
        this.result = new Boolean(result);
        this.description = (description == null) ? StringUtils.EMPTY : description;
    }

    public BasicResult(String description, String eventType, Object source, ExecutionEnvironment environment, boolean result) {
		this(eventType, source, environment);
		this.result = new Boolean(result);
		this.description = description;
	}

	public BasicResult(String eventType, Object source, ExecutionEnvironment environment, boolean result) {
		this(eventType, source, environment);
		this.result = new Boolean(result);
	}

	public BasicResult(String eventType, Object source, ExecutionEnvironment environment) {
		super(source);
		this.type = eventType;
		this.timestamp = new DateTime(); 
		this.environment = environment;
	}

	@Override
	public String getType() {
		return type;
	}

	@Override
	public DateTime getTimestamp() {
		return timestamp;
	}
	
	@Override
	public ExecutionEnvironment getEnvironment(){
		return environment;
	}
	
	@Override
	public Boolean getResult(){
		return result;
	}

	@Override
	public String getDescription() {
	    return description;
	}
	
	@Override
	public Map<String, ?> getResultDetails() {
	    if (resultDetails == null) {
	        return Collections.emptyMap();
	    } else {
	        return Collections.unmodifiableMap(resultDetails);
	    }
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
