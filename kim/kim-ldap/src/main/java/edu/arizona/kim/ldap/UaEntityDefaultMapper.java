package edu.arizona.kim.ldap;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.kim.api.identity.affiliation.EntityAffiliation;
import org.kuali.rice.kim.api.identity.employment.EntityEmployment;
import org.kuali.rice.kim.api.identity.entity.EntityDefault;
import org.kuali.rice.kim.api.identity.external.EntityExternalIdentifier;
import org.kuali.rice.kim.api.identity.principal.Principal;
import org.kuali.rice.kim.api.identity.type.EntityTypeContactInfoDefault;
import org.kuali.rice.kim.impl.KIMPropertyConstants;
import org.kuali.rice.kim.ldap.InvalidLdapEntityException;
import org.springframework.ldap.core.DirContextOperations;

import edu.arizona.kim.eds.UaEdsRecord;
import edu.arizona.kim.eds.UaEdsRecordFactory;

/**
 * Created by shaloo & kosta on 8/20/15.
 */
public class UaEntityDefaultMapper extends UaBaseMapper<EntityDefault> {

	private static final Logger LOG = Logger.getLogger(UaEntityDefaultMapper.class);

	private UaEntityAffiliationMapper affiliationMapper;
	private UaEntityTypeContactInfoDefaultMapper entityTypeContactInfoDefaultMapper;
	private UaEntityNameMapper defaultNameMapper;
	private UaEntityEmploymentMapper employmentMapper;

	@Override
	EntityDefault mapDtoFromContext(DirContextOperations context) {
		EntityDefault.Builder builder = mapBuilderFromContext(context);
		return builder != null ? builder.build() : null;
	}

	EntityDefault.Builder mapBuilderFromContext(DirContextOperations context) {

		UaEdsRecord edsRecord = UaEdsRecordFactory.getEdsRecord(context);
		if (edsRecord == null) {
			LOG.debug("No active and valid EDS eduPersonAffiliation found for context: " + context.getAttributes());
			return null;
		}

		final String entityId = edsRecord.getUaId();
		final String principalName = edsRecord.getUid();

		final EntityDefault.Builder person = EntityDefault.Builder.create(entityId);

		if (entityId == null) {
			throw new InvalidLdapEntityException("LDAP Search Results yielded an invalid result with attributes " + context.getAttributes());
		}

		person.setAffiliations(new ArrayList<EntityAffiliation.Builder>());
		person.setExternalIdentifiers(new ArrayList<EntityExternalIdentifier.Builder>());

		person.setAffiliations(getAffiliationMapper().mapBuilderFromContext(context));
		person.setDefaultAffiliation(person.getAffiliations().get(0));

		person.setEntityTypeContactInfos(new ArrayList<EntityTypeContactInfoDefault.Builder>());
		person.getEntityTypeContactInfos().add(getEntityTypeContactInfoDefaultMapper().mapBuilderFromContext(context));

		person.setName(getDefaultNameMapper().mapBuilderFromContext(context));
		person.setEntityId(entityId);

		// this is a hack to get the employee id passed for inactive employees
		List<EntityEmployment.Builder> employmentInfos = getEmploymentMapper().mapBuilderFromContext(context);
		EntityEmployment.Builder primaryEmployment = (employmentInfos != null && employmentInfos.size() > 0) ? employmentInfos.get(0) : null;

		if (employmentInfos == null || employmentInfos.isEmpty()) {
			if (StringUtils.isNotEmpty(edsRecord.getEmplId())) {
				final EntityExternalIdentifier.Builder extid = EntityExternalIdentifier.Builder.create();
				extid.setExternalIdentifierTypeCode(KIMPropertyConstants.Person.EMPLOYEE_ID);
				extid.setExternalId(edsRecord.getEmplId());
				person.getExternalIdentifiers().add(extid);
			}
		}
		person.setEmployment(primaryEmployment);

		person.setEntityId(entityId);
		person.setPrincipals(new ArrayList<Principal.Builder>());
		person.setActive(edsRecord.isActive());

		// **AZ UPGRADE 3.0-6.0** - don't add principal with no name - causes
		// exception
		if (StringUtils.isNotBlank(principalName)) {
			final Principal.Builder defaultPrincipal = Principal.Builder.create(principalName);
			defaultPrincipal.setPrincipalId(entityId);
			defaultPrincipal.setEntityId(entityId);
			defaultPrincipal.setActive(true);
			person.getPrincipals().add(defaultPrincipal);
		}

		final Principal.Builder defaultPrincipal = Principal.Builder.create(edsRecord.getUaId());

		defaultPrincipal.setPrincipalId(entityId);
		defaultPrincipal.setEntityId(entityId);
		if ( principalName != null ) {
			defaultPrincipal.setPrincipalName(principalName);
		}

		List<Principal.Builder> entityPrincipals = person.getPrincipals();
		entityPrincipals.add(defaultPrincipal);

		final EntityExternalIdentifier.Builder externalId = EntityExternalIdentifier.Builder.create();
		externalId.setExternalIdentifierTypeCode(getConstants().getTaxExternalIdTypeCode());
		externalId.setExternalId(entityId);
		person.getExternalIdentifiers().add(externalId);

		if (LOG.isDebugEnabled()) {
			LOG.debug("Finished mapping person with name " + person.getName().getLastName());
		}

		return person;
	}

	/**
	 * Gets the value of affiliationMapper
	 *
	 * @return the value of affiliationMapper
	 */
	public final UaEntityAffiliationMapper getAffiliationMapper() {
		return this.affiliationMapper;
	}

	/**
	 * Sets the value of affiliationMapper
	 *
	 * @param argAffiliationMapper
	 *            Value to assign to this.affiliationMapper
	 */
	public final void setAffiliationMapper(final UaEntityAffiliationMapper argAffiliationMapper) {
		this.affiliationMapper = argAffiliationMapper;
	}

	/**
	 * Gets the value of entityTypeMapper
	 *
	 * @return the value of entityTypeMapper
	 */
	public final UaEntityTypeContactInfoDefaultMapper getEntityTypeContactInfoDefaultMapper() {
		return this.entityTypeContactInfoDefaultMapper;
	}

	/**
	 * Sets the value of entityTypeMapper
	 *
	 * @param argEntityTypeMapper
	 *            Value to assign to this.entityTypeMapper
	 */
	public final void setEntityTypeContactInfoDefaultMapper(final UaEntityTypeContactInfoDefaultMapper argEntityTypeMapper) {
		this.entityTypeContactInfoDefaultMapper = argEntityTypeMapper;
	}

	/**
	 * Gets the value of defaultNameMapper
	 *
	 * @return the value of defaultNameMapper
	 */
	public final UaEntityNameMapper getDefaultNameMapper() {
		return this.defaultNameMapper;
	}

	/**
	 * Sets the value of defaultNameMapper
	 *
	 * @param argDefaultNameMapper
	 *            Value to assign to this.defaultNameMapper
	 */
	public final void setDefaultNameMapper(final UaEntityNameMapper argDefaultNameMapper) {
		this.defaultNameMapper = argDefaultNameMapper;
	}

	/**
	 * Gets the value of employmentMapper
	 *
	 * @return the value of employmentMapper
	 */
	public final UaEntityEmploymentMapper getEmploymentMapper() {
		return this.employmentMapper;
	}

	/**
	 * Sets the value of employmentMapper
	 *
	 * @param argEmploymentMapper
	 *            Value to assign to this.employmentMapper
	 */
	public final void setEmploymentMapper(final UaEntityEmploymentMapper argEmploymentMapper) {
		this.employmentMapper = argEmploymentMapper;
	}
}
