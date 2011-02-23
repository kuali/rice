package org.kuali.rice.shareddata.impl.campus;

import static java.util.Collections.singletonMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.shareddata.api.campus.Campus;
import org.kuali.rice.shareddata.api.campus.CampusService;
import org.kuali.rice.shareddata.api.campus.CampusType;

public class CampusServiceImpl implements CampusService {
	private static Logger LOG = Logger.getLogger(CampusServiceImpl.class);
	//private KualiModuleService kualiModuleService;
	private BusinessObjectService boService;
	
	@Override
	public Campus getCampus(String code) {
		if (StringUtils.isBlank(code)) {
            LOG.debug("The campus code cannot be empty String.");
            return null;
        }

        CampusBo campusBo = boService.findByPrimaryKey(CampusBo.class, singletonMap("code", code));
        //CampusBo campusBo =  kualiModuleService.getResponsibleModuleService(CampusBo.class)
        //        .getExternalizableBusinessObject(CampusBo.class, campusMap);

        return CampusBo.to(campusBo);
	}

	@Override
	public List<Campus> findAllCampuses() {
		List<CampusBo> campusBos = (List<CampusBo>)boService.findAll(CampusBo.class);
        //List<CampusBo> campusBos =  kualiModuleService.getResponsibleModuleService(CampusBo.class)
        //        .getExternalizableBusinessObjectsList(CampusBo.class, new HashMap<String, Object>());

        return this.convertListOfCampusBosToImmutables(campusBos);
	}

	@Override
	public CampusType getCampusType(String code) {
		if (StringUtils.isBlank(code)) {
            LOG.debug("The campus type code cannot be empty String.");
            return null;
        }

		CampusTypeBo campusTypeBo = boService.findByPrimaryKey(CampusTypeBo.class, singletonMap("code", code));
        //CampusTypeBo campusTypeBo =  kualiModuleService.getResponsibleModuleService(CampusTypeBo.class)
        //        .getExternalizableBusinessObject(CampusTypeBo.class, campusTypeMap);

        return CampusTypeBo.to(campusTypeBo);
	}

	@Override
	public List<CampusType> findAllCampusTypes() {
		List<CampusTypeBo> campusTypeBos = (List<CampusTypeBo>)boService.findAll(CampusTypeBo.class);
		//List<CampusTypeBo> campusTypeBos =  kualiModuleService.getResponsibleModuleService(CampusTypeBo.class)
        //	.getExternalizableBusinessObjectsList(CampusTypeBo.class, new HashMap<String, Object>());

		return this.convertListOfCampusTypesBosToImmutables(campusTypeBos);
	}

	/**
     * Sets the kualiModuleService attribute value.
     * 
     * @param kualiModuleService The kualiModuleService to set.
     */
    /*public void setKualiModuleService(KualiModuleService kualiModuleService) {
        this.kualiModuleService = kualiModuleService;
    }*/
    
    public void setBusinessObjectService(BusinessObjectService boService) {
        this.boService = boService;
    }

    private List<Campus> convertListOfCampusBosToImmutables(List<CampusBo> campusBos) {
        ArrayList<Campus> campuses = new ArrayList<Campus>();
        for (CampusBo bo : campusBos) {
            Campus campus = CampusBo.to(bo);
            campuses.add(campus) ;
        }
        return Collections.unmodifiableList(campuses);
    }
    
    private List<CampusType> convertListOfCampusTypesBosToImmutables(List<CampusTypeBo> campusTypeBos) {
        ArrayList<CampusType> campusTypes = new ArrayList<CampusType>();
        for (CampusTypeBo bo : campusTypeBos) {
            CampusType campusType = CampusTypeBo.to(bo);
            campusTypes.add(campusType) ;
        }
        return Collections.unmodifiableList(campusTypes);
    }
}
