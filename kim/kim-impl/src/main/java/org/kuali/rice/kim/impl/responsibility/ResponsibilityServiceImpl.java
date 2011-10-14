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
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader;
import org.kuali.rice.kim.api.common.delegate.DelegateType;
import org.kuali.rice.kim.api.common.template.Template;
import org.kuali.rice.kim.api.common.template.TemplateQueryResults;
import org.kuali.rice.kim.api.responsibility.Responsibility;
import org.kuali.rice.kim.api.responsibility.ResponsibilityAction;
import org.kuali.rice.kim.api.responsibility.ResponsibilityQueryResults;
import org.kuali.rice.kim.api.responsibility.ResponsibilityService;
import org.kuali.rice.kim.api.role.Role;
import org.kuali.rice.kim.api.role.RoleMembership;
import org.kuali.rice.kim.api.role.RoleResponsibilityAction;
import org.kuali.rice.kim.api.role.RoleService;
import org.kuali.rice.kim.api.type.KimType;
import org.kuali.rice.kim.api.type.KimTypeInfoService;
import org.kuali.rice.kim.framework.responsibility.ResponsibilityTypeService;
import org.kuali.rice.kim.impl.KIMPropertyConstants;
import org.kuali.rice.kim.impl.common.attribute.AttributeTransform;
import org.kuali.rice.kim.impl.common.attribute.KimAttributeDataBo;
import org.kuali.rice.kim.impl.role.RoleResponsibilityActionBo;
import org.kuali.rice.kim.impl.role.RoleResponsibilityBo;
import org.kuali.rice.krad.service.BusinessObjectService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.kuali.rice.core.api.criteria.PredicateFactory.*;

public class ResponsibilityServiceImpl implements ResponsibilityService {

    private static final Integer DEFAULT_PRIORITY_NUMBER = Integer.valueOf(1);
    private static final Log LOG = LogFactory.getLog(ResponsibilityServiceImpl.class);

    private BusinessObjectService businessObjectService;
    private CriteriaLookupService criteriaLookupService;
    private ResponsibilityTypeService defaultResponsibilityTypeService;
    private KimTypeInfoService kimTypeInfoService;
    private RoleService roleService;

    @Override
    public Responsibility createResponsibility(final Responsibility responsibility)
            throws RiceIllegalArgumentException, RiceIllegalStateException {
        incomingParamCheck(responsibility, "responsibility");

        if (StringUtils.isNotBlank(responsibility.getId()) && getResponsibility(responsibility.getId()) != null) {
            throw new RiceIllegalStateException("the responsibility to create already exists: " + responsibility);
        }
        List<ResponsibilityAttributeBo> attrBos = Collections.emptyList();
        if (responsibility.getTemplate() != null) {
            attrBos = KimAttributeDataBo.createFrom(ResponsibilityAttributeBo.class, responsibility.getAttributes(), responsibility.getTemplate().getKimTypeId());
        }
        ResponsibilityBo bo = ResponsibilityBo.from(responsibility);
        bo.setAttributeDetails(attrBos);
        return ResponsibilityBo.to(businessObjectService.save(bo));
    }

    @Override
    public Responsibility updateResponsibility(final Responsibility responsibility)
            throws RiceIllegalArgumentException, RiceIllegalStateException {
        incomingParamCheck(responsibility, "responsibility");

        if (StringUtils.isBlank(responsibility.getId()) || getResponsibility(responsibility.getId()) == null) {
            throw new RiceIllegalStateException("the responsibility does not exist: " + responsibility);
        }

       List<ResponsibilityAttributeBo> attrBos = Collections.emptyList();
        if (responsibility.getTemplate() != null) {
            attrBos = KimAttributeDataBo.createFrom(ResponsibilityAttributeBo.class, responsibility.getAttributes(), responsibility.getTemplate().getKimTypeId());
        }
        ResponsibilityBo bo = ResponsibilityBo.from(responsibility);

        if (bo.getAttributeDetails() != null) {
            bo.getAttributeDetails().clear();
            bo.setAttributeDetails(attrBos);
        }

        return ResponsibilityBo.to(businessObjectService.save(bo));
    }

    @Override
    public Responsibility getResponsibility(final String id) throws RiceIllegalArgumentException {
        incomingParamCheck(id, "id");

        return ResponsibilityBo.to(businessObjectService.findBySinglePrimaryKey(ResponsibilityBo.class, id));
    }

