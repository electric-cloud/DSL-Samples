/*
Format: Electric Flow DSL
File: Onboard new application flow.groovy
Description: Test setting properties from CLI and job step

Command-line run instructions
-----------------------------
ectool evalDsl --dslFile "Onboard new application flow.groovy"

*/

def dslDir = "/vagrant/DSL-Samples/onboarding/"

/* TODO

1. Create parameters and parameter form from file:
{
  "paramA": {
    "label": "Param A Label",
    "documentation": "Detailed description",
    "defaultValue": "defVal",
    "options": [
      "opt1",
      "opt2"
    ]
  }
}

(See Custom parameter form contents)
ec_parameterForm:
<editor>
    <formElement>
        <label>One:</label>
        <property>one</property>
        <documentation>The first parameter.</documentation>
        <type>entry</type>
        <value>Test value</value>
    </formElement>
</editor>


*/

project "Application Onboarding", {

	property "dslDir", value: dslDir
	property "stages", value: '[dev: "Development", qa: "Testing", st: "Staging", pr: "Release"]'
	
	procedure "Onboard new application flow",{
		formalParameter "appName", required: "1"
		formalParameter "artifactKey", required: "1"
		formalParameter "artifactGroup", required: "1"
		formalParameter "buildIp", required: "1"
		formalParameter "deployIp", required: "1"
		formalParameter "appTech", required: "1", type: "select", default: "java"
		ec_customEditorData.parameters.appTech.with {
			formType = "standard"
			options.with {
				type = "simpleList"
				list = "java|dotNet"
			}
		} // property sheet ec_customEditorData
		
		step "Generate Procedures",
			description: "Create Build, Snapshot, Unit Test, System Test procedures",
			command: new File(dslDir + "genProcedures.groovy").text,
			shell: "ectool evalDsl --dslFile {0}"
		step "Generate Environment and Application Models",
			command: new File(dslDir + "genApp.groovy").text,
			shell: "ectool evalDsl --dslFile {0}"
		step "Generate Pipeline",
			command: new File(dslDir + "genPipe.groovy").text,
			shell: "ectool evalDsl --dslFile {0}"
		step "Generate CI Configuration",
			command: new File(dslDir + "genCi.groovy").text,
			shell: "ectool evalDsl --dslFile {0}"
		
	} // Procedure "Onboard new application flow"
}