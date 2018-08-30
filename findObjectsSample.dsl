// Imports needed for invoking findObjects in the DSL script
import com.electriccloud.query.Filter
import com.electriccloud.query.CompositeFilter
import com.electriccloud.query.PropertyFilter
import com.electriccloud.query.Operator

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


// make the call
findObjects(objectType: 'pipeline', filter: constructFilters(filters))

/**
 * Helper function to convert the list of filters a filter structure 
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
