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
package edu.iu.uis.eden.messaging.objectremoting;

import javax.xml.namespace.QName;

import org.kuali.rice.definition.ObjectDefinition;

import edu.iu.uis.eden.messaging.ServiceInfo;

/**
 * Can remote a service for the given {@link ObjectDefinition}.  This allows for
 * as-needed remoting of objects created via ObjectDefinitions.  This would be
 * used for remoting attributes which are defined by class name rather than a
 * service name.  For example, this would includes PostProcessor, WorkflowAttribute, etc.
 *
 * @author rkirkend
 */
public interface ObjectRemoterService {

	public ServiceInfo getRemotedClassURL(ObjectDefinition objectDefinition);
	public void removeService(QName serviceName);
}
