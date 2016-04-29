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
 
 def cloudProviderPluginConfiguration = 'azure'
 def configMgmtPluginConfiguration    = 'chef'
 
 // 4 (a) Set the following configurations for Azure
 def cloudProviderConfigurations = [
    'config': cloudProviderPluginConfiguration,
    //TODO: Azure CreateConfiguration parameters
  ]

 // 4 (b) Set the following provisioning parameters for Azure
 def provisioningParameters = [
    'connection_config': cloudProviderPluginConfiguration,
    'region': 'East US',
    'resource_group_name': 'IIS_production1_servers',
    'vm_admin_password': 'admin',
    'vm_admin_user': 'admin',
    'vm_name': 'Prod_IIS',
  ]
  
 // 5 Set the following configuration management parameters for Chef
 def configMgmtParameters = [
      // Puppet master IP or Hostname
      'master_host': '10.168.30.1',
      'masterport': null,
	  // Path to the puppet agent binary on the provisioned VM
      'puppet_path': 'sudo /usr/bin/puppet',
	  // Any additional options to pass to the puppet agent command
	  'additional_options': null,
      'cert_name': null,
      'debug': '0',
 ]
 
	
 // End of resource template parameters -----------------------------
 
 // Cloud provider plugin configuration
 def cloudProvider = [ 
   name : 'EC-Azure',
   procedureName : 'Create VM',
   parameters : provisioningParameters,
   configurations: cloudProviderConfigurations
 ]
 
 def configMgmtProvider = [ 
   name : 'EC-Puppet',
   procedureName : 'ConfigureAgent',
   parameters : configMgmtParameters,
 ]
 
 // Create the plugin configurations if required
 
 if (createOrReplaceConfiguration) {
 
	// TODO: Handle Cloud provider plugin configuration
	//1. Delete Configuration in case it exists
	//2. Create the configuration
	
	//EC-Puppet does not have a configuration
	
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