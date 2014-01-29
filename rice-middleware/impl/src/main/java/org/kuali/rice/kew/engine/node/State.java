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
package org.kuali.rice.kew.engine.node;

import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.data.jpa.PortableSequenceGenerator;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * A KeyValuePair that adds an id fields that makes it sufficient for storing in a database.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@MappedSuperclass
@AttributeOverride(name="objectId", column=@Column(name="VAL", updatable = false, insertable = false))
public abstract class State extends PersistableBusinessObjectBase implements KeyValue {
    @Id
    @PortableSequenceGenerator(name="KREW_RTE_NODE_S")
    @GeneratedValue(generator="KREW_RTE_NODE_S")
	protected String stateId;
    @Column(name="KEY_CD")
	private String key;
    @Column(name="VAL")
    private String value;

    public State() {}
    
    public State(String key, String value) {
    	this.key = key;
    	this.value = value;
    }

    public String getStateId() {
        return stateId;
    }

    public void setStateId(String stateId) {
        this.stateId = stateId;
    }
    
    @Override
    public String getKey() {
    	return key;
    }
    
    @Override
    public String getValue() {
    	return value;
    }
    
    public void setKey(String key) {
		this.key = key;
	}

	public void setValue(String value) {
		this.value = value;
	}

    @Override
    public String toString(){
        return "stateId: " +getStateId();
    }
}
