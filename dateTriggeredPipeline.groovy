/*
Format: Electric Flow DSL
File: dateTriggeredPipeline.groovy
Description: Simple pipeline with SMS notification

Model Details
-------------
- A 2-stage pipeline, Dev and QA
- An entry gate to QA
- Pipeline parameters for time and date to trigger Dev to QA promotion
- Task in Dev stage to create a schedule
- Schedule calls a procedure when its time has been reached
- The procedure triggers the gate to QA then deletes the schedule

Command-line run instructions
-----------------------------
ectool evalDsl --dslFile dateTriggeredPipeline.groovy

Instructions
------------
Run the pipeline and enter the date & time when prompted

*/

def proj = "Default"
def pipe = "Schedule Triggered Pipeline"

project proj, {
	
	// Procedure to trigger the pipeline gate
	procedure "Trigger Pipeline Gate", description: "Trigger a pipeline gate", {
		formalParameter "flowRuntimeId", required: "true"
		formalParameter "stageName", required: "true"
		formalParameter "taskName", required: "true"
		formalParameter "gateType", required: "true", // PRE | POST
			description: "PRE or POST"
		formalParameter "action", required: "true", // approve | reject
			description: "approve or reject"
		formalParameter "evidence", default: 'Triggered by $[/myJob]'
		
		step "Trigger", 
			command: 'ectool completeManualTask "$[flowRuntimeId]" "$[stageName]" "$[taskName]" --actualParameter action="$[action]" evidence="$[evidence]" --gateType "$[gateType]"'
			
		step "Delete Schedule",
			command: "ectool deleteSchedule \"$projectName\" \"\$[/mySchedule]\" "
	}
	
	procedure "Create Schedule", description: "Create a schedule that runs a procedure at a particular time and date", {
		formalParameter "date", required: "true", description: "Date format: yyyy-mm-dd"
		formalParameter "time", required: "true", description: "Time format: hh:mm"
		
		step "Create Schedule",
			command: "ectool createSchedule \"$projectName\" " + 
				'"$[/myPipelineRuntime]" ' +
				'--beginDate "$[date]" ' +
				'--startTime "$[time]" ' +
				'--procedureName "Trigger Pipeline Gate" ' +
				'--actualParameter ' +
					'stageName=QA ' +
					'taskName="Entry Approval" ' +
					'gateType="PRE" ' +
					'action="approve" ' +
					'evidence="Promoted by job $[/myJob]" ' +
					'flowRuntimeId="$[/myPipelineRuntime/flowRuntimeId]" '
					
		step "Create Link",
			command: """\
			ectool setProperty "/myPipelineStageRuntime/ec_summary/Schedule Time and Date" --value "\$[time]   \$[date]"
			
			ectool setProperty "/myPipelineStageRuntime/ec_summary/Schedule Definition" --value '<html><a href=" /commander/link/editSchedule/projects/$projectName/schedules/\$[/myPipelineRuntime]">View</a></html>'
			""".stripIndent()
	}
	
	pipeline pipe, description: "Two stage pipeline with entry gate to second stage, QA", {
		formalParameter "date", defaultValue: '$[/javascript var now = new Date();((now.getFullYear())+"-"+(now.getMonth()+1))+"-"+(now.getDate())]', description: "Date format: yyyy-mm-dd"
		// Default value now + 1 min
		formalParameter "time", defaultValue: '$[/javascript var now = new Date(Date.now()+60000);now.getHours()+":"+(now.getMinutes())]', description: "Time format: hh:mm"
		
		stage "Dev", {
			task "Create Schedule",
				taskType: 'PROCEDURE',
				subproject: projectName,
				subprocedure: "Create Schedule",
				actualParameter: [
					date: '$[date]',
					time: '$[time]'
				]
		} // Dev

		stage "QA", {
			task "Entry Approval",
				taskType: "APPROVAL",
				gateType: "PRE"
				
		} // QA

	} // Pipeline

} // Project
