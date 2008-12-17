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
package org.kuali.rice.kew.engine.node;

import java.io.Serializable;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.SequenceGenerator;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * A KeyValuePair that adds an id fields that makes it sufficient for storing in a database.
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
@MappedSuperclass
public abstract class State extends KeyValuePair implements Serializable {
    @Id
	@GeneratedValue(strategy=javax.persistence.GenerationType.SEQUENCE, generator="KREWSeq")
	@SequenceGenerator(name="KREWSeq",sequenceName="KREW_RTE_NODE_S", allocationSize=1)
    protected Long stateId;

    public State() {}

    public State(String key, String value) {
        super(key, value);
    }

    public Long getStateId() {
        return stateId;
    }

    public void setStateId(Long stateId) {
        this.stateId = stateId;
    }
    
    public String toString() {
        return new ToStringBuilder(this)
            .append("stateId", stateId)
            .append("key", key)
            .append("value", value)
            .toString();
    }
}