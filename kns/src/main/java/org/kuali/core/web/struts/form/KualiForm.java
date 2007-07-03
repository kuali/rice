/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package org.kuali.core.web.struts.form;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.datadictionary.HeaderNavigation;
import org.kuali.core.util.ActionFormUtilMap;
import org.kuali.core.util.TabState;
import org.kuali.core.util.TypedArrayList;
import org.kuali.core.util.WebUtils;
import org.kuali.core.web.struts.pojo.PojoFormBase;
import org.kuali.core.web.ui.ExtraButton;
import org.kuali.core.web.ui.KeyLabelPair;

/**
 * This class common properites for all action forms.
 */
public class KualiForm extends PojoFormBase {
    private static final long serialVersionUID = 1L;
    private String methodToCall;
    private String refreshCaller;
    private String anchor;
    private Map<String, String> tabStates;
    private Map actionFormUtilMap;
    private Map displayedErrors = new HashMap();
    private int currentTabIndex = 0;
    private int arbitrarilyHighIndex = 1000000;

    private String navigationCss;
    private HeaderNavigation[] headerNavigationTabs;
    protected List<ExtraButton> extraButtons = new TypedArrayList( ExtraButton.class ) ;

    private KeyLabelPair additionalDocInfo1;
    private KeyLabelPair additionalDocInfo2;

    /**
     * no args constructor which must init our tab states list
     */
    public KualiForm() {
        this.tabStates = new HashMap<String, String>();
        this.actionFormUtilMap = new ActionFormUtilMap();
    }

    /**
     * Checks for methodToCall parameter, and if not populated in form calls utility method to parse the string from the request.
     */
    public void populate(HttpServletRequest request) {
        super.populate(request);

        if (StringUtils.isEmpty(this.getMethodToCall())) {
            // call utility method to parse the methodToCall from the request.
            setMethodToCall(WebUtils.parseMethodToCall(request));
        }
    }

    public Map getDisplayedErrors() {
        return displayedErrors;
    }

    /**
     * Used by the dispatch action to determine which action method to call into.
     * 
     * @return Returns the methodToCall.
     */
    public String getMethodToCall() {
        return methodToCall;
    }


    /**
     * @param methodToCall The methodToCall to set.
     */
    public void setMethodToCall(String methodToCall) {
        this.methodToCall = methodToCall;
    }


    /**
     * Can be used by actions refresh method to determine what called the the refresh method.
     * 
     * @return Returns the refreshCaller.
     */
    public String getRefreshCaller() {
        return refreshCaller;
    }


    /**
     * @param refreshCaller The refreshCaller to set.
     */
    public void setRefreshCaller(String refreshCaller) {
        this.refreshCaller = refreshCaller;
    }

    /**
     * @return the tab state list
     */
    public Map<String, String> getTabStates() {
        return tabStates;
    }

    /**
     * simple setter for the tab state Map
     * 
     * @param tabStates
     */
    public void setTabStates(Map<String, String> tabStates) {
        this.tabStates = tabStates;
    }
    
    /**
     * Special getter based on key to work with multi rows for tab state objects
     */
    public String getTabState(String key) {
        String state = "OPEN";
        if (tabStates.containsKey(key)) {
            if (tabStates.get(key) instanceof String) {
            	state = tabStates.get(key);
            }
            else {
            	//This is the case where the value is an Array of String,
            	//so we'll have to get the first element
            	Object result = tabStates.get(key);
            	result.getClass();
            	state = ((String[])result)[0];
            }
        }
        
        return state;
    }

    public int getCurrentTabIndex() {
        return currentTabIndex;
    }

    public void setCurrentTabIndex(int currentTabIndex) {
        this.currentTabIndex = currentTabIndex;
    }
    
    public void incrementTabIndex() {
        this.currentTabIndex++;
    }
    
    public int getNextArbitrarilyHighIndex() {
        return this.arbitrarilyHighIndex++;
    }

    /**
     * @return Returns the validOptionsMap.
     */
    public Map getActionFormUtilMap() {
        return actionFormUtilMap;
    }

    /**
     * @param validOptionsMap The validOptionsMap to set.
     */
    public void setActionFormUtilMap(Map validOptionsMap) {
        this.actionFormUtilMap = validOptionsMap;
    }

    /**
     * Gets the headerNavigationTabs attribute.
     * 
     * @return Returns the headerNavigationTabs.
     */
    public HeaderNavigation[] getHeaderNavigationTabs() {
        return headerNavigationTabs;
    }

    /**
     * Sets the headerNavigationTabs attribute value.
     * 
     * @param headerNavigationTabs The headerNavigationTabs to set.
     */
    public void setHeaderNavigationTabs(HeaderNavigation[] headerNavigationTabs) {
        this.headerNavigationTabs = headerNavigationTabs;
    }

    /**
     * Gets the navigationCss attribute.
     * 
     * @return Returns the navigationCss.
     */
    public String getNavigationCss() {
        return navigationCss;
    }

    /**
     * Sets the navigationCss attribute value.
     * 
     * @param navigationCss The navigationCss to set.
     */
    public void setNavigationCss(String navigationCss) {
        this.navigationCss = navigationCss;
    }

    /**
     * Gets the additionalDocInfo1 attribute.
     * 
     * @return Returns the additionalDocInfo1.
     */
    public KeyLabelPair getAdditionalDocInfo1() {
        return additionalDocInfo1;
    }

    /**
     * Sets the additionalDocInfo1 attribute value.
     * 
     * @param additionalDocInfo1 The additionalDocInfo1 to set.
     */
    public void setAdditionalDocInfo1(KeyLabelPair additionalDocInfo1) {
        this.additionalDocInfo1 = additionalDocInfo1;
    }

    /**
     * Gets the additionalDocInfo2 attribute.
     * 
     * @return Returns the additionalDocInfo2.
     */
    public KeyLabelPair getAdditionalDocInfo2() {
        return additionalDocInfo2;
    }

    /**
     * Sets the additionalDocInfo2 attribute value.
     * 
     * @param additionalDocInfo2 The additionalDocInfo2 to set.
     */
    public void setAdditionalDocInfo2(KeyLabelPair additionalDocInfo2) {
        this.additionalDocInfo2 = additionalDocInfo2;
    }

    public String getAnchor() {
        return anchor;
    }

    public void setAnchor(String anchor) {
        this.anchor = anchor;
    }

    public List<ExtraButton> getExtraButtons() {
        return extraButtons;
    }

    public void setExtraButtons(List<ExtraButton> extraButtons) {
        if ( extraButtons instanceof TypedArrayList ) {
            this.extraButtons = extraButtons;
        } else {
            this.extraButtons.clear();
            this.extraButtons.addAll( extraButtons );
        }
    }

    public ExtraButton getExtraButton( int index ) {
        return extraButtons.get( index );
    }

    public void setExtraButton( int index, ExtraButton extraButton ) {
        extraButtons.set( index, extraButton );
    }
}