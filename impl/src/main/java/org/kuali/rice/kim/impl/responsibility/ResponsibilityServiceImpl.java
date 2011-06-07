package org.kuali.rice.kim.impl.responsibility;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.rice.core.api.criteria.CriteriaLookupService;
import org.kuali.rice.core.api.criteria.GenericQueryResults;
import org.kuali.rice.core.api.criteria.LookupCustomizer;
import org.kuali.rice.core.api.criteria.Predicate;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;
import org.kuali.rice.core.api.exception.RiceIllegalStateException;
import org.kuali.rice.core.api.mo.common.Attributes;
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.core.util.AttributeSet;
import org.kuali.rice.kim.impl.common.attribute.AttributeTransform;
import org.kuali.rice.kim.api.common.delegate.Delegate;
import org.kuali.rice.kim.api.common.template.Template;
import org.kuali.rice.kim.api.common.template.TemplateQueryResults;
import org.kuali.rice.kim.api.responsibility.Responsibility;
import org.kuali.rice.kim.api.responsibility.ResponsibilityAction;
import org.kuali.rice.kim.api.responsibility.ResponsibilityQueryResults;
import org.kuali.rice.kim.api.responsibility.ResponsibilityService;
import org.kuali.rice.kim.api.role.Role;
import org.kuali.rice.kim.api.role.RoleResponsibilityAction;
import org.kuali.rice.kim.api.type.KimType;
import org.kuali.rice.kim.api.type.KimTypeInfoService;
import org.kuali.rice.kim.bo.role.dto.DelegateInfo;
import org.kuali.rice.kim.bo.role.dto.RoleMembershipInfo;
import org.kuali.rice.kim.impl.common.attribute.KimAttributeDataBo;
import org.kuali.rice.kim.impl.role.RoleResponsibilityActionBo;
import org.kuali.rice.kim.impl.role.RoleResponsibilityBo;
import org.kuali.rice.kim.service.RoleService;
import org.kuali.rice.kim.util.KIMPropertyConstants;
import org.kuali.rice.kns.service.BusinessObjectService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.kuali.rice.core.api.criteria.PredicateFactory.*;

public class ResponsibilityServiceImpl implements ResponsibilityService {

    private static final Integer DEFAULT_PRIORITY_NUMBER = Integer.valueOf(1);
    private static final Log LOG = LogFactory.getLog(ResponsibilityServiceImpl.class);

    private BusinessObjectService businessObjectService;
    private CriteriaLookupService criteriaLookupService;
    private KimResponsibilityTypeService defaultResponsibilityTypeService;
    private KimTypeInfoService kimTypeInfoService;
    private RoleService roleService;

    @Override
    public void createResponsibility(final Responsibility responsibility) throws RiceIllegalArgumentException, RiceIllegalStateException {
        if (responsibility == null) {
            throw new RiceIllegalArgumentException("responsibility is null");
        }

        if (StringUtils.isNotBlank(responsibility.getId()) && getResponsibility(responsibility.getId()) != null) {
            throw new RiceIllegalStateException("the responsibility to create already exists: " + responsibility);
        }
        List<ResponsibilityAttributeBo> attrBos = KimAttributeDataBo.createFrom(ResponsibilityAttributeBo.class, responsibility.getAttributes(), responsibility.getTemplate().getKimTypeId());
        ResponsibilityBo bo = ResponsibilityBo.from(responsibility);
        bo.setAttributeDetails(attrBos);
        businessObjectService.save(bo);
    }

    @Override
    public void updateResponsibility(final Responsibility responsibility) throws RiceIllegalArgumentException, RiceIllegalStateException {
        if (responsibility == null) {
            throw new RiceIllegalArgumentException("responsibility is null");
        }

        if (StringUtils.isBlank(responsibility.getId()) || getResponsibility(responsibility.getId()) == null) {
            throw new RiceIllegalStateException("the responsibility does not exist: " + responsibility);
        }

        List<ResponsibilityAttributeBo> attrBos = KimAttributeDataBo.createFrom(ResponsibilityAttributeBo.class, responsibility.getAttributes(), responsibility.getTemplate().getKimTypeId());
        ResponsibilityBo bo = ResponsibilityBo.from(responsibility);
        bo.getAttributeDetails().addAll(attrBos);
        businessObjectService.save(bo);
    }

