package org.kuali.rice.kew.edl.extract.dao;

import java.util.List;

import org.kuali.rice.kew.edl.extract.Dump;
import org.kuali.rice.kew.edl.extract.Fields;


public interface ExtractDAO {
	
    public Dump getDumpByRouteHeaderId(Long routeHeaderId);

    public List getFieldsByRouteHeaderId(Long routeHeaderId);
    
    public void saveDump(Dump dump);
    
    public void deleteDump(Long routeHeaderId);

	public void saveField(Fields field);
    


}
