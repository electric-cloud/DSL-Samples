/*
 * DSL for a master component for a WAR deployment on JBoss
 */
 
 /* ------------------------------------------------------
  Parameters used by the master component DSL
  Edit these parameter values based on your requirements
 */
 
 // Name of Master Component for JBoss AS
 def masterComponentName = 'JBoss AS Deployment and Undeployment Procedures'
 
 // Project name where component will be created
 def projectName = 'Default'

 // End of master component parameters
 
 
def result

project projectName, { 

result = component 'JBoss AS Deployment and Undeployment Procedures', pluginName: null, {
  description = 'This master component contains steps for deployment and undeployment of J2EE applications in JBoss application server.'
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

	formalParameter 'JBoss Config', defaultValue: null, {
		description = 'Configuration name of ElectricFlow JBoss plugin'
		expansionDeferred = '0'
		label = 'JBoss Configuration Name'
		orderIndex = '3'
		required = '1'
		type = 'entry'
	}

	formalParameter 'JBoss Home', defaultValue: null, {
		description = 'Directory where JBoss AS is installed'
		expansionDeferred = '0'
		label = 'JBoss Installation Directory'
		orderIndex = '4'
		required = '1'
		type = 'entry'
	}

	formalParameter 'MySQL Connection', defaultValue: 'jdbc:mysql://localhost:3306/database?user=mysql_user&password=mysql_password', {
		description = 'JDBC URL of MySQL database'
		expansionDeferred = '0'
		label = 'MySQL Connection String'
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

    processStep 'update jdbc properties', {
      errorHandling = 'failProcedure'
      processStepType = 'command'
      
      subprocedure = 'RunCommand'
      subproject = '/plugins/EC-Core/project'
      actualParameter 'commandToRun', '''
	    cd "$[/myComponent/ec_content_details/directory]"
	  	mkdir WEB-INF
	  	echo "jdbc.driverClassName=com.mysql.jdbc.Driver
	  		jdbc.url=$[MySQL Connection]" > WEB-INF/jdbc.properties

	    jar uf $[Artifact Name] WEB-INF/jdbc.properties
	  '''
      actualParameter 'shellToUse', 'sh'
    }

    processStep 'Deploy', {
      errorHandling = 'failProcedure'
      processStepType = 'plugin'
      
      subprocedure = 'DeployApp'
      subproject = '/plugins/EC-JBoss/project'
      actualParameter 'appname', ''
      actualParameter 'assignallservergroups', '0'
      actualParameter 'assignservergroups', ''
      actualParameter 'force', '0'
      actualParameter 'runtimename', ''
      actualParameter 'scriptphysicalpath', '$[JBoss Home]/bin/jboss-cli.sh'
      actualParameter 'serverconfig', '$[JBoss Config]'
      actualParameter 'warphysicalpath', '$[Artifact Name]'
    }

	processDependency 'get app files', targetProcessStepName: 'update jdbc properties', {
		branchType = 'ALWAYS'
	}
  
    processDependency 'update jdbc properties', targetProcessStepName: 'Deploy', {
      branchType = 'ALWAYS'
    }
  }

  process 'Undeploy', {
    processType = 'UNDEPLOY'
    
    processStep 'Undeploy', {
      errorHandling = 'failProcedure'
      processStepType = 'plugin'
      
      subprocedure = 'UndeployApp'
      subproject = '/plugins/EC-JBoss/project'
      actualParameter 'allrelevantservergroups', '0'
      actualParameter 'appname', '$[Artifact Name]'
      actualParameter 'keepcontent', '0'
      actualParameter 'scriptphysicalpath', '$[JBoss Home]/bin/jboss-cli.sh'
      actualParameter 'serverconfig', '$[JBoss Config]'
      actualParameter 'servergroups', ''
    }
  }
  
  property 'ec_content_details',  {
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