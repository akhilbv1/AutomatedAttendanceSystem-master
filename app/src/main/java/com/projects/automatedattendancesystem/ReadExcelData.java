package com.projects.automatedattendancesystem;


import com.projects.automatedattendancesystem.Tables.Student;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;


class ReadExcelData {



    static  Single<List<Student>>  intialiseSingle(InputStream inputStream, String fileName){
       return Single.create(emitter -> {
             List<Student> studentsDataList = new ArrayList<>();
             try {
                 Workbook workbook = null;
                 if (fileName.toLowerCase().endsWith("xlsx")) {
                     workbook = new XSSFWorkbook(inputStream);
                 } else if (fileName.toLowerCase().endsWith("xls")) {
                     workbook = new HSSFWorkbook(inputStream);
                 }
                 int number_Of_Sheets = 0;
                 if (workbook != null) {
                     number_Of_Sheets = workbook.getNumberOfSheets();
                 }
                 int slNo=1;

                 for (int i = 0; i < number_Of_Sheets; i++) {
                     Sheet sheet = workbook.getSheetAt(i);
                     for (int rowIn=1;rowIn<=sheet.getLastRowNum();rowIn++)
                     {
                         Student student = new Student();

                         Row row1 = sheet.getRow(rowIn);

                         student.setSlNo(String.valueOf(slNo));

                         student.setStud_Id(row1.getCell(1).getStringCellValue());

                         student.setStud_Name(row1.getCell(2).getStringCellValue());

                         student.setFather_Name(row1.getCell(3).getStringCellValue());

                         student.setClass_Name(row1.getCell(4).getStringCellValue());

                         student.setPhone(String.valueOf((long)row1.getCell(5).getNumericCellValue()));

                         studentsDataList.add(student);
                         slNo++;
                     }

                 }
                 emitter.onSuccess(studentsDataList);
             } catch (Exception e) {
                 e.printStackTrace();
                 emitter.onError(e);
             }
         });
    }

}
