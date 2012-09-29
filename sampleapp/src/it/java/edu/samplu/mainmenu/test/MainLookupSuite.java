package edu.samplu.mainmenu.test;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        AgendaLookUpLegacyIT.class,
        ContextLookUpLegacyIT.class,
        TermLookUpLegacyIT.class,
        TermSpecificationLookUpLegacyIT.class
})
public class MainLookupSuite {}
