project ('Hello Project') {
  procedure ('testRunProcedure') {
    formalParameter('friend')  {
      required=1
      defaultValue='Bob'
    }
    step ('helloFriend') {
               command= '''printf("Hello $[friend] from EF DSL!\\n");
sleep(5);
exit(1);
'''
      shell='ec-perl'
    }
  }
}

def resp
// Now let's run this newly created procedure
// This is in a transaction on purpose
transaction{
  logFile = new File("/tmp/dsl.log")
  logFile << "\n\n-------\n"

  resp=runProcedure(
    projectName: 'Hello Project',
    procedureName: 'testRunProcedure',
    actualParameter: [
      friend: 'James',
    ]
  )

}

  // Now let's grab the jobId launched to run the procedure
  def id=resp.jobId
  // Let's wait for it to finish
  logFile << "jobId:" + id.toString() + "\n"

  def String status=''
  while(status != "completed") {
    // We need the polling in a different transaction started after
    //    the runProcedure one.
  transaction{
    status=getProperty(propertyName: 'status', jobId: id).value;
    logFile << "status:" + status + "\n"
               }
    sleep (1000)
  }
  def String outcome=getProperty(propertyName: 'outcome', jobId: id).value;

  logFile << "outcome:" + outcome + "\n"
