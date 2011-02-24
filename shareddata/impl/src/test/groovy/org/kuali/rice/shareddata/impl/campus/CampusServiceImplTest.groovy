package org.kuali.rice.shareddata.impl.campus

import groovy.mock.interceptor.MockFor

import java.util.Map

import org.junit.Assert
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.kuali.rice.kns.service.BusinessObjectService
import org.kuali.rice.shareddata.api.campus.Campus
import org.kuali.rice.shareddata.api.campus.CampusType

class CampusServiceImplTest {
	private def MockFor mockBoService
	private final shouldFail = new GroovyTestCase().&shouldFail
	private CampusServiceImpl campusService;
	static Map<String, CampusBo> sampleCampuses = new HashMap<String, CampusBo>()
	static Map<String, CampusTypeBo> sampleCampusTypes = new HashMap<String, CampusTypeBo>()
	
	@BeforeClass
	static void createSampleCountryBOs() {
	  CampusBo amesCampusBo = new CampusBo(active: true, code: "AMES", name: "IOWA STATE UNIVERSITY - AMES", shortName: "ISU - AMES", campusTypeCode: "F")
	  CampusBo indCampusBo = new CampusBo(active: true, code: "IND", name: "INDIANA UNIVERSITY - INDIANAPOLIS", shortName: "IU - IND", campusTypeCode: "B")
	  CampusTypeBo fiscalTypeBo = new CampusTypeBo(active: true, code: "F", name: "FISCAL")
	  CampusTypeBo bothTypeBo = new CampusTypeBo(active: true, code: "B", name: "BOTH")
	  for (bo in [amesCampusBo, indCampusBo]) {
		sampleCampuses.put(bo.code, bo)
	  }
	  for (bo in [fiscalTypeBo, bothTypeBo]) {
		  sampleCampusTypes.put(bo.code, bo)
	  }
	}
	
	@Before
    void setupBoServiceMockContext() {
        mockBoService = new MockFor(BusinessObjectService)
        campusService = new CampusServiceImpl()
    }
	
	@Test
	public void testGetCampusEmptyCode() {
	  Campus c = campusService.getCampus("")
	  Assert.assertNull(c)
	}
  
	@Test
	public void testGetCampusNullCode() {
	  Campus c = campusService.getCampus(null)
	  Assert.assertNull(c)
	}
	
	@Test
	public void testGetCampusTypeEmptyCode() {
	  CampusType ct = campusService.getCampusType("")
	  Assert.assertNull(ct)
	}
  
	@Test
	public void testGetCampusTypeNullCode() {
	  CampusType ct = campusService.getCampusType(null)
	  Assert.assertNull(ct)
	}
	
	@Test
	public void testGetCampus() {
		mockBoService.demand.findByPrimaryKey (1..2) {
			Class clazz, Map map -> return sampleCampuses.get(map.get("code"))
		}
        def boService = mockBoService.proxyDelegateInstance()
        campusService.setBusinessObjectService(boService);
		for (CampusBo campusBo in sampleCampuses.values()) {
        	Assert.assertEquals (CampusBo.to(campusBo), campusService.getCampus(campusBo.code))
		}   
	}
	
	@Test
	public void testGetCampusType() {
		mockBoService.demand.findByPrimaryKey (1..2) {
			Class clazz, Map map -> return sampleCampusTypes.get(map.get("code"))
		}
		def boService = mockBoService.proxyDelegateInstance()
		campusService.setBusinessObjectService(boService);
		for (CampusTypeBo campusTypeBo in sampleCampusTypes.values()) {
			Assert.assertEquals (CampusTypeBo.to(campusTypeBo), campusService.getCampusType(campusTypeBo.code))
		}
	}
	
	@Test
	public void testGetAllCampuses() {
		mockBoService.demand.findMatching (1..1) {
			Class clazz, Map map -> return new ArrayList<Campus>(sampleCampuses.values())
		}
		def boService = mockBoService.proxyDelegateInstance()
		campusService.setBusinessObjectService(boService);
		
		//create list of campuses
		List<Campus> campusList = new ArrayList<Campus>();
		for (CampusBo campusBo in sampleCampuses.values()) {
			campusList.add(CampusBo.to(campusBo))
		}
		
		//get list from service
		List<Campus> returnedCampuses = campusService.findAllCampuses()
		
		//compare lists
		Assert.assertEquals(campusList.size(), returnedCampuses.size())
		for (Campus campus in campusList) {
			Assert.assertTrue(returnedCampuses.contains(campus))
		}
	}

	public void testGetAllCampusTypes() {
		mockBoService.demand.findMatching (1..1) {
			Class clazz, Map map -> return new ArrayList<Campus>(sampleCampusTypes.values())
		}
		def boService = mockBoService.proxyDelegateInstance()
		campusService.setBusinessObjectService(boService);
		
		//create list of campuses
		List<CampusType> campusTypeList = new ArrayList<CampusType>();
		for (CampusTypeBo campusTypeBo in sampleCampusTypes.values()) {
			campusTypeList.add(CampusTypeBo.to(campusTypeBo))
		}
		
		//get list from service
		List<Campus> returnedCampusTypes = campusService.findAllCampusTypes()
		
		//compare lists
		Assert.assertEquals(campusTypeList.size(), returnedCampusTypes.size())
		for (CampusType campusType in campusTypeList) {
			Assert.assertTrue(returnedCampusTypes.contains(campusType))
		}
	}
}
