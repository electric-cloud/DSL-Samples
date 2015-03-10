/*
Format: Electric Flow DSL
File: basicDeployModel.groovy
Description: Electric Flow Deploy model template

ectool --format json evalDsl --dslFile basicDeployModel.groovy

ectool createArtifact --groupId com.ec.sample --artifactKey sample_app_tier1_comp
ectool createArtifact --groupId com.ec.sample --artifactKey sample_app_tier2_comp
ectool publishArtifactVersion --artifactName com.ec.sample:sample_app_tier2_comp --includePatterns installer.sh --fromDirectory . --version 1.5
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

// ---------------------------------------

def envTiers = appEnvTiers.values()
def appTiers = appEnvTiers.keySet()

// Remove old application model
deleteApplication (projectName: "Default", applicationName: appName) 

// Remove old Environment models
envs.each { env ->
	appTiers.each() { tier ->
		def res = "${env}_${tier}"
		deleteResource resourceName: res
	}
	deleteEnvironment(projectName: "Default", environmentName: env)
}

// Create new -------------------------------

project "Default", {

	// Create Environments, Tiers and Resources
	envs.each { env ->
		environment environmentName: env, {
			envTiers.each() { tier ->
				def res = "${env}_${tier}"
				resource resourceName: res, hostName : "localhost"
				environmentTier tier, { addResourceToEnvironmentTier(resourceName: res) }
			}
		}
	} // Environments

	application applicationName: appName, {
		
		process processName: "Deploy"
		
		appTiers.each() { tier ->
			applicationTier tier, {
				def compName = "${tier}_comp"
				component componentName: compName, pluginName: "EC-Artifact-1.0.9.76076", {
					ec_content_details.with { 
						pluginProjectName = "EC-Artifact"
						pluginProcedure = "Retrieve"
						artifactName = "$artifactRoot:$compName"
						filterList = ""
						overwrite = "update"
						versionRange = "1.5"
						artifactVersionLocationProperty = "/myJob/retrievedArtifactVersions/\$" + "[assignedResourceName]"
					}
					// TODO: Create artifact and artifact versions
					//createArtifact groupId: artifactRoot, artifactKey: compName
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
				environmentProjectName: "Default", // Replace with projectName reference
				environmentName: env,
				tierMapping: appEnvTiers			
		}

	} // Applications

} // project
