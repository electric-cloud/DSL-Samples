# Application Onboarding

Example of how complete end-to-end CI/CD processes can be implemented with Electric Flow for new and existing software projects.

## Instructions

The DSL file "Onboard new application flow.groovy" is run to create a parameterized procedure from a JSON file that generates the pieces needed to implement a end-to-end flow:
- CI configuration
- Build , Code Scan, System Test, Smoke Test procedures
- Artifact location
- Environment and application models for deployment
- Release pipeline

This DSL file needs to be run from the Commander Server

1. Retrieve this directory's parent to the Commander Server (git clone https://github.com/electriccommunity/DSL-Samples.git)
2. Edit dslDir to match where the "onboarding" directory is located
3. Login as Admin (ectool login admin)
4. Run the DSL code (ectool evalDsl --dslFile "Onboard new application flow.groovy")
5. From Electric Flow UI logged in as admin, navigate to the project "Application Onboarding" and run "Onboard new application flow" and modify the parameters as desired
6. Navigate to the CI Dashboard
7. Run the CI Configuration <Your app name><Your app name>
8. Once the procedure is completed, navigate to the pipeline from the tool tip over the green square job icon

## To Clean out objects created by onboarding produre
- Run the 'clean' procedure, pass it the jobId of the job that did the on-boarding

