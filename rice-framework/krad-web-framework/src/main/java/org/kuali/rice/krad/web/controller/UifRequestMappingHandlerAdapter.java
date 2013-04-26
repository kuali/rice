package org.kuali.rice.krad.web.controller;

import org.kuali.rice.krad.web.bind.UifServletRequestDataBinderFactory;
import org.springframework.web.method.support.InvocableHandlerMethod;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.ServletRequestDataBinderFactory;

import java.util.List;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class UifRequestMappingHandlerAdapter extends RequestMappingHandlerAdapter {

    @Override
    protected ServletRequestDataBinderFactory createDataBinderFactory(
            List<InvocableHandlerMethod> binderMethods) throws Exception {
        return new UifServletRequestDataBinderFactory(binderMethods, getWebBindingInitializer());
    }
}
