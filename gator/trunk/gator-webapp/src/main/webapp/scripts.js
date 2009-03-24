/**
 * Checks or unchecks all checkboxes of the given name on a form.
 */
function select(name, isSelected) {
  for (var n=0; n < document.forms[0].length; n++) {
    if (document.forms[0].elements[n].type == 'checkbox' &&
        document.forms[0].elements[n].name == name) {
      document.forms[0].elements[n].checked = isSelected;
    }
  }
}
