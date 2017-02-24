project "$[/myProject]", {  // That is, the Onboarding project
	procedure "CLEAN: $[appName]",{
		step "Remove all objects",
			command: """\
				
				$[resources].each {
					deleteResource resourceName: it
				}
				
				deleteArtifact artifactName: "$[artifacts]"
				
				$[environments].each {
					deleteEnvironment environmentName: it, projectName: "$[projName]"
				}

				deleteApplication applicationName: "$[appName]",
					projectName: "$[projName]"
				deletePipeline pipelineName: "$[appName]",
					projectName: "$[projName]"
				
				// Remove project from CI property list for admin
				setProperty(propertyName: "/users/admin/ec_ci/ciProjects", value:
				getProperty(propertyName: "/users/admin/ec_ci/ciProjects").value.minus("$[appName]\\n").minus("$[appName]") )
				
				setProperty(propertyName: "/users/admin/ec_ci/openProjects", value:
				getProperty(propertyName: "/users/admin/ec_ci/openProjects").value.minus("$[appName]\\n").minus("$[appName]") )
				
				deleteProject projectName: "$[appName]"

			""".stripIndent(),
			shell: "ectool evalDsl --dslFile {0}"
			
		step "Remove this procedure",
		// TODO: replace procedure reference to '$'+"[/myProcedure]"
			command: 'ectool deleteProcedure "$[/myProject]" "CLEAN: $[appName]"'
	}
}

