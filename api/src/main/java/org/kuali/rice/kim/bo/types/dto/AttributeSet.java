/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.rice.kim.bo.types.dto;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

/**
 * Specialization of HashMap to facilitate web services and simplify API definitions.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class AttributeSet extends HashMap<String,String> {

	public AttributeSet() {
		super();
	}
	
	/**
	 * @see HashMap#HashMap(int)
	 * 
	 * @param initialSize
	 */
	public AttributeSet( int initialSize ) {
		super( initialSize );
	}
	
	public AttributeSet( Map<String,String> map ) {
		super();
		if ( map != null ) {
			putAll( map );
		}
	}
	
	public String formattedDump( int indent ) {
		int maxKeyLen = 1;
		for ( String key : this.keySet() ) {
			if ( key.length() > maxKeyLen ) {
				maxKeyLen = key.length();
			}
		}
		StringBuffer sb = new StringBuffer();
		String indentStr = StringUtils.repeat( " ", indent );
		for ( String key : this.keySet() ) {
			sb.append( indentStr );
			sb.append( StringUtils.rightPad( key, maxKeyLen, ' ' ));
			sb.append( " --> " );
			sb.append( get( key ) );
			sb.append( '\n' );
		}
		return sb.toString();
	}
}
