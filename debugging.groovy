/*

ectool evalDsl --dslFile debugging.groovy

*/

"rm -f /tmp/debug.log".execute()
def logfile= new File('/tmp/debug.log')

transaction {
logfile << getApplications(projectName: 'Default').application //.responses[0].application[0].applicationName
}