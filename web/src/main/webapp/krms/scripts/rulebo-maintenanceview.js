/* namespace for rulebo maintenanceview */
rulebo_maintenanceview = (function() {
    // debounce impl cribbed from Underscore.js
    // http://underscorejs.org/#debounce
    function underscore_debounce(func, wait, immediate) {
        var timeout;
        return function() {
            var context = this, args = arguments;
            var later = function() {
                timeout = null;
                if (!immediate) func.apply(context, args);
            };
            if (immediate && !timeout) func.apply(context, args);
            clearTimeout(timeout);
            timeout = setTimeout(later, wait);
        };
    };

    return {
        // binds event handlers that enable or disables the Copy Rule button depending on whether
        // copyRuleName field value is empty
        "bind_copyRuleName": function() {
            var empty_re = /^\\s*$/;
            var button = jq("#copyRuleButton");
            var input = jq("#copyRuleName");
            function enable_or_disable_copyRuleButton() {
                var val = jq(this).val();
                if (!val || empty_re.exec(val)) {
                    button.attr("disabled", "disabled");
                } else {
                    button.removeAttr("disabled");
                }
            }
            input.change(enable_or_disable_copyRuleButton);
            input.keyup(underscore_debounce(enable_or_disable_copyRuleButton, 500));
        }
    };
})();