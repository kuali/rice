/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.kuali.rice.krms.impl.repository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;
import org.kuali.rice.krms.api.repository.NaturalLanguageTree;
import org.kuali.rice.krms.api.repository.RuleManagementService;
import org.kuali.rice.krms.api.repository.TranslateBusinessMethods;
import org.kuali.rice.krms.api.repository.agenda.AgendaDefinition;
import org.kuali.rice.krms.api.repository.agenda.AgendaItemDefinition;
import org.kuali.rice.krms.api.repository.language.NaturalLanguageTemplate;
import org.kuali.rice.krms.api.repository.language.NaturalLanguageTemplaterContract;
import org.kuali.rice.krms.api.repository.proposition.PropositionDefinition;
import org.kuali.rice.krms.api.repository.proposition.PropositionParameter;
import org.kuali.rice.krms.api.repository.proposition.PropositionParameterType;
import org.kuali.rice.krms.api.repository.proposition.PropositionType;
import org.kuali.rice.krms.api.repository.rule.RuleDefinition;
import org.kuali.rice.krms.api.repository.term.TermDefinition;
import org.kuali.rice.krms.api.repository.term.TermParameterDefinition;
import org.kuali.rice.krms.api.repository.term.TermRepositoryService;

/**
 * @author nwright
 */
public class TranslationUtility implements TranslateBusinessMethods {

    private RuleManagementService ruleManagementService;
    private NaturalLanguageTemplaterContract templater;

    public TranslationUtility(RuleManagementService ruleManagementService,
            NaturalLanguageTemplaterContract templater) {
        this.ruleManagementService = ruleManagementService;
        this.templater = templater;
    }

    public RuleManagementService getRuleManagementService() {
        return ruleManagementService;
    }

    public void setRuleManagementService(RuleManagementService ruleManagementService) {
        this.ruleManagementService = ruleManagementService;
    }

    public NaturalLanguageTemplaterContract getTemplater() {
        return templater;
    }

    public void setTemplater(NaturalLanguageTemplaterContract templater) {
        this.templater = templater;
    }

    @Override
    public String translateNaturalLanguageForObject(String naturalLanguageUsageId, String typeId, String krmsObjectId, String languageCode)
            throws RiceIllegalArgumentException {

        PropositionDefinition proposition = null;
        // TODO: find out what RICE intended for this typeId? Was it supposed to be the Simple Class name?
        if (typeId.equals("proposition")) {
            proposition = this.ruleManagementService.getProposition(krmsObjectId);
            if (proposition == null) {
                throw new RiceIllegalArgumentException(krmsObjectId + " is not an Id for a proposition");
            }
        } else if (typeId.equals("agenda")) {
            AgendaDefinition agenda = this.ruleManagementService.getAgenda(krmsObjectId);
            if (agenda == null) {
                throw new RiceIllegalArgumentException(krmsObjectId + " is not an Id for an agenda");
            }
            if (agenda.getFirstItemId() == null) {
                throw new RiceIllegalArgumentException("Agenda has no first item");
            }
            AgendaItemDefinition item = this.ruleManagementService.getAgendaItem(agenda.getFirstItemId());
            if (item.getRuleId() == null) {
                throw new RiceIllegalArgumentException("Only simple agenda's composed of one item that holds a rule is supported at this time");
            }
            RuleDefinition rule = this.ruleManagementService.getRule(item.getRuleId());
            proposition = rule.getProposition();
            if (proposition == null) {
                throw new RiceIllegalArgumentException("The agenda's rule has a proposition that is null");
            }
        }
        String propositionTypeId = proposition.getTypeId();
        NaturalLanguageTemplate naturalLanguageTemplate =
                this.ruleManagementService.findNaturalLanguageTemplateByLanguageCodeTypeIdAndNluId(languageCode,
                propositionTypeId,
                naturalLanguageUsageId);
        if (naturalLanguageTemplate == null) {
            throw new RiceIllegalArgumentException("no template found for " + languageCode
                    + " " + typeId
                    + " " + naturalLanguageUsageId);
        }
        return this.translateNaturalLanguageForProposition(naturalLanguageUsageId, proposition, languageCode);
    }

    @Override
    public String translateNaturalLanguageForProposition(String naturalLanguageUsageId,
            PropositionDefinition proposition, String languageCode)
            throws RiceIllegalArgumentException {
        NaturalLanguageTemplate naturalLanguageTemplate =
                this.ruleManagementService.findNaturalLanguageTemplateByLanguageCodeTypeIdAndNluId(languageCode,
                proposition.getTypeId(), naturalLanguageUsageId);
        if (naturalLanguageTemplate == null) {
            throw new RiceIllegalArgumentException(languageCode + "." + proposition.getTypeId() + "." + naturalLanguageUsageId);
        }
        Map<String, Object> contextMap;
        if (proposition.getPropositionTypeCode().equals(PropositionType.SIMPLE.getCode())) {
            contextMap = this.buildSimplePropositionContextMap(proposition);
        } else {
            contextMap = this.buildCompoundPropositionContextMap(null, proposition, null);
        }
        return templater.translate(naturalLanguageTemplate, contextMap);
    }

