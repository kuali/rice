package edu.arizona.kim.ldap;

import org.apache.log4j.Logger;
import org.kuali.rice.kim.api.identity.principal.Principal;
import org.springframework.ldap.core.DirContextOperations;

import edu.arizona.kim.eds.UaEdsRecord;
import edu.arizona.kim.eds.UaEdsRecordFactory;

/**
 * Created by shaloo & kosta on 8/20/15.
 */
public class UaPrincipalMapper extends UaBaseMapper<Principal> {

	private static final Logger LOG = Logger.getLogger(UaPrincipalMapper.class);

	@Override
	Principal mapDtoFromContext(DirContextOperations context) {
		Principal.Builder builder = mapBuilderFromContext(context);
		return builder != null ? builder.build() : null;
	}

	Principal.Builder mapBuilderFromContext(DirContextOperations context) {

		UaEdsRecord edsRecord = UaEdsRecordFactory.getEdsRecord(context);
		if (edsRecord == null) {
			LOG.debug("No active and valid EDS eduPersonAffiliation found for context: " + context.getAttributes());
			return null;
		}
		//For records that do not have a netid, use the uaid for principal name.
		String principalName = edsRecord.getUid();
		if ( principalName == null ) {
			principalName = edsRecord.getUaId();
		}
		final Principal.Builder person = Principal.Builder.create(principalName);

		person.setPrincipalId(edsRecord.getUaId());
		person.setEntityId(edsRecord.getUaId());
		person.setActive(edsRecord.isActive());

		return person;
	}
}
