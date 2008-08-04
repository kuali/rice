package edu.iu.uis.eden.edl.extract.dao;

import java.util.List;

import edu.iu.uis.eden.edl.extract.Dump;
import edu.iu.uis.eden.edl.extract.Fields;

public interface ExtractDAO {
	
    public Dump getDumpByRouteHeaderId(Long routeHeaderId);

    public List getFieldsByRouteHeaderId(Long routeHeaderId);
    
    public void saveDump(Dump dump);
    
    public void deleteDump(Long routeHeaderId);

	public void saveField(Fields field);
    


}
