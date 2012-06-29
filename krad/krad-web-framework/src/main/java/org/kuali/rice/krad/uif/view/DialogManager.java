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
package org.kuali.rice.krad.uif.view;

import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages the status of any modal dialogs that are used in the view.
 *
 * <p>
 * Keeps track of which modal dialogs have been asked and/or answered
 * during the life cycle of a view.
 * </p>
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class DialogManager implements Serializable {
    private static final long serialVersionUID = 4627667603510159528L;

    private static final String TRUE_VALUES = "/true/yes/y/on/1/";
    private static final String FALSE_VALUES = "/false/no/n/off/0/";

    /**
     * Status information record used to track dialog status
     */
    private static class DialogInfo implements Serializable{
        private static final long serialVersionUID = 2779403853894669510L;

        private String dialogName;
        private boolean asked;
        private boolean answered;
        private String answer;
        private String explanation;
        private String returnMethod;

        public DialogInfo(String dialogName, String returnMethod){
            this.dialogName = dialogName;
            this.asked = false;
            this.asked = answered;
            this.answer = null;
            this.explanation = null;
            this.returnMethod = returnMethod;
        }
    }

    private String currentDialogName;
    private Map<String, DialogInfo> dialogs;

    /**
     * Constructs new instance
     */
    public DialogManager(){
        // init dialogs
        dialogs = new HashMap<String, DialogInfo>();
    }

    /**
     * Indicates whether the named dialog has already been presented to the user
     *
     * @param dialogName - the key identifying the specific dialog
     * @return true if dialog has been displayed, false if not
     */
    public boolean hasDialogBeenDisplayed(String dialogName){
        if (dialogs.containsKey(dialogName)){
            return dialogs.get(dialogName).asked;
        }
        return false;
    }

    /**
     * Indicates whether the named dialog has alread been answered by the user
     *
     * @param dialogName - name of the dialog in questions
     * @return - true if the dialog has been answered by the user
     */
    public boolean hasDialogBeenAnswered(String dialogName){
        if (dialogs.containsKey(dialogName)){
            return dialogs.get(dialogName).answered;
        }
        return false;
    }

    /**
     * Gets the answer previously entered by the user when responding to this dialog
     *
     * <p>
     * Returns the key value of the option chosen by the user.
     * Returns null if the dialog has not yet been asked, or if the user has not yet
     * responded.
     * </p>
     *
     * @param dialogName - a String identifying the dialog
     * @return the key String of the option KeyValue chosen by the user
     */
    public String getDialogAnswer(String dialogName){
        if (hasDialogBeenAnswered(dialogName)){
            return dialogs.get(dialogName).answer;
        }
        return null;
    }

    /**
     * Sets the answer chosen by the user when responding to the dialog
     *
     * @param dialogName - id of the dialog
     * @param answer - value chosen by the user
     */
    public void setDialogAnswer(String dialogName, String answer){
        DialogInfo dialogInfo = dialogs.get(dialogName);
        dialogInfo.answer = answer;
        dialogInfo.answered = true;
        dialogs.put(dialogName,dialogInfo);
    }

    public String getDialogExplanation(String dialogName){
        return dialogs.get(dialogName).explanation;
    }

    public void setDialogExplanation(String dialogName, String explanation){
        DialogInfo dialogInfo = dialogs.get(dialogName);
        dialogInfo.explanation = explanation;
        dialogs.put(dialogName,dialogInfo);
    }

    /**
     * Indicates whethe the user answered affirmatively to the question
     *
     * <p>
     * The answer string is the key used for the option key/value pair selected by the user.
     * This assumes that the developer used one of the common keys used for yes/no questions.
     * The answer is checked to see if it is one of the acceptable values for "Yes". If so,
     * the method returns true. False if not.
     * Also returns false, if the question has not even been asked of the user.
     * </p>
     *
     * @param dialogName
     * @return true if the user answered the modal dialog affirmatively, false if answered negatively.
     *   also returns false if the questions hasn't yet been answered.
     */
    public boolean wasDialogAnswerAffirmative(String dialogName){
        String answer = getDialogAnswer(dialogName);
        if (answer != null){
            StringBuilder builder = new StringBuilder();
            builder.append("/").append(answer.toLowerCase()).append("/");
            String input = builder.toString();
            if(TRUE_VALUES.contains(builder.toString())) {
                return true;
            }
        }

        // TODO: Should we return false if question not even asked yet?
        //       Or should we throw an exception?
        return false;
    }

    /**
     * Retrieves the target method to redirect to when returning from a lightbox
     *
     * @param dialogName - identifies the dialog currently being handled
     * @return String - controller method to call
     */
    public String getDialogReturnMethod(String dialogName){
        if (hasDialogBeenAnswered(dialogName)){
            return dialogs.get(dialogName).returnMethod;
        }
        return null;
    }

    /**
     * sets the return method to call after returning from dialog
     * @param dialogName
     * @param returnMethod
     */
    public void setDialogReturnMethod(String dialogName, String returnMethod){
        DialogInfo dialogInfo = dialogs.get(dialogName);
        dialogInfo.returnMethod = returnMethod;
        dialogs.put(dialogName, dialogInfo);
    }

    /**
     * Creates a new DialogInfo record and adds it to the list of dialogs
     * used in the view
     *
     * <p>
     * New dialog entry is initialized to asked=false, answered=false.
     * If the dialog already has a record, nothing is performed.
     * </p>
     *
     * @param dialogName - String name identifying the dialog
     */
    public void addDialog(String dialogName, String returnMethod){
        DialogInfo dialogInfo = new DialogInfo(dialogName, returnMethod);
        dialogInfo.asked = true;
        dialogs.put(dialogName, dialogInfo);
        setCurrentDialogName(dialogName);
    }

    /**
     * Removes a dialog from the list of dialogs used in this vew.
     *
     * <p>
     * If the dialog is in the list, it is removed.
     * If the dialog is not in the list, nothing is performed.
     * </p>
     *
     * @param dialogName - String identifying the dialog to be removed
     */
    public void removeDialog(String dialogName){
        if (dialogs.containsKey(dialogName)){
            dialogs.remove(dialogName);
        }
    }

    /**
     * Sets the status of the dialog tracking record to indicate that this dialog
     * has not yet been asked or answered.
     *
     * @param dialogName - String identifier for the dialog
     */
    public void resetDialogStatus(String dialogName){
        String returnMethod = getDialogReturnMethod(dialogName);
        dialogs.put(dialogName, new DialogInfo(dialogName, returnMethod));
    }

    /**
     * Gets the Map used to track dialog interactions related to the view
     *
     * @return a Map of DialogInfo records
     */
    public Map<String, DialogInfo> getDialogs() {
        return dialogs;
    }

    /**
     * Sets the Map of DialogInfo records used to track modal dialog interactions
     * within a view
     *
     * @param dialogs - a Map of DialogInfo records keyed by the dialog id.
     */
    public void setDialogs(Map<String, DialogInfo> dialogs) {
        this.dialogs = dialogs;
    }

    /**
     * Gets the name of the currently active dialog
     *
     * @return - the name of the current dialog
     */
    public String getCurrentDialogName() {
        return currentDialogName;
    }

    /**
     * Sets the name of the currently active dialog
     *
     * @param currentDialogName - the name of the dialog
     */
    public void setCurrentDialogName(String currentDialogName) {
        this.currentDialogName = currentDialogName;
    }

}
