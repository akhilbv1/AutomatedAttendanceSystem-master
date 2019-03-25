package com.projects.automatedattendancesystem;

import android.os.Environment;
import android.util.Log;

import com.projects.automatedattendancesystem.Pojo.ReportPojo;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import io.reactivex.Single;

import static org.apache.poi.ss.usermodel.CellStyle.VERTICAL_TOP;

class WriteExcelData {


    static Single<Integer> generateExcelReportRx(String filename, List<ReportPojo> reportsLists, List<String> datesList) {
        return Single.create(emitter -> {
            try {
                int status = generateExcelReport(filename, reportsLists, datesList);
                if (status == Utils.SUCCESS) {
                    emitter.onSuccess(status);
                } else {
                    emitter.onError(new Throwable("Error occured while generating excel file,Please try again."));
                }

            } catch (Exception e) {
                e.printStackTrace();
                emitter.onError(e);
            }
        });
    }

    static Integer generateExcelReport(String filename, List<ReportPojo> reportsLists, List<String> datesList) {

        Workbook workbook;
        Map<String, Integer> dateMap = new HashMap<>();

        if (filename.endsWith("xlsx")) {
            workbook = new XSSFWorkbook();
        } else if (filename.endsWith("xls")) {
            workbook = new HSSFWorkbook();
        } else {
            return Utils.FAILURE;
        }

        Sheet sheet = workbook.createSheet("Attendance_Report");

        Row row = sheet.createRow(0);


        Cell cell_SLNo = row.createCell(1);
        cell_SLNo.setCellValue("SLNO");

        Cell cell_StudId = row.createCell(2);
        cell_StudId.setCellValue("Student_Id");

        Cell cell_StudentName = row.createCell(3);
        cell_StudentName.setCellValue("StudentName");

        Cell cell_FatherName = row.createCell(4);
        cell_FatherName.setCellValue("FatherName");

        Cell cell_ClassName = row.createCell(5);
        cell_ClassName.setCellValue("ClassName");

        int cell_index = 6;
        for (String date : datesList) {
            dateMap.put(date, cell_index);
            Cell cell_date = row.createCell(cell_index);
            cell_date.setCellValue(date);
            cell_index++;
        }
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(CellStyle.ALIGN_CENTER);
        style.setVerticalAlignment(VERTICAL_TOP);

        style.setWrapText(true);


        for (int i = 0; i < reportsLists.size(); i++) {
            ReportPojo obj = reportsLists.get(i);
            row = sheet.createRow(i + 1);
            Cell cell_Value_SLNo = row.createCell(1);
            cell_Value_SLNo.setCellValue(i+1);
            cell_Value_SLNo.setCellStyle(style);


            Cell cell_Value_StudId = row.createCell(2);
            cell_Value_StudId.setCellValue(obj.getStud_Id());
            cell_Value_StudId.setCellStyle(style);

            Cell cell_Value_StudentName = row.createCell(3);
            cell_Value_StudentName.setCellValue(obj.getStud_Name());
            cell_Value_StudentName.setCellStyle(style);

            Cell cell_Value_FatherName = row.createCell(4);
            cell_Value_FatherName.setCellValue(obj.getFather_Name());
            cell_Value_FatherName.setCellStyle(style);

            Cell cell_Value_ClassName = row.createCell(5);
            cell_Value_ClassName.setCellValue(obj.getClass_Name());
            cell_Value_ClassName.setCellStyle(style);


            for (int j = 0; j < obj.getAttendanceList().size(); j++) {
                int dateCellId = dateMap.get(obj.getAttendanceList().get(j).getCheckInDate());
                String checkInTime = obj.getAttendanceList().get(j).getCheckInTime() == null ? "" : obj.getAttendanceList().get(j).getCheckInTime() ;
               // String checkOutTime = obj.getAttendanceList().get(j).getCheckOutTime() == null ? "" :obj.getAttendanceList().get(j).getCheckOutTime() ;
                Cell cell_Value_Date = row.createCell(dateCellId);
                cell_Value_Date.setCellStyle(style);
                cell_Value_Date.setCellValue(checkInTime);
            }


        }

        return saveExcelFile(filename, workbook);
    }

    @SuppressWarnings("ALL")
    private static int saveExcelFile(String fileName, Workbook workbook) {
        try {

            File direct = new File(Environment.getExternalStorageDirectory() + "/AutomatedAttendanceReport");

            if (!direct.exists()) {
                File wallpaperDirectory = new File("/sdcard/AutomatedAttendanceReport/");
                wallpaperDirectory.mkdirs();
            }

            OutputStream fOut;
            File filePath = new File(Environment.getExternalStorageDirectory() + "/AutomatedAttendanceReport/" + fileName);
            fOut = new FileOutputStream(filePath);
            workbook.write(fOut);
            fOut.close();
            return Utils.SUCCESS;
        } catch (FileNotFoundException e) {
            Log.d("file", "file not found");
            e.printStackTrace();
            return Utils.FAILURE;
        } catch (IOException e) {
            Log.d("file", "io exception");
            e.printStackTrace();
            return Utils.FAILURE;
        }
    }
}
