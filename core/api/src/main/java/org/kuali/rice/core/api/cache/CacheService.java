package org.kuali.rice.core.api.cache;


import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;

import javax.jws.WebMethod;
import javax.jws.WebParam;

import javax.jws.soap.SOAPBinding;
import java.util.Collection;

/**
 * A service that enables executes various tasks on rice's caching infrastructure.
 */
//Specifically leaving off the @WebService annotation because this should be set manually when the service is exported.
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public interface CacheService {

    /**
     * Flushes an object from the cache based on a cache target.
     *
     * @param cacheTargets the targets to flush. cannot be null or contain null items
     * @throws IllegalArgumentException if the cacheTargets is null contains a null item.
     */
    @WebMethod(operationName="flush")
    void flush(@WebParam(name = "cacheTargets") Collection<CacheTarget> cacheTargets) throws RiceIllegalArgumentException;
}
