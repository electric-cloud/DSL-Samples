/*
Format: Electric Flow DSL
File: GroovyExcelParser.groovy
Description: Groovy file designed to parse MS Excel (xls and xlsx) and populate procedures and steps

*/

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

class GroovyExcelParser {
  //http://poi.apache.org/spreadsheet/quick-guide.html#Iterator

  def parse(path) {
    InputStream inp = new FileInputStream(path)
    Workbook wb = WorkbookFactory.create(inp);
    Sheet sheet = wb.getSheetAt(0);

    Iterator<Row> rowIt = sheet.rowIterator()
    Row row = rowIt.next()
    def headers = getRowData(row)

    def rows = []
    while(rowIt.hasNext()) {
      row = rowIt.next()
      rows << getRowData(row)
    }
    [headers, rows]
  }

  def getRowData(Row row) {
    def data = []
    for (Cell cell : row) {
      getValue(row, cell, data)
    }
    data
  }

  def getRowReference(Row row, Cell cell) {
    def rowIndex = row.getRowNum()
    def colIndex = cell.getColumnIndex()
    CellReference ref = new CellReference(rowIndex, colIndex)
    ref.getRichStringCellValue().getString()
  }
 
  def getValue(Row row, Cell cell, List data) {
    def rowIndex = row.getRowNum()
    def colIndex = cell.getColumnIndex()
    def value = ""
    switch (cell.getCellType()) {
      case Cell.CELL_TYPE_STRING:
        value = cell.getRichStringCellValue().getString();
        break;
      case Cell.CELL_TYPE_NUMERIC:
        if (DateUtil.isCellDateFormatted(cell)) {
            value = cell.getDateCellValue();
        } else {
            value = cell.getNumericCellValue();
        }
        break;
      case Cell.CELL_TYPE_BOOLEAN:
        value = cell.getBooleanCellValue();
        break;
      case Cell.CELL_TYPE_FORMULA:
        value = cell.getCellFormula();
        break;
      default:
        value = ""
    }
    data[colIndex] = value
    data
  }

  def toXml(header, row) {
    def obj = "<object>\n"       
    row.eachWithIndex { datum, i -> 
      def headerName = header[i]      
      obj += "\t<$headerName>$datum</$headerName>\n"      
    } 
    obj += "</object>"
  }    
  
  public static void main(String[]args) {
    def filename = 'C:\\cygwin64\\home\\wpullen\\TestAutomation\\Excel\\Commands.xlsx'    
    //def dslDir = "/vagrant/DSL-Samples/"
    def EFlowStep = ""
    def EFlowDesc = ""
    def EFlowComm = ""
         
    GroovyExcelParser parser = new GroovyExcelParser()
    def (headers, rows) = parser.parse(filename)
    //def (headers, rows) = parser.parse(dslDir + "Commands.xlsx")
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
    
    /*// Create the EFlow Procedure and Steps
    project ("MSExcel Slurping") {
      procedure ("Excel2Procedures") {
        rows.each { row ->
          step row[0],
              description: row[1],
              command: row[2]
        }
      }
    } // End EFlow Procedure and Steps*/    
       
  } // End main
}  // End GroovyExcelParser class