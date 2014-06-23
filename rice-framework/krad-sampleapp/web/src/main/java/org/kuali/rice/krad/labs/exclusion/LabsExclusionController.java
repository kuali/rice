package org.kuali.rice.krad.labs.exclusion;

import javax.servlet.http.HttpServletRequest;

import org.kuali.rice.krad.uif.component.Component;
import org.kuali.rice.krad.web.controller.UifControllerBase;
import org.kuali.rice.krad.web.form.UifFormBase;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller for demonstrating the use of the {@link Component#getExcludeIf()}
 * and {@link Component#getExcludeUnless()} properties.
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Controller
@RequestMapping(value = "/exclusion")
public class LabsExclusionController extends UifControllerBase {

	@Override
	protected UifFormBase createInitialForm(HttpServletRequest request) {
		return new LabsExclusionForm();
	}

}
