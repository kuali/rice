package edu.samplu.admin.test;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        ConfigNameSpaceNavIT.class,
        ConfigParameterNavIT.class,
        ConfigParameterTypeNavIT.class,
        IdentityGroupBlanketAppNavIT.class,
        IdentityGroupNavIT.class,
        IdentityPermissionBlanketAppNavIT.class,
        IdentityPermissionNavIT.class,
        IdentityPersonNavIT.class,
        IdentityResponsibilityNavIT.class,
        IdentityRoleBlanketAppNavIT.class,
        LocationCampusNavIT.class,
        LocationCountryNavIT.class,
        LocationCountyNavIT.class,
        LocationPostCodeNavIT.class,
        LocationStateNavIT.class
})

public class AdminNewCancelEditCancelSuite {}
