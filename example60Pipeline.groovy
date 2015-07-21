/*
Format: Electric Flow DSL
File: example60Pipeline.groovy
Description: Example Pipeline model using the new 6.0 pipeline feature

Run basicDeployModel first:
ectool --format json evalDsl --dslFile basicDeployModel.groovy

ectool evalDsl --dslFile example60Pipeline.groovy

*/

def dslDir = "/vagrant/demo/pipelines/"

// Settings
def proj = "Default"
def pipe = "Example Pipeline"
def app = "Sample Deploy Application"

// Environment names ["env1", "env2" ...]
def envs = [dev: "sample-dev", qa: "sample-qa"]

def stages = [dev: "Development", qa: "Testing"]

project proj, {
	
	// Procedure to create a development snapshot
	procedure "Create Snapshot", {
		formalParameter "environment",
			required: "1",
			expansionDeferred: "1"
	
		step "Delete the snapshot",
			command:  "ectool deleteSnapshot Default \"$app\" \"$app-1.0\" "
		
		step "createSnapshot", 
			command: "ectool createSnapshot Default \"$app\" \"$app-1.0\" --environmentName \"$envs.dev\" "
	}
	
	// Dummy system tests
	procedure "System Tests", {

		step "Tests", 
			command: "echo Testing..."
		
		step "Collect Test Results",
			command: "ectool setProperty /myPipelineStageRuntime/ec_summary/testResults " +
				// Dummy location...
				"\'" + '<html><a href=\"TestResultsSummary.html\">Test Results</a></html>' + "\'"
	}	
	
	pipeline pipe, {
		// Pipeline parameters
		formalParameter "app",
			type: "textentry",
			required: "1",
			defaultValue: "Example Pipeline",
			label: "Application Name",
			orderIndex: 1
		
		// Development State
		stage stages.dev, {
			task "Deploy",
				taskType: "PROCESS",
				subapplication: app,
				subproject: proj,
				subprocess: "Deploy",
				taskProcessType: "APPLICATION",
				environmentName: envs.dev,
				clearActualParameters: "true",
				actualParameter: [ ec_smartDeployOption: "true" ],
				errorHandling: "ignore"
				
			task "Create Snapshot",
				taskType: 'PROCEDURE',
				subproject: projectName,
				subprocedure: 'Create Snapshot',
				expansionDeferred: "1",
				actualParameter: [
					environment: envs.dev
				],
				errorHandling: "ignore"

		} // Dev Stage
		
		stage stages.qa, {
		
			// Entry gate to QA
			task "Entry gate approval",
				taskType: 'APPROVAL',
				approver: ['admin'],
				gateType: 'PRE',
				notificationTemplate: 'ec_default_pipeline_notification_template'

			task "Deploy",
				subapplication: app,
				subproject: projectName,
				subprocess: "Deploy",
				taskProcessType: "APPLICATION",
				environmentName: envs.qa,
				advancedMode: "1", // allow for variable snapshotName
				snapshotName: "$app-1.0",
				clearActualParameters: true,
				taskType: "PROCESS",
				actualParameter: [ ec_smartDeployOption: "true" ],
				errorHandling: "ignore"

			task "System Tests",
				taskType: 'PROCEDURE',
				subproject: projectName,
				subprocedure: 'System Tests'
				
		} // Stage QA

	} // Pipeline

} // Project
