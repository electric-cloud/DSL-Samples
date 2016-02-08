import org.apache.poi.ss.usermodel.*
import org.apache.poi.hssf.usermodel.*
import org.apache.poi.xssf.usermodel.*
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.DataFormatter
import org.apache.poi.ss.util.*
import org.apache.commons.csv.*
import java.io.*

import org.springframework.jdbc.core.JdbcTemplate

    def EFlowStep = ""
    def EFlowDesc = ""
    def EFlowComm = ""
         
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
    println '------------------'
    
    rows.each { row ->
      println parser.toXml(headers, row)
      EFlowStep = row[0]
      EFlowDesc = row[1]
      EFlowComm = row[2]      
      //println EFlowStep
      //println EFlowDesc
      //println EFlowComm        
    }  // End row iteration
    
    // Create the EFlow Procedure and Steps
    project "Training1", {
      procedure "Excel2Procedure", {
        rows.each { row ->
          step row[0],
              description: row[1],
              command: row[2]
        }
      }
    } // End EFlow Procedure and Steps
    
