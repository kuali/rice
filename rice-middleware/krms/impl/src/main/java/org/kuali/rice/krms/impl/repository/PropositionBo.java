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
package org.kuali.rice.krms.impl.repository;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.mo.common.Versioned;
import org.kuali.rice.krad.data.DataObjectService;
import org.kuali.rice.krad.data.jpa.PortableSequenceGenerator;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.krms.api.repository.LogicalOperator;
import org.kuali.rice.krms.api.repository.proposition.PropositionDefinition;
import org.kuali.rice.krms.api.repository.proposition.PropositionDefinitionContract;
import org.kuali.rice.krms.api.repository.proposition.PropositionParameter;
import org.kuali.rice.krms.api.repository.proposition.PropositionParameterType;
import org.kuali.rice.krms.api.repository.proposition.PropositionType;
import org.kuali.rice.krms.impl.ui.CustomOperatorUiTranslator;
import org.kuali.rice.krms.impl.ui.TermParameter;
import org.kuali.rice.krms.impl.util.KrmsImplConstants;
import org.kuali.rice.krms.impl.util.KrmsServiceLocatorInternal;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "KRMS_PROP_T")
public class PropositionBo implements PropositionDefinitionContract, Versioned, Serializable {

    private static final long serialVersionUID = 1l;

    private static final String PROP_SEQ_NAME = "KRMS_PROP_S";
    static final RepositoryBoIncrementer propositionIdIncrementer = new RepositoryBoIncrementer(PROP_SEQ_NAME);
    static final RepositoryBoIncrementer propositionParameterIdIncrementer = new RepositoryBoIncrementer("KRMS_PROP_PARM_S");

    @PortableSequenceGenerator(name = PROP_SEQ_NAME)
    @GeneratedValue(generator = PROP_SEQ_NAME)
    @Id
    @Column(name = "PROP_ID")
    private String id;

    @Column(name = "DESC_TXT")
    private String description;

    @Column(name = "RULE_ID")
    private String ruleId;

    @Column(name = "TYP_ID")
    private String typeId;

