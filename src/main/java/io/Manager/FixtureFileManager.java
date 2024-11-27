package io.Manager;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Assert;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.*;

/**
 * @author kedarpi
 */
public class FixtureFileManager {

    private static FixtureFileManager _instance =null;
    private String uniqueTestId;
    ArrayList<String> scenarioData = new ArrayList<String>();
    ArrayList<String> dataHeader = new ArrayList<String>();
    LinkedHashMap<String, ArrayList<String>> excelData = new LinkedHashMap<String,ArrayList<String>>();

    private String fileName, sheetName = "DataSheet";
    protected  boolean testIdFlag = false;
    private int keyIndex=0;

    public void setFileName(String fileName){
        if(fileName.contains("./"))
            this.fileName = fileName;
        else
        {
            try{
                ClassLoader classLoader = getClass().getClassLoader();
                File file = new File(classLoader.getResource(fileName).getFile());
            }
            catch(Exception e){
                Assert.fail("File not found : "+fileName);
            }
        }
    }

    public LinkedHashMap<String,ArrayList<String>> getExcelData(String sheetName){
        LinkedHashMap<String,ArrayList<String>> excelData = new LinkedHashMap<String,ArrayList<String>>();
        XSSFSheet sheet;
        XSSFWorkbook workbook;
        FileInputStream inputStream = null;
        try{
            inputStream = new FileInputStream(new File(fileName));
            DataFormatter formatter = new DataFormatter();
            workbook = new XSSFWorkbook(inputStream);
            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
            sheet = workbook.getSheet(sheetName);
            Row row;
            Iterator<Row> rowIterator = sheet.iterator();
            int maxColumn = sheet.getRow(0).getLastCellNum();
            while (rowIterator.hasNext()){
                ArrayList<String> rowValues = new ArrayList<String>();
                row = rowIterator.next();
                for (int cellCounter = 0; cellCounter < maxColumn; cellCounter++) {
                    Cell cell;
                    if(row.getCell(cellCounter)==null){
                        cell = row.createCell(cellCounter);
                        rowValues.add(cell.toString());
                    }
                    else{
                        cell = row.getCell(cellCounter);
                        rowValues.add(formatter.formatCellValue(cell,evaluator));
                    }
                }
                String key = rowValues.get(getKeyIndex(rowValues));
                rowValues.remove(getKeyIndex(rowValues));
                excelData.put(key,rowValues);
            }
            inputStream.close();
        }
        catch(Exception e){
            Assert.fail("File Not Found or corrupted : "+fileName);
        }
        finally{
            sheet = null;
            workbook = null;
        }
        return excelData;
    }

    public void putExcelData(LinkedHashMap<String,ArrayList<String>> excelData){
        if(fileName != null){
            FileInputStream inputStream;
            XSSFSheet sheet;
            XSSFWorkbook workbook;
            try{
                inputStream = new FileInputStream(new File(fileName));
                workbook = new XSSFWorkbook(inputStream);
                sheet = workbook.getSheet(sheetName);
                Set<String> keyset = excelData.keySet();
                int rownum = 0;
                for (String key: keyset) {
                    Row row = sheet.createRow(rownum++);
                    ArrayList<String> rowValues = excelData.get(key);
                    rowValues.add(keyIndex,key);
                    int cellnum =0;
                    for (String value: rowValues ) {
                        Cell cell = row.createCell(cellnum++);
                        if(value!=null &&value!="")
                            cell.setCellValue(value);
                        else
                            cellnum = cellnum++;
                    }
                }
                FileOutputStream outputStream = new FileOutputStream(new File(fileName));
                workbook.write(outputStream);
                outputStream.close();
                workbook = null;
            }
            catch(Exception e){
                Assert.fail("File doesn't exist");
                LoggerManager.getInstance().logError("Excel Writing Error : "+e.getMessage());
            }
            finally {

            }
        } else
            Assert.fail("File not present : "+fileName);
    }

    private int getKeyIndex(ArrayList<String> header){
        if(!testIdFlag){
            testIdFlag = true;
            keyIndex = header.indexOf("Test_Id");
            return  keyIndex;
        }
        else
            return  keyIndex;
    }

    private FixtureFileManager(){

    }

    public static FixtureFileManager getInstance(){
        if(_instance == null){
            _instance = new FixtureFileManager();
        }
        return _instance;
    }

    public String getUniqueTestId(){
        return uniqueTestId;
    }

    public void setUniqueTestId(String uniqueTestId){
        this.uniqueTestId = uniqueTestId;
    }

    public void reteriveScenarioTestData(String fileName){
        setFileName(fileName);
        excelData = getExcelData(sheetName);
        testIdFlag = false;
        dataHeader = excelData.get("Test_Id");
        if(uniqueTestId == null)
            Assert.fail("Test id not present in excel.");
        scenarioData = excelData.get(uniqueTestId);
    }

    private int getColumnIndex(String columnName){
        int columnIndex=0;
        for (String header: dataHeader){
            if(header.equalsIgnoreCase(columnName))
                return columnIndex;
            else
                columnIndex++;
        }
        Assert.fail("Column Name :"+columnName +" does not exist in excel file");
        return Integer.parseInt(null);
    }

    public HashMap<String,String> getTestDataWithHeader(){
        HashMap<String,String> scenarioDataWithHeader = new HashMap<String,String>();
        for (int counter = 0; counter < dataHeader.size(); counter++) {
            scenarioDataWithHeader.put(dataHeader.get(counter),scenarioData.get(counter));
        }
        return scenarioDataWithHeader;
    }

    public void updateScenarioTestData(){
        putExcelData(excelData);
    }

    public void cleanDataPool(){
        if(excelData!=null || excelData.isEmpty())
            excelData.clear();
    }

    public void clean(){
        uniqueTestId = null;
        dataHeader.clear();
        scenarioData.clear();
        excelData.clear();
    }

    public void setTestData(String columnName, String value){

        scenarioData.set(getColumnIndex(columnName),value);
        excelData.put(uniqueTestId,scenarioData);
    }

    public String getTestData(String columnName){
        if(columnName.startsWith("#")){
            columnName = columnName.substring(1,columnName.length());
            String value = scenarioData.get(getColumnIndex(columnName));
            return value;
        }
        else
            return columnName;
    }

    public static void writeToExcel(List<Map<String, String>> excelData, String fileName, String sheetName) {
        try (FileInputStream inputStream = new FileInputStream(new File(fileName))) {
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            XSSFSheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                sheet = workbook.createSheet(sheetName);
            }
            //Write Column Headers
            XSSFRow row = sheet.createRow(0);
            int cellNum = 0;
            for (String column : excelData.get(0).keySet()) {
                Cell cell = row.createCell(cellNum++);
                cell.setCellValue(column);
            }

            for (int i = 0; i < excelData.size(); i++) {
                row = sheet.createRow(i + 1);
                Map<String, String> dataMap = excelData.get(i);
                cellNum = 0;
                for (Map.Entry<String, String> entry : dataMap.entrySet()) {
                    Cell cell = row.createCell(cellNum++);
                    cell.setCellValue(entry.getValue());
                }
            }
            FileOutputStream outputStream = new FileOutputStream(new File(fileName));
            workbook.write(outputStream);
            outputStream.close();
        } catch (Exception e) {
            Assert.fail("Issue while writting to file. " + e.getMessage());
        }
    }


}
