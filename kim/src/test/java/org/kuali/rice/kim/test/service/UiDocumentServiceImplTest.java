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
import org.kuali.rice.kim.bo.entity.EntityEmploymentInformation;
import org.kuali.rice.kim.bo.entity.impl.EntityAddressImpl;
import org.kuali.rice.kim.bo.entity.impl.EntityAffiliationImpl;
import org.kuali.rice.kim.bo.entity.impl.EntityEmailImpl;
import org.kuali.rice.kim.bo.entity.impl.EntityEntityTypeImpl;
import org.kuali.rice.kim.bo.entity.impl.EntityExternalIdentifierImpl;
import org.kuali.rice.kim.bo.entity.impl.EntityNameImpl;
import org.kuali.rice.kim.bo.entity.impl.EntityPhoneImpl;
import org.kuali.rice.kim.bo.entity.impl.EntityPrivacyPreferencesImpl;
import org.kuali.rice.kim.bo.entity.impl.KimEntityImpl;
import org.kuali.rice.kim.bo.entity.impl.KimPrincipalImpl;
import org.kuali.rice.kim.bo.impl.KimAttributes;
import org.kuali.rice.kim.bo.types.impl.KimAttributeImpl;
import org.kuali.rice.kim.bo.types.impl.KimTypeAttributeImpl;
import org.kuali.rice.kim.bo.types.impl.KimTypeImpl;
import org.kuali.rice.kim.bo.ui.PersonDocumentAddress;
import org.kuali.rice.kim.bo.ui.PersonDocumentAffiliation;
import org.kuali.rice.kim.bo.ui.PersonDocumentEmail;
import org.kuali.rice.kim.bo.ui.PersonDocumentEmploymentInfo;
import org.kuali.rice.kim.bo.ui.PersonDocumentName;
import org.kuali.rice.kim.bo.ui.PersonDocumentPhone;
import org.kuali.rice.kim.bo.ui.PersonDocumentPrivacy;
import org.kuali.rice.kim.bo.ui.PersonDocumentRole;
import org.kuali.rice.kim.document.IdentityManagementPersonDocument;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.service.impl.UiDocumentServiceImpl;
import org.kuali.rice.kim.service.support.KimTypeService;
import org.kuali.rice.kim.service.support.impl.KimTypeServiceBase;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.test.RiceTestCase;
import org.kuali.rice.test.lifecycles.JettyServerLifecycle;
import org.kuali.rice.test.web.HtmlUnitUtil;

/**
 * This is a description of what this class does - shyu don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 *
 */
public class UiDocumentServiceImplTest extends RiceTestCase {

