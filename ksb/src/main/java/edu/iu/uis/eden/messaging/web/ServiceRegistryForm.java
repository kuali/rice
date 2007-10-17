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
package edu.iu.uis.eden.messaging.web;

import java.util.ArrayList;
import java.util.List;

import org.apache.struts.action.ActionForm;

import edu.iu.uis.eden.messaging.ServiceInfo;

/**
 * Struts ActionForm for the {@link ServiceRegistryAction}.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class ServiceRegistryForm extends ActionForm {

    private String methodToCall;
    private List<ServiceInfo> publishedServices = new ArrayList<ServiceInfo>();
    private List<ServiceInfo> publishedTempServices = new ArrayList<ServiceInfo>();
    private List<ServiceInfo> globalRegistryServices = new ArrayList<ServiceInfo>();

    private String myIpAddress;
    private String myMessageEntity;
    private Boolean devMode;

    public String getMethodToCall() {
        return this.methodToCall;
    }
    public void setMethodToCall(String methodToCall) {
        this.methodToCall = methodToCall;
    }
    public List<ServiceInfo> getGlobalRegistryServices() {
        return this.globalRegistryServices;
    }
    public void setGlobalRegistryServices(List<ServiceInfo> globalRegistryServices) {
        this.globalRegistryServices = globalRegistryServices;
    }
    public List<ServiceInfo> getPublishedServices() {
        return this.publishedServices;
    }
    public void setPublishedServices(List<ServiceInfo> publishedServices) {
        this.publishedServices = publishedServices;
    }
    public List<ServiceInfo> getPublishedTempServices() {
        return this.publishedTempServices;
    }
    public void setPublishedTempServices(List<ServiceInfo> publishedTempServices) {
        this.publishedTempServices = publishedTempServices;
    }
    public String getMyIpAddress() {
        return this.myIpAddress;
    }
    public void setMyIpAddress(String myIpAddress) {
        this.myIpAddress = myIpAddress;
    }
    public String getMyMessageEntity() {
        return this.myMessageEntity;
    }
    public void setMyMessageEntity(String myMessageEntity) {
        this.myMessageEntity = myMessageEntity;
    }
    public Boolean getDevMode() {
        return this.devMode;
    }
    public void setDevMode(Boolean devMode) {
        this.devMode = devMode;
    }

}
