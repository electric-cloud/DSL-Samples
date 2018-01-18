# CSV to Report Data Converter

Reads from an MS Excel file and loads data into Devops Insight Server for a custom report object type

## Instructions

1. Retrieve this directory's parent to the Commander Server (git clone https://github.com/electriccommunity/DSL-Samples.git)
2. Copy the files in 'CSV2Report/server' to a directory say '/opt/dsl/server' on the ElectricFlow server.
3. Login as admin (ectool login admin)
4. Run evalDsl to execute the script.
```ectool evalDsl --dslFile "csv2report.groovy" --serverLibraryPath /opt/dsl/server/mylib --parameters '{"xlFile": "/opt/dsl/server/ReportData.xlsx"}'```

The script will load data into the Devops Insight Server based on the rows in "/opt/dsl/server/ReportData.xlsx". If 'createSampleDashboard' is set to true in the parameters argument, then 'Sample Dashboard' will
also be created with one sample widget and report using the custom data.
```ectool evalDsl --dslFile "csv2report.groovy" --serverLibraryPath /opt/dsl/server/mylib --parameters '{"xlFile": "/opt/dsl/server/ReportData.xlsx", "createSampleDashboard": "true"}'```

### Understanding ReportData.xlsx format
1. The first column is expected to be 'Report Object'. It should be populated with the name of custom report object type, e.g., 'codecommit', 'codescan', etc, that we want to load into DevOps Insight Server.
2. If there is a column named 'documentId', its values will be used to uniquely identify the record (called 'document' in ElasticSearch) in the Devops Insight Server. This column can be used to update existing records. If the column is not present, each run of the script will result in new records getting added to the DOIS server.



