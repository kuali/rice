package org.kuali.rice.kim.api.identity;

import org.apache.commons.collections.CollectionUtils;
import org.kuali.rice.core.api.mo.common.Defaultable;
import org.kuali.rice.core.api.mo.common.active.Inactivatable;

import java.util.Collection;

public class EntityUtils {

    private EntityUtils() {
        throw new UnsupportedOperationException("do not call.");
    }

    public static <T extends Defaultable & Inactivatable> T getDefaultItem( Collection<T> collection ) {
		// find the default entry
        if (CollectionUtils.isEmpty(collection)) {
            return null;
        }
		for ( T item : collection ) {
			if ( item.isDefaultValue() && item.isActive() ) {
				return (T)item;
			}
		}
		// if no default, return the first
		for ( T item : collection ) {
		    return item;
		}
		// if neither, return null
		return null;
	}
}
