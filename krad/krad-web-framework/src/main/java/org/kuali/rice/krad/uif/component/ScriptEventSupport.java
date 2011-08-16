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
package org.kuali.rice.krad.uif.component;

/**
 * Declares methods for determining which client side events are supported by a
 * <code>Component</code> and methods for retrieving the event code
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
	 * Indicates whether the component supports the onLoad event
	 * 
	 * @return boolean true if event is supported, false if the event is not
	 *         supported
	 */
	public boolean getSupportsOnLoad();

	/**
	 * Script that should be executed when the component's onLoad event is fired
	 * 
	 * @return String JavaScript code
	 */
	public String getOnLoadScript();

    /**
     * Setter for the onLoad script
     *
     * @param script - script for on load
     */
    public void setOnLoadScript(String script);

	/**
	 * Indicates whether the component supports the document ready event
	 * 
	 * @return boolean true if event is supported, false if the event is not
	 *         supported
	 */
	public boolean getSupportsOnDocumentReady();

	/**
	 * Script to be run when the document ready event is triggered
	 * 
	 * @return the onDocumentReadyScript
	 */
	public String getOnDocumentReadyScript();

	/**
	 * Indicates whether the component supports the onUnload event
	 * 
	 * @return boolean true if event is supported, false if the event is not
	 *         supported
	 */
	public boolean getSupportsOnUnload();

	/**
	 * Script that should be executed when the component's onUnload event is
	 * fired
	 * 
	 * @return String JavaScript code
	 */
	public String getOnUnloadScript();

	/**
	 * Indicates whether the component supports the onClose event
	 * 
	 * @return boolean true if event is supported, false if the event is not
	 *         supported
	 */
	public boolean getSupportsOnClose();

	/**
	 * Script that should be executed when the component's onClose event is
	 * fired
	 * 
	 * @return String JavaScript code
	 */
	public String getOnCloseScript();

	/**
	 * Indicates whether the component supports the onBlur event
	 * 
	 * @return boolean true if event is supported, false if the event is not
	 *         supported
	 */
	public boolean getSupportsOnBlur();

	/**
	 * Script that should be executed when the component's onBlur event is fired
	 * 
	 * @return String JavaScript code
	 */
	public String getOnBlurScript();

    /**
     * Setter for the onblur script
     *
     * @param script
     */
    public void setOnBlurScript(String script);

	/**
	 * Indicates whether the component supports the onChange event
	 * 
	 * @return boolean true if event is supported, false if the event is not
	 *         supported
	 */
	public boolean getSupportsOnChange();

	/**
	 * Script that should be executed when the component's onChange event is
	 * fired
	 * 
	 * @return String JavaScript code
	 */
	public String getOnChangeScript();

	/**
	 * Indicates whether the component supports the onClick event
	 * 
	 * @return boolean true if event is supported, false if the event is not
	 *         supported
	 */
	public boolean getSupportsOnClick();

	/**
	 * Script that should be executed when the component's onClick event is
	 * fired
	 * 
	 * @return String JavaScript code
	 */
	public String getOnClickScript();

	/**
	 * Indicates whether the component supports the onDblClick event
	 * 
	 * @return boolean true if event is supported, false if the event is not
	 *         supported
	 */
	public boolean getSupportsOnDblClick();

	/**
	 * Script that should be executed when the component's onDblClick event is
	 * fired
	 * 
	 * @return String JavaScript code
	 */
	public String getOnDblClickScript();

	/**
	 * Indicates whether the component supports the onFocus event
	 * 
	 * @return boolean true if event is supported, false if the event is not
	 *         supported
	 */
	public boolean getSupportsOnFocus();

	/**
	 * Script that should be executed when the component's onFocus event is
	 * fired
	 * 
	 * @return String JavaScript code
	 */
	public String getOnFocusScript();

	/**
	 * Indicates whether the component supports the onSubmit event
	 * 
	 * @return boolean true if event is supported, false if the event is not
	 *         supported
	 */
	public boolean getSupportsOnSubmit();

	/**
	 * Script that should be executed when the component's onSubmit event is
	 * fired
	 * 
	 * @return String JavaScript code
	 */
	public String getOnSubmitScript();

	/**
	 * Indicates whether the component supports the onKeyPress event
	 * 
	 * @return boolean true if event is supported, false if the event is not
	 *         supported
	 */
	public boolean getSupportsOnKeyPress();

	/**
	 * Script that should be executed when the component's onKeyPress event is
	 * fired
	 * 
	 * @return String JavaScript code
	 */
	public String getOnKeyPressScript();

	/**
	 * Indicates whether the component supports the onKeyUp event
	 * 
	 * @return boolean true if event is supported, false if the event is not
	 *         supported
	 */
	public boolean getSupportsOnKeyUp();

	/**
	 * Script that should be executed when the component's onKeyUp event is
	 * fired
	 * 
	 * @return String JavaScript code
	 */
	public String getOnKeyUpScript();

	/**
	 * Indicates whether the component supports the onKeyDown event
	 * 
	 * @return boolean true if event is supported, false if the event is not
	 *         supported
	 */
	public boolean getSupportsOnKeyDown();

	/**
	 * Script that should be executed when the component's onKeyDown event is
	 * fired
	 * 
	 * @return String JavaScript code
	 */
	public String getOnKeyDownScript();

	/**
	 * Indicates whether the component supports the onMouseOver event
	 * 
	 * @return boolean true if event is supported, false if the event is not
	 *         supported
	 */
	public boolean getSupportsOnMouseOver();

	/**
	 * Script that should be executed when the component's onMouseOver event is
	 * fired
	 * 
	 * @return String JavaScript code
	 */
	public String getOnMouseOverScript();

	/**
	 * Indicates whether the component supports the onMouseOut event
	 * 
	 * @return boolean true if event is supported, false if the event is not
	 *         supported
	 */
	public boolean getSupportsOnMouseOut();

	/**
	 * Script that should be executed when the component's onMouseOut event is
	 * fired
	 * 
	 * @return String JavaScript code
	 */
	public String getOnMouseOutScript();

	/**
	 * Indicates whether the component supports the onMouseUp event
	 * 
	 * @return boolean true if event is supported, false if the event is not
	 *         supported
	 */
	public boolean getSupportsOnMouseUp();

	/**
	 * Script that should be executed when the component's onMouseUp event is
	 * fired
	 * 
	 * @return String JavaScript code
	 */
	public String getOnMouseUpScript();

	/**
	 * Indicates whether the component supports the onMouseDown event
	 * 
	 * @return boolean true if event is supported, false if the event is not
	 *         supported
	 */
	public boolean getSupportsOnMouseDown();

	/**
	 * Script that should be executed when the component's onMouseDown event is
	 * fired
	 * 
	 * @return String JavaScript code
	 */
	public String getOnMouseDownScript();

	/**
	 * Indicates whether the component supports the onMouseMove event
	 * 
	 * @return boolean true if event is supported, false if the event is not
	 *         supported
	 */
	public boolean getSupportsOnMouseMove();

	/**
	 * Script that should be executed when the component's onMouseMove event is
	 * fired
	 * 
	 * @return String JavaScript code
	 */
	public String getOnMouseMoveScript();

}
