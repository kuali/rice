package org.kuali.rice.krms.framework.engine.result;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.EventObject;

import org.kuali.rice.krms.api.engine.ExecutionEnvironment;
import org.kuali.rice.krms.api.engine.ResultEvent;

public class TimingResult extends EventObject implements ResultEvent {
	private String type;
	private Date start;
	private Date end;
	private ExecutionEnvironment environment;
	
	public TimingResult(String type, Object source, ExecutionEnvironment environment, Date start, Date end){
		super(source);
		this.type = type;
		this.environment = environment;
		this.start = start;
		this.end = end;
	}
	
	public Long getElapsedTimeInMilliseconds(){
		return Long.valueOf(end.getTime() - start.getTime());
	}
	
	public ExecutionEnvironment getEnvironment(){
		return environment;
	};
	
	public String toString(){
		StringBuffer sb = new StringBuffer();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH.mm.ss.SSS");
		sb.append(df.format(end));
		sb.append(" EventType: "+ type);
		sb.append(" (Start = " + df.format(start));
		sb.append(", End = " + df.format(end));
		sb.append(",  Elapsed Time = "+ getElapsedTimeInMilliseconds().toString());
		sb.append(" milliseconds.)");
		return sb.toString();
	}

	@Override
	public Boolean getResult() {
		return null;
	}

	@Override
	public Date getTimestamp() {
		return new Date(end.getTime());
	}

	@Override
	public String getType() {
		return type;
	}
}
