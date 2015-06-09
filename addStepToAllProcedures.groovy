/*
Format: Electric Flow DSL
file: addStepToAllProcedures.groovy
Description: Add step to all procedures in a project

Features illustrated
--------------------
- Use get API for data-driven actions

Command-line run instructions
-----------------------------
	Run helloProcedure.groovy first to populate the project:
	
	ectool --format json evalDsl --dslFile helloProcedure.groovy
	
	ectool --format json evalDsl --dslFile addStepToAllProcedures.groovy
*/

"rm -f /tmp/debug.log".execute()
def logfile= new File('/tmp/debug.log')

project "Hello Project", {
	getProcedures().each { 
		procedure it.name, {
			step "Added to all my procedures",
				command: "echo Added to each procedure" 
		}
	}
}
