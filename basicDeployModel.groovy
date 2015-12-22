/*
Format: Electric Flow DSL
File: DeployModelConfigAdaptation.groovy
Description: Electric Flow Deploy model with configuration adaptation based on an environment property
- Creates environment model (with resources all pointing to localhost by default)
- Creates application model
- Creates and runs a procedure that creates template and "installer" files and publish them as an artifact version

Set up Instructions
-------------------
ectool --format json evalDsl --dslFile DeployModelConfigAdaptation.groovy

Running Instructions
--------------------
Once the procedure to create the artifact version has completed, deploy the application to one of its environments
Examine the job details to see the log outputs showing the adapted configuration file

*/

// Customizable values ------------------

// Application Name
def appName = "Application with config adaptation"

// Environment names ["env1", "env2" ...]
def envs = ["sample-dev","sample-qa"]

// Application-Environment tier mapping ["apptier1":"envtier1", "apptier2":"envtier2" ...]
def appEnvTiers = ["app":"app"]

// Artifact group id
def artifactRoot = "com.sample"

// Project name - currently only "Default" is supported by the Electric Flow Deploy UI
def projectName = "Default"

// Host(s) to use for the environment resources ["desired base resource name #1":"ip #1", "desired base resource name #2":"ip #2" ...]
def hosts = ["local":"localhost"]

// ---------------------------------------

def envTiers = appEnvTiers.values()
def appTiers = appEnvTiers.keySet()

// Remove old application model
deleteApplication (projectName: projectName, applicationName: appName) 

// Remove old Environment models
envs.each { env ->
	appTiers.each() { tier ->
		hosts.each { name, ip ->
			deleteResource resourceName: "${name}_${env}_${tier}"
		}
	}
	deleteEnvironment(projectName: projectName, environmentName: env)
}

// Create new -------------------------------

def artifactVersions = []

