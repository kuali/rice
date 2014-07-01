/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.krad.uif.lifecycle;

import java.io.Serializable;
import java.text.MessageFormat;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.messages.MessageService;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.util.ScriptUtils;
import org.kuali.rice.krad.uif.view.View;
import org.kuali.rice.krad.uif.view.ViewModel;
import org.kuali.rice.krad.util.ErrorMessage;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.GrowlMessage;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.MessageMap;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class LifecycleBase implements Serializable {
    private static final long serialVersionUID = 9150578453981086356L;

    public static void performPostLifecycleProcessing(View view, Object model, ViewPostMetadata viewPostMetadata) {
        String growlScript = buildGrowlScript(view.isGrowlMessagingEnabled());
        ((ViewModel) model).setGrowlScript(growlScript);

    }

    /**
     * Builds JS script that will invoke the show growl method to display a growl message when the
     * page is rendered.
     *
     * <p>A growl call will be created for any explicit growl messages added to the message map.</p>
     *
     * @param growlMessagingEnabled indicates whether growls have been enabled, if not any growl messages are
     * written as info messages
     * @return JS script string for generated growl messages
     */
    protected static String buildGrowlScript(boolean growlMessagingEnabled) {
        StringBuilder growlScript = new StringBuilder();

        MessageService messageService = KRADServiceLocatorWeb.getMessageService();

        MessageMap messageMap = GlobalVariables.getMessageMap();
        for (GrowlMessage growl : messageMap.getGrowlMessages()) {
            if (growlMessagingEnabled) {
                String message = messageService.getMessageText(growl.getNamespaceCode(), growl.getComponentCode(),
                        growl.getMessageKey());

                if (StringUtils.isBlank(message)) {
                    continue;
                }

                if (growl.getMessageParameters() != null) {
                    message = message.replace("'", "''");
                    message = MessageFormat.format(message, (Object[]) growl.getMessageParameters());
                }

                // escape single quotes in message or title since that will cause problem with plugin
                message = message.replace("'", "\\'");

                String title = growl.getTitle();
                if (StringUtils.isNotBlank(growl.getTitleKey())) {
                    title = messageService.getMessageText(growl.getNamespaceCode(), growl.getComponentCode(),
                            growl.getTitleKey());
                }
                title = title.replace("'", "\\'");

                growlScript.append(ScriptUtils.buildFunctionCall(UifConstants.JsFunctions.SHOW_GROWL, message, title,
                        growl.getTheme()));

            } else {
                ErrorMessage infoMessage = new ErrorMessage(growl.getMessageKey(), growl.getMessageParameters());
                infoMessage.setNamespaceCode(growl.getNamespaceCode());
                infoMessage.setComponentCode(growl.getComponentCode());

                messageMap.putInfoForSectionId(KRADConstants.GLOBAL_INFO, infoMessage);
            }
        }

        return growlScript.toString();
    }

}
