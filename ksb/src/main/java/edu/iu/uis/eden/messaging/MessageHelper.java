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
package edu.iu.uis.eden.messaging;

import java.io.Serializable;
import java.util.Date;

import javax.xml.namespace.QName;

import edu.emory.mathcs.backport.java.util.concurrent.TimeUnit;


/**
 * Interface for aquiring services asynchronously.
 *
 * @author rkirkend
 */
public interface MessageHelper {
	public String serializeObject(Serializable object);
	public Object deserializeObject(String serializedObject);
	public Object getServiceAsynchronously(QName qname);
	public Object getServiceAsynchronously(QName qname, Date deliveryDate);
	public Object getServiceAsynchronously(QName qname, Date deliveryDate, AsynchronousCallback callback);
	public Object getServiceAsynchronously(QName qname, Date deliveryDate, AsynchronousCallback callback, TimeUnit unit, Long repeatDelay);
	public Object getServiceAsynchronously(QName qname, Date deliveryDate, AsynchronousCallback callback, TimeUnit repeatTimeUnit, Long repeatDelay, Serializable context);
}