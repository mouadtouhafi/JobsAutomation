package dataorganize;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class DataToExcel {

    private Workbook workbook;
    private Sheet sheet;
    private int currentRow = 1;
    private CellStyle dataCellStyle;
    private CellStyle headerCellStyle;

    public void createWorkbook(){
        workbook = new XSSFWorkbook();
        sheet = workbook.createSheet("Jobs");
        Row titleRow = sheet.createRow(currentRow++);
        Cell cell = titleRow.createCell(2);
        cell.setCellValue("List of jobs by companies");

        /* Setting style for the title */
        CellStyle cellStyle = workbook.createCellStyle();
        Font titleFont = workbook.createFont();
        titleFont.setBold(true);
        titleFont.setFontHeightInPoints((short)20);
        titleFont.setColor(IndexedColors.GREEN.getIndex());

        cellStyle.setFont(titleFont);
        cell.setCellStyle(cellStyle);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
    }

    public void writeTableHeader(){
        currentRow = currentRow + 3;
        Row headerRow = sheet.createRow(currentRow);
        String[] headers = {"Company", "Job title", "Location", "Contract type", "Work mode", "Publish date", "Missions", "Qualifications", "Link"};
        for(int i=0; i<headers.length; i++){
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);

            cell.setCellStyle(headerCellStyle);
        }
        currentRow = currentRow + 1;
    }

    public void writeData(String company,
                          HashMap<Integer, List<String>> jobInfo,
                          HashMap<Integer, String> jobsLinks,
                          HashMap<Integer, StringBuilder> jobMissions,
                          HashMap<Integer, StringBuilder> jobQualifications){

        for(int i = 0; i<jobInfo.size(); i++){
            Row row = sheet.createRow(currentRow);
            Cell cell_0 = row.createCell(0);
            cell_0.setCellValue(company);
            cell_0.setCellStyle(dataCellStyle);

            List<String> job = jobInfo.get(i);
            String link = jobsLinks.get(i);

            /*
             * Preparing the final missions String before writing it to Excel file
             * */
            StringBuilder missions = jobMissions.get(i);
            String[] missionsSplit = missions.toString().split("- ");
            StringBuilder buildMissions = new StringBuilder("'- ");
            for(String str : missionsSplit){
                str = str.strip();
                if(!str.isEmpty()){
                    buildMissions.append(str).append("\r\n").append("- ");
                }
            }
            String finalMissions = buildMissions.toString();
            if(finalMissions.endsWith("\r\n- ")){
                finalMissions = finalMissions.substring(0, finalMissions.length() - 4);
            }


            /*
             * Preparing the final qualifications String before writing it to Excel file
             * */
            StringBuilder qualifications = jobQualifications.get(i);
            String[] qualificationsSplit = qualifications.toString().split("- ");
            StringBuilder buildQualifications = new StringBuilder("'- ");
            for(String str : qualificationsSplit){
                str = str.strip();
                if(!str.isEmpty()){
                    buildQualifications.append(str).append("\r\n").append("- ");
                }
            }
            String finalQualifications = buildQualifications.toString();
            if(finalQualifications.endsWith("\r\n- ")){
                finalQualifications = finalQualifications.substring(0, finalQualifications.length() - 4);
            }

            /*
            *  Creating the cells
            * */
            for(int m=0; m<job.size(); m++){
                Cell cell = row.createCell(m+1);
                if(!job.get(m).isEmpty()){
                    cell.setCellValue(job.get(m));
                }else{
                    cell.setCellValue("N/A");
                }
                cell.setCellStyle(dataCellStyle);
            }

            Cell missionCell = row.createCell(6);
            missionCell.setCellValue(finalMissions);
            missionCell.setCellStyle(dataCellStyle);

            Cell qualificationCell = row.createCell(7);
            qualificationCell.setCellValue(finalQualifications);
            qualificationCell.setCellStyle(dataCellStyle);

            Cell linkCell = row.createCell(8);
            linkCell.setCellValue(link);
            linkCell.setCellStyle(dataCellStyle);

            currentRow = currentRow + 1;
        }
    }

    public void applyHeaderStyle() {
        /*
         * Setting Header style :
         *   1 - Setting font to bold
         *   2 - Setting cells color to gray
         *   3 - Setting cells borders
         * */

        headerCellStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerCellStyle.setFont(headerFont);

        byte[] rgb = new byte[]{(byte) 191, (byte) 191, (byte) 191};
        XSSFColor grayColor = new XSSFColor(rgb, null);
        headerCellStyle.setFillForegroundColor(grayColor);
        headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        headerCellStyle.setBorderTop(BorderStyle.THIN);
        headerCellStyle.setBorderRight(BorderStyle.THIN);
        headerCellStyle.setBorderBottom(BorderStyle.THIN);
        headerCellStyle.setBorderLeft(BorderStyle.THIN);
        headerCellStyle.setWrapText(true);
        headerCellStyle.setVerticalAlignment(VerticalAlignment.TOP);
    }

    public void applyBorderStyle() {
        dataCellStyle = workbook.createCellStyle();
        dataCellStyle.setBorderTop(BorderStyle.THIN);
        dataCellStyle.setBorderRight(BorderStyle.THIN);
        dataCellStyle.setBorderBottom(BorderStyle.THIN);
        dataCellStyle.setBorderLeft(BorderStyle.THIN);
        dataCellStyle.setWrapText(true);
        dataCellStyle.setVerticalAlignment(VerticalAlignment.TOP);
    }

    public void saveWorkbook(String filepath){
        try(FileOutputStream output = new FileOutputStream(filepath)) {
            /*
             * Auto-size columns for better readability
             * */
            for (int i = 0; i < 9; i++) {
                sheet.autoSizeColumn(i);
            }
            sheet.setColumnWidth(0, 15 * 256);
            sheet.setColumnWidth(3, 15 * 256);
            sheet.setColumnWidth(4, 50 * 256);
            sheet.setColumnWidth(5, 20 * 256);
            sheet.setColumnWidth(6, 50 * 256);
            sheet.setColumnWidth(7, 50 * 256);
            sheet.setColumnWidth(8, 50 * 256);

            workbook.write(output);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }finally {
            try {
                if(workbook != null) {
                    workbook.close();
                }
            } catch (IOException e) {
                System.err.println("Error closing workbook: " + e.getMessage());
            }
        }
    }
}
