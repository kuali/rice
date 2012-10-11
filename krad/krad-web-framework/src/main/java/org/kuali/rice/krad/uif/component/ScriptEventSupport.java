/**
 * Copyright 2005-2012 The Kuali Foundation
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
package org.kuali.rice.krad.uif.component;

/**
 * Declares methods for retrieving the event script code
 *
 * <p>
 * The code returned by the get*Script methods will be wrapped in the
 * appropriate event registration code, therefore only the body needs to be
 * returned
 * </p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface ScriptEventSupport {

	/**
	 * Script that should be executed when the component's onLoad event is fired
	 *
	 * @return String JavaScript code
	 */
	public String getOnLoadScript();

    /**
     * Script that should be executed when the component's onLoad event is fired
     *
     * @return String JavaScript code
     */
    public void setOnLoadScript(String onLoadScript);

	/**
	 * Script to be run when the document ready event is triggered
	 *
	 * @return the onDocumentReadyScript
	 */
	public String getOnDocumentReadyScript();

	/**
	 * Script that should be executed when the component's onUnload event is
	 * fired
	 *
	 * @return String JavaScript code
	 */
	public String getOnUnloadScript();

	/**
	 * Script that should be executed when the component's onClose event is
	 * fired
	 *
	 * @return String JavaScript code
	 */
	public String getOnCloseScript();

	/**
	 * Script that should be executed when the component's onBlur event is fired
	 *
	 * @return String JavaScript code
	 */
	public String getOnBlurScript();

    /**
     * Script that should be executed when the component's onBlur event is fired
     *
     * @return String JavaScript code
     */
    public void setOnBlurScript(String onBlurScript);

	/**
	 * Script that should be executed when the component's onChange event is
	 * fired
	 *
	 * @return String JavaScript code
	 */
	public String getOnChangeScript();

	/**
	 * Script that should be executed when the component's onClick event is
	 * fired
	 *
	 * @return String JavaScript code
	 */
	public String getOnClickScript();

	/**
	 * Script that should be executed when the component's onDblClick event is
	 * fired
	 *
	 * @return String JavaScript code
	 */
	public String getOnDblClickScript();

	/**
	 * Script that should be executed when the component's onFocus event is
	 * fired
	 *
	 * @return String JavaScript code
	 */
	public String getOnFocusScript();

	/**
	 * Script that should be executed when the component's onSubmit event is
	 * fired
	 *
	 * @return String JavaScript code
	 */
	public String getOnSubmitScript();

	/**
	 * Script that should be executed when the component's onKeyPress event is
	 * fired
	 *
	 * @return String JavaScript code
	 */
	public String getOnKeyPressScript();

	/**
	 * Script that should be executed when the component's onKeyUp event is
	 * fired
	 *
	 * @return String JavaScript code
	 */
	public String getOnKeyUpScript();

	/**
	 * Script that should be executed when the component's onKeyDown event is
	 * fired
	 *
	 * @return String JavaScript code
	 */
	public String getOnKeyDownScript();

	/**
	 * Script that should be executed when the component's onMouseOver event is
	 * fired
	 *
	 * @return String JavaScript code
	 */
	public String getOnMouseOverScript();

	/**
	 * Script that should be executed when the component's onMouseOut event is
	 * fired
	 *
	 * @return String JavaScript code
	 */
	public String getOnMouseOutScript();

	/**
	 * Script that should be executed when the component's onMouseUp event is
	 * fired
	 *
	 * @return String JavaScript code
	 */
	public String getOnMouseUpScript();

	/**
	 * Script that should be executed when the component's onMouseDown event is
	 * fired
	 *
	 * @return String JavaScript code
	 */
	public String getOnMouseDownScript();

	/**
	 * Script that should be executed when the component's onMouseMove event is
	 * fired
	 *
	 * @return String JavaScript code
	 */
	public String getOnMouseMoveScript();

}
