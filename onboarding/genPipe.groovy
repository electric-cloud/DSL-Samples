/*
Format: Electric Flow DSL
File: genPipe.groovy
Description: Create pipeline

*/

def projName  = "$[projName]"
def appName  = "$[appName]"
def appTech  = "$[appTech]"
//def stages = $ [/myProject/stages] // Literal string to create stage mapping of logical and actual stage names
def stages = "$[stages]".split(",")

project projName, {
	pipeline appName, {
		
		// Development State
		//stage stages.dev, {
		stage stages[0], { // Assume first stage is a development
			task "Code Scan",
				taskType: 'PROCEDURE',
				subproject: projectName,
				subprocedure: 'Code Scan',
				expansionDeferred: "1",
				errorHandling: "continueOnError"
				
			task "Build",
				taskType: 'PROCEDURE',
				subproject: projectName,
				subprocedure: 'Build',
				expansionDeferred: "1",
				errorHandling: "continueOnError"
		
			task "Deploy",
				taskType: "PROCESS",
				subapplication: appName,
				subproject: projectName,
				subprocess: "Deploy",
				taskProcessType: "APPLICATION",
				environmentName: "dev-${appName}",
				clearActualParameters: "true",
				actualParameter: [ ec_smartDeployOption: "true" ],
				errorHandling: "continueOnError"
				
			task "Application URL",
				taskType: 'PROCEDURE',
				subproject: projectName,
				subprocedure: 'Application URL',
				expansionDeferred: "1",
				errorHandling: "continueOnError"
				
			task "Create Snapshot",
				taskType: 'PROCEDURE',
				subproject: projectName,
				subprocedure: 'Create Snapshot',
				expansionDeferred: "1",
				actualParameter: [
					env: (String) "dev-${appName}"
				],
				errorHandling: "continueOnError"

		} // Dev Stage
			
		stages.drop(1).each { stg -> // drop the dev
		//["st", "pr"].each { stg -> 
			//stage stages[stg], {
			stage stg, {
				// Entry gate to QA
				task "Entry gate approval",
					taskType: 'APPROVAL',
					approver: ['admin'],
					gateType: 'PRE',
					notificationTemplate: 'ec_default_pipeline_notification_template'

				task "Deploy",
					subapplication: appName,
					subproject: projectName,
					subprocess: "Deploy",
					taskProcessType: "APPLICATION",
					environmentName: "${stg}-${appName}",
					advancedMode: "1", // allow for variable snapshotName
					snapshotName: "${appName}-1.0",
					clearActualParameters: true,
					taskType: "PROCESS",
					actualParameter: [ ec_smartDeployOption: "true" ],
					errorHandling: "continueOnError"
				
				task "Application URL",
					taskType: 'PROCEDURE',
					subproject: projectName,
					subprocedure: 'Application URL',
					expansionDeferred: "1",
					errorHandling: "continueOnError"
				
				task "Smoke Tests",
					taskType: 'PROCEDURE',
					subproject: projectName,
					subprocedure: 'Smoke Tests'
			}
		} // Stages staging and prod

	} // Pipeline
}
