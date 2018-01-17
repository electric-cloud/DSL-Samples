import groovy.json.JsonOutput

GroovyExcelParser parser = new GroovyExcelParser()
	
def (headers, rows) = parser.parse(args.xlFile)
println "\n"
println "\n"
println 'Headers'
println '------------------'
headers.each { header ->
  println header
}
println "\n"
println 'Rows'
rows.eachWithIndex { row, index ->
    println 'Row ' + (index + 1)
    row.each { col ->
        println col
    }
}
println '------------------'

validate(headers, rows)
createOrUpdateReportObject(headers, rows)
if (args.createSampleDashboard) {
    createSampleDashboardWithReport(headers, rows)
}

sendReportingDataToDevOpsInsightServer(headers, rows)

def sendReportingDataToDevOpsInsightServer(def headers, def rows) {
    def reportObjectName = getReportObjectName(rows)
    def columns = headers.subList(1, headers.size)

    rows.each { fullRow ->
        def payload = [:]
        def row = fullRow.subList(1, fullRow.size) // strip first column which is report object name
        row.eachWithIndex { data, index ->
            //TODO: validate DATE format
            if (data != null && data != '') {
                payload << [(columns[index]): "$data"]
            }
        }

        if (payload) {
            def payloadStr = JsonOutput.toJson(payload)
            println "Sending payload: $payloadStr"
            sendReportingData reportObjectTypeName: reportObjectName, payload: payloadStr
        }
    }
}

def createOrUpdateReportObject(def headers, def rows) {
    def reportObjectName = getReportObjectName(rows)

    // do not allow updating report object structure
    // for objects that are populated through EC plugins.
    // This may be allowed in the future but locking this
    // down for now to be on the safe side so as not to break
    // OOB dashboards.
    assert !reportObjectName.equalsIgnoreCase('build')
    assert !reportObjectName.equalsIgnoreCase('defect')
    assert !reportObjectName.equalsIgnoreCase('feature')
    assert !reportObjectName.equalsIgnoreCase('incident')
    assert !reportObjectName.equalsIgnoreCase('quality')

    reportObjectType reportObjectName, {
        headers.eachWithIndex { col, index ->
            if (index > 0) {
                //TODO: handle DATE attribute types
                def columnType = col.endsWith('Date') ? 'DATE': 'STRING'
                reportObjectAttribute col, type: columnType
            }
        }
    }
}

def createSampleDashboardWithReport(def headers, def rows) {
    def projName = 'Sample Dashboards'
    def dashboardName = 'Sample Dashboard'

    def reportObjectName = getReportObjectName(rows)
    def sampleReportName = "Sample Report - ${reportObjectName}"
    def widgetName = "Sample Widget - ${reportObjectName}"

    project projName, {

        report sampleReportName, {
            reportObjectTypeName = reportObjectName
            definition = '''
            {
                "size": 10,
                "query": {
                    "bool" : {
                        "filter": [
                            @@ELECTRIC_FLOW_REPORT_FILTERS@@
                        ]
                    }
                }
            }'''
        }

        //TODO: temp
        deleteDashboard dashboardName: dashboardName, projectName: projName
        dashboard dashboardName, {
            //TODO: add DateFilter dynamically based on report object structure
            reportingFilter 'DateFilter', {
                description = 'Filter by the timestamp field identified by parameter name.'
                parameterName = 'Commit Date'
                type = 'DATE'
                operator = 'BETWEEN'
                required = '1'
            }

            widget widgetName, clearAttributeDataTypes: true, clearAttributePaths: true, clearColors: true, clearVisualizationProperties: true, {
                description = 'Sample widget description'
                reportName = sampleReportName
                reportProjectName = projName
                visualization = 'TABLE'
                attributePath = ['column1': 'Commit Description',
                                 'column2': 'Commit ID',
                                 'column3': 'Associated Defect']
                attributeDataType = ['column1': 'STRING',
                                     'column2': 'STRING',
                                     'column3': 'STRING']
            }

        }

    }
}

def getReportObjectName(def rows) {
    def reportObjectName = rows[0][0]
    reportObjectName
}

def boolean validate(def headers, def rows) {
    // there must be atleast 2 columns
    // one for report object name  - "Report Object"
    // second for atleast one attribute/column to be populated in DevOps Insight server
    assert headers?.size >= 2
    rows.each { row ->
        assert row.size == headers.size
    }

    def reportObjectName = getReportObjectName(rows)
    assert reportObjectName

    // do not allow loading reporting data for ElectricFlow objects
    assert !reportObjectName.equalsIgnoreCase('deployment')
    assert !reportObjectName.equalsIgnoreCase('pipelineRun')
    assert !reportObjectName.equalsIgnoreCase('release')

    // all values in the first column (represents report object name)
    // must be the same
    rows.each { row ->
        row[0] == reportObjectName
    }
}

