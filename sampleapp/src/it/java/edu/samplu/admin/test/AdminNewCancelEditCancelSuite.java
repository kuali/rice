package edu.samplu.admin.test;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        ConfigNameSpaceNavIT.class,
        ConfigParameterLegacyIT.class,
        ConfigParameterTypeLegacyIT.class,
        IdentityGroupBlanketAppNavIT.class,
        IdentityGroupLegacyIT.class,
        IdentityPermissionBlanketAppNavIT.class,
        IdentityPermissionLegacyIT.class,
        IdentityPersonLegacyIT.class,
        IdentityResponsibilityLegacyIT.class,
        IdentityRoleBlanketAppNavIT.class,
        LocationCampusLegacyIT.class,
        LocationCountryLegacyIT.class,
        LocationCountyLegacyIT.class,
        LocationPostCodeLegacyIT.class,
        LocationStateLegacyIT.class
})

public class AdminNewCancelEditCancelSuite {}
