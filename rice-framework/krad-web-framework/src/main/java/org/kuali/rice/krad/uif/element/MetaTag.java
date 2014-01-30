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
 *  builds <meta> tags with various attributes
 *  in the <head> of the html document
 *
 */

@BeanTags({@BeanTag(name = "view-MetaTag", parent = "Uif-MetaTag")})
public class MetaTag extends ContentElementBase  {


    private String name;
    private String content;
    private String http_equiv;


    public MetaTag() {
        super();
    }

    /**
     *
     * @return  name
     */
    @BeanTagAttribute(name = "name")
    public String getName() {
        return name;
    }

    /**  sets name attribute for <meta>
     *
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @return  content
     */
    @BeanTagAttribute(name = "content")
    public String getContent() {
        return content;
    }

    /**  sets content attribute for <meta>
     *
     * @param content
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     *
     * @return  http_equiv
     */
    @BeanTagAttribute(name = "http_equiv")
    public String getHttp_equiv() {
        return http_equiv;
    }

    /**  sets http_equiv attribute for <meta>
     *
     * @param http_equiv
     */
    public void setHttp_equiv(String http_equiv) {
        this.http_equiv = http_equiv;
    }


    @Override
    protected <T> void copyProperties(T component) {
        super.copyProperties(component);

        MetaTag MetaTagCopy = (MetaTag) component;

        MetaTagCopy.setName(this.name);
        MetaTagCopy.setContent(this.content);
        MetaTagCopy.setHttp_equiv(this.http_equiv);

    }



}
