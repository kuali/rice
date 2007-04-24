/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.core.rules;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.kuali.Constants;
import org.kuali.core.document.Document;
import org.kuali.core.question.ConfirmationQuestion;
import org.kuali.core.rule.PreRulesCheck;
import org.kuali.core.rule.event.PreRulesCheckEvent;
import org.kuali.core.web.struts.form.KualiForm;

/**
 * 
 * This class simplifies requesting clarifying user input prior to applying business rules. It mostly shields the classes that
 * extend it from being aware of the web layer, even though the input is collected via a series of one or more request/response
 * cycles.
 * 
 * Beware: method calls with side-effects will have unusual results. While it looks like the doRules method is executed
 * sequentially, in fact, it is more of a geometric series: if n questions are asked, then the code up to and including the first
 * question is executed n times, the second n-1 times, ..., the last question only one time.
 * 
 * 
 */
public abstract class PreRulesContinuationBase implements PreRulesCheck {

    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PreRulesContinuationBase.class);

    protected String question;
    protected String buttonClicked;
    protected PreRulesCheckEvent event;
    protected KualiForm form;

    private class IsAskingException extends RuntimeException {
    }

    /**
     * 
     * This class acts similarly to HTTP session, but working inside a REQUEST parameter
     * 
     * 
     */
    public class ContextSession {
        private final static String DELIMITER = ".";
        PreRulesCheckEvent event;

        public ContextSession(String context, PreRulesCheckEvent event) {
            this.event = event;

            this.event.setQuestionContext(context);
            if (this.event.getQuestionContext() == null) {
                this.event.setQuestionContext("");
            }

        }

        public boolean hasAsked(String id) {
            return StringUtils.contains(event.getQuestionContext(), id);
        }

        public void askQuestion(String id, String text) {
            event.setQuestionId(id);
            event.setQuestionType(Constants.CONFIRMATION_QUESTION);
            event.setQuestionText(text);
            event.setPerformQuestion(true);

        }

        public void setAttribute(String name, String value) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("setAttribute(" + name + "," + value + ")");
            }
            event.setQuestionContext(event.getQuestionContext() + DELIMITER + name + DELIMITER + value);

        }

        public String getAttribute(String name) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("getAttribute(" + name + ")");
            }
            String result = null;

            Iterator values = Arrays.asList(event.getQuestionContext().split("\\" + DELIMITER)).iterator();

            while (values.hasNext()) {
                if (values.next().equals(name)) {
                    try {
                        result = (String) values.next();
                    }
                    catch (NoSuchElementException e) {
                        result = null;
                    }
                }
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("returning " + result);
            }
            return result;
        }

    }

    public abstract boolean doRules(Document document);

    private boolean isAborting;

    ContextSession session;

    public PreRulesContinuationBase() {
    }


    public boolean processPreRuleChecks(ActionForm form, HttpServletRequest request, PreRulesCheckEvent event) {

        question = request.getParameter(Constants.QUESTION_INST_ATTRIBUTE_NAME);
        buttonClicked = request.getParameter(Constants.QUESTION_CLICKED_BUTTON);
        this.event = event;
        this.form = (KualiForm) form;


        if (LOG.isDebugEnabled()) {
            LOG.debug("Question is: " + question);
            LOG.debug("ButtonClicked: " + buttonClicked);
            LOG.debug("QuestionContext() is: " + event.getQuestionContext());
        }

        session = new ContextSession(request.getParameter(Constants.QUESTION_CONTEXT), event);

        boolean result = false;

        try {
            result = doRules(event.getDocument());
        }
        catch (IsAskingException e) {
            return false;
        }

        if (isAborting) {
            return false;
        }

        return result;
    }

    /**
     * 
     * This bounces the user back to the document as if they had never tried to routed it. (Business rules are not invoked.)
     * 
     */
    public void abortRulesCheck() {
        event.setActionForwardName(Constants.MAPPING_BASIC);
        isAborting = true;
    }

    /**
     * 
     * This method poses a Y/N question to the user.
     * 
     * Code that invokes this method will behave a bit strangely, so you should try to keep it as simple as possible.
     * 
     * @param id
     * @param text
     * @return
     */
    public boolean askOrAnalyzeYesNoQuestion(String id, String text) {

        if (LOG.isDebugEnabled()) {
            LOG.debug("Entering askOrAnalyzeYesNoQuestion(" + id + "," + text + ")");
        }

        String cached = (String) session.getAttribute(id);
        if (cached != null) {
            LOG.debug("returning cached value: " + id + "=" + cached);
            return new Boolean(cached).booleanValue();
        }

        if (id.equals(question)) {
            session.setAttribute(id, Boolean.toString(!ConfirmationQuestion.NO.equals(buttonClicked)));
            return !ConfirmationQuestion.NO.equals(buttonClicked);
        }
        else if (!session.hasAsked(id)) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Forcing question to be asked: " + id);
            }
            session.askQuestion(id, text);
        }

        LOG.debug("Throwing Exception to force return to Action");
        throw new IsAskingException();
    }

}
