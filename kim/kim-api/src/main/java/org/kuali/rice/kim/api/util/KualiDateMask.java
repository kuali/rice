/*
 * Copyright 2007-2009 The Kuali Foundation
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
package org.kuali.rice.kim.api.util;


import java.util.Date;
import org.kuali.rice.kim.util.KimConstants;

/**
 * This class is for masking java.util.Date using Kuali mask. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
public class KualiDateMask extends Date{

	private static final long serialVersionUID = 1L;
	
	private String mask;
	
	private KualiDateMask(String s){
		
		mask = s;
	}

	public static KualiDateMask getInstance(){
		
		return new KualiDateMask(KimConstants.RESTRICTED_DATA_MASK);
	}
	
	public String toString(){
		
		return this.mask;
	}
	
	public static void main(String[] args){
		System.out.println("masked date\t" + KualiDateMask.getInstance());
	}

}