    @Override
    public Responsibility getResponsibility(final String id) {
        if (id == null) {
            throw new RiceIllegalArgumentException("id is null");
        }

        return ResponsibilityBo.to(businessObjectService.findBySinglePrimaryKey(ResponsibilityBo.class, id));
    }

    @Override
    public List<Responsibility> findRespsByNamespaceCodeAndName(final String namespaceCode, final String name) {
        if (namespaceCode == null) {
            throw new RiceIllegalArgumentException("namespaceCode is null");
        }

        if (name == null) {
            throw new RiceIllegalArgumentException("name is null");
        }

        final Map<String, String> crit = new HashMap<String, String>();
        crit.put("namespaceCode", namespaceCode);
        crit.put("name", name);
        crit.put("active", "Y");

        final Collection<ResponsibilityBo> bos = businessObjectService.findMatching(ResponsibilityBo.class, Collections.unmodifiableMap(crit));
        final List<Responsibility> ims = new ArrayList<Responsibility>();
        if (bos != null) {
            for (ResponsibilityBo bo : bos) {
                if (bo != null) {
                    ims.add(ResponsibilityBo.to(bo));
                }
            }
        }

        return Collections.unmodifiableList(ims);
    }

    @Override
    public Template getResponsibilityTemplate(final String id) {
        if (id == null) {
            throw new RiceIllegalArgumentException("id is null");
        }

        return ResponsibilityTemplateBo.to(businessObjectService.findBySinglePrimaryKey(ResponsibilityTemplateBo.class, id));
    }

    @Override
    public List<Template> findRespTemplatesByNamespaceCodeAndName(final String namespaceCode, final String name) {
        if (namespaceCode == null) {
            throw new RiceIllegalArgumentException("namespaceCode is null");
        }

        if (name == null) {
            throw new RiceIllegalArgumentException("name is null");
        }

        final Map<String, String> crit = new HashMap<String, String>();
        crit.put("namespaceCode", namespaceCode);
        crit.put("name", name);
        crit.put("active", "Y");

        final Collection<ResponsibilityTemplateBo> bos = businessObjectService.findMatching(ResponsibilityTemplateBo.class, Collections.unmodifiableMap(crit));
        final List<Template> ims = new ArrayList<Template>();
        if (bos != null) {
            for (ResponsibilityTemplateBo bo : bos) {
                if (bo != null) {
                    ims.add(ResponsibilityTemplateBo.to(bo));
                }
            }
        }

        return Collections.unmodifiableList(ims);
    }

    @Override
    public boolean hasResponsibility(final String principalId, final String namespaceCode, final String respName, final Attributes qualification, final Attributes responsibilityDetails) {
        // get all the responsibility objects whose name match that requested
        final List<Responsibility> responsibilities = findRespsByNamespaceCodeAndName(namespaceCode, respName);
        return hasResp(principalId, namespaceCode, responsibilities, qualification, responsibilityDetails);
    }

    @Override
    public boolean hasResponsibilityByTemplateName(final String principalId, final String namespaceCode, final String respTemplateName, final Attributes qualification, final Attributes responsibilityDetails) {
        // get all the responsibility objects whose name match that requested
        final List<Responsibility> responsibilities = findRespsByNamespaceCodeAndTemplateName(namespaceCode, respTemplateName);
        return hasResp(principalId, namespaceCode, responsibilities, qualification, responsibilityDetails);
    }

    private boolean hasResp(final String principalId, final String namespaceCode, final List<Responsibility> responsibilities, final Attributes qualification, final Attributes responsibilityDetails) {
        // now, filter the full list by the detail passed
        final List<String> ids = new ArrayList<String>();
        for (Responsibility r : getMatchingResponsibilities(responsibilities, responsibilityDetails)) {
            ids.add(r.getId());
        }
        final List<String> roleIds = getRoleIdsForResponsibilities(ids, qualification);
        return roleService.principalHasRole(principalId, roleIds, new AttributeSet(qualification.toMap()));
    }

    @Override
    public List<ResponsibilityAction> getResponsibilityActions(final String namespaceCode, final String responsibilityName, final Attributes qualification, final Attributes responsibilityDetails) {
        // get all the responsibility objects whose name match that requested
        List<Responsibility> responsibilities = findRespsByNamespaceCodeAndName(namespaceCode, responsibilityName);
        return getRespActions(namespaceCode, responsibilities, qualification, responsibilityDetails);
    }

