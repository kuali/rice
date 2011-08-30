package org.kuali.rice.core.web.cache;

import org.kuali.rice.krad.web.controller.UifControllerBase;
import org.kuali.rice.krad.web.form.UifFormBase;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping(value = "/core/admin/cache")
public final class CacheAdminController extends UifControllerBase {

    @Override
    protected CacheAdminForm createInitialForm(HttpServletRequest request) {
        return new CacheAdminForm();
    }

    @Override
	@RequestMapping(params = "methodToCall=start")
	public ModelAndView start(@ModelAttribute("KualiForm") UifFormBase form, BindingResult result,
			HttpServletRequest request, HttpServletResponse response) {
        System.out.println("I'm here!!!");
        return super.start(form, result, request, response);
    }
}
