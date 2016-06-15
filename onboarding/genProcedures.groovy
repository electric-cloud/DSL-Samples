/*
Format: Electric Flow DSL
File: genProcedures.groovy
Description: 

*/
import groovy.json.JsonOutput

def projName  = "$[projName]"
def appName  = "$[appName]"
def appTech  = "$[appTech]"
def artifactGroup = "$[artifactGroup]"
def artifactKey = "$[artifactKey]"

def artifactName_ = "${artifactGroup}:${artifactKey}"
		
project projName, {
	procedure "Build",{
		
		step "Get Sources"
		
		step "Compile UNIX",
			subproject: "/plugins/EC-FileOps/project",
			subprocedure: "AddTextToFile",
			actualParameter: [
				Path: "installer.sh",
				Content: (String) "echo installing $appName",
				AddNewLine: "0",
				Append: "0"
			]
			
		step "Compile Windows",
			subproject: "/plugins/EC-FileOps/project",
			subprocedure: "AddTextToFile",
			actualParameter: [
				Path: "installer.bat",
				Content: (String) "echo installing $appName",
				AddNewLine: "0",
				Append: "0"
			]
			
		step "Post Results",
			command: "ectool setProperty /myPipelineStageRuntime/ec_summary/buildResults " +
				'\"<html><a href=\\\"http://www.electric-cloud.com/\\\">Build Results</a></html>\"'		
		
		step "Create Artifact Location",
			command: "artifact groupId: \"$artifactGroup\", artifactKey: \"$artifactKey\"",
			shell: "ectool evalDsl --dslFile {0}"

		property '/jobs/$[/myJob]/artifacts',
			value: "${artifactGroup}:${artifactKey}"
			
		step "Publish Artifact",
			subproject: "/plugins/EC-Artifact/project",
			subprocedure: "Publish",
			actualParameter: [
				artifactName: (String) artifactName_,
				artifactVersionVersion: "1.0.0-" + '$' + "[/increment /myProject/buildIndex]",
				includePatterns: "installer.*",
				repositoryName: "Default"
				//fromLocation:
			]
	} // Procedure Build
	
	procedure "Create Snapshot",{
		formalParameter "env"
		
		step "Delete the snapshot",
			command:  "ectool deleteSnapshot \"${projName}\" \"${appName}\" \"${appName}-1.0\""
		
		step "createSnapshot", 
			command: "ectool createSnapshot \"${projName}\" \"${appName}\" \"${appName}-1.0\" --environmentName \"" + '$' + "[env]\""		
	}
	
	procedure "Code Scan",{
		
		step "Post Results",
			command: "ectool setProperty /myPipelineStageRuntime/ec_summary/codeScanResults " +
				'\"<html><a href=\\\"http://www.electric-cloud.com/\\\">Code Scan Results</a></html>\"'
		
	} 

	procedure "Application URL",{
		
		step "Create application link",
			command: "ectool setProperty /myPipelineStageRuntime/ec_summary/appUrl " +
				'\"<html><a href=\\\"http://www.electric-cloud.com/\\\">Application URL</a></html>\"'
		
	}
	
	procedure "System Tests",{
		
		step "Post Results",
			command: "ectool setProperty /myPipelineStageRuntime/ec_summary/systemTestResults " +
				'\"<html><a href=\\\"http://www.electric-cloud.com/\\\">System Test</a></html>\"'
		
	}

	procedure "Smoke Tests",{
		
		step "Post Results",
			command: "ectool setProperty /myPipelineStageRuntime/ec_summary/smokeTestResults " +
				'\"<html><a href=\\\"http://www.electric-cloud.com/\\\">Smoke Test Results</a></html>\"'
		
	}

}