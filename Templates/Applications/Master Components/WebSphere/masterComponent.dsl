/*
 * DSL for a master component for a WAR deployment on WebSphere
 */

/* ------------------------------------------------------
 Parameters used by the master component DSL
 Edit these parameter values based on your requirements
 */

// Name of Master Component for WebSphere AS
def masterComponentName = 'WebSphere AS Deployment and Undeployment Procedures'

// Project name where component will be created
def projectName = 'Default'

// End of master component parameters

def result

project projectName, {
	component masterComponentName, pluginName: null, {
		description = 'This master component contains steps for deployment and undeployment of J2EE applications in WebSphere application server.'
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

		formalParameter 'WebSphere Config', defaultValue: 'websphereConfig', {
			description = 'Configuration name of ElectricFlow WebSphere plugin'
			expansionDeferred = '0'
			label = 'WebSphere Configuration Name'
			orderIndex = '3'
			required = '1'
			type = 'entry'
		}

		formalParameter 'WebSphere Home', defaultValue: null, {
			description = 'Directory where WebSphere profile is installed'
			expansionDeferred = '0'
			label = 'WebSphere Profile Directory'
			orderIndex = '4'
			required = '1'
			type = 'entry'
		}

		formalParameter 'WebSphere Server', defaultValue: 'localhost', {
			description = 'Name of WebSphere server to which application will be deployed'
			expansionDeferred = '0'
			label = 'WebSphere Server'
			orderIndex = '5'
			required = '1'
			type = 'entry'
		}

		formalParameter 'MySQL Connection', defaultValue: 'jdbc:mysql://localhost:3306/database?user=mysql_user&password=mysql_password', {
			description = 'JDBC URL of MySQL database'
			expansionDeferred = '0'
			label = 'MySQL Connection String'
			orderIndex = '6'
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
	  	echo -e "jdbc.driverClassName=com.mysql.jdbc.Driver\njdbc.url=$[MySQL Connection]" > WEB-INF/jdbc.properties
	    jar uf $[Artifact Name] WEB-INF/jdbc.properties
	  '''
				actualParameter 'shellToUse', 'sh'
			}

			processStep 'Deploy', {
				errorHandling = 'failProcedure'
				processStepType = 'plugin'
				subprocedure = 'DeployApp'
				subproject = '/plugins/EC-WebSphere/project'
				actualParameter 'additionalcommands', ''
				actualParameter 'appname', '$[/javascript "$[Artifact Name]".slice(0, -4)]'
				actualParameter 'apppath', '$[Artifact Name]'
				actualParameter 'classpath', ''
				actualParameter 'commands', ''
				actualParameter 'configname', '$[WebSphere Config]'
				actualParameter 'connectionType', 'SOAP'
				actualParameter 'javaparams', ''
				actualParameter 'wsadminabspath', '$[WebSphere Home]/bin/wsadmin.sh'
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
				subproject = '/plugins/EC-WebSphere/project'
				actualParameter 'additionalcommands', ''
				actualParameter 'appname', '$[/javascript "$[Artifact Name]".slice(0, -4)]'
				actualParameter 'classpath', ''
				actualParameter 'commands', ''
				actualParameter 'configname', '$[WebSphere Config]'
				actualParameter 'connectiontype', 'soap'
				actualParameter 'javaparams', ''
				actualParameter 'wsadminabspath', '$[WebSphere Home]/bin/wsadmin.sh'
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