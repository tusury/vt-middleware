/**
 * Checks or unchecks all checkboxes of the given name on a form.
 */
function select(name, isSelected) {
  for (var n=0; n < document.forms[0].length; n++) {
	var e = document.forms[0].elements[n];
    if (e.type == 'checkbox' && e.name == name) {
      e.checked = isSelected;
    }
  }
}

/**
 * Checks or unchecks all checkboxes of the given names on a form, where
 * names is an array of names of checkbox form elements to toggle.
 */
function selectMultiple(names, isSelected) {
  for (var i=0; i < document.forms[0].length; i++) {
	var e = document.forms[0].elements[i];
    if (e.type == 'checkbox') {
    	for (var j=0; j < names.length; j++) {
          if (e.name == names[j]) {
        	  e.checked = isSelected;
        	  break;
          }
    	}
    }
  }
}
