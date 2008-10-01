
CREATE TABLE KR_KIM_TEST_BO (
    pk VARCHAR2(40)
,   prncpl_id VARCHAR2(40)
)
/


  <class-descriptor class="org.kuali.rice.kim.test.bo.BOContainingPerson" table="KR_KIM_TEST_BO">
    <field-descriptor name="boPrimaryKey" column="PK" jdbc-type="VARCHAR" primarykey="true" />
    <field-descriptor name="principalId" column="PRNCPL_ID" jdbc-type="VARCHAR" />
  </class-descriptor>
