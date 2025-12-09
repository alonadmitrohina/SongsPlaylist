package profitsoft.intership.songsplaylist.utils;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import profitsoft.intership.songsplaylist.dto.song.SongInfoDto;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class ExcelGenerator {

    /**
     * Створення репорту в форматі excel
     * @param songs
     * @return
     * @throws IOException
     */
    public static byte[] generateReport(List<SongInfoDto> songs) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Songs");

            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("ID");
            header.createCell(1).setCellValue("Name");
            header.createCell(2).setCellValue("Artist");
            header.createCell(3).setCellValue("Year");

            int rowIdx = 1;
            for (SongInfoDto song : songs) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(song.getId());
                row.createCell(1).setCellValue(song.getName());
                row.createCell(2).setCellValue(song.getArtist());
                row.createCell(3).setCellValue(song.getYear());
            }

            for (int i = 0; i < 4; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }
}

