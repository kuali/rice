package org.kuali.rice.core.api.mo;

import org.apache.commons.beanutils.PropertyUtils;

class Bean implements Beanable {
    InnerBean innerBean = new InnerBean();

    @Override
    public InnerBean getInnerBean() {
        return innerBean;
    }
}