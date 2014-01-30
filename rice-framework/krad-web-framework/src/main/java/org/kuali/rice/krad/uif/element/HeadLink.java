/*
 * Copyright 2006-2014 The Kuali Foundation
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
import org.kuali.rice.krad.datadictionary.parse.BeanTags;

/**
 *  builds <link> tags with various attributes
 *  in the <head> of the html document
 *
 */
@BeanTags({@BeanTag(name = "view-headLink", parent = "Uif-HeadLink")})
public class HeadLink extends ContentElementBase  {

    private String media;
    private String href;
    private String relation;
    private String type;
    private String includeCondition;


    public HeadLink() {
        super();
    }


    /**
     *
     * @return   media
     */
    @BeanTagAttribute(name = "media")
    public String getMedia() {
        return media;
    }

    /**
     *
     * @param media
     */
    public void setMedia(String media) {
        this.media = media;
    }

    /**
     *
     * @return  href
     */
    @BeanTagAttribute(name = "href")
    public String getHref() {
        return href;
    }

    /**
     *
     * @param href
     */
    public void setHref(String href) {
        this.href = href;
    }

    /**
     *  rel attribute for <link>
     *
     * @return relation
     */
    @BeanTagAttribute(name = "relation")
    public String getRelation() {
        return relation;
    }

    /**
     *
     * @param rel
     */
    public void setRelation(String relation) {
        this.relation = relation;
    }

    /**
     *
     * @return type
     */
    @BeanTagAttribute(name = "type")
    public String getType() {
        return type;
    }

    /**
     *
     * @param type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * includeCondition wraps conditional html comments for
     * choosing css files based on browser info
     * exampe:
     * for the folling code
     *  {@code
     *      <!--[if  ie 9]>
     *         <link href="ie9.css" type="text/stylesheet"></link>
     *      <![endif]-->
     *  }
     *
     *  the includeCondition would be   "if  ie 9"
     *
     * @return  includeCondition
     */
    @BeanTagAttribute(name = "includeCondition")
    public String getIncludeCondition() {
        return includeCondition;
    }

    /**
     *
     * @param includeCondition
     */
    public void setIncludeCondition(String includeCondition) {
        this.includeCondition = includeCondition;
    }

    @Override
    protected <T> void copyProperties(T component) {
        super.copyProperties(component);

        HeadLink headLinkCopy = (HeadLink) component;

        headLinkCopy.setMedia(this.media);
        headLinkCopy.setHref(this.href);
        headLinkCopy.setRelation(this.relation);
        headLinkCopy.setType(this.type);
        headLinkCopy.setIncludeCondition(this.includeCondition);

    }

}