    @Override
    public List<ResponsibilityAction> getResponsibilityActionsByTemplateName(final String namespaceCode, final String respTemplateName, final Attributes qualification, final Attributes responsibilityDetails) {
        // get all the responsibility objects whose name match that requested
        List<Responsibility> responsibilities = findRespsByNamespaceCodeAndTemplateName(namespaceCode, respTemplateName);
        return getRespActions(namespaceCode, responsibilities, qualification, responsibilityDetails);
    }

    private List<ResponsibilityAction> getRespActions(final String namespaceCode, final List<Responsibility> responsibilities, final Attributes qualification, final Attributes responsibilityDetails) {
        // now, filter the full list by the detail passed
        List<Responsibility> applicableResponsibilities = getMatchingResponsibilities(responsibilities, responsibilityDetails);
        List<ResponsibilityAction> results = new ArrayList<ResponsibilityAction>();
        for (Responsibility r : applicableResponsibilities) {
            List<String> roleIds = getRoleIdsForResponsibility(r.getId(), qualification);
            results.addAll(getActionsForResponsibilityRoles(r, roleIds, qualification));
        }
        return results;
    }

    private List<ResponsibilityAction> getActionsForResponsibilityRoles(Responsibility responsibility, List<String> roleIds, Attributes qualification) {
        List<ResponsibilityAction> results = new ArrayList<ResponsibilityAction>();
        Collection<RoleMembershipInfo> roleMembers = roleService.getRoleMembers(roleIds, new AttributeSet(qualification.toMap()));
        for (RoleMembershipInfo rm : roleMembers) {
            // only add them to the list if the member ID has been populated
            if (StringUtils.isNotBlank(rm.getMemberId())) {
                final ResponsibilityAction.Builder rai = ResponsibilityAction.Builder.create();
                rai.setMemberRoleId(rm.getEmbeddedRoleId());
                rai.setRoleId(rm.getRoleId());
                rai.setQualifier(Attributes.fromMap(rm.getQualifier()));
                final List<Delegate.Builder> bs = new ArrayList<Delegate.Builder>();
                for (DelegateInfo d : rm.getDelegates()) {
                    Delegate.Builder newD = Delegate.Builder.create(d.getDelegationId(), d.getDelegationTypeCode(), d.getMemberId(), d.getMemberTypeCode(), d.getRoleMemberId(), d.getQualifier());
                    bs.add(newD);
                }
                rai.setDelegates(bs);
                rai.setResponsibilityId(responsibility.getId());
                rai.setResponsibilityName(responsibility.getName());
                rai.setResponsibilityNamespaceCode(responsibility.getNamespaceCode());

                if (rm.getMemberTypeCode().equals(Role.PRINCIPAL_MEMBER_TYPE)) {
                    rai.setPrincipalId(rm.getMemberId());
                } else {
                    rai.setGroupId(rm.getMemberId());
                }
                // get associated resp resolution objects
                RoleResponsibilityAction action = getResponsibilityAction(rm.getRoleId(), responsibility.getId(), rm.getRoleMemberId());
                if (action == null) {
                    LOG.error("Unable to get responsibility action record for role/responsibility/roleMember: "
                            + rm.getRoleId() + "/" + responsibility.getId() + "/" + rm.getRoleMemberId());
                    LOG.error("Skipping this role member in getActionsForResponsibilityRoles()");
                    continue;
                }
                // add the data to the ResponsibilityActionInfo objects
                rai.setActionTypeCode(action.getActionTypeCode());
                rai.setActionPolicyCode(action.getActionPolicyCode());
                rai.setPriorityNumber(action.getPriorityNumber() == null ? DEFAULT_PRIORITY_NUMBER : action.getPriorityNumber());
                rai.setForceAction(action.isForceAction());
                rai.setParallelRoutingGroupingCode((rm.getRoleSortingCode() == null) ? "" : rm.getRoleSortingCode());
                rai.setRoleResponsibilityActionId(action.getId());
                results.add(rai.build());
            }
        }
        return results;
    }

