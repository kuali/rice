/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.rice.krad;

import java.text.ParseException;
import java.util.Locale;

import org.springframework.format.Formatter;

/**
 * This is a description of what this class does - swgibson don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class KradBooleanFormatter implements Formatter<Boolean> {

	/**
	 * This overridden method ...
	 * 
	 * @see org.springframework.format.Printer#print(java.lang.Object, java.util.Locale)
	 */
	@Override
	public String print(Boolean object, Locale locale) {
		// TODO swgibson - THIS METHOD NEEDS JAVADOCS
		return null;
	}

	/**
	 * This overridden method ...
	 * 
	 * @see org.springframework.format.Parser#parse(java.lang.String, java.util.Locale)
	 */
	@Override
	public Boolean parse(String text, Locale locale) throws ParseException {
		// TODO swgibson - THIS METHOD NEEDS JAVADOCS
		return null;
	}

}
