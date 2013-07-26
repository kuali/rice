package org.kuali.rice.core.api.mo.common;

import org.kuali.rice.core.api.mo.common.active.InactivatableFromTo;

public interface Historical extends InactivatableFromTo{

    Long getHistoryId();

    boolean isActiveNow();

}
