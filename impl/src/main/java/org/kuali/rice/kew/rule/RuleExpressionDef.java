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
package org.kuali.rice.kew.rule;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.CascadeType;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Entity;

import org.apache.commons.lang.ObjectUtils;

/**
 * BO for rule expressions 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 */
@Entity
@Table(name="KREW_RULE_EXPR_T")
public class RuleExpressionDef {
    /**
     * Primary key
     */
    @Id
	@Column(name="RULE_EXPR_ID")
	@GeneratedValue(strategy=GenerationType.AUTO, generator="KREW_RULE_EXPR_SEQ_GEN")
	@SequenceGenerator(name="KREW_RULE_EXPR_SEQ_GEN", sequenceName="KREW_RULE_EXPR_S") 
	private Long id;
    /**
     * The type of the expression
     */
    @Column(name="TYP")
	private String type;
    /**
     * The content of the expression
     */
    @Column(name="RULE_EXPR", nullable=true)
	private String expression;
    /**
     * @return the id
     */
    public Long getId() {
        return this.id;
    }
    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }
    /**
     * @return the type
     */
    public String getType() {
        return this.type;
    }
    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }
    /**
     * @return the expression
     */
    public String getExpression() {
        return this.expression;
    }
    /**
     * @param expression the expression to set
     */
    public void setExpression(String expression) {
        this.expression = expression;
    }

    /**
     * Returns whether the object is an <i>equivalent</i> rule expression, i.e.
     * the type and expression are the same.  This is necessary for rule duplicate
     * detection.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof RuleExpressionDef)) return false;
        RuleExpressionDef arg = (RuleExpressionDef) obj;
        return ObjectUtils.equals(type, arg.getType()) && ObjectUtils.equals(expression, arg.getExpression());
    }
}
