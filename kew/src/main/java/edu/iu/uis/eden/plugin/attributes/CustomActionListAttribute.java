/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
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
package edu.iu.uis.eden.plugin.attributes;

import java.io.Serializable;

import edu.iu.uis.eden.actionitem.ActionItem;
import edu.iu.uis.eden.actionlist.DefaultCustomActionListAttribute;
import edu.iu.uis.eden.actionlist.DisplayParameters;
import edu.iu.uis.eden.actionlist.web.ActionListAction;
import edu.iu.uis.eden.actions.ActionSet;
import edu.iu.uis.eden.web.session.UserSession;

/**
 * An attribute which allows for the Action List to be customized to provide 
 * Mass Actions and an internal frame for displaying a summary view of the
 * document on each row in the Action List.
 * 
 * @see ActionListAction
 * @see DefaultCustomActionListAttribute
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
public interface CustomActionListAttribute extends Serializable {

	/**
	 * Returns the set of legal actions for this Action List attribute.  This set of actions dictates
	 * which actions can be taken on the document from the ActionList.  If this method returns null then
	 * action invocation will not be available from the action list.
	 */
	public ActionSet getLegalActions(UserSession userSession, ActionItem actionItem) throws Exception;
	
	/**
	 * Returns the display parameters for the inline framed doc handler on the Action List.
	 * If this method returns null, then a default value will be used.
	 */
	public DisplayParameters getDocHandlerDisplayParameters(UserSession userSession, ActionItem actionItem) throws Exception;
    
}
