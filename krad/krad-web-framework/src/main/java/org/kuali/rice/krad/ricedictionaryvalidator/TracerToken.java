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

package org.kuali.rice.krad.ricedictionaryvalidator;

import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.uif.component.Configurable;
import org.kuali.rice.krad.uif.component.DataBinding;

import java.util.ArrayList;

/**
 * Linear collection of identifiers for individual Spring Beans starting with the base bean and ending with the most
 * recent.
 */
public class TracerToken {

    // A collection identifiers for the traced beans
    private ArrayList<String> beanIds;

    // A collection of types for the traced beans
    private ArrayList<String> beanTypes;

    // Constant identifer for a trace entry where the bean has no identifier itself
    public static final String NO_BEAN_ID = "NOBEANID";

    /**
     * Constructor for an empty token to start a trace
     */
    public TracerToken(){
        beanIds = new ArrayList<String>();
        beanTypes = new ArrayList<String>();
    }

    /**
     * Retrieves a single entry in the BeanId trace list
     * @param index
     * @return String Identifier for the bean at the provided index of the trace
     */
    public String getBeanId(int index){
        return beanIds.get(index);
    }

    /**
     * Retrieves a single entry in the BeanType trace list
     * @param index
     * @return String Type for the bean at the provided index of the trace
     */
    public String getBeanType(int index){
        return beanTypes.get(index);
    }

    /**
     * Retrieves the number of beans in the trace list
     * @return Number of beans stored in the trace
     */
    public int getTraceSize(){
        return beanIds.size();
    }

    /**
     * Retrieves the complete trace path with each bean shown in the form beanId(BeanType)
     * @return The String path of the trace
     */
    public String getBeanLocation(){
        String path="";

        for(int i=0;i< beanTypes.size()-1;i++){
            path=path+ beanTypes.get(i)+"("+beanIds.get(i)+")"+".";
        }

        if(getTraceSize()>0)path=path+ beanTypes.get(beanTypes.size()-1)+"("+beanIds.get(beanTypes.size()-1)+")";

        return path;
    }

    /**
     * Adds a single entry into the trace
     * @param beanId - An identifier for the bean
     * @param beanType - The type of bean
     */
    public void addBean(String beanType, String beanId){
        beanIds.add(beanId);
        beanTypes.add(beanType);
    }

    public void addBean(Component component){
        String beanId=NO_BEAN_ID;
        String beanType=component.getClass().getSimpleName();
        if(component.getId()!=null){
            if(component.getId().compareTo("null")!=0){
                beanId=component.getId();
            } else{
                try{
                    beanId=((DataBinding) component).getPropertyName();

                }catch(Exception e){
                    beanId=NO_BEAN_ID;
                }
            }
        }else {
            try{
                beanId=((DataBinding) component).getPropertyName();
            }catch(Exception e){
                beanId=NO_BEAN_ID;
            }
        }
        addBean(beanType,beanId);
    }

    public void addBean(Configurable configurable){
        String beanId="configurable";
        String beanType = configurable.getClass().getSimpleName();
        addBean(beanType,beanId);
    }

    /**
     * Removes an entry from the trace
     * @param index
     */
    public void removeBean(int index){
        beanIds.remove(index);
        beanTypes.remove(index);
    }

    /**
     * Replaces a trace entry's information
     * @param index
     * @param beanId - An identifier for the bean
     * @param beanType - The type of bean
     */
    public void modifyBean(int index, String beanId, String beanType){
        beanIds.set(index,beanId);
        beanTypes.set(index, beanType);
    }

    /**
     * Creates a copy of the TracerToken
     * @return A complete copy of the current token
     */
    public TracerToken getCopy(){
        TracerToken copy = new TracerToken();

        for(int i=0;i<getTraceSize();i++){
            copy.addBean(getBeanType(i),getBeanId(i));
        }

        return copy;
    }
}