	private UiDocumentServiceImpl uiDocumentService;

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
		uiDocumentService = (UiDocumentServiceImpl)GlobalResourceLoader.getService(new QName("KIM", "kimUiDocumentService"));
	}

	@After
	public void tearDown() throws Exception {}

	@Test
	public void testSaveToEntity() {
		IdentityManagementPersonDocument personDoc = initPersonDoc();
		uiDocumentService.saveEntityPerson(personDoc);
		KimEntityImpl entity = (KimEntityImpl)KIMServiceLocator.getIdentityService().getEntity(personDoc.getEntityId());
        EntityEntityTypeImpl entityType = entity.getEntityTypes().get(0);
        personDoc.getExternalIdentifiers();
		assertAddressTrue((PersonDocumentAddress)personDoc.getAddrs().get(0), (EntityAddressImpl)entityType.getAddresses().get(0));
		assertPhoneTrue((PersonDocumentPhone)personDoc.getPhones().get(0), (EntityPhoneImpl)entityType.getPhoneNumbers().get(0));
		assertEmailTrue((PersonDocumentEmail)personDoc.getEmails().get(0), (EntityEmailImpl)entityType.getEmailAddresses().get(0));
		assertNameTrue((PersonDocumentName)personDoc.getNames().get(0), (EntityNameImpl)entity.getNames().get(0));
		assertPrincipalTrue(personDoc, entity.getPrincipals().get(0));
		assertExtIdTrue(personDoc, entity.getExternalIdentifiers());
		
		assertAffiliationTrue(personDoc.getAffiliations().get(0), entity.getAffiliations().get(0));
		assertEmpInfoTrue(personDoc.getAffiliations().get(0).getEmpInfos().get(0), entity.getEmploymentInformation().get(0));
		//		List<String> groupIds = groupService.getDirectMemberGroupIds("g1");
//		System.out.println( groupIds );
//		assertTrue( "g1 must contain group g2", groupIds.contains( "g2" ) );
//		assertFalse( "g1 must not contain group g3", groupIds.contains( "g3" ) );
//
//		groupIds = groupService.getDirectMemberGroupIds("g2");
//		System.out.println( groupIds );
//		assertTrue( "g2 must contain group g3", groupIds.contains( "g3" ) );
//		assertFalse( "g2 must not contain group g4 (inactive)", groupIds.contains( "g4" ) );
		
	}
	
	@Test
	public void testLoadToPersonDocument() {
		KimEntityImpl entity = (KimEntityImpl)KIMServiceLocator.getIdentityService().getEntity("ent123");
		IdentityManagementPersonDocument personDoc = new IdentityManagementPersonDocument();
		uiDocumentService.loadEntityToPersonDoc(personDoc, entity);
        EntityEntityTypeImpl entityType = entity.getEntityTypes().get(0);
        personDoc.getExternalIdentifiers();
		assertAddressTrue((PersonDocumentAddress)personDoc.getAddrs().get(0), (EntityAddressImpl)entityType.getAddresses().get(0));
		assertPhoneTrue((PersonDocumentPhone)personDoc.getPhones().get(0), (EntityPhoneImpl)entityType.getPhoneNumbers().get(0));
		assertEmailTrue((PersonDocumentEmail)personDoc.getEmails().get(0), (EntityEmailImpl)entityType.getEmailAddresses().get(0));
		assertNameTrue((PersonDocumentName)personDoc.getNames().get(0), (EntityNameImpl)entity.getNames().get(0));
		//assertPrincipalTrue(personDoc, entity.getPrincipals().get(0));
		assertExtIdTrue(personDoc, entity.getExternalIdentifiers());
		assertAffiliationTrue(personDoc.getAffiliations().get(0), entity.getAffiliations().get(0));
		assertEmpInfoTrue(personDoc.getAffiliations().get(0).getEmpInfos().get(0), entity.getEmploymentInformation().get(0));

	}
	
	// test principal membership
	@Test
	public void testSetAttributeEntry() {
		PersonDocumentRole personDocRole = initPersonDocRole();
        KimTypeService kimTypeService = (KimTypeServiceBase)KIMServiceLocator.getService(personDocRole.getKimRoleType().getKimTypeServiceName());
		personDocRole.setDefinitions(kimTypeService.getAttributeDefinitions(personDocRole.getKimRoleType()));

		personDocRole.setAttributeEntry( uiDocumentService.getAttributeEntries( personDocRole.getDefinitions() ) );
		for (Object key : personDocRole.getAttributeEntry().keySet()) {
			if (key.equals(KimAttributes.NAMESPACE_CODE)) {
				Map value = (Map)personDocRole.getAttributeEntry().get(key);
				assertEquals("Parameter Namespace Code", value.get("label"));
				assertEquals("Nmspc Cd", value.get("shortLabel"));
				assertEquals(new Integer(20), value.get("maxLength"));
			} else if (key.equals("campusCode")) {
				Map value = (Map)personDocRole.getAttributeEntry().get(key);
				assertEquals("Campus Code", value.get("label"));
				assertEquals("Campus Code", value.get("shortLabel"));
				assertEquals(new Integer(2), value.get("maxLength"));
			} else {
				assertFalse("Should not have this key "+key, true);
			}
		}
	}
	
	private PersonDocumentRole initPersonDocRole() {
//		Map pkMap = new HashMap();
//		pkMap.put("roleId", "r1");
//		PersonDocumentRole docRole = (PersonDocumentRole)uiDocumentService.getBusinessObjectService().findByPrimaryKey(PersonDocumentRole.class, pkMap);
		PersonDocumentRole docRole = new PersonDocumentRole();
		docRole.setKimTypeId("roleType1");
		docRole.setRoleId("r1");
		KimTypeImpl kimType = new KimTypeImpl();
		kimType.setKimTypeId("roleType1");
		kimType.setKimTypeServiceName("kimRoleTypeService");
		List<KimTypeAttributeImpl> attributeDefinitions = new ArrayList<KimTypeAttributeImpl>();
		Map pkMap = new HashMap();
		pkMap.put("kimTypeAttributeId", "kimAttr3");
		KimTypeAttributeImpl attr1 = (KimTypeAttributeImpl)uiDocumentService.getBusinessObjectService().findByPrimaryKey(KimTypeAttributeImpl.class, pkMap);

//		attr1.setKimAttributeId("kimAttrDefn2");
//		attr1.setSortCode("a");
//		attr1.setKimTypeAttributeId("kimAttr3");

		attributeDefinitions.add(attr1);
//		attr1 = new KimTypeAttributeImpl();
//		attr1.setKimAttributeId("kimAttrDefn3");
//		attr1.setSortCode("b");
//		attr1.setKimTypeAttributeId("kimAttr4");
		
		pkMap.put("kimTypeAttributeId", "kimAttr4");
		KimTypeAttributeImpl attr2 = (KimTypeAttributeImpl)uiDocumentService.getBusinessObjectService().findByPrimaryKey(KimTypeAttributeImpl.class, pkMap);

		attributeDefinitions.add(attr2);
		kimType.setAttributeDefinitions(attributeDefinitions);
		docRole.setKimRoleType(kimType);
		
		return docRole;
	}
	
	// init section
	private IdentityManagementPersonDocument initPersonDoc() {
		IdentityManagementPersonDocument personDoc = new IdentityManagementPersonDocument();
		personDoc.setEntityId("ent123");
		personDoc.setDocumentNumber("1");
		personDoc.setPrincipalId("pid123");
		personDoc.setPrincipalName("test");
		personDoc.setTaxId("12345-67");
//		personDoc.setUnivId("1234567890");
		personDoc.setAffiliations(initAffiliations());
		personDoc.setNames(initNames());
		personDoc.setAddrs(initAddresses());
		//personDoc.setRoles(initRoles());
		//personDoc.setGroups();
		personDoc.setPhones(initPhones());
		personDoc.setEmails(initEmails());	
		return personDoc;
	}
	
	
	private List<PersonDocumentName> initNames() {
		List<PersonDocumentName> docNames = new ArrayList<PersonDocumentName>();
			PersonDocumentName docName = new PersonDocumentName();
			docName.setEntityNameId("nameId123");
			docName.setNameTypeCode("PREFERRED");
			docName.setFirstName("John");
			docName.setLastName("Doe");
			docName.setMiddleName("M");
			docName.setTitle("Mr");
			docName.setSuffix("Jr");
			docName.setActive(true);
			docName.setDflt(true);
			docNames.add(docName);
		return docNames;
	}
	
	private List<PersonDocumentAffiliation> initAffiliations() {
		List<PersonDocumentAffiliation> docAffiliations = new ArrayList<PersonDocumentAffiliation>();
			PersonDocumentAffiliation docAffiliation = new PersonDocumentAffiliation();
			docAffiliation.setAffiliationTypeCode("FCLTY");
			docAffiliation.setEntityAffiliationId("aflID123");
			docAffiliation.setCampusCode("BL");
			docAffiliation.setActive(true);
			docAffiliation.setDflt(true);
			
			// EntityAffiliationImpl does not define empinfos as collection
			docAffiliations.add(docAffiliation);
			List<PersonDocumentEmploymentInfo> docEmploymentInformations = new ArrayList<PersonDocumentEmploymentInfo>();
				PersonDocumentEmploymentInfo docEmpInfo = new PersonDocumentEmploymentInfo();
				docEmpInfo.setEmployeeId("12345");
				docEmpInfo.setEntityAffiliationId(docAffiliation.getEntityAffiliationId());
				docEmpInfo.setEntityEmploymentId("empId123");
				docEmpInfo.setEmploymentRecordId("emprec1");
				docEmpInfo.setBaseSalaryAmount(new KualiDecimal(8000));
				docEmpInfo.setPrimaryDepartmentCode("BL-CHEM");
				docEmpInfo.setEmployeeStatusCode("A");
				docEmpInfo.setEmployeeTypeCode("P");
				docEmpInfo.setActive(true);
				docEmploymentInformations.add(docEmpInfo);
			docAffiliation.setEmpInfos(docEmploymentInformations);
		
		return docAffiliations;

	}

	private PersonDocumentPrivacy initPrivacyReferences(EntityPrivacyPreferencesImpl privacyPreferences) {
		PersonDocumentPrivacy docPrivacy = new PersonDocumentPrivacy();
		docPrivacy.setSuppressAddress(true);
		docPrivacy.setSuppressEmail(false);
		docPrivacy.setSuppressName(false);
		docPrivacy.setSuppressPhone(false);
		docPrivacy.setSuppressPersonal(true);
		return docPrivacy;
	}
	private List<PersonDocumentPhone> initPhones() {
		List<PersonDocumentPhone> docPhones = new ArrayList<PersonDocumentPhone>();
			PersonDocumentPhone docPhone = new PersonDocumentPhone();
			docPhone.setPhoneTypeCode("HM");
			docPhone.setEntityPhoneId("phoneId123");
			docPhone.setEntityTypeCode("PERSON");
			docPhone.setPhoneNumber("123-45'6789");
			docPhone.setExtensionNumber("123");
			docPhone.setActive(true);
			docPhone.setDflt(true);
			docPhones.add(docPhone);
		return  docPhones;

	}

	private List<PersonDocumentEmail> initEmails() {
		List<PersonDocumentEmail> emails = new ArrayList<PersonDocumentEmail>();
			PersonDocumentEmail docEmail = new PersonDocumentEmail();
			//docEmail.setEntityId(email.getEntityId());
			docEmail.setEntityEmailId("emailId123");
			docEmail.setEntityTypeCode("PERSON");
			docEmail.setEmailTypeCode("HM");
			docEmail.setEmailAddress("test@abc.com");
			docEmail.setActive(true);
			docEmail.setDflt(true);
			emails.add(docEmail);
		return emails;
	}
	
	private  List<PersonDocumentAddress> initAddresses() {
		List<PersonDocumentAddress> docAddresses = new ArrayList<PersonDocumentAddress>();
			PersonDocumentAddress docAddress = new PersonDocumentAddress();
			docAddress.setEntityTypeCode("PERSON");
			docAddress.setEntityAddressId("addrId123");
			docAddress.setAddressTypeCode("HM");
			docAddress.setLine1("PO box 123");
			docAddress.setStateCode("IN");
			docAddress.setPostalCode("46123");
			docAddress.setCountryCode("US");
			docAddress.setCityName("Indianapolis");
			docAddress.setActive(true);
			docAddress.setDflt(true);
			docAddresses.add(docAddress);
		return docAddresses;
	}

