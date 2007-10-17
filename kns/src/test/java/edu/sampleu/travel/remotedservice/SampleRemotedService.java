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
package edu.sampleu.travel.remotedservice;

import edu.iu.uis.eden.messaging.KEWXMLService;

/**
 * Sample remoted service 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class SampleRemotedService implements KEWXMLService {

    public void invoke(String message) throws Exception {
	System.out.println("A MESSAGE WAS RECIEVED!!!");
	System.out.println("");
	System.out.println("");
	System.out.println("");
	System.out.println("");
	System.out.println("");
	System.out.println("");
	System.out.println("THE MESSAGE WAS -->" + message);
    }
}
