/*
Format: Electric Flow DSL
File: clean.groovy
Description: Remove all objects created by generate release

*/
def release = "$[release]"
def pipeline = "$[pipeline]"
def artifacts  = $[artifacts]
def environments  = $[environments]
def applications = $[applications]
def resources = $[resources]

def delArtifacts = ""
def delResources = ""
def delEnvironments = ""
def delApplications = ""
artifacts.each {delArtifacts += "ectool deleteArtifact \"$it\"\n"}
resources.each {delResources += "ectool deleteResource \"$it\"\n"}
environments.each {delEnvironments += "ectool deleteEnvironment Default \"$it\"\n"}
applications.each {delApplications += "ectool deleteApplication Default \"$it\"\n"}

project "$[/myProject]", {
	procedure "Clean - $release",{
		step "Delete Release", command: "ectool deleteRelease Default \"$release\""
		step "Delete Pipeline", command: "ectool deletePipeline Default \"$pipeline\""
		step "Delete Applications", command: delApplications
		step "Delete Environments", command: delEnvironments
		step "Delete Resources", command: delResources
		step "Delete Artifacts", command: delArtifacts
		step "Delete me", command: "ectool deleteProcedure \"$[/myProject]\" \"Clean - $release\""
	}
}