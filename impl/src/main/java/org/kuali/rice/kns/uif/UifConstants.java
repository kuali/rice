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
package org.kuali.rice.kns.uif;

import org.kuali.rice.core.util.JSTLConstants;

/**
 * General constants used within the User Interface Framework
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class UifConstants extends JSTLConstants {
	private static final long serialVersionUID = 3935664282036793486L;

	public static final String CONTROLLER_METHOD_DISPATCH_PARAMETER_NAME = "methodToCall";

	public static final String DEFAULT_MODEL_NAME = "KualiForm";
	public static final String DEFAULT_VIEW_NAME = "default";
	public static final String SPRING_VIEW_ID = "ApplicationView";

	public static class Position {
		public static final String BOTTOM = "BOTTOM";
		public static final String LEFT = "LEFT";
		public static final String RIGHT = "RIGHT";
		public static final String TOP = "TOP";
	}

	public static class NavigationType {
		public static final String VERTICAL_MENU = "VERTICAL_MENU";
		public static final String HORIZONTAL_TABS = "HORIZONTAL_TABS";
	}

	public static class Orientation {
		public static final String HORIZONTAL = "HORIZONTAL";
		public static final String VERTICAL = "VERTICAL";
	}

	public static class MessageType {
		public static final String NORMAL = "NORMAL";
		public static final String SUMMARY = "SUMMARY";
		public static final String CONSTRAINT = "CONSTRAINT";
		public static final String REQUIRED = "REQUIRED";
	}

	public static class ViewType {
		public static final String DEFAULT = "DEFAULT";
		public static final String INQUIRY = "INQUIRY";
		public static final String LOOKUP = "LOOKUP";
		public static final String MAINTENANCE = "MAINTENANCE";
		public static final String TRANSACTIONAL = "TRANSACTIONAL";
	}

	public static class ViewTypeParameterNames {
		public static final String NAME = "name";
		public static final String INQUIRY_OBJECT_CLASS_NAME = "inquiryObjectClassName";
		public static final String OBJECT_CLASS_NAME = "objectClassName";
	}

	public static class MethodToCallNames {
		public static final String NAVIGATE = "navigate";
		public static final String START = "start";
		public static final String SAVE = "save";
		public static final String CLOSE = "close";
		public static final String ADD_LINE = "addLine";
		public static final String DELETE_LINE = "deleteLine";
	}
	
	public static class LayoutComponentOptions {
		public static final String COLUMN_SPAN = "colSpan";
		public static final String ROW_SPAN = "rowSpan";
	}

	public static class IdSuffixes {
		public static final String ADD_LINE = "_addLine";
		public static final String ATTRIBUTE = "_attribute";
		public static final String DIV = "_div";
		public static final String LABEL = "_label";
	}

	public static class ViewPhases {
		public static final String INITIALIZE = "INITIALIZE";
		public static final String APPLY_MODEL = "APPLY_MODEL";
		public static final String FINALIZE = "FINALIZE";
	}

	public static class ViewStatus {
		public static final String CREATED = "C";
		public static final String INITIALIZED = "I";
		public static final String FINAL = "F";
	}

}
