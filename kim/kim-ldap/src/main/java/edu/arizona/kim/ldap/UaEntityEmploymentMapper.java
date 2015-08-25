package edu.arizona.kim.ldap;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.kuali.rice.coreservice.api.parameter.Parameter;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kim.api.identity.CodedAttribute;
import org.kuali.rice.kim.api.identity.affiliation.EntityAffiliation;
import org.kuali.rice.kim.api.identity.employment.EntityEmployment;
import org.springframework.ldap.core.DirContextOperations;

import edu.arizona.kim.eds.UaEdsAffiliation;
import edu.arizona.kim.eds.UaEdsRecord;
import edu.arizona.kim.eds.UaEdsRecordFactory;

/**
 * Created by shaloo & kosta on 8/20/15.
 */
public class UaEntityEmploymentMapper extends UaBaseMapper<List<EntityEmployment>> {

	private static final Logger LOG = Logger.getLogger(UaEntityEmploymentMapper.class);

	private ParameterService parameterService;
	private UaEntityAffiliationMapper affiliationMapper;

	@Override
	List<EntityEmployment> mapDtoFromContext(DirContextOperations context) {
		List<EntityEmployment.Builder> builders = mapBuilderFromContext(context);
		List<EntityEmployment> employments = new ArrayList<EntityEmployment>();
		if (builders != null) {
			for (EntityEmployment.Builder builder : builders) {
				employments.add(builder.build());
			}
		}

		return employments;
	}

	List<EntityEmployment.Builder> mapBuilderFromContext(DirContextOperations context) {

		UaEdsRecord edsRecord = UaEdsRecordFactory.getEdsRecord(context);
		if (edsRecord == null) {
			LOG.debug("No active and valid EDS eduPersonAffiliation found for context: " + context.getAttributes());
			return null;
		}

		List<EntityEmployment.Builder> employments = new ArrayList<EntityEmployment.Builder>();

		int affId = 1; // The affiliation id is just its position in the ordered
						// set

		for (UaEdsAffiliation edsAff : edsRecord.getOrderedAffiliations()) {

			// Non-employees don't have employment info, like student or retiree
			Set<String> nonEmployeeAffs = getValueSetForParameter(getEdsConstants().getEdsNonEmployeeAffsParamKey());
			if (nonEmployeeAffs.contains(edsAff.getAffiliatonString())) {
				continue;
			}

			final EntityEmployment.Builder employmentInfo = EntityEmployment.Builder.create();

			if (affId == 1) {
				// this is the primary affiliation
				employmentInfo.setPrimary(true);
			} else {
				// This field defaults as 'true' when instantiated, so
				// explicitly set to false
				employmentInfo.setPrimary(false);
			}

			List<EntityAffiliation.Builder> affiliations = getAffiliationMapper().mapBuilderFromContext(context);
			// only map affiliation if it exists
			if (affiliations.size() > 0) {
				employmentInfo.setEntityAffiliation(affiliations.get(0));
			}

			employmentInfo.setEmploymentRecordId(Integer.toString(affId));
			employmentInfo.setEmployeeId(edsRecord.getEmplId());
			employmentInfo.setActive(edsAff.isActive());
			employmentInfo.setEmployeeStatus(CodedAttribute.Builder.create(edsAff.getStatusCode()));
			employmentInfo.setPrimaryDepartmentCode(getConstants().getDefaultChartCode() + " " + edsAff.getDeptCode());
			employmentInfo.setEmployeeType(CodedAttribute.Builder.create(edsAff.getEmployeeType()));

			employments.add(employmentInfo);
			affId++;
		}

		return employments;

	}

	private Set<String> getValueSetForParameter(String parameterKey) {
		String listAsCommaString = getStringForParameter(parameterKey);
		String[] listAsArray = listAsCommaString.split(getEdsConstants().getKfsParamDelimiter());
		Set<String> resultSet = new HashSet<String>();
		for (String result : listAsArray) {
			resultSet.add(result);
		}
		return resultSet;
	}

	private String getStringForParameter(String parameterKey) {
		String namespaceCode = getEdsConstants().getParameterNamespaceCode();
		String detailTypeCode = getEdsConstants().getParameterDetailTypeCode();
		Parameter parameter = getParameterService().getParameter(namespaceCode, detailTypeCode, parameterKey);
		if (parameter == null) {
			String message = String.format("ParameterService returned null for parameterKey: '%s', namespaceCode: '%s', detailTypeCode: '%s'", parameterKey, namespaceCode, detailTypeCode);
			throw new RuntimeException(message);
		}
		return parameter.getValue();
	}

	private ParameterService getParameterService() {
		return parameterService;
	}

	public void setParameterService(ParameterService parameterService) {
		this.parameterService = parameterService;
	}

	public final UaEntityAffiliationMapper getAffiliationMapper() {
		return this.affiliationMapper;
	}

	public void setAffiliationMapper(UaEntityAffiliationMapper entityAffiliationMapper) {
		this.affiliationMapper = entityAffiliationMapper;
	}

}
