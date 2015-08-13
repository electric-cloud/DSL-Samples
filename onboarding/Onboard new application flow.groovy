/*
Format: Electric Flow DSL
File: Onboard new application flow.groovy
Description: Test setting properties from CLI and job step

Command-line run instructions
-----------------------------
ectool evalDsl --dslFile "Onboard new application flow.groovy"

*/
import groovy.json.JsonSlurper

def dslDir = "/vagrant/DSL-Samples/onboarding/"

project "Application Onboarding", {

	property "dslDir", value: dslDir
	property "stages", value: '[dev: "Development", qa: "Testing", st: "Staging", pr: "Release"]'
	
	procedure "Onboard new application flow",{
	
		// Parse in parameters from file
		def jsonSlurper = new JsonSlurper()
		def params = jsonSlurper.parseText(new File(dslDir + "parameters.json").text)
		def ec_parameterForm="<editor>\n"
		params.each { param ->
			def xmlType = "entry"
			formalParameter param.name, required: "1", type: xmlType // Default parameter type
			ec_parameterForm += "\t<formElement>\n"
			ec_parameterForm += "\t\t<property>$param.name</property>\n"
			param.each { k, v ->
				switch (k) {
					case "name":
						break
					case "options":
						xmlType = "select"
						formalParameter param.name, type: xmlType
						ec_customEditorData.parameters.appTech.with {
							formType = "standard"
							options.with {
								type = "simpleList"
								list = v.join("|")
							}
						} // property sheet ec_customEditorData
						v.each { val ->
							ec_parameterForm += "\t\t\t<option>\n"
							ec_parameterForm += "\t\t\t\t<name>$val</name>\n"
							ec_parameterForm += "\t\t\t\t<value>$val</value>\n"
							ec_parameterForm += "\t\t\t</option>\n"
						}
						break
					case ["description","label","defaultValue"]:
						formalParameter param.name, (k): v
						if (k=="description") k="documentation"
						if (k=="defaultValue") k="value"
						ec_parameterForm += "\t\t<$k>$v</$k>\n"
						break
				}
			}
			ec_parameterForm += "\t\t<type>$xmlType</type>\n"		
			ec_parameterForm += "\t</formElement>\n"
		}​
		
		ec_parameterForm += "</editor>\n"
		property "ec_parameterForm", value: ec_parameterForm
		// End of parse in parameters

		step "Open Permissions",
			shell: "ectool evalDsl --dslFile {0}",
			command: """\
				aclEntry principalName : 'project: \$[appName]',
					principalType : 'user',
					systemObjectName : 'server',
					changePermissionsPrivilege : 'allow',
					executePrivilege : 'allow',
					modifyPrivilege : 'allow',
					readPrivilege : 'allow'
				property '/jobs/\$[/myJob]/aclEntry', value: 'project: \$[appName]'
			""".stripIndent()		
			
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
	
	procedure "Clean",{
		formalParameter "refJobId", required: "true"
		step "Remove all objects",
			command: new File(dslDir + "clean.groovy").text,
			shell: "ectool evalDsl --dslFile {0}"
	}
}