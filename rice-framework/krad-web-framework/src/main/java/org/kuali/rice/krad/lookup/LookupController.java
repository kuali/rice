/**
 * Copyright 2005-2014 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.krad.lookup;

import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.web.controller.UifControllerBase;
import org.kuali.rice.krad.web.form.UifFormBase;
import org.kuali.rice.krad.web.service.CollectionControllerService;
import org.kuali.rice.krad.web.service.ControllerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller that handles requests for a {@link LookupView}.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Controller
@RequestMapping(value = KRADConstants.ControllerMappings.LOOKUP)
public class LookupController extends UifControllerBase {

    /**
     * {@inheritDoc}
     */
    @Override
    protected LookupForm createInitialForm() {
        return new LookupForm();
    }

    /**
     * @see LookupControllerService#search(org.kuali.rice.krad.lookup.LookupForm)
     */
    @RequestMapping(params = "methodToCall=search")
    public ModelAndView search(LookupForm lookupForm) {
        return getControllerService().search(lookupForm);
    }

    /**
     * @see LookupControllerService#clearValues(org.kuali.rice.krad.lookup.LookupForm)
     */
    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=clearValues")
    public ModelAndView clearValues(LookupForm lookupForm) {
        return getControllerService().clearValues(lookupForm);
    }

    /**
     * @see LookupControllerService#selectAllPages(org.kuali.rice.krad.lookup.LookupForm)
     */
    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=selectAllPages")
    public ModelAndView selectAllPages(LookupForm lookupForm) {
        return getControllerService().selectAllPages(lookupForm);
    }

    /**
     * @see LookupControllerService#deselectAllPages(org.kuali.rice.krad.lookup.LookupForm)
     */
    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=deselectAllPages")
    public ModelAndView deselectAllPages(LookupForm lookupForm) {
        return getControllerService().deselectAllPages(lookupForm);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @RequestMapping(params = "methodToCall=retrieveCollectionPage")
    public ModelAndView retrieveCollectionPage(UifFormBase form) {
        return getCollectionControllerService().retrieveCollectionPage(form);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @RequestMapping(method = RequestMethod.GET, params = "methodToCall=tableJsonRetrieval")
    public ModelAndView tableJsonRetrieval(UifFormBase form) {
        return getCollectionControllerService().tableJsonRetrieval(form);
    }

    /**
     * @see LookupControllerService#returnSelected(org.kuali.rice.krad.lookup.LookupForm,
     * org.springframework.web.servlet.mvc.support.RedirectAttributes)
     */
    @RequestMapping(method = RequestMethod.POST, params = "methodToCall=returnSelected")
    public String returnSelected(LookupForm lookupForm, final RedirectAttributes redirectAttributes) {
        return getControllerService().returnSelected(lookupForm, redirectAttributes);
    }

    @Override
    protected LookupControllerService getControllerService() {
        return (LookupControllerService) super.getControllerService();
    }

    @Override
    @Autowired
    @Qualifier("lookupControllerService")
    public void setControllerService(ControllerService controllerService) {
        super.setControllerService(controllerService);
    }

    @Override
    @Autowired
    @Qualifier("lookupCollectionControllerService")
    public void setCollectionControllerService(CollectionControllerService collectionControllerService) {
        super.setCollectionControllerService(collectionControllerService);
    }

}
