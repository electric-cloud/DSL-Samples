/*
Format: Electric Flow DSL
File: masterComponent.groovy
Description: Example of creation and copy of a master component

Set up Instructions
-------------------
    ectool --format json evalDsl --dslFile masterComponent.groovy

*/

// Settings
def _proj = "Default"
def _app = "universe"
def _comp ="masterOfTheUniverse"
def _tier="Tier1"

project (_proj) {
  // Create Master Component
  component (_comp) {
    pluginKey= "EC-Artifact"
      ec_content_details.with {
        pluginProjectName = "EC-Artifact"
        pluginProcedure = "Retrieve"
        artifactName = "com.sample:package1"
        filterList = ""
        overwrite = "update"
        versionRange = ""
        artifactVersionLocationProperty = "/myJob/retrievedArtifactVersions/\$" + "[assignedResourceName]"
      }
    }

    application(_app) {
    description = "The best app ever"
      applicationTier (_tier) {
        description = "This is my DEV app Tier"
      }
  }
}

// 6.0.1 method: you need to copy the masterCOmponent to your
// applicationTier
copyComponent(
    componentName: _comp,
    toApplicationName: _app,
    newComponentName: "Prince",
    projectName: _proj,
    applicationTierName: _tier
)
