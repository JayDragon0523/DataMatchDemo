package com.test.utils;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.FileInputStream;

/**
 * @program: DataMatchDemo
 * @description: 读取excel表格工具类
 * @author: JayDragon
 * @create: 2020-12-29 14:54
 **/
public class ExcelUtil {

    private HSSFSheet sheet;


    /**
     * 构造函数，初始化excel数据
     * @param filePath  excel路径
     * @param sheetName sheet表名
     */
    public ExcelUtil(String filePath, String sheetName){
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(filePath);
            HSSFWorkbook sheets = new HSSFWorkbook(fileInputStream);
            //获取sheet
            sheet = sheets.getSheet(sheetName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
    * @description: 根据行和列的索引获取单元格的数据
    * @param: [row, column]
    * @return: java.lang.String
    * @author: JayDragon
    * @date: 2020/12/29
    */
    public String getExcelDateByIndex(int row,int column){
        HSSFRow row1 = sheet.getRow(row);
        String cell = row1.getCell(column).toString();
        return cell;
    }



    /**
    * @description: 根据某一列值为“******”的这一行，来获取该行第x列的值
    * @param: [caseName, currentColumn 当前单元格列的索引, targetColumn 目标单元格列的索引]
    * @return: java.lang.String
    * @author: JayDragon
    * @date: 2020/12/29
    */
    public String getCellByCaseName(String caseName,int currentColumn,int targetColumn){
        String operateSteps="";
        //获取行数
        int rows = sheet.getPhysicalNumberOfRows();
        for(int i=0;i<rows;i++){
            HSSFRow row = sheet.getRow(i);
            String cell = row.getCell(currentColumn).toString();
            if(cell.equals(caseName)){
                operateSteps = row.getCell(targetColumn).toString();
                break;
            }
        }
        return operateSteps;
    }

    /**
    * @description: 打印excel数据
    * @param: []
    * @return: void
    * @author: JayDragon
    * @date: 2020/12/29
    */
    public void readExcelData() {
        //获取行数
        int rows = sheet.getPhysicalNumberOfRows();
        for (int i = 0; i < rows; i++) {
            //获取列数
            HSSFRow row = sheet.getRow(i);
            int columns = row.getPhysicalNumberOfCells();
            for (int j = 0; j < columns; j++) {
                String cell = row.getCell(j).toString();
                System.out.println(cell);
            }
        }
    }

    public HSSFSheet getSheet() {
        return sheet;
    }

    public void setSheet(HSSFSheet sheet) {
        this.sheet = sheet;
    }
}
