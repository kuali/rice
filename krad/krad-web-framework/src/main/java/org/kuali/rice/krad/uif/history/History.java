/*
 * Copyright 2011 The Kuali Foundation Licensed under the Educational Community
 * License, Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.opensource.org/licenses/ecl1.php Unless required by applicable law
 * or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.kuali.rice.krad.uif.history;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.krad.uif.UifConstants;
import org.kuali.rice.krad.uif.container.View;
import org.kuali.rice.krad.uif.util.ViewModelUtils;
import org.kuali.rice.krad.web.spring.form.UifFormBase;

/**
 * History class used to keep track of views visited so they can be displayed in the ui
 * as breadcrumbs - both as homeward path and history path interpretations.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class History implements Serializable{    
	
	private static final long serialVersionUID = -8279297694371557335L;

	public History() {
        historyEntries = new ArrayList<HistoryEntry>();
    }

    private static final Logger LOG = Logger.getLogger(History.class);
    public static final String ENTRY_TOKEN = "$";
    public static final String VAR_TOKEN = ",";
    private List<HistoryEntry> homewardPath;
    private List<HistoryEntry> historyEntries;
    private boolean appendHomewardPath;
    private boolean appendPassedHistory;
    private HistoryEntry current;

    /**
     * Gets the predetermined homeward path for this view's history.
     * This is set by the same property in the view's Breadcrumbs configuration.
     * @return the homewardPath
     */
    public List<HistoryEntry> getHomewardPath() {
        return this.homewardPath;
    }

    /**
     * @param homewardPath
     *            the homewardPath to set
     */
    public void setHomewardPath(List<HistoryEntry> homewardPath) {
        this.homewardPath = homewardPath;
    }

    /**
     * Gets a list of the current HistoryEntries not including the current entry.
     * This list does not include the "&history=" query parameter on each HistoryEntry's
     * url variable.  For HistoryEntries that include history information to be passed to the
     * view they are retrieving, getGeneratedBreadcrumbs is used.
     * 
     * @return the history
     */
    public List<HistoryEntry> getHistoryEntries() {
        return this.historyEntries;
    }

    /**
     * 
     * @param history
     *            the history to set
     */
    public void setHistoryEntries(List<HistoryEntry> history) {
        this.historyEntries = history;
    }

    /**
     * Gets the current view's HistoryEntry.
     * This does not include the "&history=" query parameter on its
     * url variable.  For the HistoryEntry that includes history information to be passed
     * on the url it is retrieving, getGeneratedCurrentBreadcrumb is used.
     * 
     * @return the current
     */
    public HistoryEntry getCurrent() {
        return this.current;
    }

    /**
     * @param current
     *            the current to set
     */
    private void setCurrent(String viewId, String pageId, String title, String url, String formKey) {
        HistoryEntry entry = new HistoryEntry(viewId, pageId, title, url, formKey);
        current = entry;
    }

    /**
     * @param current
     *            the current to set
     */
    public void setCurrent(HistoryEntry current) {
        this.current = current;
    }

    /**
     * This method takes in the encoded history query parameter string passed on the url and parses it to create
     * the list of historyEntries.  It will also append any homeward path if appendHomewardPath is true.  This
     * append will happen after the passedHistory entries are appended so it will not make sense to use both settings in
     * most cases.
     * 
     * @param parameterString
     */
    public void buildHistoryFromParameterString(String parameterString) {
        if (StringUtils.isNotEmpty(parameterString)) {
            try {
                parameterString = URLDecoder.decode(parameterString, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                LOG.error("Error decoding history param", e);
            }
            historyEntries = new ArrayList<HistoryEntry>();
            if (appendPassedHistory) {
                String[] historyTokens = parameterString.split("\\" + ENTRY_TOKEN);
                for (String token : historyTokens) {
                    String[] params = token.split(VAR_TOKEN);
                    pushToHistory(params[0], params[1], params[2], params[3], params[4]);
                }
            }
        }

        if (appendHomewardPath) {
            historyEntries.addAll(homewardPath);
        }
    }

    /**
     * This method gets the encoded and tokenized history parameter string that is representative of the HistoryEntries
     * currently in History and includes the current view's HistoryEntry.  This parameter should be appended on any
     * appropriate links which perform view swapping.
     * 
     * @return
     */
    public String getHistoryParameterString() {
        String historyString = "";
            for (HistoryEntry e : historyEntries) {
                if (historyEntries.indexOf(e) == 0) {
                    historyString = historyString + e.toParam();
                } else {
                    historyString = historyString + ENTRY_TOKEN + e.toParam();
                }
            }
        // add current
        if (historyString.equals("")) {
            historyString = historyString + current.toParam();
        } else {
            historyString = historyString + ENTRY_TOKEN + current.toParam();
        }
        try {
            historyString = URLEncoder.encode(historyString, "UTF-8");
        } catch (Exception e) {
            LOG.error("Error encoding history param", e);
        }
        return historyString;
    }
    
    /**
     * This method generates a list of HistoryEntries that can be used as breadcrumbs by the breadcrumb widget.  This
     * method appends the appropriate history information on the HistoryEntry url variables so when a view is requested
     * its history can be regenerated for use in its breadcrumbs.  It also sets the the passed showHome variable to false
     * to prevent showing the homeward path more than once (as it is passed through the history variable backwards).
     * This does not include the current HistoryEntry as a breadcrumb.
     * 
     * @return
     */
    public List<HistoryEntry> getGeneratedBreadcrumbs(){
        List<HistoryEntry> breadcrumbs = new ArrayList<HistoryEntry>();
        for(int i=0; i < historyEntries.size(); i++){
            if(i == 0){
                breadcrumbs.add(copyEntry(historyEntries.get(i)));
            }
            else{
                HistoryEntry breadcrumb = copyEntry(historyEntries.get(i));
                String historyParam = "";
                for(int j=0; j < i; j++){
                   historyParam = historyParam + ENTRY_TOKEN + historyEntries.get(j).toParam();
                }
                historyParam = historyParam.replaceFirst("\\" + ENTRY_TOKEN, "");
                try {
                    historyParam = URLEncoder.encode(historyParam, "UTF-8");
                } catch (Exception e) {
                    LOG.error("Error encoding history param", e);
                }
                
                String url = "";
                if(breadcrumb.getUrl().contains("?")){
                    url = breadcrumb.getUrl() + "&" + UifConstants.UrlParams.HISTORY + "=" + historyParam;
                }
                else{
                    url = breadcrumb.getUrl() + "?" + UifConstants.UrlParams.HISTORY + "=" + historyParam;
                }
                breadcrumb.setUrl(url);
                breadcrumbs.add(breadcrumb);
            }
        }
        
        return breadcrumbs;
    }
    
    /**
     * This method gets the current HistoryEntry in the breadcrumb format described in getGeneratedBreadcrumbs.
     * 
     * @return
     */
    public HistoryEntry getGeneratedCurrentBreadcrumb(){
        HistoryEntry breadcrumb = copyEntry(current);
        String historyParam = "";
        for(int j=0; j < historyEntries.size(); j++){
           historyParam = historyParam + ENTRY_TOKEN + historyEntries.get(j).toParam();
        }
        historyParam = historyParam.replaceFirst("\\" + ENTRY_TOKEN, "");
        
        try {
            historyParam = URLEncoder.encode(historyParam, "UTF-8");
        } catch (Exception e) {
            LOG.error("Error encoding history param", e);
        }
        
        String url = "";
        if(breadcrumb.getUrl().contains("?")){
            url = breadcrumb.getUrl() + "&" + UifConstants.UrlParams.HISTORY + "=" + historyParam;
        }
        else{
            url = breadcrumb.getUrl() + "?" + UifConstants.UrlParams.HISTORY + "=" + historyParam;
        }
        breadcrumb.setUrl(url);
        return breadcrumb;
    }
    
    /**
     * Copies a HistoryEntry, for use during breadcrumb generation.
     * 
     * @param e
     * @return
     */
    private HistoryEntry copyEntry(HistoryEntry e){
        return new HistoryEntry(e.getViewId(), e.getPageId(), e.getTitle(), e.getUrl(), e.getFormKey());
    }

    /**
     * This method pushes the information passed in to history.
     * Note: currently only used internally in the class - be cautious about its external use.
     * 
     * @param viewId
     * @param pageId
     * @param title
     * @param url
     * @param formKey
     */
    public void pushToHistory(String viewId, String pageId, String title, String url, String formKey) {
        HistoryEntry entry = new HistoryEntry(viewId, pageId, title, url, formKey);
        historyEntries.add(entry);
    }

    /**
     * When this is set to true, the homeward path will be appended.
     * Note:  For most cases this should only be on during the first view load.
     * This setting is set automatically in most cases.
     * @param appendHomewardPath
     *            the appendHomewardPath to set
     */
    public void setAppendHomewardPath(boolean appendHomewardPath) {
        this.appendHomewardPath = appendHomewardPath;
    }

    /**
     * @return the appendHomewardPath
     */
    public boolean isAppendHomewardPath() {
        return appendHomewardPath;
    }

    /**
     * Appends the passed history as each different view is shown.  This setting should be used when displaying
     * passed history is relevant to the user (ie inquiry/lookup chains).  This setting is set automatically in
     * most cases.
     * @param appendPassedHistory
     *            the appendPassedHistory to set
     */
    public void setAppendPassedHistory(boolean appendPassedHistory) {
        this.appendPassedHistory = appendPassedHistory;
    }

    /**
     * @return the appendPassedHistory
     */
    public boolean isAppendPassedHistory() {
        return appendPassedHistory;
    }

    /**
     * Sets the current HistoryEntry using information from the form and the request.  This history parameter is extracted
     * out of the url inorder for a "clean" url to be used in history parameter and breadcrumb generation, as passing history
     * history through the nested urls is unnecessary.
     * 
     * @param form
     * @param request
     */
    @SuppressWarnings("unchecked")
    public void setCurrent(UifFormBase form, HttpServletRequest request) {
       if(!request.getMethod().equals("POST")){
           boolean showHomeValue = false;
           boolean pageIdValue = false;
           boolean formKeyValue = false;
           String queryString = "";
           String url = request.getRequestURL().toString();
           //remove history attribute
           Enumeration<String> params = request.getParameterNames();
           while(params.hasMoreElements()){
               String key = params.nextElement();
               if(!key.equals(UifConstants.UrlParams.HISTORY)){
                   for(String value: request.getParameterValues(key)){
                       queryString = queryString + "&" + key + "=" + value;  
                   }
               }
               else if(key.equals(UifConstants.UrlParams.PAGE_ID)){
                   pageIdValue = true;
               }
               else if(key.equals(UifConstants.UrlParams.SHOW_HOME)){
                   showHomeValue = true;
               }
               else if(key.equals(UifConstants.UrlParams.FORM_KEY)){
                   formKeyValue = true;
               }
           }
           
           //add formKey and pageId to url
           if(StringUtils.isNotBlank(form.getFormKey()) && !formKeyValue){
               queryString = queryString + "&" + UifConstants.UrlParams.FORM_KEY + "=" + form.getFormKey();
           }
           if(StringUtils.isNotBlank(form.getPageId()) && !pageIdValue){
               queryString = queryString + "&" + UifConstants.UrlParams.PAGE_ID + "=" + form.getPageId();
           }
           if(!showHomeValue){
               queryString = queryString + "&" + UifConstants.UrlParams.SHOW_HOME + "=false";
           }
           
           queryString = queryString.replaceFirst("&", "");
           
           if(StringUtils.isNotEmpty(queryString)){
               url = url + "?" + queryString;
           }
           
           View view = form.getView();
           String title = view.getTitle();
           //may move this into view logic instead in the future if it is required for the view's title (not just breadcrumb)
           //if so remove this and just use getTitle - this logic would be in performFinalize instead
           Object titleValue = ViewModelUtils.getValue(view, form, view.getViewLabelFieldPropertyName(), view.getViewLabelFieldBindingInfo());
           String titleAppend = "";
           if(titleValue != null){
               titleAppend = titleValue.toString();
           }
           if(StringUtils.isNotBlank(titleAppend) && view.getAppendOption() != null){
               if(view.getAppendOption().equalsIgnoreCase(UifConstants.TitleAppendTypes.DASH)){
                   title = title + " - " + titleAppend;
               }
               else if(view.getAppendOption().equalsIgnoreCase(UifConstants.TitleAppendTypes.PARENTHESIS)){
                   title = title + "(" + titleAppend +")";
               }
               else if(view.getAppendOption().equalsIgnoreCase(UifConstants.TitleAppendTypes.REPLACE)){
                   title = titleAppend;
               }
               //else it is none or blank so no title modification will be used
           }
           this.setCurrent(form.getViewId(), form.getPageId(), title, url, form.getFormKey());
       }
    }
}
