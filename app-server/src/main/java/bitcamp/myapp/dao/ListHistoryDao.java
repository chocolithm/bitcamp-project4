package bitcamp.myapp.dao;

import bitcamp.myapp.vo.History;
import bitcamp.myapp.vo.User;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ListHistoryDao implements HistoryDao {

  private static final String DEFAULT_DATANAME = "histories";
  private int seqNo;
  private List<History> historyList = new ArrayList<>();
  private String path;
  private String dataName;

  public ListHistoryDao(String path) {
    this(path, DEFAULT_DATANAME);
  }

  public ListHistoryDao(String path, String dataName) {
    this.path = path;
    this.dataName = dataName;

    try (XSSFWorkbook workbook = new XSSFWorkbook(path)) {
      XSSFSheet sheet = workbook.getSheet(dataName);

      for (int i = 1; i <= sheet.getLastRowNum(); i++) {
        Row row = sheet.getRow(i);
        try {
          History history = new History();

          history.setNo(Integer.parseInt(row.getCell(0).getStringCellValue()));
          history.setPlayers(row.getCell(1).getStringCellValue().split(","));
          history.setWinner(row.getCell(2).getStringCellValue());
          history.setDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
              .parse(row.getCell(3).getStringCellValue()));

          historyList.add(history);

        } catch (Exception e) {
          System.out.printf("%s 번 전적의 데이터 형식이 맞지 않습니다.\n", row.getCell(0).getStringCellValue());
        }
      }

      seqNo = historyList.getLast().getNo();

    } catch (Exception e) {
      System.out.println("전적 데이터 로딩 중 오류 발생!");
      e.printStackTrace();
    }
  }

  public void save() throws Exception {
    try (FileInputStream in = new FileInputStream(path);
        XSSFWorkbook workbook = new XSSFWorkbook(in)) {

      int sheetIndex = workbook.getSheetIndex(dataName);
      if (sheetIndex != -1) {
        workbook.removeSheetAt(sheetIndex);
      }

      XSSFSheet sheet = workbook.createSheet(dataName);

      String[] cellHeaders = {"no", "players", "winner", "date"};
      Row headerRow = sheet.createRow(0);
      for (int i = 0; i < cellHeaders.length; i++) {
        headerRow.createCell(i).setCellValue(cellHeaders[i]);
      }

      int rowNo = 1;
      for (History history : historyList) {
        Row dataRow = sheet.createRow(rowNo++);
        dataRow.createCell(0).setCellValue(String.valueOf(history.getNo()));
        dataRow.createCell(1).setCellValue(history.getPlayers()[0] + "," + history.getPlayers()[1]);
        dataRow.createCell(2).setCellValue(history.getWinner());
        dataRow.createCell(3).setCellValue(
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(history.getDate()));
      }

      in.close();

      try (FileOutputStream out = new FileOutputStream(path)) {
        workbook.write(out);
      }
    }
  }

  @Override
  public boolean insert(History history) throws Exception {
    history.setNo(++seqNo);
    history.setDate(new Date());
    historyList.add(history);
    return true;
  }

  @Override
  public List<History> list(String userName) throws Exception {
    List<History> list = new ArrayList<>();

    for(History history : historyList) {
      if(history.getPlayers()[0].equals(userName) || history.getPlayers()[1].equals(userName)) {
        list.add(history);
      }
    }

    return list.stream().toList();
  }
}
