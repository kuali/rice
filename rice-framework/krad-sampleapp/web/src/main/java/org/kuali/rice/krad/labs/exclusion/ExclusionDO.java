/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.krad.labs.exclusion;

/**
 * Data object for supporting a collection for {@link LabsExclusionForm}.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class ExclusionDO {

	private String foo;
	private String bar;
	private String baz;
	
	/**
	 * Gets the foo property.
	 * 
	 * @return property value
	 */
	public String getFoo() {
		return foo;
	}
	
	/**
	 * @see #getFoo()
	 */
	public void setFoo(String foo) {
		this.foo = foo;
	}
	
	/**
	 * Gets the bar property.
	 * 
	 * @return property value
	 */
	public String getBar() {
		return bar;
	}
	
	/**
	 * @see #getBar()
	 */
	public void setBar(String bar) {
		this.bar = bar;
	}
	
	/**
	 * Gets the baz property.
	 * 
	 * @return property value
	 */
	public String getBaz() {
		return baz;
	}
	
	/**
	 * @see #getBaz()
	 */
	public void setBaz(String baz) {
		this.baz = baz;
	}
	
}