    @Override
    public Responsibility findRespByNamespaceCodeAndName(final String namespaceCode, final String name)
            throws RiceIllegalArgumentException {
        incomingParamCheck(namespaceCode, "namespaceCode");
        incomingParamCheck(name, "name");

        final Map<String, String> crit = new HashMap<String, String>();
        crit.put("namespaceCode", namespaceCode);
        crit.put("name", name);
        crit.put("active", "Y");

        final Collection<ResponsibilityBo> bos = businessObjectService.findMatching(ResponsibilityBo.class, Collections.unmodifiableMap(crit));

        if (bos != null) {
            if (bos.size() > 1) {
                throw new RiceIllegalStateException("more than one Responsibility found with namespace code: " + namespaceCode + " and name: " + name);
            }

            final Iterator<ResponsibilityBo> i = bos.iterator();
            return i.hasNext() ? ResponsibilityBo.to(i.next()) : null;
        }
        return null;
    }

    @Override
    public Template getResponsibilityTemplate(final String id) throws RiceIllegalArgumentException {
        incomingParamCheck(id, "id");

        return ResponsibilityTemplateBo.to(businessObjectService.findBySinglePrimaryKey(ResponsibilityTemplateBo.class, id));
    }

    @Override
    public Template findRespTemplateByNamespaceCodeAndName(final String namespaceCode, final String name) throws RiceIllegalArgumentException {
        incomingParamCheck(namespaceCode, "namespaceCode");
        incomingParamCheck(name, "name");

        final Map<String, String> crit = new HashMap<String, String>();
        crit.put("namespaceCode", namespaceCode);
        crit.put("name", name);
        crit.put("active", "Y");

        final Collection<ResponsibilityTemplateBo> bos = businessObjectService.findMatching(ResponsibilityTemplateBo.class, Collections.unmodifiableMap(crit));
        if (bos != null) {
            if (bos.size() > 1) {
                throw new RiceIllegalStateException("more than one Responsibility Template found with namespace code: " + namespaceCode + " and name: " + name);
            }

            final Iterator<ResponsibilityTemplateBo> i = bos.iterator();
            return i.hasNext() ? ResponsibilityTemplateBo.to(i.next()) : null;
        }
        return null;
    }

    @Override
    public boolean hasResponsibility(final String principalId, final String namespaceCode,
            final String respName, final Map<String, String> qualification,
            final Map<String, String> responsibilityDetails) throws RiceIllegalArgumentException {
        incomingParamCheck(principalId, "principalId");
        incomingParamCheck(namespaceCode, "namespaceCode");
        incomingParamCheck(respName, "respName");
        incomingParamCheck(qualification, "qualification");
        incomingParamCheck(responsibilityDetails, "responsibilityDetails");

        // get all the responsibility objects whose name match that requested
        final List<Responsibility> responsibilities = Collections.singletonList(findRespByNamespaceCodeAndName(namespaceCode, respName));
        return hasResp(principalId, namespaceCode, responsibilities, qualification, responsibilityDetails);
    }

    @Override
    public boolean hasResponsibilityByTemplateName(final String principalId, final String namespaceCode,
            final String respTemplateName, final Map<String, String> qualification,
            final Map<String, String> responsibilityDetails) throws RiceIllegalArgumentException {
        incomingParamCheck(principalId, "principalId");
        incomingParamCheck(namespaceCode, "namespaceCode");
        incomingParamCheck(respTemplateName, "respTemplateName");
        incomingParamCheck(qualification, "qualification");
        incomingParamCheck(responsibilityDetails, "responsibilityDetails");


        // get all the responsibility objects whose name match that requested
        final List<Responsibility> responsibilities = findRespsByNamespaceCodeAndTemplateName(namespaceCode, respTemplateName);
        return hasResp(principalId, namespaceCode, responsibilities, qualification, responsibilityDetails);
    }

    private boolean hasResp(final String principalId, final String namespaceCode,
            final List<Responsibility> responsibilities, final Map<String, String> qualification,
            final Map<String, String> responsibilityDetails) throws RiceIllegalArgumentException {
        // now, filter the full list by the detail passed
        final List<String> ids = new ArrayList<String>();
        for (Responsibility r : getMatchingResponsibilities(responsibilities, responsibilityDetails)) {
            ids.add(r.getId());
        }
        final List<String> roleIds = getRoleIdsForResponsibilities(ids, qualification);
        return roleService.principalHasRole(principalId, roleIds, qualification);
    }

