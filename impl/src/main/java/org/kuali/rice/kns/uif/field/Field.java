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
package org.kuali.rice.kns.uif.field;

import org.kuali.rice.kns.uif.Component;

/**
 * Component that contains one or more user interface elements and can be placed
 * into a <code>Container</code>
 * 
 * <p>
 * Used to hold one or more elements so they can be placed into a container and
 * rendered using a <code>LayoutManager</code>. Implementations exist for
 * various types of elements and properties to configure that element.
 * </p>
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface Field extends Component {
	
	public String getLabel();
	
	public String getShortLabel();
	
	public boolean isShowLabel();
	
	public LabelField getLabelField();
	
	public boolean isLabelFieldRendered();
	
	public void setLabelFieldRendered(boolean labelFieldRendered);

}