// assert section
	
	private void assertExtIdTrue(IdentityManagementPersonDocument personDoc, List<EntityExternalIdentifierImpl> extIds) {
		for (EntityExternalIdentifierImpl extId : extIds) {
			if (extId.getExternalIdentifierTypeCode().equals("TAX")) {
				assertEquals(personDoc.getTaxId(), extId.getExternalId());
				
			}
//			else if (extId.getExternalIdentifierTypeCode().equals("LOGON")) {
//				assertEquals(personDoc.getUnivId(), extId.getExternalId());
//				
//			}
			
		}
	}

	private void assertPrincipalTrue(IdentityManagementPersonDocument personDoc, KimPrincipalImpl principal) {
		assertEquals(personDoc.getPrincipalId(), principal.getPrincipalId());
		assertEquals(personDoc.getPrincipalName(), principal.getPrincipalName());
	}

	private void assertAddressTrue(PersonDocumentAddress docAddress, EntityAddressImpl entityAddress) {
		assertEquals(docAddress.getAddressTypeCode(), entityAddress.getAddressTypeCode());
		assertEquals(docAddress.getCountryCode(), entityAddress.getCountryCode());
		assertEquals(docAddress.getLine1(), entityAddress.getLine1());
		assertEquals(docAddress.getCityName(), entityAddress.getCityName());
		assertEquals(docAddress.getPostalCode(), entityAddress.getPostalCode());
		assertEquals(docAddress.getStateCode(), entityAddress.getStateCode());
	}

	private void assertEmailTrue(PersonDocumentEmail docEmail, EntityEmailImpl entityEmail) {
		assertEquals(docEmail.getEntityEmailId(), entityEmail.getEntityEmailId());
		assertEquals(docEmail.getEmailAddress(), entityEmail.getEmailAddress());
		assertEquals(docEmail.getEmailTypeCode(), entityEmail.getEmailTypeCode());
	}

	private void assertPhoneTrue(PersonDocumentPhone docPhone, EntityPhoneImpl entityPhone) {
		assertEquals(docPhone.getEntityPhoneId(), entityPhone.getEntityPhoneId());
		assertEquals(docPhone.getCountryCode(), entityPhone.getCountryCode());
		assertEquals(docPhone.getPhoneNumber(), entityPhone.getPhoneNumber());
		assertEquals(docPhone.getExtensionNumber(), entityPhone.getExtensionNumber());
		assertEquals(docPhone.getPhoneTypeCode(), entityPhone.getPhoneTypeCode());
	}

	private void assertNameTrue(PersonDocumentName docName, EntityNameImpl entityName) {
		assertEquals(docName.getEntityNameId(), entityName.getEntityNameId());
		assertEquals(docName.getFirstName(), entityName.getFirstName());
		assertEquals(docName.getLastName(), entityName.getLastName());
		assertEquals(docName.getNameTypeCode(), entityName.getNameTypeCode());
		assertEquals(docName.getSuffix(), entityName.getSuffix());
		assertEquals(docName.getTitle(), entityName.getTitle());
	}
	
	private void assertAffiliationTrue(PersonDocumentAffiliation docAffln, EntityAffiliationImpl entityAffln) {
		assertEquals(docAffln.getAffiliationTypeCode(), entityAffln.getAffiliationTypeCode());
		assertEquals(docAffln.getCampusCode(), entityAffln.getCampusCode());
		assertEquals(docAffln.getEntityAffiliationId(), entityAffln.getEntityAffiliationId());
	}

	private void assertEmpInfoTrue(PersonDocumentEmploymentInfo docEmpInfo, EntityEmploymentInformation entityEmpInfo) {
		assertEquals(docEmpInfo.getEmployeeId(), entityEmpInfo.getEmployeeId());
		assertEquals(docEmpInfo.getEmployeeTypeCode(), entityEmpInfo.getEmployeeTypeCode());
		assertEquals(docEmpInfo.getEmployeeStatusCode(), entityEmpInfo.getEmployeeStatusCode());
		assertEquals(docEmpInfo.getEmploymentRecordId(), entityEmpInfo.getEmploymentRecordId());
		assertEquals(docEmpInfo.getBaseSalaryAmount(), entityEmpInfo.getBaseSalaryAmount());
	}


}
