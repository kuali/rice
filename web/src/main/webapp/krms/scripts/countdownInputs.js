var ENABLED = true;

function countdownInit() {
    jq('.countdown').each(function() {
        if (jq(this).val() != null && jq(this).val() != undefined) {
            var length = jq(this).val().length;
            var id = jq(this).prop("id");
            var maxLength = jq(this).prop("maxLength");
            if (id == null || maxLength == -1) {
//                alert('countdown css class must have id (' + id + ') and maxLength (' + maxLength + ') properties set');
                return;
            }
            if (document.getElementById(id+'_constraint_span') != null) {
                document.getElementById(id+'_constraint_span').innerHTML = "size " + maxLength + " (" + (maxLength - length) + " characters remaining)";
            }
        }
        jq(this).keyup(function() {
            var maxLength = jq(this).prop("maxLength");
            var length = jq(this).val().length;
            if (document.getElementById(id+'_constraint_span') != null) {
                document.getElementById(id+'_constraint_span').innerHTML = "size " + maxLength + " (" + (maxLength - length) + " characters remaining)";
            }
        });
    });
}

jq(document).ready(function() {
    if (ENABLED) {
        countdownInit();
    }
});
