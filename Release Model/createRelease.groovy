/*
Format: Electric Flow DSL
File: createRelease.groovy
Description: Create release

*/

def releaseName = "$[release]"
def applications = "$[applications]".split(",") // comma separated list
def stages = "$[stages]".split(",") // comma separated list

project "Default", {
	release releaseName,
		pipelineName: releaseName,
		pipelineProjectName: projectName,
		plannedStartDate: "2015-11-16",
		plannedEndDate: "2015-12-01",{
			deployer "Main Applications",{
				applications.each { app ->
					deployerApplication app,
						applicationProjectName: projectName,
						//orderIndex: ,
						processName: "Deploy",
						//snapshotName: , 
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