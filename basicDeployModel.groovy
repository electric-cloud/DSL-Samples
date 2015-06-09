/*
Format: Electric Flow DSL
File: basicDeployModel.groovy
Description: Electric Flow Deploy model template

ectool --format json evalDsl --dslFile basicDeployModel.groovy

Run from Command Step
---------------------
	Set shell to:  ectool evalDsl --dslFile {0}
*/

// Customizable values ------------------

// Application Name
def appName = "Sample Deploy Application"

// Environment names ["env1", "env2" ...]
def envs = ["sample-dev","sample-qa"]

// Application-Environment tier mapping ["apptier1":"envtier1", "apptier2":"envtier2" ...]
def appEnvTiers = ["sample_app_tier1":"sample_env_tier1", "sample_app_tier2":"sample_env_tier2"]

// Artifact group id
def artifactRoot = "com.ec.sample"

// Project name - currently only "Default" is supported by the Electric Flow Deploy UI
def projectName = "Default"

// ---------------------------------------

def envTiers = appEnvTiers.values()
def appTiers = appEnvTiers.keySet()

// Remove old application model
deleteApplication (projectName: projectName, applicationName: appName) 

// Remove old Environment models
envs.each { env ->
	appTiers.each() { tier ->
		def res = "${env}_${tier}"
		deleteResource resourceName: res
	}
	deleteEnvironment(projectName: projectName, environmentName: env)
}

// Create new -------------------------------

def artifactVersions = []

project projectName, {

	// Create Environments, Tiers and Resources
	envs.each { env ->
		environment environmentName: env, {
			envTiers.each() { tier ->
				def res = "${env}_${tier}"
				environmentTier tier, {
					// create and add resource to the Tier
					resource resourceName: res, hostName : "localhost"
				}
			}
		}
	} // Environments

	application applicationName: appName, {
		
		process processName: "Deploy"
		
		appTiers.each() { tier ->
			applicationTier tier, {
				def compName = "${tier}_comp"
				def artifactVersion = "1.35"
				def artifactName_ = artifactRoot + ':' + compName
				artifactVersions << [artifactName: artifactName_, artifactVersion: artifactVersion]
				// Create artifact
				artifact groupId: artifactRoot, artifactKey: compName
				//def artifactName = "com.ec.test:test"
			
				//component componentName: compName, pluginName: "EC-Artifact-1.0.9.76076", {
				component componentName: compName, pluginName: getPlugin(pluginName: "EC-Artifact").pluginName, {
					ec_content_details.with { 
						pluginProjectName = "EC-Artifact"
						pluginProcedure = "Retrieve"
						artifactName = artifactName_
						filterList = ""
						overwrite = "update"
						versionRange = artifactVersion
						artifactVersionLocationProperty = "/myJob/retrievedArtifactVersions/\$" + "[assignedResourceName]"
					}

					process processName: "Install",
						processType: "DEPLOY",
						//componentApplicationName: this.applicationName,
						componentApplicationName: appName,
						applicationName: null,
						{
						processStep processStepName: "Retrieve Artifact",
							//componentApplicationName: appName, //???
							processStepType: "component",
							subprocedure: "Retrieve",
							errorHandling: "failProcedure",
							subproject: "/plugins/EC-Artifact/project",
							applicationName: null,
							applicationTierName: null,
							//includeCompParameterRef: true
							actualParameter: [ 
								artifactName : "\$" + "[/myComponent/ec_content_details/artifactName]",
								artifactVersionLocationProperty : "\$" + "[/myComponent/ec_content_details/artifactVersionLocationProperty]",
								filterList : "\$" + "[/myComponent/ec_content_details/filterList]",
								overwrite : "\$" + "[/myComponent/ec_content_details/overwrite]",
								versionRange : "\$" + "[/myJob/ec_" + compName + "-version]"
							]
							
						processStep processStepName: "Deploy Artifact",
							applicationName: null,
							applicationTierName: null,
							componentApplicationName: appName,
							command: "echo testing $compName..."
							
						processStep processStepName: "Deploy Artifact",
							processStepType: 'command',
							subproject: '/plugins/EC-Core/project',
							subprocedure: 'RunCommand',
							actualParameter: [
								shellToUse: 'sh',
								commandToRun: 'sh $' + '[/myJob/retrievedArtifactVersions/$' + '[assignedResourceName]/$' + '[/myComponent/ec_content_details/artifactName]/cacheLocation]/installer.sh'
								],
							applicationName: null,
							applicationTierName: null,
							componentApplicationName: appName		
							
						createProcessDependency componentApplicationName: appName,
							processStepName: "Retrieve Artifact",
							targetProcessStepName: "Deploy Artifact"
							
					}

					processStep  processStepName: "Install $compName",
						processName: "Deploy",
						processStepType: 'process',
						componentName: null,
						applicationName: appName,
						componentApplicationName: appName,
						errorHandling: 'failProcedure',
						subcomponent: compName,
						subcomponentApplicationName: appName,
						subcomponentProcess: "Install",
						applicationTierName: tier
						
				} // Components
			} 
		} // Application Tiers

		envs.each { env -> 
			tierMap tierMapName: "$appName-$env",
				environmentProjectName: projectName, // Replace with projectName reference
				environmentName: env,
				tierMapping: appEnvTiers			
		}

	} // Applications

} // project

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

artifactVersions.each { ar ->
	// Create artifact version
	transaction {
		runProcedure procedureName: "publishArtifact", projectName: projectName,
			actualParameter: [
				artifactName: ar.artifactName,
				fileContent: "echo Installing " + ar.artifactName,
				fileName: "installer.sh",
				artifactVersion: ar.artifactVersion
				]
	}
}


