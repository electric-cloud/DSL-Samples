/*
Format: Electric Flow DSL
File: createPipeline.groovy
Description: Create release pipeline

*/

def stages = "$[stages]".split(",") // comma separated list
def pipelineName = "$[release]"

project "Default", {
	pipeline pipelineName, {
		stages.each { st ->
			stage st,{
				task "Batch Deploy",
					taskType: "DEPLOYER"
				task "Entry gate approval",
					taskType: 'APPROVAL',
					approver: ['admin'],
					gateType: 'PRE',
					notificationTemplate: 'ec_default_pipeline_notification_template'
			} // stage
		} // Each stage
	} // Pipeline
} // Project