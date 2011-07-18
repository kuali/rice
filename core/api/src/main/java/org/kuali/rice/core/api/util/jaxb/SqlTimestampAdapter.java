/*
 * Copyright 2007-2010 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.core.api.util.jaxb;

import org.apache.commons.lang.StringUtils;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.sql.Timestamp;

/**
 * This class allows for a {@link java.sql.Timestamp} instance to be passed across the wire by jaxws enabled services
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class SqlTimestampAdapter extends XmlAdapter<String, Timestamp> {

	/**
	 * This overridden method ...
	 * 
	 * @see javax.xml.bind.annotation.adapters.XmlAdapter#marshal(java.lang.Object)
	 */
	@Override
	public String marshal(Timestamp timestamp) throws Exception {
		return (null != timestamp ? Long.toString(timestamp.getTime()) : null);
	}

	/**
	 * This overridden method ...
	 * 
	 * @see javax.xml.bind.annotation.adapters.XmlAdapter#unmarshal(java.lang.Object)
	 */
	@Override
	public Timestamp unmarshal(String timestampStr) throws Exception {
		return (StringUtils.isNotBlank(timestampStr) ? new Timestamp(Long.parseLong(timestampStr)) : null);
	}

}
