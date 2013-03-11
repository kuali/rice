package edu.samplu.mainmenu.test;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        AgendaLookUpNavIT.class,
        ContextLookUpNavIT.class,
        TermLookUpNavIT.class,
        TermSpecificationLookUpNavIT.class
})
public class MainLookupSuite {}
