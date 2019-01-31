project 'Formal Parameters 2.0', {

  application 'Demo App1', {

    applicationTier 'Tier 1', {
      applicationName = 'Demo App1'
      projectName = 'Formal Parameters 2.0'
    }

    process 'test', {
      applicationName = 'Demo App1'
      processType = 'OTHER'
      serviceName = null
      smartUndeployEnabled = null
      timeLimitUnits = null
      workingDirectory = null
      workspaceName = null

      formalParameter 'ec_enforceDependencies', defaultValue: '0', {
        checkedValue = null
        expansionDeferred = '1'
        label = null
        orderIndex = null
        required = '0'
        type = 'checkbox'
        uncheckedValue = null
      }

      // Custom properties

      property 'ec_deploy', {

        // Custom properties
        ec_notifierStatus = '0'
      }
    }

    // Custom properties

    property 'ec_deploy', {

      // Custom properties
      ec_notifierStatus = '0'
    }
  }

  application 'Demo App2', {
    description = ''

    // Custom properties

    property 'ec_deploy', {

      // Custom properties
      ec_notifierStatus = '0'
    }
  }

  pipeline 'demo', {
    description = ''
    disableRestart = '0'
    enabled = '1'
    overrideWorkspace = '0'
    pipelineRunNameTemplate = null
    releaseName = null
    skipStageMode = 'ENABLED'
    templatePipelineName = null
    templatePipelineProjectName = null
    type = null
    workspaceName = null

    formalParameter 'select param', defaultValue: null, {
      expansionDeferred = '0'
      label = null
      optionsDsl = '''def options = new FormalParameterOptionsResult()

options.add(/*value*/ \'value1\', /*displayString*/ \'Value One\')
options.add(/*value*/ \'value2\', /*displayString*/ \'Value Two\')
options.add(/*value*/ \'value3\', /*displayString*/ \'Value Three\')

return options'''
      orderIndex = '1'
      required = '1'
      type = 'select'
    }

    formalParameter 'ec_stagesToRun', defaultValue: null, {
      expansionDeferred = '1'
      label = null
      orderIndex = null
      required = '0'
      type = null
    }

    stage 'Stage 1', {
      colorCode = '#00adee'
      completionType = 'auto'
      condition = null
      duration = null
      parallelToPrevious = null
      pipelineName = 'demo'
      plannedEndDate = null
      plannedStartDate = null
      precondition = null
      resourceName = null
      waitForPlannedStartDate = '0'

      gate 'PRE', {
        condition = null
        precondition = null
      }

      gate 'POST', {
        condition = null
        precondition = null
      }
    }
  }

  pipeline 'pipeline 1', {
    description = ''
    disableRestart = '0'
    enabled = '1'
    overrideWorkspace = '0'
    pipelineRunNameTemplate = null
    releaseName = null
    skipStageMode = 'ENABLED'
    templatePipelineName = null
    templatePipelineProjectName = null
    type = null
    workspaceName = null

    formalOutputParameter 'de'

    formalParameter 'Special Projects', defaultValue: null, {
      expansionDeferred = '0'
      label = null
      optionsDsl = '''def options = new FormalParameterOptionsResult()
    
    
// Get all projects and loop over the list considering only those that
// have the property \'customFlag\' defined and set to \'5\'.
 
def projects = getProjects()
 
projects.each { p ->
  // Check if the custom property is set on the project
  // and that its value is equal to \'5\'. If so, add the
  // project as the options result.
  
  // DSL tip: The groovy Elvis operator can be used
  // to do an existence check and then get a property
  // value if the property exists like this. 
  
  if(p.customFlag?.value == \'5\') {
    // The first argument is value: the actual parameter value to return for the option if selected
    // The second argument is text: the text to display in the drop-down for the option
    options.add(/*value*/p.projectName, /*display text*/p.projectName)
  }
}
 
// Finally return the options list
// \'return\' is not needed in groovy. The response of the last
// statement is returned automatically
options'''
      orderIndex = '1'
      required = '1'
      type = 'select'
    }

    formalParameter 'Application', defaultValue: null, {
      dependsOn = 'Special Projects'
      expansionDeferred = '0'
      label = null
      optionsDsl = '''def options = new FormalParameterOptionsResult()

//TODO: Declare this dependency in the parameter definition
def selectedProjectName = args.parameters[\'Special Projects\']
 
// If no project is selected then no apps to get
if (selectedProjectName) {
    def applications = getApplications(projectName: selectedProjectName)
    applications.each {
       options.add(/*value*/it.applicationName, /*display text*/it.applicationName)
    }
}
    
options'''
      orderIndex = '2'
      required = '1'
      type = 'select'
    }

    formalParameter 'Versions from properties (classic)', defaultValue: null, {
      expansionDeferred = '0'
      label = null
      optionsFromPropertySheet = '$[/myProject/versions_classic]'
      orderIndex = '3'
      required = '1'
      type = 'select'
    }

    formalParameter 'Versions from properties (new)', defaultValue: null, {
      expansionDeferred = '0'
      label = null
      orderIndex = '4'
      propertyReference = '$[/myProject/versions]'
      required = '1'
      type = 'select'
    }

    formalParameter 'select', defaultValue: null, {
      expansionDeferred = '0'
      label = null
      options = [
        'op1': 'va1',
        'op2': 'val2',
      ]
      orderIndex = '5'
      required = '1'
      type = 'select'
    }

    formalParameter 'ec_stagesToRun', defaultValue: null, {
      expansionDeferred = '1'
      label = null
      orderIndex = null
      required = '0'
      type = null
    }

    stage 'Stage 1', {
      colorCode = '#00adee'
      completionType = 'auto'
      condition = null
      duration = null
      parallelToPrevious = null
      pipelineName = 'pipeline 1'
      plannedEndDate = null
      plannedStartDate = null
      precondition = null
      resourceName = null
      waitForPlannedStartDate = '0'

      gate 'PRE', {
        condition = null
        precondition = null
      }

      gate 'POST', {
        condition = null
        precondition = null
      }
    }

    // Custom properties

    property 'ec_customEditorData', {

      // Custom properties

      property 'parameters', {

        // Custom properties

        property 'Versions from properties (classic)', {

          // Custom properties

          property 'options', {

            // Custom properties
            propertyPath = '$[/myProject/versions_classic]'

            property 'type', value: 'entry', {
              expandable = '1'
              suppressValueTracking = '0'
            }
          }
          formType = 'standard'
        }

        property 'select', {

          // Custom properties

          property 'options', {

            // Custom properties

            property 'option1', {

              // Custom properties

              property 'text', value: 'op1', {
                expandable = '1'
                suppressValueTracking = '0'
              }

              property 'value', value: 'va1', {
                expandable = '1'
                suppressValueTracking = '0'
              }
            }

            property 'option2', {

              // Custom properties

              property 'text', value: 'op2', {
                expandable = '1'
                suppressValueTracking = '0'
              }

              property 'value', value: 'val2', {
                expandable = '1'
                suppressValueTracking = '0'
              }
            }
            optionCount = '2'

            property 'type', value: 'list', {
              expandable = '1'
              suppressValueTracking = '0'
            }
          }
          formType = 'standard'
        }
      }
    }
  }

  pipeline 'pipeline 2', {
    description = ''
    disableRestart = '0'
    enabled = '1'
    overrideWorkspace = '0'
    pipelineRunNameTemplate = null
    releaseName = null
    skipStageMode = 'ENABLED'
    templatePipelineName = null
    templatePipelineProjectName = null
    type = null
    workspaceName = null

    formalParameter 'cbproject', defaultValue: 'true', {
      checkedValue = 'true'
      expansionDeferred = '0'
      label = 'Use Existing Project'
      orderIndex = '1'
      required = '0'
      type = 'checkbox'
      uncheckedValue = 'false'
    }

    formalParameter 'New Project', defaultValue: null, {
      expansionDeferred = '0'
      label = null
      orderIndex = '2'
      renderCondition = '${cbproject} != \'true\''
      required = '0'
      type = 'entry'
    }

    formalParameter 'Existing Project', defaultValue: null, {
      expansionDeferred = '0'
      label = null
      orderIndex = '3'
      renderCondition = '${cbproject} == \'true\''
      required = '0'
      type = 'project'
    }

    formalParameter 'ec_stagesToRun', defaultValue: null, {
      expansionDeferred = '1'
      label = null
      orderIndex = null
      required = '0'
      type = null
    }

    stage 'Stage 1', {
      colorCode = '#00adee'
      completionType = 'auto'
      condition = null
      duration = null
      parallelToPrevious = null
      pipelineName = 'pipeline 2'
      plannedEndDate = null
      plannedStartDate = null
      precondition = null
      resourceName = null
      waitForPlannedStartDate = '0'

      gate 'PRE', {
        condition = null
        precondition = null
      }

      gate 'POST', {
        condition = null
        precondition = null
      }
    }

    // Custom properties

    property 'ec_customEditorData', {

      // Custom properties

      property 'parameters', {

        // Custom properties

        property 'Use Existing Project', {

          // Custom properties

          property 'checkedValue', value: 'true', {
            expandable = '1'
            suppressValueTracking = '0'
          }
          formType = 'standard'

          property 'uncheckedValue', value: 'false', {
            expandable = '1'
            suppressValueTracking = '0'
          }
        }

        property 'cbproject', {

          // Custom properties

          property 'checkedValue', value: 'true', {
            expandable = '1'
            suppressValueTracking = '0'
          }
          formType = 'standard'

          property 'uncheckedValue', value: 'false', {
            expandable = '1'
            suppressValueTracking = '0'
          }
        }
      }
    }
  }

  pipeline 'pipeline 3', {
    description = ''
    disableRestart = '0'
    enabled = '1'
    overrideWorkspace = '0'
    pipelineRunNameTemplate = null
    releaseName = null
    skipStageMode = 'ENABLED'
    templatePipelineName = null
    templatePipelineProjectName = null
    type = null
    workspaceName = null

    formalParameter 'User', defaultValue: null, {
      expansionDeferred = '0'
      label = null
      orderIndex = '1'
      required = '1'
      type = 'entry'
      validationDsl = '''if (args.parameters[\'User\'] != \'usingh\') {
  return "\'${args.parameters[\'User\']}\' is not valid value"

} '''
    }

    formalParameter 'ec_stagesToRun', defaultValue: null, {
      expansionDeferred = '1'
      label = null
      orderIndex = null
      required = '0'
      type = null
    }

    stage 'Stage 1', {
      colorCode = '#00adee'
      completionType = 'auto'
      condition = null
      duration = null
      parallelToPrevious = null
      pipelineName = 'pipeline 3'
      plannedEndDate = null
      plannedStartDate = null
      precondition = null
      resourceName = null
      waitForPlannedStartDate = '0'

      gate 'PRE', {
        condition = null
        precondition = null
      }

      gate 'POST', {
        condition = null
        precondition = null
      }
    }

    // Custom properties

    property 'ec_counters', {

      // Custom properties
      pipelineCounter = '1'
    }
  }

  pipeline 'pipeline 4', {
    description = ''
    disableRestart = '0'
    enabled = '1'
    overrideWorkspace = '0'
    pipelineRunNameTemplate = null
    releaseName = null
    skipStageMode = 'ENABLED'
    templatePipelineName = null
    templatePipelineProjectName = null
    type = null
    workspaceName = null

    formalParameter 'New Formal Parameter Types', defaultValue: null, {
      description = 'New formal parameter types in 9.0 are date, integer and header which is this parameter itself.'
      expansionDeferred = '0'
      label = 'New Formal Parameter Types'
      orderIndex = '1'
      required = '0'
      type = 'header'
    }

    formalParameter 'Complete by', defaultValue: null, {
      expansionDeferred = '0'
      label = null
      orderIndex = '2'
      required = '1'
      type = 'date'
    }

    formalParameter 'Minimum number of Appropers', defaultValue: null, {
      expansionDeferred = '0'
      label = null
      orderIndex = '3'
      required = '1'
      type = 'integer'
    }

    formalParameter 'ec_stagesToRun', defaultValue: null, {
      expansionDeferred = '1'
      label = null
      orderIndex = null
      required = '0'
      type = null
    }

    stage 'Stage 1', {
      colorCode = '#00adee'
      completionType = 'auto'
      condition = null
      duration = null
      parallelToPrevious = null
      pipelineName = 'pipeline 4'
      plannedEndDate = null
      plannedStartDate = null
      precondition = null
      resourceName = null
      waitForPlannedStartDate = '0'

      gate 'PRE', {
        condition = null
        precondition = null
      }

      gate 'POST', {
        condition = null
        precondition = null
      }

      task 'sdas', {
        description = ''
        advancedMode = '0'
        afterLastRetry = null
        alwaysRun = '0'
        condition = null
        deployerExpression = null
        deployerRunType = null
        duration = null
        emailConfigName = null
        enabled = '1'
        environmentName = null
        environmentProjectName = null
        environmentTemplateName = null
        environmentTemplateProjectName = null
        errorHandling = 'stopOnError'
        gateCondition = null
        gateType = null
        groupName = null
        groupRunType = null
        insertRollingDeployManualStep = '0'
        instruction = null
        notificationEnabled = null
        notificationTemplate = null
        parallelToPrevious = null
        plannedEndDate = null
        plannedStartDate = null
        precondition = null
        requiredApprovalsCount = null
        resourceName = null
        retryCount = null
        retryInterval = null
        retryType = null
        rollingDeployEnabled = null
        rollingDeployManualStepCondition = null
        skippable = '0'
        snapshotName = null
        stageSummaryParameters = null
        startingStage = null
        subErrorHandling = null
        subapplication = null
        subpipeline = null
        subpluginKey = null
        subprocedure = null
        subprocess = null
        subproject = 'Formal Parameters 2.0'
        subrelease = null
        subreleasePipeline = null
        subreleasePipelineProject = null
        subreleaseSuffix = null
        subservice = null
        subworkflowDefinition = null
        subworkflowStartingState = null
        taskProcessType = null
        taskType = null
        triggerType = null
        useApproverAcl = '0'
        waitForPlannedStartDate = '0'
      }
    }
  }

  // Custom properties

  property 'versions', {
    description = ''

    // Custom properties

    property 'version1.0', value: '1.0', {
      description = ''
      expandable = '1'
      suppressValueTracking = '0'
    }

    property 'version2.0', value: '2.0', {
      description = ''
      expandable = '1'
      suppressValueTracking = '0'
    }
  }

  property 'versions_classic', {
    description = ''

    // Custom properties

    property 'option1', {
      description = ''

      // Custom properties

      property 'text', value: 'Option One', {
        description = ''
        expandable = '1'
        suppressValueTracking = '0'
      }

      property 'value', value: 'option1', {
        description = ''
        expandable = '1'
        suppressValueTracking = '0'
      }
    }
    optionCount = '1'
  }
  customFlag = '5'
}
