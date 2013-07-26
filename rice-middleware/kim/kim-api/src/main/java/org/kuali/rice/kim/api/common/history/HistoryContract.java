package org.kuali.rice.kim.api.common.history;

import org.kuali.rice.core.api.mo.common.active.InactivatableFromTo;

public interface HistoryContract extends InactivatableFromTo{

    String getHistoryId();

    boolean isActiveNow();

}
