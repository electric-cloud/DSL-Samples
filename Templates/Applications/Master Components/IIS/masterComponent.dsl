/*
 * DSL for a master component for a Web Application deployment on IIS
 */

/* ------------------------------------------------------
 Parameters used by the master component DSL
 Edit these parameter values based on your requirements
 */

// Name of Master Component for IIS
def masterComponentName = 'IIS Deployment and Undeployment Procedures'

// Project name where component will be created
def projectName = 'Default'

// End of master component parameters

def result

project projectName, {

	component masterComponentName, pluginName: null, {
		description = 'This master component contains steps for deployment and undeployment of IIS website.'
		pluginKey = 'EC-FileSysRepo'

		formalParameter 'Artifact Name', defaultValue: 'wwwapp.zip', {
			expansionDeferred = '0'
			label = 'Artifact Name'
			orderIndex = '1'
			required = '1'
			type = 'entry'
		}

		formalParameter 'Source Directory', defaultValue: null, {
			description = 'Directory from which to retrieve artifact'
			expansionDeferred = '0'
			label = 'Source Directory'
			orderIndex = '2'
			required = '1'
			type = 'entry'
		}

		formalParameter 'IIS Config', defaultValue: 'iisConfig', {
			description = 'Configuration name of ElectricFlow IIS7 plugin'
			expansionDeferred = '0'
			label = 'IIS Configuration Name'
			orderIndex = '3'
			required = '1'
			type = 'entry'
		}

		formalParameter 'Web Site Name', defaultValue: 'Default Web Site', {
			description = 'Name of IIS web site to which add application'
			expansionDeferred = '0'
			label = 'Web Site Name'
			orderIndex = '4'
			required = '1'
			type = 'entry'
		}

		formalParameter 'Web Application Path', defaultValue: 'C:\\Inetpub\\wwwroot', {
			description = 'Physical path to web application. Files from Artifact will be placed to this location.'
			expansionDeferred = '0'
			label = 'Web Application Path'
			orderIndex = '5'
			required = '1'
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
				actualParameter 'destinationDir', '$[Web Application Path]\\$[/javascript "$[Artifact Name]".slice(0, -4)]'
				actualParameter 'zipFile', '$[Artifact Name]'
			}

			processStep 'create web application', {
				errorHandling = 'failProcedure'
				processStepType = 'plugin'
				subprocedure = 'CreateWebApplication'
				subproject = '/plugins/EC-IIS7/project'
				actualParameter 'appname', '$[Web Site Name]'
				actualParameter 'path', '/$[/javascript "$[Artifact Name]".slice(0, -4)]'
				actualParameter 'physicalpath', '$[Web Application Path]\\$[/javascript "$[Artifact Name]".slice(0, -4)]'
			}

			processDependency 'unzip files', targetProcessStepName: 'create web application', { branchType = 'ALWAYS' }

			processDependency 'get app files', targetProcessStepName: 'unzip files', { branchType = 'ALWAYS' }
		}

		process 'Undeploy', {
			processType = 'UNDEPLOY'

			processStep 'Undeploy', {
				errorHandling = 'failProcedure'
				processStepType = 'plugin'
				subprocedure = 'DeleteWebApplication'
				subproject = '/plugins/EC-IIS7/project'
				actualParameter 'appname', '$[Web Site Name]/$[/javascript "$[Artifact Name]".slice(0, -4)]'
			}
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