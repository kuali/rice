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
package org.kuali.rice.krad.uif.control;

import org.kuali.rice.krad.uif.component.Component;

/**
 * Represents an interactive element in the UI (typically an HTML control)
 * <p>
 * Each control that can be rendered in the UIF should be an implement the
 * <code>Control</code> interface. The control is a regular component, thus has
 * a corresponding template that will render the control for the UI. Controls
 * provide the mechanism for gathering data from the User or for the User to
 * initiate an action. HTML controls must be rendered within a <code>Form</code>
 * element.
 * </p>
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface Control extends Component {

	/**
	 * Unique index of the control within the tab order
	 * <p>
	 * Tab index provides a way to set the order users will tab through the
	 * controls. The control with index 1 will receive focus when the page is
	 * rendered. Tabing from the field will then take the user to the control
	 * with index 2, then index 3, and so on.
	 * </p>
	 * 
	 * @return int the tab index for the control
	 */
	public int getTabIndex();

}
