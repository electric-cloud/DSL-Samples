/*
Format: Electric Flow DSL
File: genCi.groovy
Description: 

ectool evalDsl --dslFile "genCi.groovy"


*/

def dslDir = "$[/myProject/dslDir]"

def appName  = "$[appName]"
def scmConfiguration  = "$[scmConfiguration]"
def scmRepository  = "$[scmRepository]"
def scmDestination  = "$[scmDestination]"
def stages = $[/myProject/stages] // Literal string to create stage mapping of logical and actual stage names

/*
def appName  = "Heat Clinic"
def scmConfiguration  = "svn"
def scmRepository  = "file:///opt/svn/DemoSite"
def dslDir = "/vagrant/DSL-Samples/HConboarding/"
def stages = [dev: "Development", qa: "Testing", st: "Staging", pr: "Release"]
*/

def ciScheduleName = "trunk"

project appName, {

	procedure "CI", {
		formalParameter "app"
		formalParameter "startingStage"
		step "Launch Pipeline",
			command: new File(dslDir + "Launch Pipeline.pl").text,
			shell: "ec-perl"
	} // Procedure

	schedule ciScheduleName, {
		scheduleDisabled = true
		procedureName = "CI"
		//ec_ci.checkoutSCMConfigName = ciScheduleName
		ec_ci.ciConfigDescription = "DemoSite-build"
		ec_customEditorData.QuietTimeMinutes = "0"
		ec_customEditorData.Revision_outpp = ""
		ec_customEditorData.TriggerFlag = 2 // Show all jobs in CI dashboard
		ec_customEditorData.formType = '$' + "[/plugins/ECSCM-SVN/project/scm_form/sentry]"
		ec_customEditorData.priority = ""
		ec_customEditorData.repository = scmRepository
		ec_customEditorData.scheduleDisabled = "1"
		ec_customEditorData.scmConfig = scmConfiguration
	
	},
	actualParameter: [
		app: appName,
		startingStage: stages.dev
	]
	
	// Add project to CI
	// TODO: replace with /myUser when working again
	currentProjects = getProperty(propertyName: "/users/admin/ec_ci/ciProjects").value
	property propertyName: "/users/admin/ec_ci/ciProjects", value: "${currentProjects}\n${projectName}"
	
} // Project