project projectName, {

	// Create Environments, Tiers and Resources
	envs.each { env ->
		environment environmentName: env, {
			property "db_port", value: (String) Math.round(Math.random()*1000+1000)
			
			envTiers.each() { tier ->
				environmentTier tier, {
					// create and add resource to the Tier
					hosts.each { name, ip ->
						resource resourceName: "${name}_${env}_${tier}", hostName : ip
					}
				}
			}
		}
	} // Environments

	application applicationName: appName, {
		
		process processName: "Deploy"
		
		appTiers.each() { tier ->
			applicationTier tier, {
				def compName = "${tier}_comp"
				def artifactVersion = "1.35"
				def artifactName_ = artifactRoot + ':' + compName
				artifactVersions << [artifactName: artifactName_, artifactVersion: artifactVersion]
				// Create artifact
				artifact groupId: artifactRoot, artifactKey: compName
				//def artifactName = "com.ec.test:test"
			
				component componentName: compName, pluginKey: "EC-Artifact", {
					ec_content_details.with { 
						pluginProjectName = "EC-Artifact"
						pluginProcedure = "Retrieve"
						artifactName = artifactName_
						filterList = ""
						overwrite = "update"
						versionRange = ""
						artifactVersionLocationProperty = "/myJob/retrievedArtifactVersions/\$" + "[assignedResourceName]"
					}

					process processName: "Install",
						processType: "DEPLOY",
						componentApplicationName: appName,
						applicationName: null,
						{
						processStep processStepName: "Retrieve Artifact",
							processStepType: "component",
							subprocedure: "Retrieve",
							errorHandling: "failProcedure",
							subproject: "/plugins/EC-Artifact/project",
							applicationName: null,
							applicationTierName: null,
							actualParameter: [ 
								artifactName : "\$" + "[/myComponent/ec_content_details/artifactName]",
								artifactVersionLocationProperty : "\$" + "[/myComponent/ec_content_details/artifactVersionLocationProperty]",
								filterList : "\$" + "[/myComponent/ec_content_details/filterList]",
								overwrite : "\$" + "[/myComponent/ec_content_details/overwrite]",
								versionRange : "\$" + "[/myJob/ec_" + compName + "-version]"
							]
							
						processStep processStepName: "Deploy Artifact",
							applicationName: null,
							applicationTierName: null,
							componentApplicationName: appName,
							command: "echo testing $compName..."
							
						processStep processStepName: "Deploy Artifact",
							processStepType: 'command',
							subproject: '/plugins/EC-Core/project',
							subprocedure: 'RunCommand',
							actualParameter: [
								shellToUse: '$[/javascript getProperty("/resources/$[assignedResourceName]/hostPlatform")=="windows"?"":"sh"]',
								commandToRun: '$[/javascript getProperty("/resources/$[assignedResourceName]/hostPlatform")=="windows"?"":"sh "]' + 
								'"' + 
								'$[/myJob/retrievedArtifactVersions/$[assignedResourceName]/$[/myRetrievedArtifact/artifactName]/cacheLocation]' +
								'/app.' + 
								'$[/javascript getProperty("/resources/$[assignedResourceName]/hostPlatform")=="windows"?"bat":"sh"]' +
								'"'
								],
							applicationName: null,
							applicationTierName: null,
							componentApplicationName: appName
							
						createProcessDependency componentApplicationName: appName,
							processStepName: "Retrieve Artifact",
							targetProcessStepName: "Deploy Artifact"
							
						processStep "Adapt configuration file", {
						  applicationTierName = null
						  dependencyJoinType = null
						  errorHandling = 'failProcedure'
						  processStepType = 'procedure'
						  projectName = 'Default'
						  subcomponent = null
						  subcomponentApplicationName = null
						  subcomponentProcess = null
						  subprocedure = 'AdaptFile'
						  subproject = "Deploy Utilities"
						  timeLimitUnits = null
						  workspaceName = null
						  actualParameter 'inputFile', '$[/myJob/retrievedArtifactVersions/$[assignedResourceName]/$[/myRetrievedArtifact/artifactName]/cacheLocation]/app.template'
						  actualParameter 'outputFile', 'app.cfg'
						}

						createProcessDependency componentApplicationName: appName,
							processStepName: "Deploy Artifact",
							targetProcessStepName: "Adapt configuration file"

						processStep processStepName: "Show template content",
							processStepType: 'command',
							subproject: '/plugins/EC-Core/project',
							subprocedure: 'RunCommand',
							actualParameter: [
								commandToRun: '$[/javascript getProperty("/resources/$[assignedResourceName]/hostPlatform")=="windows"?"type ":"cat "]' +
								'app.cfg'
								],
							applicationName: null,
							applicationTierName: null,
							componentApplicationName: appName
							
						createProcessDependency componentApplicationName: appName,
							processStepName: "Adapt configuration file",
							targetProcessStepName: "Show template content"	
					}

					processStep  processStepName: "Install $compName",
						processName: "Deploy",
						processStepType: 'process',
						componentName: null,
						applicationName: appName,
						componentApplicationName: appName,
						errorHandling: 'failProcedure',
						subcomponent: compName,
						subcomponentApplicationName: appName,
						subcomponentProcess: "Install",
						applicationTierName: tier
						
				} // Components
			} 
		} // Application Tiers

		envs.each { env -> 
			tierMap tierMapName: "$appName-$env",
				environmentProjectName: projectName, // Replace with projectName reference
				environmentName: env,
				tierMapping: appEnvTiers			
		}

	} // Applications

} // project

// Create publishArtifact procedure

