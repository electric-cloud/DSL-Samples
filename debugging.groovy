/*

ectool evalDsl --dslFile debugging.groovy; cat /tmp/debug.log

*/

"rm -f /tmp/debug.log".execute()
def logfile= new File('/tmp/debug.log')

transaction {
	logfile << getProperty(propertyName: "/myUser/ec_ci/ciProjects").value
	logfile << "\n"
}