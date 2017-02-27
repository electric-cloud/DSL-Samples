project 'proj_7_2', {

    procedure 'sleep', {
        step 'step1', {
            command = 'sleep 5'
        }
    }

    pipeline 'parallel_task_pipeline1', {

        stage 'stage1', {

            gate 'PRE', {

                task 'pre-task1', {
                    taskType = 'APPROVAL'
                    notificationTemplate = 'ec_default_pipeline_notification_template'
                    approver = [
                            'admin',
                    ]
                }

                task1 'pre-group', {
                    taskType = 'GROUP'

                    task 'pre-t1-procedure', {
                        groupName = 'pre-group'
                        taskType = 'PROCEDURE'
                        subprocedure = 'sleep'
                    }

                    task 'pre-t2-manual', {
                        groupName = 'pre-group'
                        taskType = 'APPROVAL'
                        instruction = ''
                        notificationTemplate = 'ec_default_pipeline_notification_template'
                        approver = [
                                'admin',
                        ]
                    }
                }

            }
            task 'task1', {
                taskType = 'MANUAL'
                instruction = ''
                notificationTemplate = 'ec_default_pipeline_manual_task_notification_template'
                approver = [
                        'admin',
                ]
            }

            task 'group1', {
                taskType = 'GROUP'

                task 't1-procedure', {
                    groupName = 'group1'
                    taskType = 'PROCEDURE'
                    subprocedure = 'sleep'
                }

                task 't2-manual', {
                    groupName = 'group1'
                    taskType = 'MANUAL'
                    instruction = ''
                    notificationTemplate = 'ec_default_pipeline_manual_task_notification_template'
                    approver = [
                            'admin',
                    ]
                }
            }
        }
    }

}