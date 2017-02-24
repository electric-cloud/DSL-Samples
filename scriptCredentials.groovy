/*
Format: Electric Flow DSL
File: scriptCredentials.groovy
Description: Examples of how to insert user/password into scripts without exposing them

Set up Instructions
-------------------
Edit the resource name lines below to point to Window and/or Linux hosts

Run:
ectool --format json evalDsl --dslFile scriptCredentials.groovy

*/

// Default resources
def winResource = 'host'
def linResource = 'local'

project "Use Credentials in scripts",{

	credential "TestCreds",
		userName: "me",
		password: "mypass"
	
	procedure "Use Credentials with Groovy",{
		formalParameter "config", defaultValue: "TestCreds"
	
		step "Get password", { attachCredential credentialName: 'TestCreds' },
			command: '''\
				password = "ectool getFullCredential TestCreds --value password".execute().text
				userName = "ectool getFullCredential TestCreds --value userName".execute().text
				print "Username: $userName\\n"
				print "Password: $password\\n"
			'''.stripIndent(),
			shell: "ec-groovy",
			resourceName: winResource
	}
	
	procedure "Use Credentials with PowerShell shell",{
		formalParameter "config", defaultValue: "TestCreds"
	
		step "Get password", { attachCredential credentialName: 'TestCreds' },
			command: '''\
				$password = (ectool getFullCredential TestCreds --value password) | Out-String
				$userName = (ectool getFullCredential TestCreds --value userName) | Out-String
				echo Username: $userName
				echo Password: $password
			'''.stripIndent(),
			resourceName: winResource,
			shell: '''powershell "& '{0}.ps1'" '''
	}
	
	procedure "Use Credentials with Windows cmd",{
		formalParameter "config", defaultValue: "TestCreds"
	
		step "Get password", { attachCredential credentialName: 'TestCreds' },
			command: '''\
				FOR /F "tokens=* USEBACKQ" %%F IN (`ectool getFullCredential TestCreds --value password`) DO (
				SET password=%%F
				)
				FOR /F "tokens=* USEBACKQ" %%F IN (`ectool getFullCredential TestCreds --value userName`) DO (
				SET userName=%%F
				)
				echo Username: %userName%
				echo Password: %password%
			'''.stripIndent(),
			resourceName: winResource
	}
	
	procedure "Use Credentials with bash",{
		formalParameter "config", defaultValue: "TestCreds"
	
		step "Get password", { attachCredential credentialName: 'TestCreds' },
			command: '''\
				userName=`ectool getFullCredential TestCreds --value userName`
				password=`ectool getFullCredential TestCreds --value password`
				echo Username: $userName
				echo Password: $password
			'''.stripIndent(),
			resourceName: linResource,
			shell: 'bash'
	}
}