    private RoleResponsibilityAction getResponsibilityAction(String roleId, String responsibilityId, String roleMemberId) {
        final Predicate p =
                or(
                        and(
                                equal("roleResponsibility.responsibilityId", responsibilityId),
                                equal("roleResponsibility.roleId", roleId),
                                equal("roleResponsibility.active", "Y"),
                                or(
                                        equal(KIMPropertyConstants.RoleMember.ROLE_MEMBER_ID, roleMemberId),
                                        equal(KIMPropertyConstants.RoleMember.ROLE_MEMBER_ID, "*")
                                )
                        ),
                        and(
                                equal("roleResponsibilityId", "*"),
                                equal(KIMPropertyConstants.RoleMember.ROLE_MEMBER_ID, roleMemberId)
                        )
                );

        final QueryByCriteria.Builder builder = QueryByCriteria.Builder.create();
        builder.setPredicates(p);
        final GenericQueryResults<RoleResponsibilityActionBo> results = criteriaLookupService.lookup(RoleResponsibilityActionBo.class, builder.build());
        final List<RoleResponsibilityActionBo> bos = results.getResults();
        //seems a little dubious that we are just returning the first result...
        return !bos.isEmpty() ? RoleResponsibilityActionBo.to(bos.get(0)) : null;
    }

    @Override
    public List<String> getRoleIdsForResponsibility(String id, Attributes qualification) {
        if (StringUtils.isBlank(id)) {
            throw new RiceIllegalArgumentException("id is blank");
        }

        if (qualification == null) {
            throw new RiceIllegalArgumentException("qualification is null");
        }

        final List<String> roleIds = getRoleIdsForPredicate(and(equal("responsibilityId", id), equal("active", "Y")));

        //TODO filter with qualifiers
        return roleIds;
    }

    @Override
    public ResponsibilityQueryResults findResponsibilities(final QueryByCriteria queryByCriteria) {
        if (queryByCriteria == null) {
            throw new RiceIllegalArgumentException("queryByCriteria is null");
        }

        LookupCustomizer.Builder<ResponsibilityBo> lc = LookupCustomizer.Builder.create();
        lc.setPredicateTransform(AttributeTransform.getInstance());

        GenericQueryResults<ResponsibilityBo> results = criteriaLookupService.lookup(ResponsibilityBo.class, queryByCriteria, lc.build());

        ResponsibilityQueryResults.Builder builder = ResponsibilityQueryResults.Builder.create();
        builder.setMoreResultsAvailable(results.isMoreResultsAvailable());
        builder.setTotalRowCount(results.getTotalRowCount());

        final List<Responsibility.Builder> ims = new ArrayList<Responsibility.Builder>();
        for (ResponsibilityBo bo : results.getResults()) {
            ims.add(Responsibility.Builder.create(bo));
        }

        builder.setResults(ims);
        return builder.build();
    }

    @Override
    public TemplateQueryResults findResponsibilityTemplates(final QueryByCriteria queryByCriteria) {
        if (queryByCriteria == null) {
            throw new RiceIllegalArgumentException("queryByCriteria is null");
        }

        GenericQueryResults<ResponsibilityTemplateBo> results = criteriaLookupService.lookup(ResponsibilityTemplateBo.class, queryByCriteria);

        TemplateQueryResults.Builder builder = TemplateQueryResults.Builder.create();
        builder.setMoreResultsAvailable(results.isMoreResultsAvailable());
        builder.setTotalRowCount(results.getTotalRowCount());

        final List<Template.Builder> ims = new ArrayList<Template.Builder>();
        for (ResponsibilityTemplateBo bo : results.getResults()) {
            ims.add(Template.Builder.create(bo));
        }

        builder.setResults(ims);
        return builder.build();
    }

    /**
     * Compare each of the passed in responsibilities with the given responsibilityDetails.  Those that
     * match are added to the result list.
     */
    private List<Responsibility> getMatchingResponsibilities(List<Responsibility> responsibilities, Attributes responsibilityDetails) {
        // if no details passed, assume that all match
        if (responsibilityDetails == null || responsibilityDetails.isEmpty()) {
            return responsibilities;
        }

        final List<Responsibility> applicableResponsibilities = new ArrayList<Responsibility>();
        // otherwise, attempt to match the permission details
        // build a map of the template IDs to the type services
        Map<String, KimResponsibilityTypeService> responsibilityTypeServices = getResponsibilityTypeServicesByTemplateId(responsibilities);
        // build a map of permissions by template ID
        Map<String, List<Responsibility>> responsibilityMap = groupResponsibilitiesByTemplate(responsibilities);
        // loop over the different templates, matching all of the same template against the type
        // service at once
        for (Map.Entry<String, List<Responsibility>> respEntry : responsibilityMap.entrySet()) {
            KimResponsibilityTypeService responsibilityTypeService = responsibilityTypeServices.get(respEntry.getKey());
            List<Responsibility> responsibilityInfos = respEntry.getValue();
            if (responsibilityTypeService == null) {
                responsibilityTypeService = defaultResponsibilityTypeService;
            }
            applicableResponsibilities.addAll(responsibilityTypeService.getMatchingResponsibilities(new AttributeSet(responsibilityDetails.toMap()), responsibilityInfos));
        }
        return applicableResponsibilities;
    }

