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
var timer;

function startPolling(time) {
    if (!timer) {
        timer = setInterval(function(){queryForRefresh()},time);
    }
}

function stopPolling(){
    if (timer) {
        clearInterval(timer);
        timer = null;
    }
}

function queryForRefresh() {
    var url = jQuery("#" + kradVariables.KUALI_FORM).attr("action");
    var queryData = {};

    queryData.methodToCall = "regUpdateQuery";
    queryData.ajaxRequest = true;
    queryData.ajaxReturnType = "update-none";
    queryData.formKey = jQuery("input[name='" + kradVariables.FORM_KEY + "']").val();

    jQuery.ajax({
        url: url,
        dataType: "json",
        beforeSend: null,
        complete: null,
        error: null,
        data: queryData,
        success: function(data){
            var updateIds = data.updateIds;
            var stop = data.stop;

            jQuery(updateIds).each(function(index, id){
                retrieveComponent(id);
            });

            // if no more updates expected, stop polling
            if (stop) {
                clearInterval(timer);
                timer = null;
            }
        }
    });
}