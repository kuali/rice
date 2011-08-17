package org.kuali.rice.core.api.cache;


import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;

import javax.jws.WebMethod;
import javax.jws.WebParam;

import javax.jws.soap.SOAPBinding;

/**
 * A service that enables executes various tasks on rice's caching infrastructure.
 */
//Specifically leaving off the @WebService annotation because this should be set manually when the service is exported.
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public interface CacheService {

    /**
     * Evicts a object from the cache based on a given key.  Keys will always be Strings even though
     * various caching implementations may allow generic Object types as keys.
     *
     * @param cache the cache store to remove the key from. cannot be null or blank.
     * @param key the key to remove. cannot be null or blank.
     * @throws IllegalArgumentException if the cache or key is null or blank.
     */
    @WebMethod(operationName="evict")
    void evict(@WebParam(name = "cache") String cache, @WebParam(name = "key") String key) throws RiceIllegalArgumentException;

    /**
     * Clears an entire cache of all objects.
     *
     * @param cache the cache store to clear.  cannot be null or blank.
     * @throws IllegalArgumentException if the cache is null or blank.
     */
    @WebMethod(operationName="clear")
	void clear(@WebParam(name = "cache") String cache) throws RiceIllegalArgumentException;
}
