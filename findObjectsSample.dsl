// Imports needed for invoking findObjects in the DSL script
import com.electriccloud.query.Filter
import com.electriccloud.query.CompositeFilter
import com.electriccloud.query.PropertyFilter
import com.electriccloud.query.Operator
import com.electriccloud.query.SelectSpec
import com.electriccloud.util.SortOrder
import com.electriccloud.util.SortSpec

/**
 * Sample filter definition for:
 * projectName equals "project-0"
 * AND
 *  (
 *     pipelineName equals "pipeline-0"
 *     or
 *     pipelineName equals "pipeline-1"   
 *  ) 
 */
def filters = [[
                       propertyName: "projectName",
                       operator: "equals",
                       operand1: "project-0"
               ],
               [ filters: [[
                                   propertyName: "pipelineName",
                                   operator: "equals",
                                   operand1: "pipeline-0"
                           ],[
                                   propertyName: "pipelineName",
                                   operator: "equals",
                                   operand1: "pipeline-1"
                           ]],
                 operator: "or"
               ]]
			   
/**
 * Simple selects can be specified simply as strings
 * Use the object structure if recurse option needs to be controlled.
 * The above 2 forms can be completed in the same list of selects.
 */ 
def selects = ["testprop1", [propertyName: "testprop2", recurse: true]]

/**
 * Sort columns
 */ 
def sorts = [[propertyName: "pipelineName", order: "descending"], [propertyName: "projectName"]]			   


// make the call
findObjects(objectType: 'pipeline', filter: constructFilters(filters), select: constructSelects(selects), sort: constructSorts(sorts))

/**
 * Helper function to convert the list of filters to a filter structure 
 * recognized by findObjects for DSL evaluation.    
 */
def constructFilters(def filters) {
    filters.collect {
        def op = Operator.valueOf(it.operator)
        if (op.isBoolean()) {
            assert it.filters
            new CompositeFilter(op, constructFilters(it.filters) as Filter[])
        } else {
            new PropertyFilter(it.propertyName, op, it.operand1, it.operand2)
        }
    }
}


/**
 * Helper function to convert the list of select strings to a list of SelectSpec  
 * recognized by findObjects for DSL evaluation.    
 */
def constructSelects(def selects) {
    selects.collect {
	    it instanceof String ? new SelectSpec(it, false) : new SelectSpec(it.propertyName, it.recurse)
    }
}

/**
 * Helper function to convert the list of sort instances to a list of SortSpec  
 * recognized by findObjects for DSL evaluation.    
 */
def constructSorts(def sorts) {
    sorts.collect {
	    new SortSpec(it.propertyName, SortOrder.valueOf(it.order?:"ascending"))
    }
}
