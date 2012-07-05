package org.kuali.rice.krad.uif;

import java.awt.print.Pageable;

/**
 * Created by IntelliJ IDEA.
 * User: sonam
 * Date: 7/3/12
 * Time: 10:48 AM
 * To change this template use File | Settings | File Templates.
 */
public enum AjaxReturnTypes {
     UPADATEPAGE("update-page"), UPDATEPAGEERRORS("update-pageErrors"), UPDATECOMPONENT("update-component"), REDIRECT("redirect"), SHOWINCIDENT("show-incident")  ;

     private String key;

     AjaxReturnTypes(String key) {
         this.key = key;
     }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
