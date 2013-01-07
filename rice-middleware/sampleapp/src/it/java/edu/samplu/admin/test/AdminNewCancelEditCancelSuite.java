package edu.samplu.admin.test;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        ConfigNameSpaceLegacyIT.class,
        ConfigParameterLegacyIT.class,
        ConfigParameterTypeLegacyIT.class,
        IdentityGroupBlanketAppLegacyIT.class,
        IdentityGroupLegacyIT.class,
        IdentityPermissionBlanketAppLegacyIT.class,
        IdentityPermissionLegacyIT.class,
        IdentityPersonLegacyIT.class,
        IdentityResponsibilityLegacyIT.class,
        IdentityRoleBlanketAppLegacyIT.class,
        LocationCampusLegacyIT.class,
        LocationCountryLegacyIT.class,
        LocationCountyLegacyIT.class,
        LocationPostCodeLegacyIT.class,
        LocationStateLegacyIT.class
})

public class AdminNewCancelEditCancelSuite {}
