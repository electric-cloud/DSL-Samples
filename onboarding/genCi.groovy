/*
Format: Electric Flow DSL
File: genCi.groovy
Description: 

*/


def projName  = "$[projName]"
def appName  = "$[appName]"
def appTech  = "$[appTech]"
def dslDir = "$[/myProject/dslDir]"
//def stages = $ [/myProject/stages] // Literal string to create stage mapping of logical and actual stage names
def stages = "$[stages]".split(",")

project projName, {

	procedure "CI", {
		formalParameter "app"
		formalParameter "startingStage"
		step "Launch Pipeline",
			command: new File(dslDir + "Launch Pipeline.pl").text,
			shell: "ec-perl"
	} // Procedure

	schedule appName, {
		scheduleDisabled = true
		procedureName = "CI"
		ec_ci.checkoutSCMConfigName = "none"
		ec_ci.ciConfigDescription = ""
		ec_customEditorData.TriggerFlag = 2 // Show all jobs in CI dashboard
	},
	actualParameter: [
		app: appName,
		//startingStage: stages.dev
		startingStage: stages[0]
	]
	
	// Add project to CI
	// TODO: replace with /myUser when working again
	currentProjects = getProperty(propertyName: "/users/admin/ec_ci/ciProjects").value
	property propertyName: "/users/admin/ec_ci/ciProjects", value: "${currentProjects}\n${projectName}"
	
} // Project

