/*
    This is a sample to create a Configuration for a plugin lioke EC-ServiceNow
*/

def conf="snow-config"
def proj="/plugins/EC-ServiceNow/project",
def uName='myUser'
def pwd='mypassword'        // or you would better grab it from an extertnal source

// Create a Transient credential
def Cred = new RuntimeCredentialImpl()		
Cred.name = conf	        
Cred.userName = uName		
Cred.password = pwd
def Creds=[Cred]

// Call the config creation procedure
// if it does not already exists
// by checking if the config property (name may be different in different plugin)
if (! getProperty("$proj/ServiceNow_cfgs/$conf")) {
  runProcedure(
    projectName : proj,
    procedureName : "CreateConfiguration",
    actualParameter : [
      config: conf,               // required
      host: "http://serviceNow",  // required
      credential: conf,           // Credential has the same name than the config
      http_proxy: "",
      proxy_credential: "",
    ],
     credential: Creds
  )
} else {
  // overwrite the  credential
  credential(
    projectName: proj,
    userNane: uName,
    password: pwd
    credentialName: conf
  )
  // overtrite properties
  setProperty("$proj/ServiceNow_cfgs/$conf/host": value: "http://myNewHost"
  setProperty("$proj/ServiceNow_cfgs/$conf/http_proxy": value: "http://myProxy"
 .....
}
