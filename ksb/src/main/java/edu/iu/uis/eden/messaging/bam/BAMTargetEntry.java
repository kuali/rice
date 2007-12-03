/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.iu.uis.eden.messaging.bam;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import edu.iu.uis.eden.messaging.AsynchronousCallback;

/**
 * An entry in the BAM representing a service method invocation.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public class BAMTargetEntry implements Serializable {

	private static final long serialVersionUID = -8376674801367598316L;

	private Long bamId;
	private String serviceName;
	private String methodName;
	private String threadName;
	private Timestamp callDate;
	private String serviceURL;
	private String targetToString;
	private String exceptionToString;
	private String exceptionMessage;
	private Boolean serverInvocation;
	private List<BAMParam> bamParams = new ArrayList<BAMParam>();
	
	//for async calls not bam
	private AsynchronousCallback callback;
	
	public void addBamParam(BAMParam bamParam) {
		this.bamParams.add(bamParam);
	}
	public String getExceptionToString() {
		return this.exceptionToString;
	}
	public void setExceptionToString(String exceptionToString) {
		this.exceptionToString = exceptionToString;
	}
	public String getServiceName() {
		return this.serviceName;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	public String getServiceURL() {
		return this.serviceURL;
	}
	public void setServiceURL(String serviceURL) {
		this.serviceURL = serviceURL;
	}
	public String getTargetToString() {
		return this.targetToString;
	}
	public void setTargetToString(String targetToString) {
		this.targetToString = targetToString;
	}
	public Long getBamId() {
		return this.bamId;
	}
	public void setBamId(Long bamId) {
		this.bamId = bamId;
	}
	public String getExceptionMessage() {
		return this.exceptionMessage;
	}
	public void setExceptionMessage(String exceptionMessage) {
		this.exceptionMessage = exceptionMessage;
	}
	public Boolean getServerInvocation() {
		return this.serverInvocation;
	}
	public void setServerInvocation(Boolean clientInvocation) {
		this.serverInvocation = clientInvocation;
	}
	public Timestamp getCallDate() {
		return this.callDate;
	}
	public void setCallDate(Timestamp callDate) {
		this.callDate = callDate;
	}
	public String getMethodName() {
		return this.methodName;
	}
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	public String getThreadName() {
		return this.threadName;
	}
	public void setThreadName(String threadName) {
		this.threadName = threadName;
	}
	public List<BAMParam> getBamParams() {
		return this.bamParams;
	}
	public void setBamParams(List<BAMParam> bamParams) {
		this.bamParams = bamParams;
	}
	public AsynchronousCallback getCallback() {
		return this.callback;
	}
	public void setCallback(AsynchronousCallback callback) {
		this.callback = callback;
	}
}
