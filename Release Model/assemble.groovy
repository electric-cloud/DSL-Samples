/*
Format: Electric Flow DSL
File: assemble.groovy
Description: Top level DSL to create release and all dependencies

ectool evalDsl --dslFile "assemble.groovy"

*/

def dslDir = "/vagrant/DSL-Samples/Release Model/"

def projectName = "On line bank Release"
def artifactGroup = "com.mybank.apps"
def applications = [
  [name: "OB - Account Statements", artifactKey: "statements", envs: ["Banking-UAT", "Banking-STG", "Banking-PROD"] ],
  [name: "OB - Credit Card Accounts", artifactKey: "cards", envs: ["Banking-UAT", "Banking-STG", "Banking-PROD"]],
  [name: "OB - Fund Transfer", artifactKey: "fund", envs: ["Banking-UAT", "Banking-STG", "Banking-PROD"] ]
]
def release = [
  name: "Quarterly Online Banking Release",
  pipeline: [
    name: "Quarterly Online Banking Pipeline",
    stages: ["UAT", "STG", "PROD"]
  ],
  apps: applications
]
project projectName, {
	procedure "Create Application",{
		formalParameter "appName", required: "1"
		formalParameter "artifactGroup", required: "1"
		formalParameter "artifactKey", required: "1"
		formalParameter "envs", required: "1"
		
	step "Create Installer",
	  subproject : "/plugins/EC-FileOps/project",
	  subprocedure : "AddTextToFile",
	  actualParameter : [
		AddNewLine: "1",
		Append: "1",
		Content: "echo installing \$[appName]",  // required
		Path: "installer.sh",  // required
	  ]
	step "Create Artifact",
	  subproject : "/plugins/EC-Artifact/project",
	  subprocedure : "Publish",
	  actualParameter : [
		artifactName: "\$[artifactGroup]:\$[artifactKey]",  // required
		artifactVersionVersion: "1.0-\$[/increment /server/ec_counters/jobCounter]",  // required
		fromLocation: ".",
		includePatterns: "installer.sh",
		repositoryName: "default",  // required
	  ]
	step "Generate Application",
			command: new File(dslDir + "createAppModel.groovy").text,
			shell: "ectool evalDsl --dslFile {0}"
	}
	
	procedure "Create Pipeline",{
		formalParameter "stages", required: "1"
		formalParameter "release", required: "1"

		step "Generate Pipeline",
			command: new File(dslDir + "createPipeline.groovy").text,
			shell: "ectool evalDsl --dslFile {0}"
	}
	
	procedure "Create Release",{
		formalParameter "release", required: "1"
		formalParameter "applications", required: "1"
		formalParameter "stages", required: "1"
		
		step "Generate Release",
			command: new File(dslDir + "createRelease.groovy").text,
			shell: "ectool evalDsl --dslFile {0}"	
		
	}
	
	procedure "Assemble",{
		// Create Application and Environment Models
		applications.each { app ->
			step "Generate Application - $app.name",
				subproject : projectName,
				subprocedure : "Create Application",
				actualParameter : [
					appName: app.name,  // required
					artifactGroup: artifactGroup,  // required
					artifactKey: (String) app.artifactKey,  // required
					envs: (String) app.envs.join(",")  // required
				]
		}

		// Create Pipeline
		step "Create Pipeline",
			subproject : projectName,
			subprocedure : "Create Pipeline",
			actualParameter : [
				stages: release.pipeline.stages.join(","),
				release: release.name
			]
			
		step "Create Release",
			subproject : projectName,
			subprocedure : "Create Release",
			actualParameter : [
				release: release.name,
				applications: release.apps.name.join(","),
				stages: release.pipeline.stages.join(",")
			]
		
	}
	
}