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
package org.kuali.rice.kew.help;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Version;

import org.kuali.rice.core.jpa.annotations.Sequence;

/**
 * Model bean representing a piece of help information which adds extra information about fields and
 * data in the user interface.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
@Entity
@Table(name="EN_HLP_T")
@NamedQueries({
  @NamedQuery(name="HelpEntry.FindById",  query="select he from HelpEntry he where he.helpId = :helpId"),
  @NamedQuery(name="HelpEntry.FindByKey",  query="select he from HelpEntry he where he.helpKey = :helpKey")
})
@Sequence(name="SEQ_HELP_ENTRY", property="helpId")
public class HelpEntry {
    @Id
	@Column(name="EN_HLP_ID")
	private Long helpId;
	@Column(name="EN_HLP_NM")
	private String helpName;
	@Column(name="EN_HLP_TXT")
	private String helpText;
	@Column(name="EN_HLP_KY")
	private String helpKey;
	@Version
	@Column(name="DB_LOCK_VER_NBR")
	private Integer lockVerNbr;
	
    public Long getHelpId() {
        return helpId;
    }
    public void setHelpId(Long helpId) {
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

