/**
 * Bind to click events on toggle buttons
 */
jQuery(function() {
    /**
     * Toggle the named input value between YES and NO, and then call customLookupChanged
     * to submit form to refresh.
     */
    function toggleSearchType(input_name) {
        var input = jQuery('input[name=' + input_name + ']');
        input.val(input.val() == "YES" ? "NO" : "YES");
        customLookupChanged();
    }
    jQuery("#toggleAdvancedSearch").click(toggleSearchType.bind(null, "isAdvancedSearch"));
    jQuery("#toggleSuperUserSearch").click(toggleSearchType.bind(null, "superUserSearch"));
    jQuery("#resetSavedSearch").click(toggleSearchType.bind(null, "resetSavedSearch"));
});

/**
 * Called on an action that requires the lookup to be refreshed
 * Invokes performCustomAction.
 */
function customLookupChanged() {
    var methodToCallElement=document.createElement("input");
    methodToCallElement.setAttribute("type","hidden");
    methodToCallElement.setAttribute("name","methodToCall");
    methodToCallElement.setAttribute("value","refresh");
    document.forms[0].appendChild(methodToCallElement);

    var refreshCallerElement=document.createElement("input");
    refreshCallerElement.setAttribute("type","hidden");
    refreshCallerElement.setAttribute("name","refreshCaller");
    refreshCallerElement.setAttribute("value","customLookupAction");
    document.forms[0].appendChild(refreshCallerElement);

    document.forms[0].submit();
}
