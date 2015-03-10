/*
Format: Electric Flow DSL
File: PopulateCI.groovy
Description: Populate the Commander CI dashboard with jobs

Features
--------
- Create procedures for each stream
	- Set to fail randomly
	- Dummy "Code Coverage" data stored to job for plotting in CI
- Create procedure to run stream procedures
- Create CI Configuration
- Create CI charts for each stream
- Run the the stream procedure runner

Command-line run instructions
-----------------------------
ectool deleteProject "Sample CI Project"
ectool evalDsl --dslFile PopulateCI.groovy

*/

def projName = "Sample CI Project"
def procedures = ["Stream1","Stream2","Stream3","Stream4"]

// Remove old Project
	//transaction {deleteProject (projectName: projName)}
	
// Create new

project projName, {
	description = ""
	
	// Default resource to use to run the "build" jobs
	resourceName = "local"
	
	// Procedure to run all the CI build jobs.  Step definitions added below for each CI procedure.
	procedure "Populate CI"
	
	// Create CI Build Procedures
	procedures.each { procedureName ->
		procedure procedureName, {
			// Procedure value to track how many times the procedure has been run
			counter = 0
			
			// Procedure job steps
			step stepName: "Check out source code"
			step stepName: "Build",
				command: "ectool setProperty /myJob/codeCoverage " + '$' + '[/javascript Math.round(90 + Math.random()*10.0).toString() ]'
			step stepName: "Publish Artifact"
			step stepName: "DEMO: Simulate Failure",
				command: '$' + '[/javascript (Math.random() > 0.9)?"exit 1":""]'
			step stepName: "DEMO: Run Again",
				condition: '$' + '[/javascript myProcedure.counter++;(myProcedure.counter < 20)?"true":"false"]',
				command: "ectool runProcedure \"$projName\" --procedureName \"$procedureName\" --scheduleName \"$procedureName\""
			}
		schedule projectName: projName,
			scheduleName: procedureName,
			scheduleDisabled: true,
			procedureName: procedureName,
			{
			ec_ci.checkoutSCMConfigName = "none"
			ec_ci.ciConfigDescription = ""
			property propertyName: "/myProject/schedules/$procedureName/ec_ci/charts/Build Time/series1-label",				value: "Elapsed time"
			property propertyName: "/myProject/schedules/$procedureName/ec_ci/charts/Build Time/series1-propertyName",		value: "elapsedTime"
			property propertyName: "/myProject/schedules/$procedureName/ec_ci/charts/Build Time/series1-scalingDivisor",	value: 1000
			property propertyName: "/myProject/schedules/$procedureName/ec_ci/charts/Code Coverage/series1-label",			value: "Code Coverage %"
			property propertyName: "/myProject/schedules/$procedureName/ec_ci/charts/Code Coverage/series1-propertyName",	value: "codeCoverage"
			property propertyName: "/myProject/schedules/$procedureName/ec_ci/charts/Code Coverage/series1-scalingDivisor",	value: 1

			ec_customEditorData.TriggerFlag = 2 // Show all jobs in CI dashboard
		}
		step procedureName: "Populate CI",
			stepName: "Run $procedureName",
			parallel: true,
			command: "ectool runProcedure \"$projName\" --procedureName \"$procedureName\" --scheduleName \"$procedureName\""
	}
}

// Add project to CI
currentProjects = getProperty(propertyName: "/myUser/ec_ci/ciProjects").value
property propertyName: "/myUser/ec_ci/ciProjects", value: "${currentProjects}\n${projName}"


transaction {runProcedure procedureName: "Populate CI", projectName: projName}

