package bitcamp.command.history;

import bitcamp.command.Command;
import bitcamp.myapp.dao.HistoryDao;
import bitcamp.myapp.vo.History;
import bitcamp.util.Prompt;
import java.text.SimpleDateFormat;

public class HistoryListCommand implements Command {

  private HistoryDao historyDao;
  private String playerName;

  public HistoryListCommand(HistoryDao historyDao, String playerName) {
    this.historyDao = historyDao;
    this.playerName = playerName;
  }

  @Override
  public void execute(String menuName) {

    try {
      if (historyDao.list(playerName).isEmpty()) {
        System.out.println("전적이 없습니다.");
        return;
      }

      System.out.println("날짜\t\t1플레이어\t2플레이어\t승자\t\t");

      for (History history : historyDao.list(playerName)) {
        System.out.printf("%s\t%s%s%s%s%s\n",
          new SimpleDateFormat("yyyy-MM-dd").format(history.getDate()),
          history.getPlayers()[0], Prompt.getSpaces(12, history.getPlayers()[0]),
          history.getPlayers()[1], Prompt.getSpaces(12, history.getPlayers()[1]),
          history.getWinner());
      }

    } catch (Exception e) {
      System.out.println("전적 목록 조회 중 오류 발생!");
      e.printStackTrace();
    }
  }
}
