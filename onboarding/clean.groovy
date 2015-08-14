project "$[/myProject]", {  // That is, the Onboarding project
	procedure "CLEAN: $[appName]",{
		step "Remove all objects",
			command: """\
				
				$[resources].each {
					deleteResource resourceName: it
				}
				
				deleteArtifact artifactName: "$[artifacts]"
				
				$[environments].each {
					deleteEnvironment environmentName: it, projectName: "Default"
				}

				deleteApplication applicationName: "$[appName]",
					projectName: "Default"
				deletePipeline pipelineName: "$[appName]",
					projectName: "Default"
					
				setProperty(propertyName: "/users/admin/ec_ci/ciProjects", value:
				getProperty(propertyName: "/users/admin/ec_ci/ciProjects").value.minus("$[appName]") )

				deleteProject projectName: "$[appName]"

			""".stripIndent(),
			shell: "ectool evalDsl --dslFile {0}"
			
		step "Remove this procedure",
			command: 'ectool deleteProcedure "$[/myProject]" "$[/myProcedure]"'
	}
}

