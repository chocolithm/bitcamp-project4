package bitcamp.myapp;

import bitcamp.context.ApplicationContext;
import bitcamp.listener.ApplicationListener;
import bitcamp.listener.StartApplicationListener;
import bitcamp.myapp.dao.HistoryDao;
import bitcamp.myapp.dao.UserDao;
import bitcamp.myapp.listener.InitApplicationListener;
import bitcamp.myapp.vo.History;
import bitcamp.myapp.vo.User;
import bitcamp.util.Ansi;
import bitcamp.util.Prompt;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static bitcamp.net.ResponseStatus.CLIENT_TURN;
import static bitcamp.net.ResponseStatus.SERVER_TURN;

public class ClientApp {

  List<ApplicationListener> listeners = new ArrayList<>();
  ApplicationContext ctx = new ApplicationContext();

  private ObjectOutputStream out;
  private ObjectInputStream in;
  private UserDao userDao;
  private HistoryDao historyDao;
  private int turn = CLIENT_TURN;

  public static void main(String[] args) {
    ClientApp app = new ClientApp();

    // 애플리케이션이 시작되거나 종료될 때 알림 받을 객체의 연락처를 등록한다.
    app.addApplicationListener(new StartApplicationListener());
    app.addApplicationListener(new InitApplicationListener());

    app.execute();
  }

  private void addApplicationListener(ApplicationListener listener) {
    listeners.add(listener);
  }

  private void removeApplicationListener(ApplicationListener listener) {
    listeners.remove(listener);
  }

  void execute() {

    try {
      String clientPlayerName = Prompt.input("플레이어 :");

      Socket socket = new Socket("127.0.0.1", 8888);

      out = new ObjectOutputStream(socket.getOutputStream());
      in = new ObjectInputStream(socket.getInputStream());
      ctx.setAttribute("inputStream", in);
      ctx.setAttribute("outputStream", out);

      userDao = (UserDao) ctx.getAttribute("userDao");
      historyDao = (HistoryDao) ctx.getAttribute("historyDao");

      // 애플리케이션이 시작될 때 리스너에게 알린다.
      for (ApplicationListener listener : listeners) {
        try {
          listener.onStart(ctx);
        } catch (Exception e) {
          System.out.println("리스너 실행 중 오류 발생!");
        }
      }

      out.writeUTF(clientPlayerName);
      out.flush();

      // Rock-Paper-Scissors to decide the starting player
      playRPS();

      // 게임 실행
      while (true) {

        System.out.println("게임 시작!");
        Thread.sleep(1000);

        while (true) {

          printMap();

          if (turn == CLIENT_TURN) {
            clientTurn();
          } else {
            serverTurn();
          }

          if (in.readUTF().equals("game over")) {
            break;
          }
        }

        gameOver();

        System.out.println("[0] 종료  [1] 다시하기  [2] 전적보기");
        String command = Prompt.input("선택>");
        out.writeUTF(command);
        out.flush();
        if (command.equals("0")) {
          out.close();
          in.close();
          socket.close();
          break;
        }

        if (command.equals("1")) {
          out.reset();
          turn = CLIENT_TURN;
          System.out.println("게임을 다시 시작합니다.");
          continue;
        }

        if (command.equals("2")) {
          printList((List<History>) in.readObject(), clientPlayerName);
          break;
        }
      }

    } catch (Exception ex) {
      System.out.println("실행 오류!");
      ex.printStackTrace();
    }

    System.out.println("종료합니다.");
    Prompt.close();

    // 애플리케이션이 종료될 때 리스너에게 알린다.
    for (ApplicationListener listener : listeners) {
      try {
        listener.onShutdown(ctx);
      } catch (Exception e) {
        System.out.println("리스너 실행 중 오류 발생!");
      }
    }
  }

  private void playRPS() throws Exception {
    System.out.println("가위, 바위, 보 게임을 시작합니다.");
    String clientMove;
    String serverMove;

    while (true) {
      clientMove = Prompt.input("가위, 바위, 보 중 하나를 입력하세요: ");
      out.writeUTF(clientMove);
      out.flush();

      serverMove = in.readUTF();
      System.out.println("서버의 선택: " + serverMove);

      if (clientMove.equals(serverMove)) {
        System.out.println("비겼습니다. 다시 시도하세요.");
      } else if ((clientMove.equals("가위") && serverMove.equals("보")) ||
          (clientMove.equals("바위") && serverMove.equals("가위")) ||
          (clientMove.equals("보") && serverMove.equals("바위"))) {
        System.out.println("당신이 이겼습니다. 당신이 먼저 시작합니다.");
        turn = CLIENT_TURN;
        break;
      } else {
        System.out.println("당신이 졌습니다. 서버가 먼저 시작합니다.");
        turn = SERVER_TURN;
        break;
      }
    }
  }


  private void printMap() throws Exception {
    System.out.println(in.readUTF());
  }

  private void clientTurn() {
    turn = SERVER_TURN;

    while (true) {
      try {
        out.writeInt(Prompt.inputInt("다음 수를 입력하세요(1~9) :"));
        out.flush();
        String message = in.readUTF();

        if (message.equals("OK")) {
          break;
        } else {
          System.out.println(message);
        }

      } catch (Exception e) {
        System.out.println("오류 발생!");
      }
    }
  }

  private void serverTurn() {
    turn = CLIENT_TURN;
    System.out.println("상대방 입력 대기 중...");
  }

  private void gameOver() throws Exception {
    printMap();
    String result = in.readUTF();
    String winner = in.readUTF();

    if (Objects.equals(result, "draw")) {
      System.out.println("무승부입니다.");
    } else {
      System.out.printf("승자 : %s\n", winner);
    }

    User clientPlayer = (User) in.readObject();
    System.out.printf("내 전적 : %d승 %d무 %d패\n", clientPlayer.getWin(), clientPlayer.getDraw(),
        clientPlayer.getLose());
    System.out.println("게임 오버\n");
  }

  private void printList(List<History> list, String playerName) {
    if (list.isEmpty()) {
      System.out.println("전적이 없습니다.");
      return;
    }

    System.out.println("날짜\t\t1플레이어\t2플레이어\t승패\t\t");

    String result;
    for (History history : list) {
      if(history.getWinner().equals(playerName)) {
        result = Ansi.BLUE + "승" + Ansi.RESET;
      } else {
        result = Ansi.RED + "패" + Ansi.RESET;
      }

      if(history.getWinner().equals("draw")) {
        result = "무";
      }

      System.out.printf("%s\t%s%s%s%s%s\n",
          new SimpleDateFormat("yyyy-MM-dd").format(history.getDate()),
          history.getPlayers()[0], Prompt.getSpaces(12, history.getPlayers()[0]),
          history.getPlayers()[1], Prompt.getSpaces(12, history.getPlayers()[1]),
          result);
    }
  }
}
