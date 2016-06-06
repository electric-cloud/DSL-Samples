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
def cloudProviderPluginConfiguration = 'cpConfig'
def configMgmtPluginConfiguration    = 'cmConfig'


// 4 (a) Set the following plugin configurations for Amazon EC2
def amazonConfigurations = [
    'config_name': cloudProviderPluginConfiguration,
    'debug': '10',
    'desc': 'EC2 configuration, created by DSL',
    'resource_pool': 'local',
    'service_url': 'https://ec2.amazonaws.com',
    'workspace': 'default',
    'attempt': '1',
    'userName': 'PROVIDE EC2 Access Key ID',
    'password': 'PROVIDE EC2 Secret Access Key',
];

// 4 (b) Update the following provisioning parameters for your EC2 account
def amazonParameters = [
    'config': cloudProviderPluginConfiguration,
    'count': '1',
	'group': 'PROVIDE Security Group ID',
    'image': 'ami-17b75453',
    'instanceInitiatedShutdownBehavior': '',
    'instanceType': 'm1.small',
    'keyname': 'ECPluginTest',
    'privateIp': '',
    'propResult': '',
    'res_poolName': '',
    'res_port': '',
    'res_workspace': '',
    'resource_zone': 'default',
    'subnet_id': 'subnet-36142770',
    'use_private_ip': '0',
    'userData': '',
    'zone': 'us-west-1b',
];

// 5 Set the following parameters for Puppet as the configuration management tool
 def puppetParameters = [
      'additional_options': null,
      'certname': null,
      'debug': '0',
      'master_host': '10.168.30.1',
      'masterport': null,
      'puppet_path': 'sudo /usr/bin/puppet',
]
 
// End of resource template parameters -----------------------------
 
// Cloud provider plugin configuration
def cloudProviders = [:]
def cloudProvider = 'Amazon';
 
cloudProviders['Amazon'] = [ 
    name : 'EC-EC2',
    procedureName : 'API_RunInstances',
    parameters : amazonParameters
];
     
def configMgmtProviders = [:]
def configMgmtProvider = 'Puppet';
 
configMgmtProviders['Puppet'] = [ 
    name : 'EC-Puppet',
    procedureName : 'ConfigureAgent',
    parameters : puppetParameters,
]
 
if (!cloudProvider || !cloudProviders[cloudProvider]) {
    throw new IllegalArgumentException ("Invalid cloud provider: $cloudProvider")
}
if (!configMgmtProvider || !configMgmtProviders[configMgmtProvider]) {
    throw new IllegalArgumentException ("Invalid configuration management provider: $configMgmtProvider")
}


// Service functions section

def createConfigAzure(P) {
    if (!P.config_name) {
        throw new IllegalArgumentException("config_name argument is required");
    }
    if (!P.userName || !P.password) {
        throw new IllegalArgumentException("userName and password arguments are required");
    }
    if (!P.vm_userName || !P.vm_password) {
        throw new IllegalArgumentException("vm_userName and vm_password arguments are required");
    }
    def MandatoryParams = [
        'debug_level',
        'resource_pool',
        'subscription_id',
        'tenant_id',
        'desc',
    ];
    Params = [:];
    MandatoryParams.each {
        def elem = P.get(it);
        if (!elem) {
            throw new IllegalArgumentException("Parameter $it is mandatory.");
        }
        Params.put(it, P.get(it));
    }

    createConfig([
                 pluginName: 'EC-Azure',
                 pluginProperty: 'azure_cfgs',
                 config_name: P.config_name,
                 //config_name_field: '',
                 credential: [
                     userName: P.userName,
                     password: P.password,
                     ],
                 credential2: [
                     credential_name: P.config_name + '_vm_credential',
                     credentialParameter: 'vm_credential',
                     userName: P.vm_userName,
                     password: P.vm_password,
                 ],
                 configParams: Params
                 ]);
    
    
}
def createConfigEC2(P) {
    if (!P.config_name) {
        throw new IllegalArgumentException("config_name argument is required");
    }
    if (!P.userName || !P.password) {
        throw new IllegalArgumentException("userName and password arguments are required");
    }
    def MandatoryParams = [
        'debug',
        'desc',
        'resource_pool',
        'service_url',
        'workspace',
        'attempt',
    ];
    Params = [:];
    MandatoryParams.each {
        def elem = P.get(it);
        if (!elem) {
            throw new IllegalArgumentException("Parameter $it is mandatory.");
        }
        Params.put(it, P.get(it));
    }
    
    createConfig([
                 pluginName: 'EC-EC2',
                 pluginProperty: 'ec2_cfgs',
                 config_name: P.config_name,
                 credential: [
                     userName: P.userName,
                     password: P.password,
                 ],
                 configParams: Params
                 ]);
}


