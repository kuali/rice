/*
 * Copyright 2005-2012 The Kuali Foundation
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
jQuery(function () {
    jQuery(".demo-appHeader, .demo-appFooter, .demo-thirdTier").show();
    jQuery(".demo-tweets > div").tweet({
        avatar_size:16,
        count:4,
        username:["kuali", "kuali_feeds"],
        loading_text:"Loading tweets..."
    }).bind("loaded", function () {
                jQuery(this).find("a").attr("target", "_blank");
            });
    linkSelection();
});

function setupExhibitHandlers() {
    jQuery("#ComponentLibraryTabGroup_tabList a").on("click", function () {
        var tabIndex = jQuery(this).parent("li").index();
        var source = jQuery("#demo-exhibitSource > pre:eq(" + tabIndex + ")");
        jQuery("div.uif-syntaxHighlighter:first > div > pre").replaceWith(jQuery(source)[0].outerHTML);
    });
}

function linkSelection() {

    var viewDiv = jQuery("div.uif-view");
    if (jQuery(viewDiv).is(".demo-componentLibView")  || jQuery(viewDiv).is(".demo-componentLibHome")) {
        var url = window.location.href;
        var link = jQuery("#Uif-Navigation").find("a[href='" + url + "']");
        if (link.length) {
            jQuery(link).css("color", "#222222");
            var accordionLi = jQuery(link).closest("li.uif-accordionTab");
            var index = jQuery(accordionLi).index();
            jQuery(accordionLi).parent().accordion("option", "active", index);
        }

        jQuery("a#Demo-LibraryLink").addClass("active");
    }
    else if (jQuery(viewDiv).is(".demo-homeView")) {
        jQuery("a#Demo-HomeLink").addClass("active");
    }
    else if (jQuery(viewDiv).is(".demo-demoView")) {
        jQuery("a#Demo-DemoLink").addClass("active");
    }
    else if (jQuery(viewDiv).is(".demo-downloadView")) {
        jQuery("a#Demo-DownloadLink").addClass("active");
    }
}

