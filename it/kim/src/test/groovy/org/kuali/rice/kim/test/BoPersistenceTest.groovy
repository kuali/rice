package org.kuali.rice.kim.test

import org.junit.Before
import org.kuali.rice.kim.bo.ui.KimDocumentBoActivatableEditableBase
import org.kuali.rice.test.persistence.PersistenceTestHelper

/**
 * Tests persisting Entity objects in order to verify ORM mappings
 */
abstract class BoPersistenceTest extends KIMTestCase {

    @Delegate PersistenceTestHelper helper
    protected factory = new EntityFactory()

    @Before
    void init() {
        helper = new PersistenceTestHelper("kimDataSource")
    }

    protected def docno_field(KimDocumentBoActivatableEditableBase bo) {
        [ FDOC_NBR: bo.documentNumber ]
    }
    protected def kimdoc_fields(KimDocumentBoActivatableEditableBase bo) {
        basic_fields(bo) + active_field(bo) + default_field(bo) + edit_field(bo) + docno_field(bo)
    }
}