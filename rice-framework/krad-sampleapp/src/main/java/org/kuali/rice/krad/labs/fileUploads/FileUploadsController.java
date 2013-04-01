package org.kuali.rice.krad.labs.fileUploads;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.krad.labs.KradLabsController;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.web.form.UifFormBase;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Controller class for the file uploads lab view
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Controller
@RequestMapping(value = "/fileUploads")
public class FileUploadsController extends KradLabsController {

    @Override
    protected FileUploadsForm createInitialForm(HttpServletRequest request) {
        return new FileUploadsForm();
    }

    @RequestMapping(params = "methodToCall=uploadOne")
    public ModelAndView uploadOne(@ModelAttribute("KualiForm") UifFormBase form, BindingResult result,
            HttpServletRequest request, HttpServletResponse response) {
        FileUploadsForm uploadForm = (FileUploadsForm) form;

        if ((uploadForm.getUploadOne() == null) || StringUtils.isBlank(uploadForm.getUploadOne().getName())) {
            GlobalVariables.getMessageMap().putError(KRADConstants.GLOBAL_ERRORS, "labs.fileUpload", "one");
        }

        return getUIFModelAndView(form);
    }

    @RequestMapping(params = "methodToCall=uploadTwo")
    public ModelAndView uploadTwo(@ModelAttribute("KualiForm") UifFormBase form, BindingResult result,
            HttpServletRequest request, HttpServletResponse response) {
        FileUploadsForm uploadForm = (FileUploadsForm) form;

        if ((uploadForm.getUploadTwo() == null) || StringUtils.isBlank(uploadForm.getUploadTwo().getName())) {
            GlobalVariables.getMessageMap().putError(KRADConstants.GLOBAL_ERRORS, "labs.fileUpload", "two");
        }

        return getUIFModelAndView(form);
    }
}
