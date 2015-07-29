/*
Format: Electric Flow DSL
File: genProcedures.groovy
Description: 

*/

def appName  = "$[appName]"
def appTech  = "$[appTech]"
def artifactGroup = "$[artifactGroup]"
def artifactKey = "$[artifactKey]"
		
project appName, {
	procedure "Build",{
		
		step "Get Sources"
		step "Compile"
		step "Post Results",
			command: "ectool setProperty /myPipelineStageRuntime/ec_summary/buildResults " +
				'\"<html>Build Results</html>\"'			
		step "Publist Artifacts"
		
	} // Procedure Build
	
	procedure "Create Snapshot",{
		formalParameter "env"
		
		step "Delete the snapshot",
			command:  "ectool deleteSnapshot Default \"${appName}\" \"${appName}-1.0\""
		
		step "createSnapshot", 
			command: "ectool createSnapshot Default \"${appName}\" \"${appName}-1.0\" --environmentName " + '$' + "[env]"		
	}
	
	procedure "Code Scan",{
		
		step "Post Results",
			command: "ectool setProperty /myPipelineStageRuntime/ec_summary/codeScanResults " +
				'\"<html>Code Scan Results</html>\"'
		
	} 

	procedure "Application URL",{
		
		step "Create application link",
			command: "ectool setProperty /myPipelineStageRuntime/ec_summary/appUrl " +
				'\"<html>Application URL</html>\"'
		
	}
	
	procedure "System Tests",{
		
		step "Post Results",
			command: "ectool setProperty /myPipelineStageRuntime/ec_summary/systemTestResults " +
				'\"<html>System Test Results</html>\"'
		
	}

	procedure "Smoke Tests",{
		
		step "Post Results",
			command: "ectool setProperty /myPipelineStageRuntime/ec_summary/smokeTestResults " +
				'\"<html>Smoke Test Results</html>\"'
		
	}

}