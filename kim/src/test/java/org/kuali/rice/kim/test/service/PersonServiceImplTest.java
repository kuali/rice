/*
 * Copyright 2007 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.kim.test.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kuali.rice.core.config.spring.ConfigFactoryBean;
import org.kuali.rice.core.lifecycle.Lifecycle;
import org.kuali.rice.core.resourceloader.GlobalResourceLoader;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.bo.impl.PersonImpl;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.service.impl.PersonServiceImpl;
import org.kuali.rice.kim.test.bo.BOContainingPerson;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.lookup.Lookupable;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.util.TypedArrayList;
import org.kuali.rice.test.RiceTestCase;
import org.kuali.rice.test.lifecycles.JettyServerLifecycle;
import org.kuali.rice.test.web.HtmlUnitUtil;

/**
 * This is a description of what this class does - kellerj don't forget to fill this in.
 *
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class PersonServiceImplTest extends RiceTestCase {

	private PersonServiceImpl personService;

	private String contextName = "/knstest";

	private String relativeWebappRoot = "/../web/src/main/webapp";

	private String testConfigFilename = "classpath:META-INF/kim-test-config.xml";

	@Override
	protected List<Lifecycle> getSuiteLifecycles() {
		List<Lifecycle> lifecycles = super.getSuiteLifecycles();
		lifecycles.add(new Lifecycle() {
			boolean started = false;

			public boolean isStarted() {
				return this.started;
			}

			public void start() throws Exception {
				System.setProperty(KEWConstants.BOOTSTRAP_SPRING_FILE, "SampleAppBeans-test.xml");
				ConfigFactoryBean.CONFIG_OVERRIDE_LOCATION = testConfigFilename;
				//new SQLDataLoaderLifecycle(sqlFilename, sqlDelimiter).start();
				new JettyServerLifecycle(HtmlUnitUtil.getPort(), contextName, relativeWebappRoot).start();
				//new KEWXmlDataLoaderLifecycle(xmlFilename).start();
				System.getProperties().remove(KEWConstants.BOOTSTRAP_SPRING_FILE);
				this.started = true;
			}

			public void stop() throws Exception {
				this.started = false;
			}

		});
		return lifecycles;
	}

	@Override
	protected String getModuleName() {
		return "kim";
	}

	@Override
	protected List<Lifecycle> getDefaultSuiteLifecycles() {
		List<Lifecycle> lifecycles = getInitialLifecycles();
		return lifecycles;
	}

	@Before
	public void setUp() throws Exception {
		super.setUp();
		personService = (PersonServiceImpl)GlobalResourceLoader.getService(new QName("KIM", "kimPersonService"));
	}

	@After
	public void tearDown() throws Exception {}

	/**
	 * Test method for {@link org.kuali.rice.kim.service.impl.PersonServiceImpl#getPersonByExternalIdentifier(java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testGetPersonByExternalIdentifier() {
		List<PersonImpl> people = personService.getPersonByExternalIdentifier( "EMPLOYEE", "EXTID1" );
		assertNotNull( "result object must not be null", people );
		assertEquals( "exactly one record should be returned", 1, people.size() );
		assertEquals( "the returned principal is not correct", "p1", people.get(0).getPrincipalId() );
	}

//	@Test
//	public void testHasRole_Inherited() {
//		Person p = personService.getPersonByPrincipalName( "wwren" );
//		assertNotNull( "person object must not be null", p );
//		assertTrue( "person must be a member of PA_MAINTENANCE_USERS", personService.hasRole( p, org.kuali.rice.kim.util.KimConstants.KIM_GROUP_DEFAULT_NAMESPACE_CODE, "PA_AP_MAINTENANCE_USERS" ) );
//		assertTrue( "person must be NOT a member of PA_MAINTENANCE_USERS", !personService.hasRole( p, org.kuali.rice.kim.util.KimConstants.KIM_GROUP_DEFAULT_NAMESPACE_CODE, "PA_MAINTENANCE_USERS" ) );
//	}
//
//	@Test
//	public void testGetPersonRoles() {
//		Person p = personService.getPerson( "KULUSER" );
//		assertNotNull( "person object must not be null", p );
//		List<KimRole> roles = personService.getPersonRoles( p, null );
//		assertNotNull( "role list must not be null", roles );
//		System.out.println( roles );
//		assertTrue( "role list must have non-zero length", roles.size() > 0 );
//		KimRole r = KIMServiceLocator.getAuthorizationService().getRoleByName( org.kuali.rice.kim.util.KimConstants.KIM_GROUP_DEFAULT_NAMESPACE_CODE, "SY_FUNCTIONAL_SUPER_USERS" );
//		assertTrue( "one of the roles must be SY_FUNCTIONAL_SUPER_USERS", roles.contains( r ) );
//	}
//
//	@Test
//	public void testHasRole() {
//		Person p = personService.getPerson( "KULUSER" );
//		assertTrue( "person must have role SY_FUNCTIONAL_SUPER_USERS", personService.hasRole( p, org.kuali.rice.kim.util.KimConstants.KIM_GROUP_DEFAULT_NAMESPACE_CODE, "SY_FUNCTIONAL_SUPER_USERS" ) );
//	}

	@Test
	public void testGetPerson() {
		Person p = personService.getPerson( "KULUSER" );
		assertNotNull( "person object must not be null", p );
		assertEquals( "class must match implementation class defined on service", personService.getPersonImplementationClass(), p.getClass() );
		assertEquals( "person name does not match", "System User", p.getFirstName() );
		assertEquals( "principal name does not match", "kuluser", p.getPrincipalName() );
		assertEquals( "KULUSER should have no address record", "", p.getAddressLine1() );
		assertEquals( "KULUSER should have no campus code", "", p.getCampusCode() );
		assertEquals( "KULUSER should have no email address", "", p.getEmailAddress() );
		assertNotNull( "entity ID should be set", p.getEntityId() );
		assertNotNull( "principal ID should be set", p.getPrincipalId() );
		assertEquals( "employee ID does not match", "KULUSER", p.getExternalId( "EMPLOYEE" ) );
		p.getName();
		assertTrue( "KULUSER must have a staff type affiliation", p.hasAffiliationOfType( "STAFF" ));
	}

	@Test
	public void testGetPersonByPrincipalName() {
		Person p = personService.getPersonByPrincipalName( "kuluser" );
		assertNotNull( "person object must not be null", p );
		assertEquals( "person name does not match", "System User", p.getFirstName() );
		assertEquals( "principal id does not match", "KULUSER", p.getPrincipalId() );
	}


	@Test
	public void testConvertPersonPropertiesToEntityProperties() {
		HashMap<String,String> criteria = new HashMap<String,String>();
		criteria.put( "firstName", "System User" );
		Map<String,String> entityCriteria = personService.getPersonDao().convertPersonPropertiesToEntityProperties( criteria );
		assertEquals( "number of criteria is not correct", 7, entityCriteria.size() );
		assertNotNull( "criteria must filter for active entities", entityCriteria.get( "active" ) );
		assertNotNull( "criteria must filter for entities with active principals", entityCriteria.get( "principals.active" ) );
		assertNotNull( "criteria must filter for active entity types", entityCriteria.get( "entityTypes.active" ) );
		assertNotNull( "criteria must filter on entity type code", entityCriteria.get( "entityTypes.entityTypeCode" ) );
		assertNotNull( "criteria must filter for first name", entityCriteria.get( "entityTypes.names.firstName" ) );
		assertNotNull( "criteria must filter for active names", entityCriteria.get( "entityTypes.names.active" ) );
		assertNotNull( "criteria must filter for the default name", entityCriteria.get( "entityTypes.names.dflt" ) );
	}

	/**
	 * Test method for {@link org.kuali.rice.kim.service.impl.PersonServiceImpl#findPeople(Map)}.
	 */
	@Test
	public void testFindPeople() {
		HashMap<String,String> criteria = new HashMap<String,String>();
		criteria.put( "firstName", "System User" );
		List<PersonImpl> people = personService.findPeople( criteria );
		assertNotNull( "result must not be null", people );
		assertEquals( "wrong number of people returned", 1, people.size() );
		Person p = people.get( 0 );
		assertEquals( "name must match criteria", "System User", p.getFirstName() );
		assertEquals( "principal name must be kuluser", "kuluser", p.getPrincipalName() );
	}

	@Test
	public void testResolvePrincipalNamesToPrincipalIds() throws Exception {
		KNSServiceLocator.getDataDictionaryService().getDataDictionary().addConfigFileLocation( "classpath:org/kuali/rice/kim/bo/datadictionary/test/SampleBO.xml" );
		KNSServiceLocator.getDataDictionaryService().getDataDictionary().parseDataDictionaryConfigurationFiles( false );

		Map<String,String> criteria = new HashMap<String,String>();
		criteria.put( "anAttribute", "aValue" );
		criteria.put( "anotherAttribute", "anotherValue" );
		criteria.put( "personAttribute.principalName", "kuluser" );
		System.out.println( "Before: " + criteria );
		Map<String,String> newCritiera = personService.resolvePrincipalNamesToPrincipalIds( new SampleBO(), criteria );
		assertNotNull( "returned map must not be null", newCritiera );
		System.out.println( "After:  " + newCritiera );
		assertTrue( "new criteria must have a personPrincipalId entry", newCritiera.containsKey( "personPrincipalId" ) );
		assertEquals( "resulting principal ID is not that expected", "KULUSER", newCritiera.get( "personPrincipalId" ) );
		assertFalse( "new criteria must not contain the original PrincipalName entry", newCritiera.containsKey( "personAttribute.principalName" ) );

		// check when already has value in result field
		criteria.put( "personPrincipalId", "NOT KULUSER" );
		System.out.println( "Before: " + criteria );
		newCritiera = personService.resolvePrincipalNamesToPrincipalIds( new SampleBO(), criteria );
		assertNotNull( "returned map must not be null", newCritiera );
		System.out.println( "After:  " + newCritiera );
		assertTrue( "new criteria must have a personPrincipalId entry", newCritiera.containsKey( "personPrincipalId" ) );
		assertEquals( "resulting principal ID should have been changed", "KULUSER", newCritiera.get( "personPrincipalId" ) );
	}

	@Test
	public void testResolvePrincipalNamesToPrincipalIds_Nested() throws Exception {
		Map<String,String> criteria = new HashMap<String,String>();
		criteria.put( "add.childBos.childsPersonAttribute.principalName", "kuluser" );
		System.out.println( "Before: " + criteria );
		Map<String,String> newCritiera = personService.resolvePrincipalNamesToPrincipalIds( new SampleBO(), criteria );
		assertNotNull( "returned map must not be null", newCritiera );
		System.out.println( "After:  " + newCritiera );
		// TODO: property is being appended in the wrong place - fix
		assertTrue( "new criteria must have a childsPersonPrincipalId entry", newCritiera.containsKey( "add.childBos.childsPersonPrincipalId" ) );
		assertFalse( "new criteria must not contain the original PrincipalName entry", newCritiera.containsKey( "add.childBos.childsPersonAttribute.principalName" ) );
	}

	@Test
	public void testUpdateWhenNecessary() {
		SampleBO bo = new SampleBO();
		bo.setPersonPrincipalId( "KULUSER" );
		Person p = bo.getPersonAttribute();
		assertNotNull( "person object must not be null", p );
		assertEquals( "principal IDs do not match", bo.getPersonPrincipalId(), p.getPrincipalId() );
		assertSame( "second retrieval must return same object since ID not changed", p, bo.getPersonAttribute() );
	}

	@Test
	public void testLookupWithPersonJoin() throws Exception {
		KNSServiceLocator.getDataDictionaryService().getDataDictionary().addConfigFileLocation( "classpath:org/kuali/rice/kim/bo/datadictionary/test/BOContainingPerson.xml" );
		KNSServiceLocator.getDataDictionaryService().getDataDictionary().parseDataDictionaryConfigurationFiles( false );
		BusinessObjectService bos = KNSServiceLocator.getBusinessObjectService();
		bos.delete( new ArrayList(bos.findAll( BOContainingPerson.class )) );
		BOContainingPerson bo = new BOContainingPerson();
		bo.setBoPrimaryKey( "ONE" );
		bo.setPrincipalId( "p1" );
		bos.save( bo );
		bo = new BOContainingPerson();
		bo.setBoPrimaryKey( "TWO" );
		bo.setPrincipalId( "p2" );
		bos.save( bo );

		Lookupable l = KNSServiceLocator.getKualiLookupable();
		l.setBusinessObjectClass( BOContainingPerson.class );
		Map<String,String> criteria = new HashMap<String,String>();
		criteria.put( "person.principalName", "user1" );
		List<BOContainingPerson> results = (List)l.getSearchResultsUnbounded( (Map)criteria );
		System.out.println( results );
		assertNotNull( "results may not be null", results );
		assertEquals( "number of results is incorrect", 1, results.size() );
		bo =  results.iterator().next();
		assertEquals( "principalId does not match", "p1", bo.getPrincipalId() );
	}

