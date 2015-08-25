package edu.arizona.kim.ldap;

import org.apache.log4j.Logger;
import org.kuali.rice.kim.api.identity.CodedAttribute;
import org.kuali.rice.kim.api.identity.address.EntityAddress;
import org.springframework.ldap.core.DirContextOperations;

import edu.arizona.kim.eds.UaEdsRecord;
import edu.arizona.kim.eds.UaEdsRecordFactory;

/**
 * Created by shaloo & kosta on 8/20/15.
 */
public class UaEntityAddressMapper extends UaBaseMapper<EntityAddress> {

	private static final Logger LOG = Logger.getLogger(UaEntityAddressMapper.class);

	@Override
	EntityAddress mapDtoFromContext(DirContextOperations context) {
		return mapDtoFromContext(context, false);
	}

	EntityAddress mapDtoFromContext(DirContextOperations context, boolean isdefault) {
		EntityAddress.Builder builder = mapBuilderFromContext(context, isdefault);
		return builder != null ? builder.build() : null;
	}

	EntityAddress.Builder mapBuilderFromContext(DirContextOperations context) {
		return mapBuilderFromContext(context, false);
	}

	EntityAddress.Builder mapBuilderFromContext(DirContextOperations context, boolean isdefault) {
		final EntityAddress.Builder builder = EntityAddress.Builder.create();

		UaEdsRecord edsRecord = UaEdsRecordFactory.getEdsRecord(context);
		if (edsRecord == null) {
			LOG.debug("No active and valid EDS eduPersonAffiliation found for context: " + context.getAttributes());
			return null;
		}

		final String line1 = context.getStringAttribute(getEdsConstants().getEmployeePrimaryDeptNameContextKey());
		final String line2 = context.getStringAttribute(getEdsConstants().getEmployeePoBoxContextKey());
		final String cityName = context.getStringAttribute(getEdsConstants().getEmployeeCityContextKey());
		final String stateCode = context.getStringAttribute(getEdsConstants().getEmployeeStateContextKey());
		final String postalCode = context.getStringAttribute(getEdsConstants().getEmployeeZipContextKey());

		builder.setAddressType(CodedAttribute.Builder.create("WORK"));
		builder.setLine1(line1);
		builder.setLine2(line2);
		builder.setCity(cityName);
		builder.setStateProvinceCode(stateCode);
		builder.setPostalCode(postalCode);
		builder.setDefaultValue(isdefault);
		builder.setActive(true);
		return builder;

	}

}
