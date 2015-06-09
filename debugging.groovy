/*

ectool evalDsl --dslFile debugging.groovy

*/

"rm -f /tmp/debug.log".execute()
def logfile= new File('/tmp/debug.log')

transaction {
//logfile << getProcedures(projectName: 'Hello Project')[0].procedure //.responses[0].application[0].applicationName
	getApplications(projectName: 'Default').each {
		logfile << it.name
		logfile << "\n"
	}
}