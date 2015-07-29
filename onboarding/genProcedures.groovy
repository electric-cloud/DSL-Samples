/*
Format: Electric Flow DSL
File: genProcedures.groovy
Description: 

*/

def appName  = "$[appName]"
def appTech  = "$[appTech]"
def artifactGroup = "$[artifactGroup]"
def artifactKey = "$[artifactKey]"

def artifactName_ = "${artifactGroup}:${artifactKey}"
		
project appName, {
	procedure "Build",{
		
		step "Get Sources"
		
		step "Compile",
			subproject: "/plugins/EC-FileOps/project",
			subprocedure: "AddTextToFile",
			actualParameter: [
				Path: "installer.sh",
				Content: (String) "echo installing $appName",
				AddNewLine: "0",
				Append: "0"
			]
			
		step "Post Results",
			command: "ectool setProperty /myPipelineStageRuntime/ec_summary/buildResults " +
				'\"<html>Build Results</html>\"'			
		
		step "Create Artifact Location",
			command: "artifact groupId: \"$artifactGroup\", artifactKey: \"$artifactKey\"",
			shell: "ectool evalDsl --dslFile {0}"
			
		step "Publish Artifact",
			subproject: "/plugins/EC-Artifact/project",
			subprocedure: "Publish",
			actualParameter: [
				artifactName: (String) artifactName_,
				artifactVersionVersion: "1.0.0-" + '$' + "[/increment /myProject/buildIndex]",
				includePatterns: "installer.sh",
				repositoryName: "Default"
				//fromLocation:
			]
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