package com.xe.lzh.rfid.Utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.InputStream;

import jxl.Sheet;
import jxl.Workbook;

/**
 * Created by Administrator on 2016/10/24 0024.
 */
public class WordUtils {
    public void readExcel(TextView view) {
        try {
            InputStream is = getClass().getResourceAsStream("/assets/aa.xls");
//           InputStream is = new FileInputStream("mnt/sdcard/test.xls");
//           Workbook book = Workbook.getWorkbook(new File("mnt/sdcard/test.xls"));
            Workbook book = Workbook.getWorkbook(is);
            int num = book.getNumberOfSheets();
            view.setText("the num of sheets is " + num + "\n");
            // 获得第一个工作表对象
            Sheet sheet = book.getSheet(0);
            int Rows = sheet.getRows();
            int Cols = sheet.getColumns();
            view.append("the name of sheet is " + sheet.getName() + "\n");
            view.append("total rows is " + Rows + "\n");
            view.append("total cols is " + Cols + "\n");
            view.append("contents:" + sheet.getCell(5, 2).getContents() + "\n");
//            for (int i = 0; i < Cols; ++i) {
//                for (int j = 0; j < Rows; ++j) {
//                    // getCell(Col,Row)获得单元格的值
//                    String contents = sheet.getCell(i, j).getContents();
//                    System.out.println(contents);
//                    txt.append("contents:" + contents + "\n");
//                }
//            }
            book.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    public static String getPath(Context context, Uri uri) {

        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = { "_data" };
            Cursor cursor = null;

            try {
                cursor = context.getContentResolver().query(uri, projection,null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
                // Eat it
            }
        }

        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }
}
