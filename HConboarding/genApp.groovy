/*
Format: Electric Flow DSL
File: genApp.groovy
Description: Create environment and application models

*/

def appName  = "$[appName]"
def appTech  = "$[appTech]"
def artifactGroup = "$[artifactGroup]"
def artifactKey = "$[artifactKey]"
def stages = $[/myProject/stages] // Literal string to create stage mapping of logical and actual stage names

def envTierApp = "Wildfly"
def appTierApp = "App"
def envTierDb = "Oracle"
def appTierDb = "DB"
def artifactVersion = "" // Latest
def artifactName_ = artifactGroup + ':' + artifactKey

def appEnvTiers = [(appTierApp):envTierApp, (appTierDb):envTierDb]

project "Default", {
	// Create Environments, Tiers and Resources
	stages.each { envShorthand, envName ->
		environment "${envShorthand}-${appName}", {
			environmentTier envTierApp, {
				// create and add resource to the app Tier
				resource resourceName: "${environmentName}_${envTierApp}", hostName : "localhost"
			} // environmentTier App
			environmentTier envTierDb, {
				// create and add resource to the DB Tier
				resource resourceName: "${environmentName}_${envTierDb}", hostName : "localhost"
			} // environmentTier DB
		} // environment
	} // stages.each
	
	application applicationName: appName, {
		applicationTier appTierApp, {

			component componentName: artifactKey, pluginName: getPlugin(pluginName: "EC-Artifact").pluginName, {
				ec_content_details.with {
					pluginProjectName = "EC-Artifact"
					pluginProcedure = "Retrieve"
					artifactName = artifactName_
					filterList = ""
					overwrite = "update"
					retrieveToDirectory = "/tmp/broadleaf"
					versionRange = artifactVersion
					artifactVersionLocationProperty = "/myJob/retrievedArtifactVersions/\$" + "[assignedResourceName]"
				} // ec_content_details.with

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
							retrieveToDirectory : "\$" + "[/myComponent/ec_content_details/retrieveToDirectory]",
							versionRange : "\$" + "[/myJob/ec_" + artifactKey + "-version]"
						]
										
					processStep processStepName: "Deploy Artifact",
						processStepType: 'plugin',
						subproject: '/plugins/EC-JBoss/project',
						subprocedure: 'DeployApp',
						actualParameter: [
							serverconfig: "wildfly",
							scriptphysicalpath: "/opt/wildfly/bin/jboss-cli.sh",
							warphysicalpath: "/tmp/broadleaf/mycompany.war",
							appname: "mycompany.war",
							runtimename: "mycompany.war",
							force: "1"
							],
						applicationName: null,
						applicationTierName: null,
						componentApplicationName: appName
						
					createProcessDependency componentApplicationName: appName,
						processStepName: "Retrieve Artifact",
						targetProcessStepName: "Deploy Artifact"			
					
				} // Install process
				
			} // warfile
		} // App tier
		
		applicationTier appTierDb, {

			component componentName: "database.sql", pluginName: getPlugin(pluginName: "EC-Artifact").pluginName, {
				ec_content_details.with { 
					pluginProjectName = "EC-Artifact"
					pluginProcedure = "Retrieve"
					artifactName = "com.mycompany.heatclinic:config"
					filterList = ""
					overwrite = "update"
					retrieveToDirectory = "/tmp/broadleaf"
					versionRange = artifactVersion
					artifactVersionLocationProperty = "/myJob/retrievedArtifactVersions/\$" + "[assignedResourceName]"
				} // ec_content_details.with
				
				process processName: "Set up database",
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
							retrieveToDirectory : "\$" + "[/myComponent/ec_content_details/retrieveToDirectory]",
							versionRange : "\$" + "[/myJob/ec_" + artifactKey + "-version]"
						]
							
					processStep processStepName: "Create Database",
						processStepType: 'command',
						subproject: '/plugins/EC-Core/project',
						subprocedure: 'RunCommand',
						actualParameter: [
							shellToUse: 'sh',
							commandToRun: """\
								set -x
								mysql -u root --password=flow -e \"drop database broadleaf;\"
								mysql -u root --password=flow -e \"create database broadleaf;\"
								mysql -u root --password=flow broadleaf </opt/heatclinic.sql
								mysql -u root --password=flow -e \"grant all on broadleaf.* to broadleaf@localhost identified by 'secret';\"
							""".stripIndent()
							],
						applicationName: null,
						applicationTierName: null,
						componentApplicationName: appName	
							
						createProcessDependency componentApplicationName: appName,
							processStepName: "Retrieve Artifact",
							targetProcessStepName: "Create Database"
				} // component process
						
			} // DB component
		} // DB tier
		
		process processName: "Deploy",{

			processStep  processStepName: "Apply Schema",
				processStepType: 'process',
				componentName: null,
				applicationName: appName,
				componentApplicationName: appName,
				errorHandling: 'failProcedure',
				subcomponent: "database.sql",
				subcomponentApplicationName: appName,
				subcomponentProcess: "Set up database",
				applicationTierName: appTierDb
				
			processStep  processStepName: "Deploy App",
				processStepType: 'process',
				componentName: null,
				applicationName: appName,
				componentApplicationName: appName,
				errorHandling: 'failProcedure',
				subcomponent: artifactKey,
				subcomponentApplicationName: appName,
				subcomponentProcess: "Install",
				applicationTierName: appTierApp
									
			createProcessDependency componentApplicationName: appName,
				processStepName: "Apply Schema",
				targetProcessStepName: "Deploy App"
			
			processStep "Wait For App",
				processStepType: "procedure",
				subproject: "DemoLibrary",
				subprocedure: "WaitForApp",
				applicationTierName: "App",
				actualParameter: [url: "mycompany"]

			createProcessDependency componentApplicationName: appName,
				processStepName: "Deploy App",
				targetProcessStepName: "Wait For App"
	
		} // App deploy process
			
		stages.each { envShorthand, envName ->
			tierMap tierMapName: "$appName-$envShorthand",
				environmentProjectName: projectName, // Replace with projectName reference
				environmentName: "${envShorthand}-${appName}",
				tierMapping: appEnvTiers			
		}

	} // Applications
} // project