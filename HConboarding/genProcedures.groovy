/*
Format: Electric Flow DSL
File: genProcedures.groovy
Description: 

*/

def appName  = "$[appName]"
def appTech  = "$[appTech]"
def artifactGroup = "$[artifactGroup]"
def artifactKey = "$[artifactKey]"
def scmConfiguration  = "$[scmConfiguration]"
def scmRepository  = "$[scmRepository]"
def scmDestination  = "$[scmDestination]"


def artifactName_ = "${artifactGroup}:${artifactKey}"
		
project appName, {
	procedure "Build",{		
		
		step "set_build_number",
			command: "ectool setProperty /myJob/build_number " + '$' + "[/increment /myProcedure/buildCounter]"
			
		step "set_status", command: "" +
			"ectool --silent setProperty /myJob/ec_job_progress_status \'Fetching source code...\'\n"+
			"ectool --silent setProperty /myJob/srcdir \'$scmDestination\'\n"+
			"ectool --silent setProperty /myJob/ec_job_description \'<html><pre>Job Name: "+'$'+"[/myJob/jobName] Launched By: "+'$'+"[/myJob/launchedByUser]</pre></html>\'"
			
		step "clean_sources", command: "rm -rf "+'$'+"[srcdir]/site"
		
		step "checkout_sources",
			subproject: "/plugins/ECSCM-SVN/project",
			subprocedure: "CheckoutCode",
			actualParameter: [
				CheckoutType: "D",
				config: scmConfiguration,
				dest: scmDestination,
				SubversionUrl: scmRepository
			]
			
		step "check_mvn_repo",
			command: "" +
			"test -d ~/.m2 && test \$(ls ~/.m2/repository/* | wc -l) -gt 20 \\\n" +
			"&& echo Repository already exists \\\n" +
			"|| (cd "+'$'+"[srcdir] && mvn install && mvn clean)"
			
		step "clean_workspace",
			command: "ectool --silent setProperty /myJob/ec_job_progress_status \'Cleaning workspace...\'\n"+
				"cd "+'$'+"[srcdir] && mvn -o -Ddependency.locations.enabled=false clean"
				
		step "build_mvn",
			command: "ectool --silent setProperty /myJob/ec_job_progress_status \'Building code...\'\n" +
				"cd "+'$'+"[srcdir] && mvn -o -Ddependency.locations.enabled=false package",
			postProcessor: "postp --loadProperty /myStep/ignoreMvnWarnings" /*, {
				property "ignoreMvnWarnings", valueFile: dslDir + "ignoreMvnWarning.pl"
			} */
				
		step "clean_artifacts",
			command: "ectool deleteArtifactVersion com.mycompany.heatclinic:warfile:1.0-"+'$'+"[build_number]\n"+
				"ectool deleteArtifactVersion com.mycompany.heatclinic:waradmin:1.0-"+'$'+"[build_number]"
				
		step "publish_site",
			command: "ectool publishArtifactVersion --version 1.0-"+'$'+"[build_number] --artifactName com.mycompany.heatclinic:warfile --includePatterns mycompany.war --fromDirectory \'"+'$'+"[srcdir]/site/target\'\n"+ 
			"ectool setProperty /myJob/artifactVersion 1.0-"+'$'+"[build_number]"
		
		/*
		step "publish_admin",
			subproject: "/plugins/EC-Artifact/project",
			subprocedure: "Publish",
			actualParameter: [
				artifactName: "com.mycompany.heatclinic:waradmin",
				artifactVersionVersion: "1.0-"+'$'+"[build_number]",
				includePatterns: "admin.war",
				repositoryName: "Default",
				fromLocation: '$'+"[srcdir]/admin/target"
			]
		*/
/*		
		step "trim_artifacts",
			command: new File(dslDir + "trimArtifactVersions.pl").text,
			shell: "ec-perl"
*/		
	} // Procedure Build
	
	procedure "Create Snapshot",{
		formalParameter "env"
		
		step "Delete the snapshot",
			command:  "ectool deleteSnapshot Default \"${appName}\" \"${appName}-1.0\""
		
		step "createSnapshot", 
			command: "ectool createSnapshot Default \"${appName}\" \"${appName}-1.0\" --environmentName \"" + '$' + "[env]\""		
	}
	
	procedure "Code Scan",{
		
		step "Post Results",
			command: "ectool setProperty /myPipelineStageRuntime/ec_summary/codeScanResults " +
				'\"<html>Code Scan Results</html>\"'
		
	} 

	procedure "Application URL",{
		
		step "Create application link",
			command: "ectool setProperty /myPipelineStageRuntime/ec_summary/appUrl " +
				'\"<html><a href=\\\"https://flow/mycompany/\\\">Application URL</a></html>\"'
		
	}
	
	procedure "System Tests",{
		
		step "Post Results",
			command: "ectool setProperty /myPipelineStageRuntime/ec_summary/systemTestResults " +
				'\"<html>System Test Results</html>\"'
		
	}

	procedure "Smoke Tests",{
		
		step "Post Results",
			command: "ectool setProperty /myPipelineStageRuntime/ec_summary/smokeTestResults " +
				'\"<html>Smoke Test Results</html>\"'
		
	}

}