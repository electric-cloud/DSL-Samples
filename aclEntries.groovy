/*
Format: Electric Flow DSL
File: aclEntries.groovy
Description: some examples of aclEntry

Command-line run instructions
-----------------------------
        ectool evalDsl --dslFile aclEntries.groovy

Run from Command Step
---------------------
        Set shell to:  ectool evalDsl --dslFile {0}

*/

// Set Everyone read access to the system object artifacts and repositories
// Note the objectType: 'systemObject'
["artifacts", "repositories"].each { systemObject ->
    aclEntry principalName : "Everyone",
        principalType : 'group',
        objectType: 'systemObject',
        systemObjectName : systemObject,
        readPrivilege : 'allow',
        modifyPrivilege : 'inherit',
        executePrivilege : 'inherit',
        changePermissionsPrivilege : 'inherit'
}

//  support group: full READ access on the top level server object
// Note the objectType: 'server'
aclEntry principalName : "Support",
    principalType : 'group',
    objectType: 'server',
    systemObjectName : 'server',
    readPrivilege : 'allow',
    modifyPrivilege : 'inherit',
    executePrivilege : 'inherit',
    changePermissionsPrivilege : 'inherit'

// Give access (RX) to all the workspaces to a list of projects
["projA", "projB"].each { proj->
    // Create project if it does not already exist
    // so this code can run
    project proj

    aclEntry principalName : "project: $proj",
        principalType : 'user',
        objectType: 'systemObject',
        systemObjectName : "workspaces",
        readPrivilege : 'allow',
        modifyPrivilege : 'inherit',
        executePrivilege : 'allow',
        changePermissionsPrivilege : 'inherit'
}

// create procA so the code can run
project 'projA', {
    procedure 'procA'
}

// Give access (RWX) to selected user groups to a project
// and to a specific procedure
['groupA', 'groupB'].each { grp ->
    // Create group so the code is valid
    group grp
    
    aclEntry principalName : grp,
        principalType : 'group',
        objectType : 'procedure',
        projectName : "projA",
        procedureName: "procA",
        readPrivilege : 'allow',
        modifyPrivilege : 'inherit',
        executePrivilege : 'allow',
        changePermissionsPrivilege : 'inherit'

    aclEntry principalName : grp,
        principalType : 'group',
        objectType : 'project',
        projectName : "projB",
        readPrivilege : 'allow',
        modifyPrivilege : 'allow',
        executePrivilege : 'allow',
        changePermissionsPrivilege : 'inherit'
}
