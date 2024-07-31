package bitcamp.myapp;

import bitcamp.command.PracticeGame;
import bitcamp.context.ApplicationContext;
import bitcamp.myapp.dao.HistoryDao;
import bitcamp.myapp.dao.UserDao;
import bitcamp.myapp.vo.History;
import bitcamp.myapp.vo.User;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Objects;

public class RequestHandler extends Thread {

  private Socket socket;
  private UserDao userDao;
  private HistoryDao historyDao;
  private ApplicationContext ctx;

  public RequestHandler(Socket socket, UserDao userDao, HistoryDao historyDao, ApplicationContext ctx) {
    this.socket = socket;
    this.userDao = userDao;
    this.historyDao = historyDao;
    this.ctx = ctx;
  }

  @Override
  public void run() {
    try (Socket socket2 = socket) {
      ObjectOutputStream out = new ObjectOutputStream(socket2.getOutputStream());
      ObjectInputStream in = new ObjectInputStream(socket2.getInputStream());

      // 클라이언트 플레이어 이름 등록
      String serverPlayerName = (String) ctx.getAttribute("serverPlayer");
      String clientPlayerName = in.readUTF();
      User serverPlayer = userDao.findByName(serverPlayerName);
      User clientPlayer = userDao.findByName(clientPlayerName);
      if(clientPlayer == null) {
        clientPlayer = new User(clientPlayerName);
        userDao.insert(clientPlayer);
      }

      System.out.println("게임 시작!");
      Thread.sleep(1000);

      String player;
      while (true) {

        System.out.println(PracticeGame.getMap());
        System.out.println("상대방 입력 대기 중...");
        out.writeUTF(PracticeGame.getMap());
        out.writeObject(PracticeGame.gameMap);
        out.flush();

        int clientMove = in.readInt();
        PracticeGame.set(clientMove, "o");
        player = PracticeGame.check(clientPlayerName);
        if(Objects.equals(player, clientPlayerName)) {
          setWinLose(clientPlayerName, serverPlayerName);
          break;
        }
        out.writeUTF("continue");
        out.flush();


        System.out.println(PracticeGame.getMap());
        out.writeUTF(PracticeGame.getMap());
        out.writeObject(PracticeGame.gameMap);
        out.flush();

        PracticeGame.move("x");
        player = PracticeGame.check(serverPlayerName);
        if(Objects.equals(player, serverPlayerName)) {
          setWinLose(serverPlayerName, clientPlayerName);
          break;
        }
        out.writeUTF("continue");
        out.flush();
      }

      out.writeUTF("game over");
      out.writeUTF(PracticeGame.getMap());
      out.writeUTF(player);
      out.flush();
      System.out.println(PracticeGame.getMap());
      System.out.printf("승자 : %s\n", player);
      System.out.println("게임 오버");


    } catch (Exception e) {
      System.out.println("클라이언트 요청 처리 중 오류 발생!");

    } finally {
      System.out.println("클라이언트 연결 종료!");
    }
  }

  private void setWinLose(String winnerName, String loserName) {
    try {
      User winner = userDao.findByName(winnerName);
      User loser = userDao.findByName(loserName);

      winner.setWin(winner.getWin() + 1);
      loser.setLose(loser.getLose() + 1);

      History history = new History();
      history.setPlayers(new String[] {winnerName, loserName});
      history.setWinner(winnerName);
      historyDao.insert(history);

    } catch (Exception e) {
      System.out.println("전적 처리 중 오류 발생");
      e.printStackTrace();
    }
  }
}


