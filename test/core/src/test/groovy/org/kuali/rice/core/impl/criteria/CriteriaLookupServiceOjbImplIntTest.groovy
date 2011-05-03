package org.kuali.rice.core.impl.criteria

import org.junit.Before
import org.junit.Test
import org.kuali.rice.core.api.criteria.QueryByCriteria
import org.kuali.rice.core.api.resourceloader.GlobalResourceLoader
import org.kuali.rice.core.impl.parameter.ParameterBo
import org.kuali.rice.core.test.CORETestCase
import org.kuali.rice.test.data.PerSuiteUnitTestData
import org.kuali.rice.test.data.UnitTestData
import org.kuali.rice.test.data.UnitTestSql
import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertTrue
import static org.kuali.rice.core.api.criteria.PredicateFactory.equal

@PerSuiteUnitTestData(value = [@UnitTestData(sqlStatements = [
  @UnitTestSql("INSERT INTO KRNS_NMSPC_T(NMSPC_CD, OBJ_ID, VER_NBR, NM, ACTV_IND, APPL_NMSPC_CD) VALUES('FOO-NS', '53680C68F595AD9BE0404F8189D80A6B', 1, 'FOO System', 'Y', 'FOO-KUALI')"),
  @UnitTestSql("INSERT INTO KRNS_PARM_TYP_T(PARM_TYP_CD, OBJ_ID, VER_NBR, NM, ACTV_IND) VALUES('FOO-T', '53680C68F593AD9BE0404F8189D80A6B', 1, 'Foo Type', 'Y')"),
  @UnitTestSql("INSERT INTO KRNS_PARM_DTL_TYP_T(NMSPC_CD, PARM_DTL_TYP_CD, OBJ_ID, VER_NBR, NM, ACTV_IND) VALUES('FOO-NS', 'All-FOO', '53680C68F596AD9BE0404F8189D80A6B', 1, 'All-FOO', 'Y')"),

  @UnitTestSql("INSERT INTO KRNS_PARM_T(NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD) VALUES('FOO-NS', 'ALL-FOO', 'TURN_FOO_ON', '5A689075D35E7AEBE0404F8189D80326', 1, 'FOO-T', 'Y', 'turn the foo on.', 'A', 'FOO-KUALI')"),
  @UnitTestSql("INSERT INTO KRNS_PARM_T(NMSPC_CD, PARM_DTL_TYP_CD, PARM_NM, OBJ_ID, VER_NBR, PARM_TYP_CD, TXT, PARM_DESC_TXT, CONS_CD, APPL_NMSPC_CD) VALUES('FOO-NS', 'ALL-FOO', 'TURN_ANOTHER_FOO_ON', '5A689075D35E7AEBE0404F8189D80325', 1, 'FOO-T', 'N', 'turn another the foo on.', 'A', 'FOO-KUALI')"),
])])
class CriteriaLookupServiceOjbImplIntTest extends CORETestCase {

    def lookup;

    @Before
    void initLookup() {
        lookup = GlobalResourceLoader.getService("criteriaLookupService");
    }

    @Test
    void test_no_predicate() {
        def builder = QueryByCriteria.Builder.<ParameterBo>create()
        def results = lookup.lookup(ParameterBo.class, builder.build());
        assertTrue "results size are ${results.getResults().size()}", results.getResults().size() > 1
    }

    @Test
    void test_basic_lookup() {
        def builder = QueryByCriteria.Builder.<ParameterBo>create()
        builder.predicates = equal("name", "TURN_ANOTHER_FOO_ON")

        def results = lookup.lookup(ParameterBo.class, builder.build());
        assertEquals 1, results.getResults().size()
        assertEquals "TURN_ANOTHER_FOO_ON", results.getResults()[0].name
    }
}