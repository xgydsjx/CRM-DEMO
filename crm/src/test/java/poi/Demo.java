package poi;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class Demo {
    public static void main (String[] args) throws IOException {
        HSSFWorkbook wb=new HSSFWorkbook();
        //创建sheet表
        HSSFSheet sheet = wb.createSheet("花名册");
        //创建行
        HSSFRow row = sheet.createRow(0);
        //创建列
        HSSFCell cell = row.createCell(0);
        cell.setCellValue("名字");
        cell=row.createCell(1);
        cell.setCellValue("年龄");

        //利用for循环创建多行
        for (int i = 1; i <=10; i++) {
            row= sheet.createRow(i);
           cell= row.createCell(0);
            cell.setCellValue("岳飞");
            cell=row.createCell(1);
            cell.setCellValue(24);
        }
        OutputStream outputStream=null;
        try {
           outputStream = new FileOutputStream("E:\\BaiduNetdiskDownload\\DIR\\Demo.xls");
           wb.write(outputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }finally {
            if (outputStream!=null){
                outputStream.close();
            }
            if (wb!=null){
                wb.close();
            }
        }
        System.out.println("create OK");
    }
}
