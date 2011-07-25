package org.kuali.rice.krms.framework.engine.result;

import java.util.Collections;
import java.util.EventObject;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.kuali.rice.krms.api.engine.ExecutionEnvironment;
import org.kuali.rice.krms.api.engine.ResultEvent;

public class TimingResult extends EventObject implements ResultEvent {
    
	private static final long serialVersionUID = 5335636381355236617L;

    private static final DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd HH.mm.ss.SSS");
	
    private String type;
	private DateTime start;
	private DateTime end;
	private ExecutionEnvironment environment;
	private String description;
	private Map<String, ?> resultDetails;

	public TimingResult(String description, String type, Object source, ExecutionEnvironment environment, DateTime start, DateTime end){
		super(source);
		this.type = type;
		this.environment = environment;
		this.start = start;
		this.end = end;
		this.description = description;
	}
	
	public TimingResult(String type, Object source, ExecutionEnvironment environment, DateTime start, DateTime end){
		super(source);
		this.type = type;
		this.environment = environment;
		this.start = start;
		this.end = end;
	}
	
	public Long getElapsedTimeInMilliseconds(){
		return Long.valueOf(end.getMillis() - start.getMillis());
	}
	
	public ExecutionEnvironment getEnvironment(){
		return environment;
	};
	
	public String toString(){
		StringBuffer sb = new StringBuffer();
		sb.append(fmt.print(end));
		sb.append(" EventType: "+ type);
		sb.append(" (Start = " + fmt.print(start));
		sb.append(", End = " + fmt.print(end));
		sb.append(",  Elapsed Time = "+ getElapsedTimeInMilliseconds().toString());
		sb.append(" milliseconds.)");
		return sb.toString();
	}

	@Override
	public Boolean getResult() {
		return null;
	}

	@Override
	public DateTime getTimestamp() {
		return end;
	}

	@Override
	public String getType() {
		return type;
	}
	
	@Override
	public Map<String, ?> getResultDetails() {
	    if (resultDetails == null) {
	        return Collections.emptyMap();
	    } else {
	        return Collections.unmodifiableMap(resultDetails);
	    }
	}
	
	@Override
	public String getDescription() {
		return description;
	}
}
