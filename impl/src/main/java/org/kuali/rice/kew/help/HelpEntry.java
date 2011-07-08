/*
 * Copyright 2005-2007 The Kuali Foundation
 * 
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
package org.kuali.rice.kew.help;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

/**
 * Model bean representing a piece of help information which adds extra information about fields and
 * data in the user interface.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Entity
@Table(name="KREW_HLP_T")
@NamedQueries({
  @NamedQuery(name="HelpEntry.FindById",  query="select he from HelpEntry he where he.helpId = :helpId"),
  @NamedQuery(name="HelpEntry.FindByKey",  query="select he from HelpEntry he where he.helpKey = :helpKey")
})
//@Sequence(name="KREW_HLP_S", property="helpId")
public class HelpEntry {
    @Id
    @GeneratedValue(generator="KREW_HLP_S")
	@GenericGenerator(name="KREW_HLP_S",strategy="org.hibernate.id.enhanced.SequenceStyleGenerator",parameters={
			@Parameter(name="sequence_name",value="KREW_HLP_S"),
			@Parameter(name="value_column",value="id")
	})
	@Column(name="HLP_ID")
	private String helpId;
	@Column(name="NM")
	private String helpName;
	@Column(name="HLP_TXT")
	private String helpText;
	@Column(name="KEY_CD")
	private String helpKey;
	@Version
	@Column(name="VER_NBR")
	private Integer lockVerNbr;
	
    public String getHelpId() {
        return helpId;
    }
    public void setHelpId(String helpId) {
        this.helpId = helpId;
    }
    public String getHelpName() {
        return helpName;
    }
    public void setHelpName(String helpName) {
        this.helpName = helpName;
    }
    public String getHelpText() {
    	return helpText;
    }
    public void setHelpText(String helpText) {
    	this.helpText = helpText;
    }
    public Integer getLockVerNbr() {
        return lockVerNbr;
    }
    public void setLockVerNbr(Integer lockVerNbr) {
        this.lockVerNbr = lockVerNbr;
    }
    public String getHelpKey() {
        return helpKey;
    }
    public void setHelpKey(String helpKey) {
        this.helpKey = helpKey;
    }
}

