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
 * Constants used within the User Interface Framework
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class UifConstants extends JSTLConstants {
	public static final String CONTROLLER_METHOD_DISPATCH_PARAMETER_NAME = "methodToCall";

	public static final String DEFAULT_MODEL_NAME = "KualiForm";
	public static final String DEFAULT_VIEW_NAME = "default";
	public static final String SPRING_VIEW_ID = "View";

	public static class RequestParameterName {
		public static final String VIEW_ID = "viewId";
		public static final String VIEW_NAME = "viewName";
		public static final String BUSINESS_OBJECT_CLASS = "businessObjectClassName";
	}

	public static class PersistenceMode {
		public static final String REQUEST = "REQUEST";
		public static final String SESSION = "SESSION";
	}

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

	public static class ViewTypeIndexParameterNames {
		public static final String NAME = "NAME";
		public static final String MODEL_CLASS = "MODEL_CLASS";
	}

	public static class ActionParameterNames {
		public static final String METHOD_TO_CALL = "methodToCall";
		public static final String SELLECTED_COLLECTION_PATH = "selectedCollectionPath";
		public static final String SELECTED_LINE_INDEX = "selectedLineIndex";
	}

	public static class IdSuffixes {
		public static final String ADD_LINE = "_addLine";
		public static final String ATTRIBUTE = "_attribute";
		public static final String DIV = "_div";
		public static final String LABEL = "_label";
	}
}
