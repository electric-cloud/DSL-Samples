/*
 * DSL to create a resource template with the given configuration
 */
 
 // ------------------------------------------------------
 // Parameters used by the resource template
 // You should set these parameter values 
 // to create the required resource template.
 // Once the resource template has been created 
 // on the ElectricFlow server, it can be used 
 // to provision dynamic environments for 
 // application deployments.

 // 1. Set the resource template name 
 def resourceTemplateName = 'PROVIDE RESOURCE TEMPLATE NAME'
 
 // 2. Update the project name if the resource template
 // should be created in a project other than 'Default'. 
 // The project will be created if it does not exist.
 def projectName = 'Default'
 
 // 3. Whether the referenced plugin configurations 
 // should be created or replaced if they already exists.
 // Set this flag to false if the configurations already 
 // exist and you do not want the script to replace them.
 // This flag applies to both the cloud provider plugin 
 // configuration and the configuration management plugin.
 def createOrReplaceConfiguration = true
 
 def cloudProviderPluginConfiguration = 'ec2'
 def configMgmtPluginConfiguration    = 'chef'
 
 // 4 (a) Set the following configurations for Amazon EC2
 def cloudProviderConfigurations = [
    'config': cloudProviderPluginConfiguration,
    //TODO: EC2 CreateConfiguration parameters
  ]

 // 4 (b) Set the following provisioning parameters for Amazon EC2
 def provisioningParameters = [
    'config': cloudProviderPluginConfiguration,
    'count': '1',
	'group': 'sg-1e7bcf7b',
    'image': 'ami-17b75453',
    'instanceInitiatedShutdownBehavior': '',
    'instanceType': 't2.medium',
    'keyname': 'ECPluginTest',
    'privateIp': '',
    'propResult': '',
    'res_poolName': '',
    'res_port': '',
    'res_workspace': '',
    'resource_zone': 'default',
    'subnet_id': 'subnet-be3293db',
    'use_private_ip': '1',
    'userData': '',
    'zone': 'us-west-1c',
  ]
  
  // 5 (a) Set the following configurations for Chef
 def configMgmtConfigurations = [
    'config': configMgmtPluginConfiguration,
    //TODO: Chef CreateConfiguration parameters
  ]

 // 5 (b) Set the following configuration management parameters for Chef
 def configMgmtParameters = [
    additional_arguments : '',
    chef_client_path : '/usr/bin/chef-client',
    config : configMgmtPluginConfiguration,
    node_name : '',
    run_list : 'tomcat, java',
    use_sudo : '1',
 ]
 
	
 // End of resource template parameters -----------------------------
 
 // Cloud provider plugin configuration
 def cloudProvider = [ 
   name : 'EC-EC2',
   procedureName : 'API_RunInstances',
   parameters : provisioningParameters,
   configurations: cloudProviderConfigurations
 ]
 
 def configMgmtProvider = [ 
   name : 'EC-Chef',
   procedureName : '_RegisterAndConvergeNode',
   parameters : configMgmtParameters,
   configurations: configMgmtConfigurations
 ]
 
 // Create the plugin configurations if required
 
 if (createOrReplaceConfiguration) {
 
	// TODO: Handle Cloud provider plugin configuration
	//1. Delete Configuration in case it exists
	//2. Create the configuration
	
	// TODO: Handle Configuration management plugin configuration
	//1. Delete Configuration in case it exists
	//2. Create the configuration
	
 }
 
 // Resource template DSL
def result 

project projectName, { 
  result = resourceTemplate resourceTemplateName, {

    cloudProviderPluginKey = cloudProvider.name
    cloudProviderProcedure = cloudProvider.procedureName
    cloudProviderProjectName = null
  
    cloudProviderParameter = cloudProvider.parameters
  
    cfgMgrPluginKey = configMgmtProvider.name
    cfgMgrProcedure = configMgmtProvider.procedureName
    cfgMgrProjectName = null
  
    cfgMgrParameter = configMgmtProvider.parameters

  }

}
// return the resource template created
result