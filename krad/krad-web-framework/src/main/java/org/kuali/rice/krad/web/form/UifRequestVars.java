package org.kuali.rice.krad.web.form;

import java.util.Map;

/**
 * A Pojo for the form request parameters
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class UifRequestVars {

    private Map<String, String> actionParameters;
    private Map<String, Object> clientStateForSyncing;
    private boolean renderFullView = true;
    private boolean skipViewInit = false;
    private String updateComponentId;

    /**
     * @see org.kuali.rice.krad.uif.view.ViewModel#getActionParameters()
     */
    public Map<String, String> getActionParameters() {
        return actionParameters;
    }

    /**
     * @see org.kuali.rice.krad.uif.view.ViewModel#setActionParameters(java.util.Map)
     */
    public void setActionParameters(Map<String, String> actionParameters) {
        this.actionParameters = actionParameters;
    }

    /**
     *  Returns if the full view needs to be rendered.
     *
     * @return Boolean
     */
    public boolean isRenderFullView() {
        return renderFullView;
    }

    /**
     * Setter for renderFullView
     *
     * @param renderFullView
     */
    public void setRenderFullView(boolean renderFullView) {
        this.renderFullView = renderFullView;
    }

    /**
     *  Returns if the full view initialization can be skipped and its a partial refresh.
     *
     * @return Boolean
     */
    public boolean isSkipViewInit() {
        return skipViewInit;
    }

    /**
     * Setter for skipViewInit
     *
     * @param skipViewInit
     */
    public void setSkipViewInit(boolean skipViewInit) {
        this.skipViewInit = skipViewInit;
    }

    /**
     * Returns the id of the component that needs to be refreshed.
     *
     * @return
     */
    public String getUpdateComponentId() {
        return updateComponentId;
    }

    /**
     * Setter for updateComponentId
     *
     * @param updateComponentId
     */
    public void setUpdateComponentId(String updateComponentId) {
        this.updateComponentId = updateComponentId;
    }

     /**
     * @see org.kuali.rice.krad.uif.view.ViewModel#getClientStateForSyncing()
     */
    public Map<String, Object> getClientStateForSyncing() {
        return clientStateForSyncing;
    }



}