    @Override
    public List<ResponsibilityAction> getResponsibilityActions(final String namespaceCode,
            final String responsibilityName, final Map<String, String> qualification,
            final Map<String, String> responsibilityDetails) throws RiceIllegalArgumentException {
        incomingParamCheck(namespaceCode, "namespaceCode");
        incomingParamCheck(responsibilityName, "responsibilityName");
        incomingParamCheck(qualification, "qualification");
        incomingParamCheck(responsibilityDetails, "responsibilityDetails");

        // get all the responsibility objects whose name match that requested
        List<Responsibility> responsibilities = Collections.singletonList(findRespByNamespaceCodeAndName(namespaceCode, responsibilityName));
        return getRespActions(namespaceCode, responsibilities, qualification, responsibilityDetails);
    }

    @Override
    public List<ResponsibilityAction> getResponsibilityActionsByTemplateName(final String namespaceCode,
            final String respTemplateName, final Map<String, String> qualification,
            final Map<String, String> responsibilityDetails) throws RiceIllegalArgumentException {
        incomingParamCheck(namespaceCode, "namespaceCode");
        incomingParamCheck(respTemplateName, "respTemplateName");
        incomingParamCheck(qualification, "qualification");
        incomingParamCheck(responsibilityDetails, "responsibilityDetails");

        // get all the responsibility objects whose name match that requested
        List<Responsibility> responsibilities = findRespsByNamespaceCodeAndTemplateName(namespaceCode, respTemplateName);
        return getRespActions(namespaceCode, responsibilities, qualification, responsibilityDetails);
    }

    private List<ResponsibilityAction> getRespActions(final String namespaceCode, final List<Responsibility> responsibilities, final Map<String, String> qualification, final Map<String, String> responsibilityDetails) {
        // now, filter the full list by the detail passed
        List<Responsibility> applicableResponsibilities = getMatchingResponsibilities(responsibilities, responsibilityDetails);
        List<ResponsibilityAction> results = new ArrayList<ResponsibilityAction>();
        for (Responsibility r : applicableResponsibilities) {
            List<String> roleIds = getRoleIdsForResponsibility(r.getId(), qualification);
            results.addAll(getActionsForResponsibilityRoles(r, roleIds, qualification));
        }
        return results;
    }