    @Column(name = "DSCRM_TYP_CD")
    private String propositionTypeCode;

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "proposition")
    @OrderBy("sequenceNumber")
    private List<PropositionParameterBo> parameters = new ArrayList<PropositionParameterBo>();

    @Column(name = "CMPND_OP_CD")
    private String compoundOpCode;

    @Column(name = "CMPND_SEQ_NO")
    private Integer compoundSequenceNumber;

    @Column(name = "VER_NBR")
    @Version
    private Long versionNumber;

    @ManyToMany(targetEntity = PropositionBo.class, cascade = { CascadeType.REFRESH, CascadeType.REMOVE, CascadeType.PERSIST })
    @JoinTable(name = "KRMS_CMPND_PROP_PROPS_T", joinColumns = { @JoinColumn(name = "CMPND_PROP_ID", referencedColumnName = "PROP_ID") }, inverseJoinColumns = { @JoinColumn(name = "PROP_ID", referencedColumnName = "PROP_ID") })
    @OrderBy("compoundSequenceNumber")
    private List<PropositionBo> compoundComponents;

    @Transient
    private String parameterDisplayString;

    @Transient
    private boolean editMode = false;

    @Transient
    private String categoryId;

    @Transient
    private String termSpecId;

    @Transient
    private boolean showCustomValue;

    @Transient
    private String termParameter;

    @Transient
    private List<TermParameter> termParameterList = new ArrayList<TermParameter>();

    @Transient
    private String newTermDescription = "new term " + UUID.randomUUID().toString();

    @Transient
    private Map<String, String> termParameters = new HashMap<String, String>();

    private void setupParameterDisplayString() {
        if (PropositionType.SIMPLE.getCode().equalsIgnoreCase(getPropositionTypeCode())) {
            // Simple Propositions should have 3 parameters ordered in reverse polish notation.  
            // TODO: enhance to get term names for term type parameters.  
            List<PropositionParameterBo> parameters = getParameters();

            if (parameters != null && parameters.size() == 3) {
                StringBuilder sb = new StringBuilder();
                String valueDisplay = getParamValue(parameters.get(1));
                sb.append(getParamValue(parameters.get(0))).append(" ").append(getParamValue(parameters.get(2)));

                if (valueDisplay != null) {
                    // !=null and =null operators values will be null and should not be displayed  
                    sb.append(" ").append(valueDisplay);
                }

                setParameterDisplayString(sb.toString());
            } else {
                // should not happen
            }
        }
    }

    /**
     * returns the string summary value for the given proposition parameter.
     *
     * @param param the proposition parameter to get the summary value for
     * @return the summary value
     */
    private String getParamValue(PropositionParameterBo param) {
        CustomOperatorUiTranslator customOperatorUiTranslator =
                KrmsServiceLocatorInternal.getCustomOperatorUiTranslator();

        if (PropositionParameterType.TERM.getCode().equalsIgnoreCase(param.getParameterType())) {
            String termName = "";
            String termId = param.getValue();

            if (termId != null && termId.length() > 0) {
                if (termId.startsWith(KrmsImplConstants.PARAMETERIZED_TERM_PREFIX)) {
                    if (!StringUtils.isBlank(newTermDescription)) {
                        termName = newTermDescription;
                    } else {
                        TermSpecificationBo termSpec = getDataObjectService().find(TermSpecificationBo.class,
                                termId.substring(1 + termId.indexOf(":")));
                        termName = termSpec.getName() + "(...)";
                    }
                } else {
                    TermBo term = getDataObjectService().find(TermBo.class, termId);
                    termName = term.getSpecification().getName();
                }
            }

            return termName;

        } else if (PropositionParameterType.FUNCTION.getCode().equalsIgnoreCase(param.getParameterType()) ||
                PropositionParameterType.OPERATOR.getCode().equalsIgnoreCase(param.getParameterType())) {
            if (customOperatorUiTranslator.isCustomOperatorFormValue(param.getValue())) {
                String functionName = customOperatorUiTranslator.getCustomOperatorName(param.getValue());
                if (!StringUtils.isEmpty(functionName)) {
                    return functionName;
                }
            }
        }

        return param.getValue();
    }

    /**
     * @return the parameterDisplayString
     */
    public String getParameterDisplayString() {
        setupParameterDisplayString();

        return this.parameterDisplayString;
    }

    /**
     * @param parameterDisplayString the parameterDisplayString to set
     */
    public void setParameterDisplayString(String parameterDisplayString) {
        this.parameterDisplayString = parameterDisplayString;
    }

    public boolean getEditMode() {
        return this.editMode;
    }

    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
    }

    public String getCategoryId() {
        return this.categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    /**
     * set the typeId.  If the parameter is blank, then this PropositionBo's
     * typeId will be set to null
     *
     * @param typeId
     */
    public void setTypeId(String typeId) {
        if (StringUtils.isBlank(typeId)) {
            this.typeId = null;
        } else {
            this.typeId = typeId;
        }
    }

    public Long getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(Long versionNumber) {
        this.versionNumber = versionNumber;
    }

    public Map<String, String> getTermParameters() {
        return termParameters;
    }

    public void setTermParameters(Map<String, String> termParameters) {
        this.termParameters = termParameters;
    }

    public DataObjectService getDataObjectService() {
        return KRADServiceLocator.getDataObjectService();
    }

    /**
     * Converts a mutable bo to it's immutable counterpart
     *
     * @param bo the mutable business object
     * @return the immutable object
     */
    public static PropositionDefinition to(PropositionBo bo) {
        if (bo == null) {
            return null;
        }

        return PropositionDefinition.Builder.create(bo).build();
    }

    /**
     * Converts a immutable object to it's mutable bo counterpart
     *
     * @param im immutable object
     * @return the mutable bo
     */
    public static PropositionBo from(PropositionDefinition im) {
        if (im == null) {
            return null;
        }

        PropositionBo bo = new PropositionBo();
        bo.id = im.getId();
        bo.description = im.getDescription();

        // we don't set rule here, it is set in RuleBo.from

        setRuleIdRecursive(im.getRuleId(), bo);

        bo.typeId = im.getTypeId();
        bo.propositionTypeCode = im.getPropositionTypeCode();
        bo.parameters = new ArrayList<PropositionParameterBo>();

        for (PropositionParameter parm : im.getParameters()) {
            PropositionParameterBo parmBo = PropositionParameterBo.from(parm);
            bo.parameters.add(parmBo);
            parmBo.setProposition(bo);
        }

        bo.compoundOpCode = im.getCompoundOpCode();
        bo.compoundSequenceNumber = im.getCompoundSequenceNumber();
        bo.compoundComponents = new ArrayList<PropositionBo>();

        if (im.getCompoundComponents() != null) for (PropositionDefinition prop : im.getCompoundComponents()) {
            bo.compoundComponents.add(PropositionBo.from(prop));
        }

        bo.setVersionNumber(im.getVersionNumber());

        return bo;
    }

    private static void setRuleIdRecursive(String ruleId, PropositionBo prop) {
        prop.ruleId = ruleId;

        if (prop.compoundComponents != null) {
            for (PropositionBo child : prop.compoundComponents) {
                if (child != null) {
                    setRuleIdRecursive(ruleId, child);
                }
            }
        }
    }

    /**
     * This method creates a partially populated Simple PropositionBo with
     * three parameters:  a term type paramter (value not assigned)
     * a operation parameter
     * a constant parameter (value set to empty string)
     * The returned PropositionBo has an generatedId. The type code and ruleId properties are assigned the
     * same value as the sibling param passed in.
     * Each PropositionParameter has the id generated, and type, sequenceNumber,
     * propId default values set. The value is set to "".
     *
     * @param sibling -
     * @param pType
     * @return a PropositionBo partially populated.
     */
    public static PropositionBo createSimplePropositionBoStub(PropositionBo sibling, String pType) {
        // create a simple proposition Bo  
        PropositionBo prop = null;
        if (PropositionType.SIMPLE.getCode().equalsIgnoreCase(pType)) {
            prop = new PropositionBo();
            prop.setId(propositionIdIncrementer.getNewId());
            prop.setPropositionTypeCode(pType);
            prop.setEditMode(true);

            if (sibling != null) {
                prop.setRuleId(sibling.getRuleId());
            }

            // create blank proposition parameters  
            PropositionParameterBo pTerm = new PropositionParameterBo();
            pTerm.setId(propositionParameterIdIncrementer.getNewId());
            pTerm.setParameterType("T");
            pTerm.setProposition(prop);
            pTerm.setSequenceNumber(new Integer("0"));
            pTerm.setVersionNumber(new Long(1));
            pTerm.setValue("");

            // create blank proposition parameters  
            PropositionParameterBo pOp = new PropositionParameterBo();
            pOp.setId(propositionParameterIdIncrementer.getNewId());
            pOp.setParameterType("O");
            pOp.setProposition(prop);
            pOp.setSequenceNumber(new Integer("2"));
            pOp.setVersionNumber(new Long(1));

            // create blank proposition parameters  
            PropositionParameterBo pConst = new PropositionParameterBo();
            pConst.setId(propositionParameterIdIncrementer.getNewId());
            pConst.setParameterType("C");
            pConst.setProposition(prop);
            pConst.setSequenceNumber(new Integer("1"));
            pConst.setVersionNumber(new Long(1));
            pConst.setValue("");
            List<PropositionParameterBo> paramList = Arrays.asList(pTerm, pConst, pOp);

            prop.setParameters(paramList);
        }

        return prop;
    }

    public static PropositionBo createCompoundPropositionBoStub(PropositionBo existing, boolean addNewChild) {
        // create a simple proposition Bo  
        PropositionBo prop = new PropositionBo();
        prop.setId(propositionIdIncrementer.getNewId());
        prop.setPropositionTypeCode(PropositionType.COMPOUND.getCode());
        prop.setCompoundOpCode(LogicalOperator.AND.getCode());

        // default to and  
        prop.setDescription("");
        prop.setEditMode(true);

        if (existing != null) {
            prop.setRuleId(existing.getRuleId());
        }

        List<PropositionBo> components = new ArrayList<PropositionBo>(2);
        components.add(existing);

        if (addNewChild) {
            PropositionBo newProp = createSimplePropositionBoStub(existing, PropositionType.SIMPLE.getCode());
            components.add(newProp);
            prop.setEditMode(false);
        }

        prop.setCompoundComponents(components);

        return prop;
    }

    public static PropositionBo createCompoundPropositionBoStub2(PropositionBo existing) {
        // create a simple proposition Bo  
        PropositionBo prop = new PropositionBo();
        prop.setId(propositionIdIncrementer.getNewId());
        prop.setPropositionTypeCode(PropositionType.COMPOUND.getCode());
        prop.setRuleId(existing.getRuleId());
        prop.setCompoundOpCode(LogicalOperator.AND.getCode());
        // default to and  
        prop.setDescription("");
        prop.setEditMode(true);
        List<PropositionBo> components = new ArrayList<PropositionBo>();
        ((ArrayList<PropositionBo>) components).add(existing);
        prop.setCompoundComponents(components);

        return prop;
    }

    public static PropositionBo copyProposition(PropositionBo existing) {
        // Note: RuleId is not set  
        PropositionBo newProp = new PropositionBo();
        newProp.setId(propositionIdIncrementer.getNewId());
        newProp.setDescription(existing.getDescription());
        newProp.setPropositionTypeCode(existing.getPropositionTypeCode());
        newProp.setTypeId(existing.getTypeId());
        newProp.setCompoundOpCode(existing.getCompoundOpCode());
        newProp.setCompoundSequenceNumber(existing.getCompoundSequenceNumber());

        // parameters  
        List<PropositionParameterBo> newParms = new ArrayList<PropositionParameterBo>();

        for (PropositionParameterBo parm : existing.getParameters()) {
            PropositionParameterBo p = new PropositionParameterBo();
            p.setId(propositionParameterIdIncrementer.getNewId());
            p.setParameterType(parm.getParameterType());
            p.setProposition(newProp);
            p.setSequenceNumber(parm.getSequenceNumber());
            p.setValue(parm.getValue());
            ((ArrayList<PropositionParameterBo>) newParms).add(p);
        }

        newProp.setParameters(newParms);

        // compoundComponents
        List<PropositionBo> newCompoundComponents = new ArrayList<PropositionBo>();
        for (PropositionBo component : existing.getCompoundComponents()) {
            PropositionBo newComponent = copyProposition(component);
            ((ArrayList<PropositionBo>) newCompoundComponents).add(component);
        }

        newProp.setCompoundComponents(newCompoundComponents);

        return newProp;
    }

    /*
     * This is being done because there is a  major issue with lazy relationships, in ensuring that the relationship is
     * still available after the object has been detached, or serialized. For most JPA providers, after serialization
     * any lazy relationship that was not instantiated will be broken, and either throw an error when accessed,
     * or return null.
     */
    private void writeObject(ObjectOutputStream stream) throws IOException, ClassNotFoundException {
        parameters.size();
        stream.defaultWriteObject();
    }

    public String getTermSpecId() {
        return termSpecId;
    }

    public void setTermSpecId(String componentId) {
        this.termSpecId = componentId;
    }

    public boolean isShowCustomValue() {
        return showCustomValue;
    }

    public void setShowCustomValue(boolean showCustomValue) {
        this.showCustomValue = showCustomValue;
    }

    public String getTermParameter() {
        return termParameter;
    }

    public void setTermParameter(String termParameter) {
        this.termParameter = termParameter;
    }

    public List<TermParameter> getTermParameterList() {
        return termParameterList;
    }

    public void setTermParameterList(List<TermParameter> termParameterList) {
        this.termParameterList = termParameterList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRuleId() {
        return ruleId;
    }

    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }

    public String getTypeId() {
        return typeId;
    }

    public String getPropositionTypeCode() {
        return propositionTypeCode;
    }

    public void setPropositionTypeCode(String propositionTypeCode) {
        this.propositionTypeCode = propositionTypeCode;
    }

    public List<PropositionParameterBo> getParameters() {
        return parameters;
    }

    public void setParameters(List<PropositionParameterBo> parameters) {
        this.parameters = parameters;
    }

    public String getCompoundOpCode() {
        return compoundOpCode;
    }

    public void setCompoundOpCode(String compoundOpCode) {
        this.compoundOpCode = compoundOpCode;
    }

    public Integer getCompoundSequenceNumber() {
        return compoundSequenceNumber;
    }

    public void setCompoundSequenceNumber(Integer compoundSequenceNumber) {
        this.compoundSequenceNumber = compoundSequenceNumber;
    }

    public List<PropositionBo> getCompoundComponents() {
        return compoundComponents;
    }

    public void setCompoundComponents(List<PropositionBo> compoundComponents) {
        this.compoundComponents = compoundComponents;
    }

    public boolean getShowCustomValue() {
        return showCustomValue;
    }

    public String getNewTermDescription() {
        return newTermDescription;
    }

    public void setNewTermDescription(String newTermDescription) {
        this.newTermDescription = newTermDescription;
    }
}
