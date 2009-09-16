/*
 * Copyright 2008-2009 The Kuali Foundation
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
package org.kuali.rice.kim.bo.types;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Immutable version of an attribute set - not used at present. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
@Deprecated 
public class KimAttributeSet implements Iterable<KimAttributeSet.AttributeHolder> {

	protected Map<String,String> attributes = new HashMap<String,String>();
	
	public KimAttributeSet( Map<String,String> attributes ) {
		this.attributes.putAll( attributes );
	}
	
	public String getValue( String name ) {
		return attributes.get( name );
	}
	
	public Set<String> getAttributeNames() {
		return Collections.unmodifiableSet( attributes.keySet() );
	}
	
	/**
	 * @see java.lang.Iterable#iterator()
	 */
	public Iterator<AttributeHolder> iterator() {
		final Iterator<String> keySetIterator = attributes.keySet().iterator();
		return new Iterator<AttributeHolder>() {
			/**
			 * @see java.util.Iterator#hasNext()
			 */
			public boolean hasNext() {
				return keySetIterator.hasNext();
			}
			/**
			 * @see java.util.Iterator#next()
			 */
			public AttributeHolder next() {
				final String key = keySetIterator.next();
				final String value = attributes.get( key );
				return new AttributeHolder() {
					public String getName() {
						return key;
					}					
					public String getValue() {
						return value;
					}
				};
			}
			/**
			 * @see java.util.Iterator#remove()
			 */
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

	public interface AttributeHolder {
		public String getName();		
		public String getValue();
	}
	
}
