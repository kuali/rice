/*
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
jQuery(document).ready(function () {

    jQuery('.selectpicker').selectpicker({
        'size':'10'
    });

    jQuery(document).on('click', 'dt', function(event) {
        var opts = jQuery(this).parent().nextUntil(':has(dt)').andSelf();
        var inactive = opts.filter(':not(.selected)');
        var toggleMe = inactive;
        if (inactive.length == 0) {
            toggleMe = opts;
        }
        toggleMe.children('a').click();
        event.preventDefault();
        return false;
    });
});