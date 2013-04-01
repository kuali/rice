package org.kuali.rice.krad.labs.fileUploads;

import org.kuali.rice.krad.labs.KradLabsForm;
import org.kuali.rice.krad.uif.util.SessionTransient;
import org.springframework.web.multipart.MultipartFile;

/**
 * Form class for the file uploads lab view
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class FileUploadsForm extends KradLabsForm {
    private static final long serialVersionUID = -6189618372290245896L;

    @SessionTransient
    private MultipartFile uploadOne;
    @SessionTransient
    private MultipartFile uploadTwo;

    public MultipartFile getUploadOne() {
        return uploadOne;
    }

    public void setUploadOne(MultipartFile uploadOne) {
        this.uploadOne = uploadOne;
    }

    public MultipartFile getUploadTwo() {
        return uploadTwo;
    }

    public void setUploadTwo(MultipartFile uploadTwo) {
        this.uploadTwo = uploadTwo;
    }
}
