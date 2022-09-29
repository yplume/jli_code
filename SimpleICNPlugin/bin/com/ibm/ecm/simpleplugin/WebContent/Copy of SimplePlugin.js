require(["dojo/_base/declare",
         "dojo/_base/lang"], 
function(declare, lang) {		
	
	/**
	 * Use this function to add any global JavaScript methods your plug-in requires.
	 */
lang.setObject("simpleAction", function(repository, items, callback, teamspace, resultSet, parameterMap) {
 /*
  * Add custom code for your action here. For example, your action might launch a dialog or call a plug-in service.
  */
	
	window.open("http://jlcm2008:9081/navigator/ittc/ittc/index.html", "", "toolbar=no,menubar=no,location=no,resizable=no,scrollbars=no");
});
});