project "Deploy Utilities",{
	procedure 'AdaptFile', {
	  description = 'Helper procedure to take an input file, run property path expansions on it, and save it to an output file.'

	  formalParameter 'inputFile', required: 'true'
	  formalParameter 'outputFile', required: 'true'

	  step 'Adapt File', {
		command = '''\
			use strict;
			use ElectricCommander ();
			$| = 1;
			my $ec = new ElectricCommander->new();
			my $inputFile = $ec->getProperty("inputFile")->find("//value")->string_value;
			#my $inputFile = $[inputFile]; # User has to provide quotes...
			#$inputFile =~ s#\\\\#/#g;
			unless(open INFILE, $inputFile) {
				# Die with error message 
				# if we can't open it.
				die "\\nUnable to open $inputFile\\n";
			}			
			my $outputFile = $ec->getProperty("outputFile")->find("//value")->string_value;
			$outputFile =~ s#\\\\#/#g;
			open OUTFILE, '>'.$outputFile;	
			
			local $/;
			print "Input file: ", $inputFile, "\\n";
			my $inContent = <INFILE>;
			print "Input Content______________________\\n";
			print $inContent;
			print "\\n\\n";
			close INFILE;
			
			print "Output file: ",$outputFile,"\\n";
			my $outContent = $ec->expandString($inContent)->find("//value")->string_value;
			print "Output Content______________________\\n";
			print $outContent;
			print OUTFILE $outContent;
			close OUTFILE;'''.stripIndent()
		shell = 'ec-perl'
	  }
	}
	
	procedure "Create template file",{
		formalParameter 'fileName', required: 'true'
		formalParameter 'fileContent', expansionDeferred: 'true', type: 'textarea'
			step 'Generate File', {
				command = '''\
					use strict;
					use ElectricCommander ();
					#use Data::Dumper;
					$| = 1;
					my $ec = new ElectricCommander->new();
					my $fileName = $ec->getProperty("fileName")->find("//value")->string_value;
					#$fileName =~ s#\\\\#/#g;
					open OUTFILE, '>'.$fileName;
					local $/;
					my $fileContent = $ec->getProperty('fileContent',{expand=>'false'})->find("//value")->string_value;
					print $fileContent;
					print OUTFILE $fileContent;
					close OUTFILE;
				'''.stripIndent()
				shell = 'ec-perl'
			}
	}

	procedure "publishArtifact", {
		formalParameter "artifactName", type: "textentry", required: "1"
		formalParameter "artifactVersion", type: "textentry", required: "1"
		formalParameter "fileName", type: "textentry", required: "1"
		formalParameter "fileContent", type: "textarea", required: "1"
		
		step "Create installer Windows",
			subproject: "/plugins/EC-FileOps/project",
			subprocedure: "AddTextToFile",
			actualParameter: [
				Path: '$' + "[fileName].bat",
				Content: '$' + "[fileContent]",
				AddNewLine: "0",
				Append: "0"
			]
		step "Create installer Linux",
			subproject: "/plugins/EC-FileOps/project",
			subprocedure: "AddTextToFile",
			actualParameter: [
				Path: '$' + "[fileName].sh",
				Content: '$' + "[fileContent]",
				AddNewLine: "0",
				Append: "0"
			]
		step "Create Config template",
			subproject: '$[/myProject/projectName]',
			subprocedure: "Create template file",
			actualParameter: [
				fileName: '$' + "[fileName].template",
				//fileContent: '# App config file\ndb_port = $[/myEnvironment/db_port]'
				fileContent: '''\
				# App config file
				# Environment: $[/myEnvironment/environmentName]
				db_port = $[/myEnvironment/db_port]
				'''.stripIndent()
			]
		step "Publish Artifact",
			subproject: "/plugins/EC-Artifact/project",
			subprocedure: "Publish",
			actualParameter: [
				artifactName: '$' + "[artifactName]",
				artifactVersionVersion: '$' + "[artifactVersion]",
				includePatterns: '$' + "[fileName].*",
				repositoryName: "Default"
				//fromLocation:
			]
	}
}

artifactVersions.each { ar ->
	// Create artifact version
	transaction {
		runProcedure procedureName: "publishArtifact", projectName: "Deploy Utilities",
			actualParameter: [
				artifactName: ar.artifactName,
				fileContent: "echo Installing " + ar.artifactName,
				fileName: "app",
				artifactVersion: ar.artifactVersion + '-$[/increment /myProject/artifactIndex]'
				]
	}
}