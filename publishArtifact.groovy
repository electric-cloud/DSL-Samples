/*
Format: Electric Flow DSL
file: publishArtifact.groovy
Description: Create an artifact and publish artifact version

What it does
------------
Creates artifact version directly with DSL
Creates a procedure to publish the artifact version
- This is necessary because publishing requires an agent/job, but DSL is Server-side actions
Runs the procedure

Command-line run instructions
----------------------------- 
	ectool --format json evalDsl --dslFile publishArtifact.groovy
	
Run from Command Step
---------------------
	Set shell to:  ectool evalDsl --dslFile {0}
*/

projectName="test2"
artifactKey = "test2"
groupId = "com.ec.test"
artifactVersion = "10.2.2"
installer = "setup.sh"
fileContent = "echo testing " + groupId +':'+ artifactKey

artifactName=groupId +":"+ artifactKey

// Create publishArtifact procedure
project projectName, {
	procedure "publishArtifact", {
		formalParameter "artifactName", type: "textentry", required: "1"
		formalParameter "artifactVersion", type: "textentry", required: "1"
		formalParameter "fileName", type: "textentry", required: "1"
		formalParameter "fileContent", type: "textarea", required: "1"
		
		step "Create File",
			subproject: "/plugins/EC-FileOps/project",
			subprocedure: "AddTextToFile",
			actualParameter: [
				Path: '$' + "[fileName]",
				Content: '$' + "[fileContent]",
				AddNewLine: "0",
				Append: "0"
			]
			
		step "Publish Artifact",
			subproject: "/plugins/EC-Artifact/project",
			subprocedure: "Publish",
			actualParameter: [
				artifactName: '$' + "[artifactName]",
				artifactVersionVersion: '$' + "[artifactVersion]",
				includePatterns: '$' + "[fileName]",
				repositoryName: "Default"
				//fromLocation:
			]
	}
}

artifact groupId: groupId, artifactKey: artifactKey

transaction {
	runProcedure procedureName: "publishArtifact", projectName: projectName,
		actualParameter: [
			artifactName: artifactName,
			fileContent: fileContent,
			fileName: installer,
			artifactVersion: artifactVersion
			]
}
