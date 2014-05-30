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
package org.kuali.rice.krad.uif.element;

import org.kuali.rice.krad.datadictionary.parse.BeanTag;
import org.kuali.rice.krad.datadictionary.parse.BeanTagAttribute;

/**
 * Renders a link tag in the head of the html document.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
@BeanTag(name = "headLink", parent = "Uif-HeadLink")
public class HeadLink extends ContentElementBase  {
    private static final long serialVersionUID = -2295905449114970348L;

    private String media;
    private String href;
    private String relation;
    private String type;
    private String includeCondition;

    public HeadLink() {
        super();
    }

    /**
     * Media attribute for link tag.
     *
     * <p>Media attribute to be rendered on this link tag</p>
     *
     * @return  media attribute of link tag
     */
    @BeanTagAttribute
    public String getMedia() {
        return media;
    }

    /**
     * @see HeadLink#getMedia()
     */
    public void setMedia(String media) {
        this.media = media;
    }

    /**
     * Href attribute for link tag.
     *
     * <p>Href attribute to be rendered on this link tag</p>
     *
     * @return  href attribute of link tag
     */
    @BeanTagAttribute
    public String getHref() {
        return href;
    }

    /**
     * @see HeadLink#getHref()
     */
    public void setHref(String href) {
        this.href = href;
    }

    /**
     * Rel attribute for link tag.
     *
     * <p>Rel attribute to be rendered on this link tag</p>
     *
     * @return  rel attribute of link tag
     */
    @BeanTagAttribute
    public String getRelation() {
        return relation;
    }

    /**
     *
     * @see HeadLink#getRelation()
     */
    public void setRelation(String relation) {
        this.relation = relation;
    }

    /**
     * Type attribute for link tag.
     *
     * <p>Type attribute to be rendered on this link tag</p>
     *
     * @return  type attribute of link tag
     */
    @BeanTagAttribute
    public String getType() {
        return type;
    }

    /**
     *
     * @see HeadLink#getType()
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * IncludeCondition wraps custom html comments around link tags.
     *
     * <p>IncludeCondition wraps conditional html comments for
     * choosing css files based on browser info.
     * e.g.
     * for the following code
     *  {@code
     *      <!--[if  ie 9]>
     *         <link href="ie9.css" type="text/stylesheet"></link>
     *      <![endif]-->
     *  }
     *
     *  the includeCondition would be  "if  ie 9"</p>
     *
     * @return  includeCondition
     */
    @BeanTagAttribute
    public String getIncludeCondition() {
        return includeCondition;
    }

    /**
     *
     * @see HeadLink#getIncludeCondition()
     */
    public void setIncludeCondition(String includeCondition) {
        this.includeCondition = includeCondition;
    }

}
