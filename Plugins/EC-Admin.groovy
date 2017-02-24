/*
Format: Electric Flow DSL
File: EC-Admin.groovy
Description: an example on how to download a plugin from GitHub, install it
             and promote it

Command-line run instructions
-----------------------------
        ectool evalDsl --dslFile EC-Admin.groovy

Run from Command Step
---------------------
        Set shell to:  ectool evalDsl --dslFile {0}

*/

def String tmpDir=System.getenv('COMMANDER_DATA') + '/tmp'

// Download file from GitHub
def _url = 'http://github.com/electric-cloud/EC-Admin/blob/master/EC-Admin.jar?raw=true'
//def file = new File(tmpDir +'/EC-Admin.jar').newOutputStream()
//file << new URL(url).openStream()
//file.close()

def logFile
try {
  logFile = new File("/tmp/dsl.log")
} catch (ex) {
  logFile << "File Exception\n"
  logFile << ex

}
logFile << "---\n\n"

def resp

try{
  resp=installPlugin(url: _url)
} catch(ex) {
  logFile << "Exception\n"
  logFile << ex
  logFile << resp.toString() + "\n"
}

logFile << resp.toString() + "\n"
logFile << plugin.pluginName.toString()+ "\n"
