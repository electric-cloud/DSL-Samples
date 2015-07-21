/*

ectool evalDsl --dslFile systemCalls.groovy --parameters '{"pwd":"'$PWD'"}' ; cat /tmp/debug.log
*/

"rm -f /tmp/debug.log".execute()
def logfile= new File('/tmp/debug.log')

def prop=getProperty(propertyName: "/users/admin/fullUserName").value

transaction {
	logfile << "\nTesting...\n"
	logfile << "Working directory user.dir: " + System.getProperty("user.dir") + "\n"
	logfile << "username: $prop \n"
	logfile << "Working directory from PWD: " + args.pwd + "\n"
	logfile << "\n"
}