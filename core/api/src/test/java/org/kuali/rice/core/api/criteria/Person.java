/*
 * Copyright 2011 The Kuali Foundation
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
package org.kuali.rice.core.api.criteria;

import java.util.Date;

/**
 * A simple class used in Criteria testing. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
class Person {

	private final Name name;
	private final String displayName;
	private final Date birthDate;

	Person(Name name, String displayName, Date birthDate) {
		this.name = name;
		this.displayName = displayName;
		this.birthDate = birthDate;
	}
	
	Name getName() {
		return this.name;
	}

	String getDisplayName() {
		return this.displayName;
	}

	Date getBirthDate() {
		return this.birthDate;
	}
	
	static final class Name {
		
		private final String first;
		private final String last;
		
		Name(String first, String last) {
			this.first = first;
			this.last = last;
		}

		public String getFirst() {
			return this.first;
		}

		public String getLast() {
			return this.last;
		}
		
	}
	
}
