package org.kuali.rice.kim.api.entity;


import org.apache.commons.collections.CollectionUtils;
import org.kuali.rice.krad.bo.DefaultableInactivateable;

import java.util.Collection;

public class EntityUtils {
    public static <T extends DefaultableInactivateable> T getDefaultItem( Collection<T> collection ) {
		// find the default entry
        if (CollectionUtils.isEmpty(collection)) {
            return null;
        }
		for ( DefaultableInactivateable item : collection ) {
			if ( item.isDefaultValue() && item.isActive() ) {
				return (T)item;
			}
		}
		// if no default, return the first
		for ( DefaultableInactivateable item : collection ) {
		    return (T)item;
		}
		// if neither, return null
		return null;
	}
}
