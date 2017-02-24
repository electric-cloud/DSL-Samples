# MS Excel Parser 

Example of how to use 3-rd party libraries and Groovy custom class in a DSL script to read an MS Excel file and create a procedure based on the data in the file.

## Instructions

1. Retrieve this directory's parent to the Commander Server (git clone https://github.com/electriccommunity/DSL-Samples.git)
2. Copy the files in 'server' to a directory say '/opt/dsl/server' on the ElectricFlow server.
3. Login as admin (ectool login admin)
4. Run evalDsl to execute the script (ectool evalDsl --dslFile "xlparser.groovy" --serverLibraryPath /opt/dsl/server/mylib --parameters '{"xlFile": "/opt/dsl/server/Commands.xlsx"}')

The script will create a procedure "Excel2Procedure" in the project "Training" based on the rows in "/opt/dsl/server/Commands.xlsx".


