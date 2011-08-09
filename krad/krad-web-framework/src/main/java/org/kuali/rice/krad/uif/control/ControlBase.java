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

import org.kuali.rice.krad.uif.component.ComponentBase;

/**
 * Base class for all <code>Control</code> implementations
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 * 
 * @see org.kuali.rice.krad.uif.control.Control
 */
public abstract class ControlBase extends ComponentBase implements Control {
	private static final long serialVersionUID = -7898244978136312663L;
	
	private int tabIndex;

	public ControlBase() {
      super();
	}

	/**
	 * @see org.kuali.rice.krad.uif.component.Component#getComponentTypeName()
	 */
	@Override
	public final String getComponentTypeName() {
		return "control";
	}

	public int getTabIndex() {
		return this.tabIndex;
	}

	public void setTabIndex(int tabIndex) {
		this.tabIndex = tabIndex;
	}

    @Override
    public boolean getSupportsOnChange() {
        return true;
    }

    @Override
    public boolean getSupportsOnBlur() {
        return true;
    }

    @Override
    public boolean getSupportsOnClick() {
        return true;
    }

    @Override
    public boolean getSupportsOnDblClick() {
        return true;
    }

    @Override
    public boolean getSupportsOnFocus() {
        return true;
    }
}
