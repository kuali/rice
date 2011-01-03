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
package org.kuali.rice.kns.ui;

import org.kuali.rice.core.util.JSTLConstants;

/**
 * Constants used within the User Interface Framework
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class UIFConstants extends JSTLConstants {
	public static final String CONTROLLER_METHOD_DISPATCH_PARAMETER_NAME = "methodToCall";

	public static class PersistenceMode {
		public static final String REQUEST = "REQUEST";
		public static final String SESSION = "SESSION";
	}

	public enum Position {
		BOTTOM, LEFT, RIGHT, TOP
	}

	public enum NavigationType {
		VERTICAL_MENU, HORIZONTAL_TABS
	}

	public enum Orientation {
		HORIZONTAL, VERTICAL
	}

	public enum MessageType {
		NORMAL, SUMMARY, CONSTRAINT, REQUIRED
	}
	
	public static class ViewType {
		public static final String INQUIRY = "INQUIRY";
		public static final String LOOKUP = "LOOKUP";
		public static final String MAINTENANCE = "MAINTENANCE";
		public static final String TRANSACTIONAL = "TRANSACTIONAL";
	}
}
