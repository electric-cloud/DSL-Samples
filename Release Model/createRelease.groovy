/*
Format: Electric Flow DSL
File: createRelease.groovy
Description: Create release

*/

def releaseName = "$[release]"
def applications = "$[applications]".split(",") // comma separated list
def versions = "$[versions]".split(",") // comma separated list
def stages = "$[stages]".split(",") // comma separated list
def startDate = "$[plannedStartDate]"
def endDate = "$[plannedEndDate]"

project "Default", {
	release releaseName,
		pipelineName: releaseName,
		pipelineProjectName: projectName,
		plannedStartDate: startDate,
		plannedEndDate: endDate,{
			deployer "Main Applications",{
				applications.eachWithIndex { app, index ->
					deployerApplication app,
						applicationProjectName: projectName,
						//orderIndex: ,
						processName: "Deploy",
						snapshotName: versions[index], 
						releaseName: releaseName, {
						
							stages.each { st ->
								deployerConfiguration environmentName: "Banking-${st}",
									environmentProjectName: "Default",
									stageName: st
								
							} // Each stage
						} // Deployer application
					} // Each application
				} // Deployer
		} // Release
} // Project