// ActionList.jsp

function setActions() {
  obj=document.forms[0].elements;
  for (i=0; i<obj.length; i++) {
    if(obj[ i ].type.indexOf('select')==0) {
      if (obj[ i ].name != document.forms[0].defaultActionToTake.name) {
        var lindex = 0;
        for (j=0; j<obj[ i ].options.length; j++) {
          if (obj[ i ].options[j].value == document.forms[0].defaultActionToTake.options[document.forms[0].defaultActionToTake.selectedIndex].value) {
              lindex = j;
          }
        }
        obj[ i ].selectedIndex = lindex;
      }
    }
  }
}

function setRowMouseListeners() {
	var rowCounter = 0;
	var idPrefix = "actionlist_tr_";
	var rowClassNames = new Object();
	var currentRow = document.getElementById(idPrefix + rowCounter);
	while (currentRow != null) {
		rowClassNames[idPrefix + rowCounter] = currentRow.className;
		currentRow.onmouseover = function() { this.className = "over"; };
		currentRow.onmouseout = function () { this.className = rowClassNames[this.id]; };
		rowCounter++;
		currentRow = document.getElementById(idPrefix + rowCounter);
	}
}

setTimeout(setRowMouseListeners, 0);