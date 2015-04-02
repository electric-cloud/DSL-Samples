/*
Format: Electric Flow DSL
file: helloProcedure.groovy
Description: Simple hello-world procedure

Command-line run instructions
----------------------------- 
	ectool --format json evalDsl --dslFile helloProcedure.groovy
*/

// Create the procedure (and project if it doesn't already exist)
project "Hello Project", {
	procedure "Hello Procedure", {
		step "Hello World", 
			command: "echo Hello World from EF DSL!"
	}
}