    @Override
    public NaturalLanguageTree translateNaturalLanguageTreeForProposition(String naturalLanguageUsageId,
            PropositionDefinition proposition,
            String languageCode) throws RiceIllegalArgumentException {
        NaturalLanguageTemplate naturalLanguageTemplate = null;
        //Continue if typeid is null, some children may not be initialized yet.
        if (proposition.getTypeId() != null) {
            naturalLanguageTemplate = this.ruleManagementService.findNaturalLanguageTemplateByLanguageCodeTypeIdAndNluId(languageCode,
                    proposition.getTypeId(), naturalLanguageUsageId);
            if (naturalLanguageTemplate == null) {
                throw new RiceIllegalArgumentException(languageCode + "." + proposition.getTypeId() + "." + naturalLanguageUsageId);
            }
        }

        if (proposition.getPropositionTypeCode().equals(PropositionType.SIMPLE.getCode())) {
            NaturalLanguageTree.Builder tree = NaturalLanguageTree.Builder.create();
            Map<String, Object> contextMap = this.buildSimplePropositionContextMap(proposition);
            String naturalLanguage = templater.translate(naturalLanguageTemplate, contextMap);
            tree.setNaturalLanguage(naturalLanguage);
            return tree.build();
        }
        if (proposition.getPropositionTypeCode().equals(PropositionType.COMPOUND.getCode())) {
            NaturalLanguageTree.Builder tree = NaturalLanguageTree.Builder.create();
            Map<String, Object> contextMap = this.buildCompoundPropositionContextMap(naturalLanguageUsageId, proposition, languageCode);
            String naturalLanguage = templater.translate(naturalLanguageTemplate, contextMap);
            tree.setNaturalLanguage(naturalLanguage);

            //Null check because newly created compound propositions should also be translateable.
            if(proposition.getCompoundComponents()!=null){
                List<NaturalLanguageTree> children = new ArrayList<NaturalLanguageTree>();
                for (PropositionDefinition child : proposition.getCompoundComponents()) {
                    children.add(this.translateNaturalLanguageTreeForProposition(naturalLanguageUsageId, child, languageCode));
                }
                tree.setChildren(children);
            }

            return tree.build();
        }
        throw new RiceIllegalArgumentException("Unknown proposition type: " + proposition.getPropositionTypeCode());
    }

    protected Map<String, Object> buildSimplePropositionContextMap(PropositionDefinition proposition) {
        if (!proposition.getPropositionTypeCode().equals(PropositionType.SIMPLE.getCode())) {
            throw new RiceIllegalArgumentException("proposition is not simple " + proposition.getPropositionTypeCode() + " " + proposition.getId() + proposition.getDescription());
        }
        Map<String, Object> contextMap = new LinkedHashMap<String, Object>();
        for (PropositionParameter param : proposition.getParameters()) {
            if (param.getParameterType().equals(PropositionParameterType.TERM.getCode())) {
                if (param.getTermValue() != null) {
                    for (TermParameterDefinition termParam : param.getTermValue().getParameters()) {
                        contextMap.put(termParam.getName(), termParam.getValue());
                    }
                } else {
                    contextMap.put(param.getParameterType(), param.getValue());
                }
            } else {
                contextMap.put(param.getParameterType(), param.getValue());
            }
        }
        return contextMap;
    }
    public static final String COMPOUND_COMPONENTS = "compoundComponent";

    protected Map<String, Object> buildCompoundPropositionContextMap(String naturalLanguageUsageId, PropositionDefinition proposition, String languageCode) {
        if (!proposition.getPropositionTypeCode().equals(PropositionType.COMPOUND.getCode())) {
            throw new RiceIllegalArgumentException("proposition us not compound " + proposition.getPropositionTypeCode() + " " + proposition.getId() + proposition.getDescription());
        }
        Map<String, Object> contextMap = new LinkedHashMap<String, Object>();
        /*List<String> children = new ArrayList<String>();
         for (PropositionDefinition param : proposition.getCompoundComponents()) {
         children.add(this.translateNaturalLanguageForProposition(naturalLanguageUsageId, proposition, languageCode));
         }
         contextMap.put(COMPOUND_COMPONENTS, children);*/
        return contextMap;
    }

    protected String translateCompoundProposition(PropositionDefinition proposition, String naturalLanguageUsageId, String languageCode)
            throws RiceIllegalArgumentException {
        if (!proposition.getPropositionTypeCode().equals(PropositionType.COMPOUND.getCode())) {
            throw new RiceIllegalArgumentException("proposition us not compound " + proposition.getPropositionTypeCode() + " " + proposition.getId() + proposition.getDescription());
        }
        String compoundNaturalLanguageTypeId = this.calcCompoundNaturalLanguageTypeId(proposition.getCompoundOpCode());
        // TODO: make sure we cache the AND and OR templates
        NaturalLanguageTemplate template = this.ruleManagementService.findNaturalLanguageTemplateByLanguageCodeTypeIdAndNluId(languageCode,
                compoundNaturalLanguageTypeId, naturalLanguageUsageId);
        Map<String, Object> contextMap = this.buildCompoundPropositionContextMap(naturalLanguageUsageId, proposition, languageCode);
        return this.templater.translate(template, contextMap);

    }

    protected String calcCompoundNaturalLanguageTypeId(String compoundOpCode) throws RiceIllegalArgumentException {
        if (compoundOpCode.equals("a")) {
            return "kuali.compound.proposition.op.code." + "and";
        }
        if (compoundOpCode.equals("o")) {
            return "kuali.compound.proposition.op.code." + "or";
        }
        throw new RiceIllegalArgumentException("unsupported compound op code " + compoundOpCode);
    }

    protected String translateSimpleProposition(NaturalLanguageTemplate naturalLanguageTemplate,
            PropositionDefinition proposition)
            throws RiceIllegalArgumentException {
        if (!proposition.getPropositionTypeCode().equals(PropositionType.SIMPLE.getCode())) {
            throw new RiceIllegalArgumentException("proposition not simple " + proposition.getPropositionTypeCode() + " " + proposition.getId() + proposition.getDescription());
        }
        Map<String, Object> contextMap = this.buildSimplePropositionContextMap(proposition);
        return templater.translate(naturalLanguageTemplate, contextMap);
    }
}
