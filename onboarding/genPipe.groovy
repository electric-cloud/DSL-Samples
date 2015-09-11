/*
Format: Electric Flow DSL
File: genPipe.groovy
Description: Create pipeline

*/

def appName  = "$[appName]"
def appTech  = "$[appTech]"
//def stages = $ [/myProject/stages] // Literal string to create stage mapping of logical and actual stage names
def stages = "$[stages]".split(",")

project "Default", {
	pipeline appName, {
		
		// Development State
		//stage stages.dev, {
		stage stages[0], { // Assume first stage is a development
			task "Code Scan",
				taskType: 'PROCEDURE',
				subproject: appName,
				subprocedure: 'Code Scan',
				expansionDeferred: "1",
				errorHandling: "ignore"
				
			task "Build",
				taskType: 'PROCEDURE',
				subproject: appName,
				subprocedure: 'Build',
				expansionDeferred: "1",
				errorHandling: "ignore"
		
			task "Deploy",
				taskType: "PROCESS",
				subapplication: appName,
				subproject: projectName,
				subprocess: "Deploy",
				taskProcessType: "APPLICATION",
				environmentName: "dev-${appName}",
				clearActualParameters: "true",
				actualParameter: [ ec_smartDeployOption: "true" ],
				errorHandling: "ignore"
				
			task "Application URL",
				taskType: 'PROCEDURE',
				subproject: appName,
				subprocedure: 'Application URL',
				expansionDeferred: "1",
				errorHandling: "ignore"
				
			task "Create Snapshot",
				taskType: 'PROCEDURE',
				subproject: appName,
				subprocedure: 'Create Snapshot',
				expansionDeferred: "1",
				actualParameter: [
					env: (String) "dev-${appName}"
				],
				errorHandling: "ignore"

		} // Dev Stage
/*		
		stage stages.qa, {
		
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
				environmentName: "qa-${appName}",
				advancedMode: "1", // allow for variable snapshotName
				snapshotName: "${appName}-1.0",
				clearActualParameters: true,
				taskType: "PROCESS",
				actualParameter: [ ec_smartDeployOption: "true" ],
				errorHandling: "ignore"
				
			task "Application URL",
				taskType: 'PROCEDURE',
				subproject: appName,
				subprocedure: 'Application URL',
				expansionDeferred: "1",
				errorHandling: "ignore"
				
			task "System Tests",
				taskType: 'PROCEDURE',
				subproject: appName,
				subprocedure: 'System Tests'
		} // Stage QA
*/				
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
					errorHandling: "ignore"
				
				task "Application URL",
					taskType: 'PROCEDURE',
					subproject: appName,
					subprocedure: 'Application URL',
					expansionDeferred: "1",
					errorHandling: "ignore"
				
				task "Smoke Tests",
					taskType: 'PROCEDURE',
					subproject: appName,
					subprocedure: 'Smoke Tests'
			}
		} // Stages staging and prod

	} // Pipeline
}