    private List<ResponsibilityAction> getActionsForResponsibilityRoles(Responsibility responsibility, List<String> roleIds, Map<String, String> qualification) {
        List<ResponsibilityAction> results = new ArrayList<ResponsibilityAction>();
        Collection<RoleMembership> roleMembers = roleService.getRoleMembers(roleIds,qualification);
        for (RoleMembership rm : roleMembers) {
            // only add them to the list if the member ID has been populated
            if (StringUtils.isNotBlank(rm.getMemberId())) {
                final ResponsibilityAction.Builder rai = ResponsibilityAction.Builder.create();
                rai.setMemberRoleId((rm.getEmbeddedRoleId() == null) ? rm.getRoleId() : rm.getEmbeddedRoleId());
                rai.setRoleId(rm.getRoleId());
                rai.setQualifier(rm.getQualifier());
                final List<DelegateType.Builder> bs = new ArrayList<DelegateType.Builder>();
                for (DelegateType d : rm.getDelegates()) {
                    bs.add(DelegateType.Builder.create(d));
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
        return Collections.unmodifiableList(results);
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
    public List<String> getRoleIdsForResponsibility(String id, Map<String, String> qualification) throws RiceIllegalArgumentException {
        incomingParamCheck(id, "id");
        incomingParamCheck(qualification, "qualification");

        final List<String> roleIds = getRoleIdsForPredicate(and(equal("responsibilityId", id), equal("active", "Y")));

        //TODO filter with qualifiers
        return Collections.unmodifiableList(roleIds);
    }

    @Override
    public ResponsibilityQueryResults findResponsibilities(final QueryByCriteria queryByCriteria) throws RiceIllegalArgumentException {
        incomingParamCheck(queryByCriteria, "queryByCriteria");

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
    public TemplateQueryResults findResponsibilityTemplates(final QueryByCriteria queryByCriteria) throws RiceIllegalArgumentException {
        incomingParamCheck(queryByCriteria, "queryByCriteria");

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
    private List<Responsibility> getMatchingResponsibilities(List<Responsibility> responsibilities, Map<String, String> responsibilityDetails) {
        // if no details passed, assume that all match
        if (responsibilityDetails == null || responsibilityDetails.isEmpty()) {
            return responsibilities;
        }

        final List<Responsibility> applicableResponsibilities = new ArrayList<Responsibility>();
        // otherwise, attempt to match the permission details
        // build a map of the template IDs to the type services
        Map<String, ResponsibilityTypeService> responsibilityTypeServices = getResponsibilityTypeServicesByTemplateId(responsibilities);
        // build a map of permissions by template ID
        Map<String, List<Responsibility>> responsibilityMap = groupResponsibilitiesByTemplate(responsibilities);
        // loop over the different templates, matching all of the same template against the type
        // service at once
        for (Map.Entry<String, List<Responsibility>> respEntry : responsibilityMap.entrySet()) {
            ResponsibilityTypeService responsibilityTypeService = responsibilityTypeServices.get(respEntry.getKey());
            List<Responsibility> responsibilityInfos = respEntry.getValue();
            if (responsibilityTypeService == null) {
                responsibilityTypeService = defaultResponsibilityTypeService;
            }
            applicableResponsibilities.addAll(responsibilityTypeService.getMatchingResponsibilities(responsibilityDetails, responsibilityInfos));
        }
        return Collections.unmodifiableList(applicableResponsibilities);
    }

    private Map<String, ResponsibilityTypeService> getResponsibilityTypeServicesByTemplateId(Collection<Responsibility> responsibilities) {
        Map<String, ResponsibilityTypeService> responsibilityTypeServices = new HashMap<String, ResponsibilityTypeService>(responsibilities.size());
        for (Responsibility responsibility : responsibilities) {
            final Template t = responsibility.getTemplate();
            final KimType type = kimTypeInfoService.getKimType(t.getKimTypeId());

            final String serviceName = type.getServiceName();
            if (serviceName != null) {
                ResponsibilityTypeService responsibiltyTypeService = GlobalResourceLoader.getService(serviceName);
                if (responsibiltyTypeService != null) {
                    responsibilityTypeServices.put(responsibility.getTemplate().getId(), responsibiltyTypeService);
                } else {
                    responsibilityTypeServices.put(responsibility.getTemplate().getId(), defaultResponsibilityTypeService);
                }
            }
        }
        return Collections.unmodifiableMap(responsibilityTypeServices);
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
        return Collections.unmodifiableMap(results);
    }

    private List<String> getRoleIdsForResponsibilities(Collection<String> ids, Map<String, String> qualification) {
        final List<String> roleIds = getRoleIdsForPredicate(and(in("responsibilityId", ids.toArray()), equal("active", "Y")));

        //TODO filter with qualifiers
        return Collections.unmodifiableList(roleIds);
    }

    private List<String> getRoleIdsForPredicate(Predicate p) {
        final QueryByCriteria.Builder builder = QueryByCriteria.Builder.create();
        builder.setPredicates(p);
        final GenericQueryResults<RoleResponsibilityBo> qr = criteriaLookupService.lookup(RoleResponsibilityBo.class, builder.build());

        final List<String> roleIds = new ArrayList<String>();
        for (RoleResponsibilityBo bo : qr.getResults()) {
            roleIds.add(bo.getRoleId());
        }
        return Collections.unmodifiableList(roleIds);
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
        crit.put("template.name", templateName);
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

    public void setDefaultResponsibilityTypeService(final ResponsibilityTypeService defaultResponsibilityTypeService) {
        this.defaultResponsibilityTypeService = defaultResponsibilityTypeService;
    }

    public void setKimTypeInfoService(final KimTypeInfoService kimTypeInfoService) {
        this.kimTypeInfoService = kimTypeInfoService;
    }

    public void setRoleService(final RoleService roleService) {
        this.roleService = roleService;
    }

    private void incomingParamCheck(Object object, String name) {
        if (object == null) {
            throw new RiceIllegalArgumentException(name + " was null");
        } else if (object instanceof String
                && StringUtils.isBlank((String) object)) {
            throw new RiceIllegalArgumentException(name + " was blank");
        }
    }
}
