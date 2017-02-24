/*
Format: Electric Flow DSL
File: createAppModel.groovy
Description: Create environment and application models
*/
import groovy.json.JsonOutput

def projName  = "$[projName]"
def appName  = "$[appName]"
def artifactGroup = "$[artifactGroup]"
def artifactKey = "$[artifactKey]"
def envs = "$[envs]".split(",") // Comma separated names

def envTier = "Wildfly"
def appTier = "App Svr"
def artifactVersion = "" // Latest
def artifactName_ = artifactGroup + ':' + artifactKey

def appEnvTiers = [(appTier):envTier]

project projName, {
	// Create Environments, Tiers and Resources
	def resources = []
	def environments = []
	//envs.each { envShorthand, envName ->
	envs.each { env ->
		environments.push(env)
		environment env, {
			environmentTier envTier, {
				// create and add resource to the Tier
				res = "${environmentName}_${envTier}"
				resources.push(res)
				resource resourceName: res, hostName : "localhost"

			} // environmentTier
		} // environment
	} // Environments
	
	property '/jobs/$[/myJob]/resources', value: JsonOutput.toJson(resources)	
	property '/jobs/$[/myJob]/environments', value: JsonOutput.toJson(environments)	
	
	application applicationName: appName, {
		applicationTier appTier, {

			//artifactVersions << [artifactName: artifactName_, artifactVersion: artifactVersion]
			// Create artifact
			artifact groupId: artifactGroup, artifactKey: artifactKey
		
			//component componentName: artifactKey, pluginName: getPlugin(pluginName: "EC-Artifact").pluginName, {
			component componentName: artifactKey, pluginKey: "EC-Artifact", {
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
					componentApplicationName: appName,
					applicationName: null,
					{
					processStep processStepName: "Retrieve Artifact",
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
							//versionRange : "\$" + "[/myJob/ec_" + artifactKey + "-version]"
							versionRange : "\$" + "[/myJob/ec_" + componentName + "-version]"
						]
					/*
					processStep processStepName: "Deploy Artifact",
						applicationName: null,
						applicationTierName: null,
						componentApplicationName: appName,
						command: "echo testing $artifactKey..."
					*/
					processStep processStepName: "Deploy Artifact",
						processStepType: 'command',
						subproject: '/plugins/EC-Core/project',
						subprocedure: 'RunCommand',
						actualParameter: [
							// Linux: sh "fullPathToFile/installer.sh"
							// Windows: "fullPathToFile\installer.bat"
							commandToRun: '$' + '[/javascript myResource.hostPlatform=="linux"?"sh ":""]' +
							'\"' + // Quote entire command
							'$' + '[/myJob/retrievedArtifactVersions/$' + '[assignedResourceName]/' + 
							'$' + '[/myComponent/ec_content_details/artifactName]/cacheLocation]' + 
							'$' + '[/javascript myResource.hostPlatform=="linux"?"/":"\\\\"]' + // Slash direction 
							'installer.' + '$' + '[/javascript myResource.hostPlatform=="linux"?"sh":"bat"]' + '\"'
							],
						applicationName: null,
						applicationTierName: null,
						componentApplicationName: appName		
						
					createProcessDependency componentApplicationName: appName,
						processStepName: "Retrieve Artifact",
						targetProcessStepName: "Deploy Artifact"
						
				}
			} // Components


		}
		
		process processName: "Deploy",{
			processStep  processStepName: "Install $artifactKey",
				processStepType: 'process',
				componentName: null,
				applicationName: appName,
				componentApplicationName: appName,
				errorHandling: 'failProcedure',
				subcomponent: artifactKey,
				subcomponentApplicationName: appName,
				subcomponentProcess: "Install",
				applicationTierName: appTier
		}
		
		//envs.each { envShorthand, envName ->
		envs.each { env ->
			tierMap tierMapName: "$appName-$env",
				environmentProjectName: projectName, // Replace with projectName reference
				environmentName: "${env}",
				tierMapping: appEnvTiers			
		}

	} // Applications

}