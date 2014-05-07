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
package org.kuali.rice.krad.uif;

import java.text.MessageFormat;

/**
 * Constants for CSS style strings
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class CssConstants {

    public static final String DISPLAY = "display: ";

    public static class Displays {
        public static final String BLOCK = DISPLAY + "block;";
        public static final String INLINE = DISPLAY + "inline;";
        public static final String INLINE_BLOCK = DISPLAY + "inline-block;";
        public static final String NONE = DISPLAY + "none;";
    }

    public static final String TEXT_ALIGN = "text-align: ";

    public static class TextAligns {
        public static final String LEFT = TEXT_ALIGN + "left;";
        public static final String RIGHT = TEXT_ALIGN + "right;";
        public static final String CENTER = TEXT_ALIGN + "center;";
        public static final String JUSTIFY = TEXT_ALIGN + "justify;";
        public static final String INHERIT = TEXT_ALIGN + "inherit;";
    }

    public static final String VERTICAL_ALIGN = "vertical-align: ";

    public static class VerticalAligns {
        public static final String BASELINE = VERTICAL_ALIGN + "Baseline;";
        public static final String BOTTOM = VERTICAL_ALIGN + "bottom;";
        public static final String MIDDLE = VERTICAL_ALIGN + "middle;";
        public static final String TOP = VERTICAL_ALIGN + "top;";
    }

    public static class Margins {
        public static final String MARGIN_LEFT = "margin-left: {0};";
        public static final String MARGIN_RIGHT = "margin-right: {0};";
        public static final String MARGIN_TOP = "margin-top: {0};";
        public static final String MARGIN_BOTTOM = "margin-bottom: {0};";
    }

    public static class Padding {
        public static final String PADDING_LEFT = "padding-left: {0};";
        public static final String PADDING_RIGHT = "padding-right: {0};";
        public static final String PADDING_TOP = "padding-top: {0};";
        public static final String PADDING_BOTTOM = "padding-bottom: {0};";
    }

    public static final String WIDTH = "width: ";
    public static final String HEIGHT = "height: ";
    public static final String OVERFLOW = "overflow: ";

    /**
     * Replaces parameters in the given CSS string with the corresponding
     * parameter values given
     *
     * @param style string with parameters to replace
     * @param parameters one or more parameter values
     * @return given string with filled parameters
     */
    public static final String getCssStyle(String style, String... parameters) {
        MessageFormat cssStyle = new MessageFormat(style);

        return cssStyle.format(parameters);
    }

    public static class Classes {
        public static final String SUPPORT_TITLE_STYLE_CLASS = "uif-viewHeader-supportTitle";
        public static final String HIDE_HEADER_TEXT_STYLE_CLASS = "uif-hideHeaderText";
        public static final String HAS_ADD_LINE = "uif-hasAddLine";
        public static final String SELECT_FIELD_STYLE_CLASS = "uif-select-line";
        public static final String ACTION_COLUMN_STYLE_CLASS = "uif-collection-column-action";
        public static final String HAS_HELPER = "has-helper";
        public static final String IGNORE_VALID = "ignoreValid";
        public static final String NEW_COLLECTION_ITEM = "uif-newCollectionItem";
        public static final String TOOLTIP = "uif-tooltip";
        public static final String BTN = "btn";
        public static final String BTN_DEFAULT = "btn-default";
        public static final String ICON_ONLY_BUTTON = "uif-iconOnly";
    }

    public static class ProgressBar {
        public static final String VERTICAL_STEP_PROGRESS_BAR = "uif-stepProgressBar-vertical";
        public static final String PROGRESS_BAR = "progress-bar";
        public static final String SUCCESS_PROGRESS_BAR = "progress-bar-success";
        public static final String WARNING_PROGRESS_BAR = "progress-bar-warning";
        public static final String INFO_PROGRESS_BAR = "progress-bar-info";
        public static final String EMPTY_PROGRESS_BAR = "progress-bar-empty";
        public static final String STEP_LABEL = "uif-step";
        public static final String ACTIVE = "active";
        public static final String COMPLETE = "complete";
    }

    public static class Tabs {
        public static final String TABS_LEFT = "tabs-left";
        public static final String TABS_RIGHT = "tabs-right";
        public static final String TABS_BOTTOM = "tabs-below";
    }

    public static class CssGrid {
        public static final String CONTAINER = "container";
        public static final String ROW = "row";

        public static final String XS_COL_PREFIX = "col-xs-";
        public static final String SM_COL_PREFIX = "col-sm-";
        public static final String MD_COL_PREFIX = "col-md-";
        public static final String LG_COL_PREFIX = "col-lg-";

        public static final String SM_OFFSET_PREFIX = "col-sm-offset-";
        public static final String MD_OFFSET_PREFIX = "col-md-offset-";
        public static final String LG_OFFSET_PREFIX = "col-lg-offset-";

        public static final String XS_FLOAT_RIGHT = "xs-float-right";
        public static final String SM_FLOAT_RIGHT = "sm-float-right";
        public static final String MD_FLOAT_RIGHT = "md-float-right";
        public static final String LG_FLOAT_RIGHT = "lg-float-right";

        public static final String XS_CLEAR_LEFT = "xs-clear-left";
        public static final String SM_CLEAR_LEFT = "sm-clear-left";
        public static final String MD_CLEAR_LEFT = "md-clear-left";
        public static final String LG_CLEAR_LEFT = "lg-clear-left";
    }
}
