package bitcamp.myapp;

import bitcamp.myapp.command.GameCommand;
import bitcamp.context.ApplicationContext;
import bitcamp.myapp.dao.HistoryDao;
import bitcamp.myapp.dao.UserDao;
import bitcamp.myapp.dao.skel.HistoryDaoSkel;
import bitcamp.myapp.vo.History;
import bitcamp.myapp.vo.User;

import bitcamp.net.ResponseStatus;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.Objects;

import static bitcamp.net.ResponseStatus.SUCCESS;

public class RequestHandler implements Runnable {

  private ObjectOutputStream out;
  private ObjectInputStream in;
  private UserDao userDao;
  private HistoryDao historyDao;
  private HistoryDaoSkel historyDaoSkel;
  private ApplicationContext ctx;
  private String serverPlayerName;
  private String clientPlayerName;
  private User serverPlayer;
  private User clientPlayer;

  public RequestHandler(UserDao userDao, HistoryDao historyDao, ApplicationContext ctx) {
    this.userDao = userDao;
    this.historyDao = historyDao;
    this.ctx = ctx;
  }

  @Override
  public void run() {
    try {

      historyDaoSkel = (HistoryDaoSkel) ctx.getAttribute("historyDaoSkel");
      serverPlayerName = (String) ctx.getAttribute("serverPlayer");
      clientPlayerName = (String) ctx.getAttribute("clientPlayer");
      serverPlayer = userDao.findByName(serverPlayerName);
      clientPlayer = userDao.findByName(clientPlayerName);

      if(clientPlayer == null) {
        clientPlayer = new User(clientPlayerName);
        userDao.insert(clientPlayer);
      }

      out = (ObjectOutputStream) ctx.getAttribute("out");
      in = (ObjectInputStream) ctx.getAttribute("in");

      System.out.println("게임 시작!");
      Thread.sleep(1000);

      String player;
      String message;
      int clientMove;
      int turn = 1;
      while (true) {

        System.out.println(GameCommand.getMap());
        out.writeUTF(GameCommand.getMap());
        out.flush();

        if (turn == 1) {
          turn = 2;

          System.out.println("상대방 입력 대기 중...");

          while (true) {
            clientMove = in.readInt();
            message = GameCommand.validate(clientMove);
            if(message.equals("OK")) {
              out.writeUTF(message);
              out.flush();
              GameCommand.set(clientMove);
              break;
            } else {
              out.writeUTF(message);
              out.flush();
            }
          }

          player = GameCommand.check(clientPlayerName);
          if(Objects.equals(player, clientPlayerName)) {
            setWinLose(clientPlayerName, serverPlayerName);
            break;
          }

        } else {
          turn = 1;

          GameCommand.move("x");
          player = GameCommand.check(serverPlayerName);
          if(Objects.equals(player, serverPlayerName)) {
            setWinLose(serverPlayerName, clientPlayerName);
            break;
          }
        }

        if (Objects.equals(player, "draw")) {
          setDraw();
          break;
        }

        out.writeUTF("continue");
        out.flush();
      }

      out.writeUTF("game over");
      out.writeUTF(GameCommand.getMap());
      out.writeUTF(player);
      out.writeObject(clientPlayer);
      out.flush();
      System.out.println(GameCommand.getMap());
      if(Objects.equals(player, "draw")) {
        System.out.println("무승부입니다.");
      } else {
        System.out.printf("승자 : %s\n", player);
      }
      System.out.printf("내 전적 : %d승 %d무 %d패\n", serverPlayer.getWin(), serverPlayer.getDraw(), serverPlayer.getLose());
      System.out.println("게임 오버");

    } catch (Exception e) {
      System.out.println("클라이언트 요청 처리 중 오류 발생!");
      e.printStackTrace();
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

  private void setDraw() {
    try {
      serverPlayer.setDraw(serverPlayer.getDraw() + 1);
      clientPlayer.setDraw(clientPlayer.getDraw() + 1);

      History history = new History();
      history.setPlayers(new String[] {serverPlayerName, clientPlayerName});
      history.setWinner("draw");
      historyDao.insert(history);
    } catch (Exception e) {
      System.out.println("전적 처리 중 오류 발생");
      e.printStackTrace();
    }
  }
}


