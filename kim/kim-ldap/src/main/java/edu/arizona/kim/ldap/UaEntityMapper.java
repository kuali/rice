package edu.arizona.kim.ldap;

import static org.apache.commons.lang.StringUtils.contains;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.kim.api.identity.affiliation.EntityAffiliation;
import org.kuali.rice.kim.api.identity.employment.EntityEmployment;
import org.kuali.rice.kim.api.identity.entity.Entity;
import org.kuali.rice.kim.api.identity.external.EntityExternalIdentifier;
import org.kuali.rice.kim.api.identity.name.EntityName;
import org.kuali.rice.kim.api.identity.principal.Principal;
import org.kuali.rice.kim.api.identity.type.EntityTypeContactInfo;
import org.kuali.rice.kim.ldap.InvalidLdapEntityException;
import org.springframework.ldap.core.DirContextOperations;

import edu.arizona.kim.eds.UaEdsRecord;
import edu.arizona.kim.eds.UaEdsRecordFactory;

/**
 * Created by shaloo & kosta on 8/20/15.
 */
public class UaEntityMapper extends UaBaseMapper<Entity> {

	private static final Logger LOG = Logger.getLogger(UaEntityMapper.class);

	private UaEntityAffiliationMapper affiliationMapper;
	private UaEntityTypeContactInfoMapper entityTypeContactInfoMapper;
	private UaEntityNameMapper defaultNameMapper;
	private UaEntityEmploymentMapper employmentMapper;

	@Override
	Entity mapDtoFromContext(DirContextOperations context) {
		Entity.Builder builder = mapBuilderFromContext(context);
		return builder != null ? builder.build() : null;
	}

	Entity.Builder mapBuilderFromContext(DirContextOperations context) {

		UaEdsRecord edsRecord = UaEdsRecordFactory.getEdsRecord(context);
		if (edsRecord == null) {
			LOG.debug("No active and valid EDS eduPersonAffiliation found for context: " + context.getAttributes());
			return null;
		}

		final String entityId = edsRecord.getUaId();
		final String principalName = edsRecord.getUid();

		final Entity.Builder person = Entity.Builder.create();
		person.setId(entityId);

		if (entityId == null) {
			throw new InvalidLdapEntityException("LDAP Search Results yielded an invalid result with attributes " + context.getAttributes());
		}

		person.setAffiliations(new ArrayList<EntityAffiliation.Builder>());
		person.setExternalIdentifiers(new ArrayList<EntityExternalIdentifier.Builder>());

		final EntityExternalIdentifier.Builder externalId = EntityExternalIdentifier.Builder.create();
		externalId.setExternalIdentifierTypeCode(getConstants().getTaxExternalIdTypeCode());
		externalId.setExternalId(entityId);
		person.getExternalIdentifiers().add(externalId);

		person.setAffiliations(getAffiliationMapper().mapBuilderFromContext(context));

		person.setEntityTypes(new ArrayList<EntityTypeContactInfo.Builder>());
		person.getEntityTypeContactInfos().add(getEntityTypeContactInfoMapper().mapBuilderFromContext(context));

		final List<EntityName.Builder> names = new ArrayList<EntityName.Builder>();
		final EntityName.Builder name = getDefaultNameMapper().mapBuilderFromContext(context);
		names.add(name);
		name.setDefaultValue(true);
		person.setNames(names);
		person.setId(entityId);

		final List<EntityEmployment.Builder> employmentInfos = getEmploymentMapper().mapBuilderFromContext(context);
		final EntityEmployment.Builder primaryEmployment = (employmentInfos != null && employmentInfos.size() > 0) ? employmentInfos.get(0) : null;

		if (primaryEmployment != null) {
			primaryEmployment.setPrimary(true);
			person.setEmploymentInformation(employmentInfos);
		}

		person.setAffiliations(getAffiliationMapper().mapBuilderFromContext(context));
		if (person.getAffiliations().size() > 0) {
			person.getAffiliations().get(0).setDefaultValue(true);
		}

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

		return person;
	}

	/**
	 *
	 * Finds and returns affiliation id of the persons affiliation that matches
	 * the affilication code
	 * 
	 * @param affiliationCode
	 * @param person
	 * @return
	 */
	protected EntityAffiliation.Builder getAffiliation(String affiliationCodes, Entity.Builder person) {
		EntityAffiliation.Builder retval = null;
		for (EntityAffiliation.Builder affil : person.getAffiliations()) {
			if (contains(affiliationCodes, affil.getAffiliationType().getCode())) {
				return affil;
			}
		}
		return retval;
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
	public final UaEntityTypeContactInfoMapper getEntityTypeContactInfoMapper() {
		return this.entityTypeContactInfoMapper;
	}

	/**
	 * Sets the value of entityTypeMapper
	 *
	 * @param argEntityTypeMapper
	 *            Value to assign to this.entityTypeMapper
	 */
	public final void setEntityTypeContactInfoMapper(final UaEntityTypeContactInfoMapper entityTypeContactInfoMapper) {
		this.entityTypeContactInfoMapper = entityTypeContactInfoMapper;
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
	public final void setDefaultNameMapper(final UaEntityNameMapper defaultNameMapper) {
		this.defaultNameMapper = defaultNameMapper;
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
	public final void setEmploymentMapper(final UaEntityEmploymentMapper employmentMapper) {
		this.employmentMapper = employmentMapper;
	}
}