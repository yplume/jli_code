require(["dojo/_base/declare",
         "dojo/_base/lang"], 
function(declare, lang) {		
	/**
	 * Use this function to add any global JavaScript methods your plug-in requires.
	 */
	
	lang.extend(ecm.widget.search.BasicSearchDefinition, {
        onSearchTemplatePrepared: function(searchTemplate) {
        	
            searchTemplate.pageSize = 2000;
        }
    });
    lang.extend(ecm.widget.search.SearchForm, {
        onSearchTemplatePrepared: function(searchTemplate) {
         	
            searchTemplate.pageSize = 2000;
        }
    });
});