//	@Test
//	public void testConfirmOnlyPKUsed() {
//		HashMap<String,String> criteria = new HashMap<String,String>();
//		criteria.put( "lastName", "HUNTLEY" );
//		criteria.put( "firstName", "KEISHA" );
//		Collection<Person> people = (Collection<Person>)KNSServiceLocator.getLookupService().findCollectionBySearchUnbounded(Person.class, criteria);
//		personService.findPeople( criteria );
//		assertNotNull( "result must not be null", people );
//		assertEquals( "wrong number of people returned", 1, people.size() );
//		Person p = people.iterator().next();
//		assertEquals( "principal name does not match", "khuntley", p.getPrincipalName() );
//
//		criteria.put( "principalName", "kuluser" );
//		people = people = (Collection<Person>)KNSServiceLocator.getLookupService().findCollectionBySearchUnbounded(Person.class, criteria);
//		assertNotNull( "result must not be null", people );
//		assertEquals( "wrong number of people returned", 1, people.size() );
//		p = people.iterator().next();
//		assertEquals( "principal name must be kuluser", "kuluser", p.getPrincipalName() );
//	}

	public static class SampleBO implements BusinessObject {
		private String anAttribute;
		private String anotherAttribute;
		private String personPrincipalId;
		private Person personAttribute;
		private List<SampleChildBOWithPerson> childBos = new TypedArrayList(SampleChildBOWithPerson.class);
		public String getAnAttribute() {
			return this.anAttribute;
		}
		public void setAnAttribute(String anAttribute) {
			this.anAttribute = anAttribute;
		}
		public String getAnotherAttribute() {
			return this.anotherAttribute;
		}
		public void setAnotherAttribute(String anotherAttribute) {
			this.anotherAttribute = anotherAttribute;
		}
		public String getPersonPrincipalId() {
			return this.personPrincipalId;
		}
		public void setPersonPrincipalId(String personPrincipalId) {
			this.personPrincipalId = personPrincipalId;
		}
		public Person getPersonAttribute() {
			personAttribute = KIMServiceLocator.getPersonService().updatePersonIfNecessary( personPrincipalId, personAttribute );
			return personAttribute;
		}
		public void setPersonAttribute(Person personAttribute) {
			this.personAttribute = personAttribute;
		}
		public void prepareForWorkflow() {}
		public void refresh() {}
		public List<SampleChildBOWithPerson> getChildBos() {
			return this.childBos;
		}
		public void setChildBos(List<SampleChildBOWithPerson> childBos) {
			this.childBos = childBos;
		}
	}

	public static class SampleChildBOWithPerson implements BusinessObject {

		private String childsAttribute;
		private String childsPersonPrincipalId;
		private Person childsPersonAttribute;



		public String getChildsAttribute() {
			return this.childsAttribute;
		}
		public void setChildsAttribute(String childsAttribute) {
			this.childsAttribute = childsAttribute;
		}
		public String getChildsPersonPrincipalId() {
			return this.childsPersonPrincipalId;
		}
		public void setChildsPersonPrincipalId(String childsPersonPrincipalId) {
			this.childsPersonPrincipalId = childsPersonPrincipalId;
		}
		public Person getChildsPersonAttribute() {
			childsPersonAttribute = KIMServiceLocator.getPersonService().updatePersonIfNecessary( childsPersonPrincipalId, childsPersonAttribute );
			return childsPersonAttribute;
		}
		public void setChildsPersonAttribute(Person childsPersonAttribute) {
			this.childsPersonAttribute = childsPersonAttribute;
		}
		public void prepareForWorkflow() {}
		public void refresh() {}
	}
}