    private Map<String, KimResponsibilityTypeService> getResponsibilityTypeServicesByTemplateId(Collection<Responsibility> responsibilities) {
        Map<String, KimResponsibilityTypeService> responsibilityTypeServices = new HashMap<String, KimResponsibilityTypeService>(responsibilities.size());
        for (Responsibility responsibility : responsibilities) {
            final Template t = responsibility.getTemplate();
            final KimType type = kimTypeInfoService.getKimType(t.getKimTypeId());

            final String serviceName = type.getServiceName();
            if (serviceName != null) {
                KimResponsibilityTypeService responsibiltyTypeService = GlobalResourceLoader.getService(serviceName);
                if (responsibiltyTypeService != null) {
                    responsibilityTypeServices.put(responsibility.getTemplate().getId(), responsibiltyTypeService);
                } else {
                    responsibilityTypeServices.put(responsibility.getTemplate().getId(), defaultResponsibilityTypeService);
                }
            }
        }
        return responsibilityTypeServices;
    }

    private Map<String, List<Responsibility>> groupResponsibilitiesByTemplate(Collection<Responsibility> responsibilities) {
        final Map<String, List<Responsibility>> results = new HashMap<String, List<Responsibility>>();
        for (Responsibility responsibility : responsibilities) {
            List<Responsibility> responsibilityInfos = results.get(responsibility.getTemplate().getId());
            if (responsibilityInfos == null) {
                responsibilityInfos = new ArrayList<Responsibility>();
                results.put(responsibility.getTemplate().getId(), responsibilityInfos);
            }
            responsibilityInfos.add(responsibility);
        }
        return results;
    }

    private List<String> getRoleIdsForResponsibilities(Collection<String> ids, Attributes qualification) {
        final List<String> roleIds = getRoleIdsForPredicate(and(in("responsibilityId", ids.toArray()), equal("active", "Y")));

        //TODO filter with qualifiers
        return roleIds;
    }

    private List<String> getRoleIdsForPredicate(Predicate p) {
        final QueryByCriteria.Builder builder = QueryByCriteria.Builder.create();
        builder.setPredicates(p);
        final GenericQueryResults<RoleResponsibilityBo> qr = criteriaLookupService.lookup(RoleResponsibilityBo.class, builder.build());

        final List<String> roleIds = new ArrayList<String>();
        for (RoleResponsibilityBo bo : qr.getResults()) {
            roleIds.add(bo.getRoleId());
        }
        return roleIds;
    }

    private List<Responsibility> findRespsByNamespaceCodeAndTemplateName(final String namespaceCode, final String templateName) {
        if (namespaceCode == null) {
            throw new RiceIllegalArgumentException("namespaceCode is null");
        }

        if (templateName == null) {
            throw new RiceIllegalArgumentException("name is null");
        }

        final Map<String, String> crit = new HashMap<String, String>();
        crit.put("namespaceCode", namespaceCode);
        crit.put("name", templateName);
        crit.put("active", "Y");

        final Collection<ResponsibilityBo> bos = businessObjectService.findMatching(ResponsibilityBo.class, Collections.unmodifiableMap(crit));
        final List<Responsibility> ims = new ArrayList<Responsibility>();
        if (bos != null) {
            for (ResponsibilityBo bo : bos) {
                if (bo != null) {
                    ims.add(ResponsibilityBo.to(bo));
                }
            }
        }

        return Collections.unmodifiableList(ims);
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public void setCriteriaLookupService(final CriteriaLookupService criteriaLookupService) {
        this.criteriaLookupService = criteriaLookupService;
    }

    public void setDefaultResponsibilityTypeService(final KimResponsibilityTypeService defaultResponsibilityTypeService) {
        this.defaultResponsibilityTypeService = defaultResponsibilityTypeService;
    }

    public void setKimTypeInfoService(final KimTypeInfoService kimTypeInfoService) {
        this.kimTypeInfoService = kimTypeInfoService;
    }

    public void setRoleService(final RoleService roleService) {
        this.roleService = roleService;
    }
}
