/*
 * DSL for a master component for a Web Application deployment on IIS
 */

/* ------------------------------------------------------
 Parameters used by the master component DSL
 Edit these parameter values based on your requirements
 */

// Name of Master Component for IIS
def masterComponentName = '.NET Application Master Component'

// Project name where component will be created
def projectName = 'Default'

// End of master component parameters

def result

project projectName, {

	component masterComponentName, pluginName: null, {
		description = 'This master component contains steps for deploying and undeploying an app on an IIS website.'
		pluginKey = 'EC-FileSysRepo'

		formalParameter 'Artifact Name', defaultValue: null, {
			expansionDeferred = '0'
			label = 'Artifact Name'
			required = '1'
			orderIndex = 1
			type = 'entry'
		}

		formalParameter 'Source Directory', defaultValue: null, {
			description = 'Directory from which to retrieve artifact'
			expansionDeferred = '0'
			label = 'Source Directory'
			required = '1'
			orderIndex = 2
			type = 'entry'
		}

		/* IIS Configuration is not required for the deploy and undeploy processes
		formalParameter 'IIS Config', defaultValue: 'iisConfig', {
			description = 'Configuration name of ElectricFlow IIS7 plugin'
			expansionDeferred = '0'
			label = 'IIS Configuration Name'
			required = '1'
			orderIndex = 3
			type = 'entry'
		}*/

		formalParameter 'Web Site Name', defaultValue: 'Default Web Site', {
			description = 'Name of IIS web site on which to create the application'
			expansionDeferred = '0'
			label = 'Web Site Name'
			required = '1'
			orderIndex = 3
			type = 'entry'
		}

		formalParameter 'Application Name', defaultValue: null, {
			description = 'Name of the application created on the specified IIS site'
			expansionDeferred = '0'
			label = 'Application Name'
			required = '1'
			orderIndex = 4
			type = 'entry'
		}
		
		formalParameter 'Web Application Path', defaultValue: 'C:\\Inetpub\\wwwroot', {
			description = 'Physical path to web application. Files from Artifact will be placed to this location.'
			expansionDeferred = '0'
			label = 'Web Application Path'
			required = '1'
			orderIndex = 5
			type = 'entry'
		}

		process 'Deploy', {
			processType = 'DEPLOY'

			processStep 'get app files', {
				errorHandling = 'failProcedure'
				processStepType = 'component'
				subprocedure = 'Retrieve File Artifact'
				subproject = '/plugins/EC-FileSysRepo/project'
				actualParameter 'artifact', '$[/myComponent/ec_content_details/artifact]'
				actualParameter 'directory', '$[/myComponent/ec_content_details/directory]'
				actualParameter 'source', '$[/myComponent/ec_content_details/source]'
				actualParameter 'version', "\$[/myJob/ec_${masterComponentName}-version]"
			}

			processStep 'unzip files', {
				errorHandling = 'failProcedure'
				processStepType = 'plugin'
				subprocedure = 'Unzip File'
				subproject = '/plugins/EC-FileOps/project'
				actualParameter 'destinationDir', '$[Web Application Path]\\$[Artifact Name]'
				actualParameter 'zipFile', '$[Artifact Name].zip'
			}

			processStep 'create web application', {
				errorHandling = 'failProcedure'
				processStepType = 'plugin'
				subprocedure = 'CreateWebApplication'
				subproject = '/plugins/EC-IIS7/project'
				actualParameter 'appname', '$[Web Site Name]'
				actualParameter 'path', '/$[Application Name]'
				actualParameter 'physicalpath', '$[Web Application Path]\\$[Artifact Name]'
			}

			processStep 'create application pool', {
				errorHandling = 'failProcedure'
				processStepType = 'plugin'
				subprocedure = 'CreateAppPool'
				subproject = '/plugins/EC-IIS7/project'
				actualParameter 'apppoolname', '$[Application Name] Pool'
			}
			
			processStep 'assign application to pool', {
				errorHandling = 'failProcedure'
				processStepType = 'plugin'
				subprocedure = 'AssignAppToAppPool'
				subproject = '/plugins/EC-IIS7/project'
				actualParameter 'apppoolname', '$[Application Name] Pool'
				actualParameter 'appname', '/$[Application Name]'
				actualParameter 'sitename', '$[Web Site Name]'
			}
			
			processDependency 'create application pool', targetProcessStepName: 'assign application to pool', { branchType = 'ALWAYS' }
			
			processDependency 'create web application', targetProcessStepName: 'create application pool', { branchType = 'ALWAYS' }
						
			processDependency 'unzip files', targetProcessStepName: 'create web application', { branchType = 'ALWAYS' }

			processDependency 'get app files', targetProcessStepName: 'unzip files', { branchType = 'ALWAYS' }
		}

		process 'Undeploy', {
			processType = 'UNDEPLOY'

			processStep 'undeploy application', {
				errorHandling = 'failProcedure'
				processStepType = 'plugin'
				subprocedure = 'DeleteWebApplication'
				subproject = '/plugins/EC-IIS7/project'
				actualParameter 'appname', '$[Web Site Name]/$[Application Name]'
			}
			
			processStep 'delete application pool', {
				errorHandling = 'failProcedure'
				processStepType = 'plugin'
				subprocedure = 'DeleteAppPool'
				subproject = '/plugins/EC-IIS7/project'
				actualParameter 'apppoolname', '$[Application Name] Pool'
			}
			
			processDependency 'undeploy application', targetProcessStepName: 'delete application pool', { branchType = 'ALWAYS' }
		}

		// Custom properties

		property 'ec_content_details', {

			// Custom properties
			artifact = '$[Artifact Name]'
			directory = ''
			pluginProcedure = 'Retrieve File Artifact'
			pluginProjectName = 'EC-FileSysRepo'
			source = '$[Source Directory]'
			version = ''
		}
	}
}