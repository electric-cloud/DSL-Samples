/*
Format: Electric Flow DSL
File: simplePipeLine.groovy
Description: Simple Development Pipeline model

Features
--------


Command-line run instructions
-----------------------------
ectool deleteProject 'Simple Pipeline'
ectool evalDsl --dslFile simplePipeLine.groovy

See workflow at:
	Simple Pipeline::Release Pipeline

With https://github.com/electriccommunity/ec-pipeline-activity, view the Pipeline:
https://flow/commander/pages/PipelineActivity/activity?projectName=Simple%20Pipeline&workflowDefinitionName=Release%20Pipeline


*/

def projName = 'Simple Pipeline'
def workflow = 'Release Pipeline'
def states = ['Artifacts Ready','Test','UAT','Production']

// Create new

project projName, {
	workflowDefinition workflowDefinitionName: workflow,
		workflowNameTemplate: workflow + '_' + '$' + "[/increment /server/ec_counters/workflowCounter]", {
		states.each { stateName ->
			procedure "${stateName}_proc", 
				jobNameTemplate: stateName + '_' + '$' + "[/increment /server/ec_counters/jobCounter]", {
					step stepName: 'DEMO: NOP',
						command: ""
			}
			stateDefinition stateDefinitionName: stateName,
				subprocedure: "${stateName}_proc"
		}
		transitionDefinition stateDefinitionName: 'Artifacts Ready',
			transitionDefinitionName: "Promote to Test", targetState: 'Test'
		transitionDefinition stateDefinitionName: 'Test',
			transitionDefinitionName: "Promote to UAT", targetState: 'UAT'
		transitionDefinition stateDefinitionName: 'UAT',
			transitionDefinitionName: "Promote to Prod", targetState: 'Production'
		transitionDefinition stateDefinitionName: 'Artifacts Ready',
			transitionDefinitionName: "Hotpatch", targetState: 'Production'
	}
}