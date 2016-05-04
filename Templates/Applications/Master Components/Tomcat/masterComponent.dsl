/*
 * DSL for a master component for a WAR deployment on Tomcat
 */

/* ------------------------------------------------------
 Parameters used by the master component DSL
 Edit these parameter values based on your requirements
 */

// Name of Master Component for Tomcat AS
def masterComponentName = 'Tomcat AS Deployment and Undeployment Procedures'

// Project name where component will be created
def projectName = 'Default'

// End of master component parameters


def result

project projectName, {

	result = component masterComponentName, pluginName: null, {
		description = 'This master component contains steps for deployment and undeployment of J2EE applications in Tomcat application server.'
		pluginKey = 'EC-FileSysRepo'
		reference = '0'

		formalParameter 'Artifact Name', defaultValue: 'jpetstore-mysql.war', {
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

		formalParameter 'Tomcat Config', defaultValue: 'tomcatConfig', {
			description = 'Configuration name of ElectricFlow Tomcat plugin'
			expansionDeferred = '0'
			label = 'Tomcat Configuration Name'
			orderIndex = '3'
			required = '1'
			type = 'entry'
		}

		formalParameter 'MySQL Connection', defaultValue: 'jdbc:mysql://localhost:3306/database?user=mysql_user&password=mysql_password', {
			description = 'JDBC URL of MySQL database'
			expansionDeferred = '0'
			label = 'MySQL Connection String'
			orderIndex = '4'
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

			processStep 'update jdbc properties', {
				errorHandling = 'failProcedure'
				processStepType = 'command'
				subprocedure = 'RunCommand'
				subproject = '/plugins/EC-Core/project'
				actualParameter 'commandToRun', '''
	    cd "$[/myComponent/ec_content_details/directory]"
	  	mkdir WEB-INF
	  	echo "jdbc.driverClassName=com.mysql.jdbc.Driver\njdbc.url=$[MySQL Connection]" > WEB-INF/jdbc.properties
	    jar uf $[Artifact Name] WEB-INF/jdbc.properties
	  '''
				actualParameter 'shellToUse', 'sh'
			}

			processStep 'Deploy', {
				errorHandling = 'failProcedure'
				processStepType = 'plugin'
				subprocedure = 'DeployApp'
				subproject = '/plugins/EC-Tomcat/project'
				actualParameter 'applicationconfigfilepath', ''
				actualParameter 'apppath', '$[Artifact Name]".slice(0, -4)]'
				actualParameter 'serverconfigname', '$[Tomcat Config]'
				actualParameter 'updateapp', '0'
				actualParameter 'warfile', '$[Artifact Name]'
			}

			processDependency 'get app files', targetProcessStepName: 'update jdbc properties', { branchType = 'ALWAYS' }

			processDependency 'update jdbc properties', targetProcessStepName: 'Deploy', { branchType = 'ALWAYS' }
		}

		process 'Undeploy', {
			processType = 'UNDEPLOY'

			processStep 'Undeploy', {
				errorHandling = 'failProcedure'
				processStepType = 'plugin'
				subprocedure = 'UndeployApp'
				subproject = '/plugins/EC-Tomcat/project'
				actualParameter 'apppath', '$[Artifact Name]".slice(0, -4)]'
				actualParameter 'configname', '$[Tomcat Config]'
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

// return the master component in the evalDsl response
result