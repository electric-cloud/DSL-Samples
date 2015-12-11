/*
Format: Electric Flow DSL
File: subProcedure.groovy
Description: an example on how to create a sub-procedure call

Command-line run instructions
-----------------------------
        ectool evalDsl --dslFile subProcedure.groovy

Run from Command Step
---------------------
        Set shell to:  ectool evalDsl --dslFile {0}

*/
project ('Hello Project') {
  // create procedure to call
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

  // Creating caller
  procedure('topProcedure'){
    step('callFriend') {
      subprocedure="testRunProcedure"
      actualParameter('friend', 'James')
    }
  }
}
