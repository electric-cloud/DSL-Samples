/*
 * DSL for a master component for a WAR deployment on JBoss
 */
 
 // ------------------------------------------------------
 // Parameters used by the master component DSL
 // Edit these parameter values based on your requirements
 
 def masterComponentName = 'PROVIDE MASTER COMPONENT NAME HERE'
 def projectName = 'Default'
 
 // End of master component parameters -------------------
 
 
def result
project projectName, { 

result = component masterComponentName, pluginName: null, {
  applicationName = null
  pluginKey = 'EC-FileSysRepo'
  reference = '0'
  sourceComponentName = null
  sourceProjectName = null

  process 'Deploy', {
    applicationName = null
    processType = 'DEPLOY'
    
    timeLimitUnits = null
    workspaceName = null

    processStep 'get app files', {
      applicationTierName = null
      dependencyJoinType = null
      errorHandling = 'failProcedure'
      instruction = null
      notificationTemplate = null
      processStepType = 'component'
      
      rollbackSnapshot = null
      rollbackType = null
      rollbackUndeployProcess = null
      smartRollback = '1'
      subcomponent = null
      subcomponentApplicationName = null
      subcomponentProcess = null
      subprocedure = 'Retrieve File Artifact'
      subproject = '/plugins/EC-FileSysRepo/project'
      timeLimitUnits = null
      workspaceName = null
      actualParameter 'artifact', '$[/myComponent/ec_content_details/artifact]'
      actualParameter 'directory', '$[/myComponent/ec_content_details/directory]'
      actualParameter 'source', '$[/myComponent/ec_content_details/source]'
      actualParameter 'version', '$[/myJob/ec_jpetstore-mysql.war master-version]'
    }

    processStep 'unzip war', {
      applicationTierName = null
      dependencyJoinType = null
      errorHandling = 'failProcedure'
      instruction = null
      notificationTemplate = null
      processStepType = 'plugin'
      
      rollbackSnapshot = null
      rollbackType = null
      rollbackUndeployProcess = null
      smartRollback = '1'
      subcomponent = null
      subcomponentApplicationName = null
      subcomponentProcess = null
      subprocedure = 'Unzip File'
      subproject = '/plugins/EC-FileOps/project'
      timeLimitUnits = null
      workspaceName = null
      actualParameter 'destinationDir', './jpetstore-mysql'
      actualParameter 'zipFile', 'jpetstore-mysql.war'
    }

    processStep 'copy to webapps', {
      applicationTierName = null
      dependencyJoinType = null
      errorHandling = 'failProcedure'
      instruction = null
      notificationTemplate = null
      processStepType = 'command'
      
      rollbackSnapshot = null
      rollbackType = null
      rollbackUndeployProcess = null
      smartRollback = '1'
      subcomponent = null
      subcomponentApplicationName = null
      subcomponentProcess = null
      subprocedure = 'RunCommand'
      subproject = '/plugins/EC-Core/project'
      timeLimitUnits = null
      workspaceName = null
      actualParameter 'commandToRun', '''destination_folder=$[/myEnvironment/artifactLocation]

cp -r jpetstore-mysql $destination_folder/'''
      actualParameter 'shellToUse', 'sh'
    }

    processStep 'update jdbc properties', {
      applicationTierName = null
      dependencyJoinType = null
      errorHandling = 'failProcedure'
      instruction = null
      notificationTemplate = null
      processStepType = 'command'
      
      rollbackSnapshot = null
      rollbackType = null
      rollbackUndeployProcess = null
      smartRollback = '1'
      subcomponent = null
      subcomponentApplicationName = null
      subcomponentProcess = null
      subprocedure = 'RunCommand'
      subproject = '/plugins/EC-Core/project'
      timeLimitUnits = null
      workspaceName = null
      actualParameter 'commandToRun', '''destination_folder=$[/myEnvironment/artifactLocation]

db_host=$[/myEnvironment/db_host]
db_user=$[/myEnvironment/db_user]
db_password=$[/myEnvironment/db_password]
db_name=$[/myEnvironment/db_name]

echo "jdbc.driverClassName=com.mysql.jdbc.Driver
jdbc.url=jdbc:mysql://$db_host/$db_name
jdbc.username=$db_user
jdbc.password=$db_password" > $destination_folder/jpetstore-mysql/WEB-INF/jdbc.properties

cd $destination_folder/jpetstore-mysql
jar cvf $destination_folder/jpetstore-mysql.war .'''
      actualParameter 'shellToUse', 'sh'
    }

    processStep 'Deploy', {
      applicationTierName = null
      dependencyJoinType = null
      errorHandling = 'failProcedure'
      instruction = null
      notificationTemplate = null
      processStepType = 'plugin'
      
      rollbackSnapshot = null
      rollbackType = null
      rollbackUndeployProcess = null
      smartRollback = '1'
      subcomponent = null
      subcomponentApplicationName = null
      subcomponentProcess = null
      subprocedure = 'DeployApp'
      subproject = '/plugins/EC-JBoss/project'
      timeLimitUnits = null
      workspaceName = null
      actualParameter 'appname', ''
      actualParameter 'assignallservergroups', '0'
      actualParameter 'assignservergroups', ''
      actualParameter 'force', '0'
      actualParameter 'runtimename', ''
      actualParameter 'scriptphysicalpath', '$[/myEnvironment/wildfly9_home]/bin/jboss-cli.sh'
      actualParameter 'serverconfig', 'jbossConfig'
      actualParameter 'warphysicalpath', '$[/myEnvironment/artifactLocation]/jpetstore-mysql.war'
    }

    processDependency 'get app files', targetProcessStepName: 'unzip war', {
      branchCondition = null
      branchConditionName = null
      branchConditionType = null
      branchType = 'ALWAYS'
    }

    processDependency 'unzip war', targetProcessStepName: 'copy to webapps', {
      branchCondition = null
      branchConditionName = null
      branchConditionType = null
      branchType = 'ALWAYS'
    }

    processDependency 'copy to webapps', targetProcessStepName: 'update jdbc properties', {
      branchCondition = null
      branchConditionName = null
      branchConditionType = null
      branchType = 'ALWAYS'
    }

    processDependency 'update jdbc properties', targetProcessStepName: 'Deploy', {
      branchCondition = null
      branchConditionName = null
      branchConditionType = null
      branchType = 'ALWAYS'
    }
  }

  process 'Undeploy', {
    applicationName = null
    processType = 'UNDEPLOY'
    
    timeLimitUnits = null
    workspaceName = null

    processStep 'Undeploy', {
      applicationTierName = null
      dependencyJoinType = null
      errorHandling = 'failProcedure'
      instruction = null
      notificationTemplate = null
      processStepType = 'plugin'
      
      rollbackSnapshot = null
      rollbackType = null
      rollbackUndeployProcess = null
      smartRollback = '1'
      subcomponent = null
      subcomponentApplicationName = null
      subcomponentProcess = null
      subprocedure = 'UndeployApp'
      subproject = '/plugins/EC-JBoss/project'
      timeLimitUnits = null
      workspaceName = null
      actualParameter 'allrelevantservergroups', '0'
      actualParameter 'appname', 'jpetstore-mysql.war'
      actualParameter 'keepcontent', '0'
      actualParameter 'scriptphysicalpath', '$[/myEnvironment/wildfly9_home]/bin/jboss-cli.sh'
      actualParameter 'serverconfig', 'jbossConfig'
      actualParameter 'servergroups', ''
    }

    processStep 'delete files', {
      applicationTierName = null
      dependencyJoinType = null
      errorHandling = 'failProcedure'
      instruction = null
      notificationTemplate = null
      processStepType = 'command'
      
      rollbackSnapshot = null
      rollbackType = null
      rollbackUndeployProcess = null
      smartRollback = '1'
      subcomponent = null
      subcomponentApplicationName = null
      subcomponentProcess = null
      subprocedure = 'RunCommand'
      subproject = '/plugins/EC-Core/project'
      timeLimitUnits = null
      workspaceName = null
      actualParameter 'commandToRun', 'echo "remove file"'
      actualParameter 'shellToUse', 'sh'
    }

    processDependency 'Undeploy', targetProcessStepName: 'delete files', {
      branchCondition = null
      branchConditionName = null
      branchConditionType = null
      branchType = 'ALWAYS'
    }
  }
}
}

// return the master component in the evalDsl response
result