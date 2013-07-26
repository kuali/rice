package org.kuali.rice.kim.impl.responsibility;

import org.kuali.rice.kim.api.common.template.Template;
import org.kuali.rice.kim.api.common.template.TemplateContract;
import org.kuali.rice.kim.impl.common.template.TemplateBo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@Entity
@Table(name = "KRIM_RSP_TMPL_T")
public class ResponsibilityTemplateBo extends TemplateBo implements TemplateContract {
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "RSP_TMPL_ID")
    private String id;

    /**
     * Converts a mutable bo to its immutable counterpart
     *
     * @param bo the mutable business object
     * @return the immutable object
     */
    public static Template to(ResponsibilityTemplateBo bo) {
        if (bo == null) {
            return null;
        }

        return Template.Builder.create(bo).build();
    }

    /**
     * Converts a immutable object to its mutable counterpart
     *
     * @param im immutable object
     * @return the mutable bo
     */
    public static ResponsibilityTemplateBo from(Template im) {
        if (im == null) {
            return null;
        }

        ResponsibilityTemplateBo bo = new ResponsibilityTemplateBo();
        bo.id = im.getId();
        bo.setNamespaceCode(im.getNamespaceCode());
        bo.setName(im.getName());
        bo.setDescription(im.getDescription());
        bo.setActive(im.isActive());
        bo.setKimTypeId(im.getKimTypeId());
        bo.setVersionNumber(im.getVersionNumber());
        bo.setObjectId(im.getObjectId());

        return bo;
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


}
