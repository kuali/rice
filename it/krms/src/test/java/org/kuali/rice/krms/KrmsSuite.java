package org.kuali.rice.krms;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.kuali.rice.krms.impl.ui.AgendaEditorMaintainableIntegrationTest;
import org.kuali.rice.krms.test.ComparisonOperatorIntegrationTest;
import org.kuali.rice.krms.test.PropositionBoServiceTest;
import org.kuali.rice.krms.test.RepositoryCreateAndExecuteIntegrationTest;
import org.kuali.rice.krms.test.TermBoServiceTest;
import org.kuali.rice.krms.test.TermRelatedBoTest;
import org.kuali.rice.krms.test.ValidationIntegrationTest;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */

@RunWith(Suite.class)
@Suite.SuiteClasses({
        ComparisonOperatorIntegrationTest.class,
        PropositionBoServiceTest.class,
        RepositoryCreateAndExecuteIntegrationTest.class,
        TermBoServiceTest.class,
        TermRelatedBoTest.class,
        ValidationIntegrationTest.class,
        AgendaEditorMaintainableIntegrationTest.class
})
public class KrmsSuite {
}