def createConfigOpenStack(P) {
    if (!P.config_name) {
        throw new IllegalArgumentException("config_name argument is required");
    }
    if (!P.userName || !P.password) {
        throw new IllegalArgumentException("userName and password arguments are required");
    }
    def MandatoryParams = [
        'api_version',
        'blockstorage_api_version',
        'blockstorage_service_url',
        'compute_service_url',
        'debug_level',
        'identity_service_url',
        'image_api_version',
        'image_service_url',
        'keystone_api_version',
        //        'orchestration_service_url',
        'resource',
        'tenant_id'
    ];

    Params = [:];
    if (P.orchestration_service_url) {
        Params.orchestration_service_url = P.orchestration_service_url;
    }
    else {
        Params.orchestration_service_url = '';
    }
    MandatoryParams.each {
        def elem = P.get(it);
        if (!elem) {
            throw new IllegalArgumentException("Parameter $it is mandatory.");
        }
        Params.put(it, P.get(it));
    }
    createConfig([
                 pluginName: 'EC-OpenStack',
                 pluginProperty: 'openstack_cfgs',
                 config_name: P.config_name,
                 credential: [
                     userName: P.userName,
                     password: P.password,
                 ],
                 configParams: Params
                 ]);
}
def createConfigChef(P) {
    if (!P.config_name) {
        throw new IllegalArgumentException("config_name argument is required");
    }
    def Params = [:];
    if (P.desc) {
        Params.desc = P.desc;
    }
    if (P.server) {
        Params.server = P.server;
    }

    def Cred = [:];
    if (P.userName) {
        Cred.userName = P.userName;
    }
    if (P.password) {
        Cred.password = P.password;
    }

    createConfig([
                 config_name: P.config_name,
                 pluginName: 'EC-Chef',
                 pluginProperty: 'chef_cfgs',
                 configParams: Params,
                 credential: Cred
                 ]);
}

def createConfig(P) {
    if (!P.pluginName) {
        throw new IllegalArgumentException("pluginName argument is required");
    }
    if (!P.pluginProperty) {
        throw new IllegalArgumentException("pluginProperty argument is required");
    }
    if (!P.config_name) {
        if (P.config) {
            P.config_name = P.config;
        }
        else {
            throw new IllegalArgumentException("config_name argument is required");
        }
    }
    if (!P.configParams) {
        throw new IllegalArgumentException("configParams argument is required");
    }

    if (P.config_name_field) {
        P.configParams.put(P.config_name_field, P.config_name)
    }
    else {
        P.configParams.config = P.config_name;
    }
    
    def projectName = getPlugin([pluginName: P.pluginName]).projectName;
    if (P.credential) {
        Cred = new RuntimeCredentialImpl()
		Cred.name = P.config_name
		Cred.userName = P.credential.userName
		Cred.password = P.credential.password
        P.configParams.credential = P.config_name
    }
    if (P.credential2) {
        Cred2 = new RuntimeCredentialImpl();
        Cred2.name = P.credential2.credential_name;
        Cred2.userName = P.credential2.userName;
        Cred2.password = P.credential2.password;
        P.configParams.put(P.credential2.credentialParameter, P.credential2.credential_name);
    }
    def propertyPath = '/projects/' + projectName + '/' + P.pluginProperty;
    def prop = getProperty([propertyName: propertyPath]);
    // Let's get config
    propertyPath = propertyPath + '/' + P.config_name;

    if (!getProperty([propertyName: propertyPath])) {
        def Creds = [Cred];
        if (P.credential2) {
            Creds = [Cred, Cred2];
        }
        resp = runProcedure([
                            projectName: projectName,
                            procedureName: 'CreateConfiguration',
                            actualParameter: P.configParams,
                            
                            credential: Creds
                            ]);
        // Now let's grab the jobId launched to run the procedure
        def id=resp.jobId
        // Let's wait for it to finish
        
        def String status=''
        while(status != "completed") {
            // We need the polling in a different transaction started after
            //    the runProcedure one.
            transaction{
                status=getProperty(propertyName: 'status', jobId: id).value;
            }
            sleep (1000)
        }
        def String outcome=getProperty(propertyName: 'outcome', jobId: id).value;
                
    }
    else {        
        credential([
                   projectName: projectName,
                   userName: P.credential.userName,
                   password: P.credential.password,
                   credentialName: P.config_name
                   ]);
        P.configParams.each {
            key, val -> setProperty([propertyName: propertyPath + '/' + key, value: val]);
        }
    }
    return 1;
}

// Create the plugin configurations if required
 
if (createOrReplaceConfiguration) {
    if (configMgmtProvider == 'Chef') {
        createConfigChef(chefConfigurations);
    }
    def cloudProviderPlugin
    if (cloudProvider == 'Amazon') {
        cloudProvidePlugin = 'EC-EC2';
        createConfigEC2(amazonConfigurations)
    }
    else if (cloudProvider == 'OpenStack') {
        cloudProviderPlugin = 'EC-OpenStack';
        createConfigOpenStack(openStackConfigurations);
    }
    else if (cloudProvider == 'Azure') {
        cloudProviderPlugin = 'EC-Azure';
        createConfigAzure(azureConfigurations);
    }
    else {
        throw new IllegalArgumentException ("Invalid cloud provider: $cloudProvider")
    }
}
// Resource template DSL
def result;

project projectName, { 
    result = resourceTemplate resourceTemplateName, {
        cloudProviderPluginKey = cloudProviders[cloudProvider].name
        cloudProviderProcedure = cloudProviders[cloudProvider].procedureName
        cloudProviderProjectName = null

        cloudProviderParameter = cloudProviders[cloudProvider].parameters

        cfgMgrPluginKey = configMgmtProviders[configMgmtProvider].name
        cfgMgrProcedure = configMgmtProviders[configMgmtProvider].procedureName
        cfgMgrProjectName = null

        cfgMgrParameter = configMgmtProviders[configMgmtProvider].parameters
    }
}
// return the resource template created
result
