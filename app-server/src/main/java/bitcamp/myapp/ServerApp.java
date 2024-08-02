package bitcamp.myapp;

import bitcamp.listener.StartApplicationListener;
import bitcamp.myapp.command.GameCommand;
import bitcamp.context.ApplicationContext;
import bitcamp.listener.ApplicationListener;
import bitcamp.myapp.dao.HistoryDao;
import bitcamp.myapp.dao.UserDao;
import bitcamp.myapp.listener.InitApplicationListener;
import bitcamp.myapp.vo.History;
import bitcamp.myapp.vo.User;
import bitcamp.util.Prompt;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static bitcamp.net.ResponseStatus.CLIENT_TURN;
import static bitcamp.net.ResponseStatus.SERVER_TURN;

public class ServerApp {

  List<ApplicationListener> listeners = new ArrayList<>();
  ApplicationContext ctx = new ApplicationContext();

  ObjectOutputStream out;
  ObjectInputStream in;
  private String serverPlayerName;
  private String clientPlayerName;
  private User serverPlayer;
  private User clientPlayer;
  private UserDao userDao;
  private HistoryDao historyDao;

  int turn = CLIENT_TURN;
  String player;
  String op;
  String result;

  public static void main(String[] args) {
    ServerApp app = new ServerApp();

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

    // 애플리케이션이 시작될 때 리스너에게 알린다.
    for (ApplicationListener listener : listeners) {
      try {
        listener.onStart(ctx);
      } catch (Exception e) {
        System.out.println("리스너 실행 중 오류 발생!");
      }
    }

    userDao = (UserDao) ctx.getAttribute("userDao");
    historyDao = (HistoryDao) ctx.getAttribute("historyDao");

    // 서버 플레이어 이름 등록
    try {
      serverPlayerName = Prompt.input("플레이어 :");
      serverPlayer = userDao.findByName(serverPlayerName);

      if(serverPlayer == null) {
        serverPlayer = new User(serverPlayerName);
        userDao.insert(serverPlayer);
      }

      ctx.setAttribute("serverPlayerName", serverPlayerName);

    } catch (Exception e) {
      System.out.println("플레이어 등록 중 오류 발생!");
    }

    try (ServerSocket serverSocket = new ServerSocket(8888)) {
      GameCommand.start();
      System.out.println("게임 시작 대기 중...");

      System.out.println("클라이언트의 연결을 기다림!");
      Socket socket = serverSocket.accept();
      InetSocketAddress remoteAddr = (InetSocketAddress) socket.getRemoteSocketAddress();
      System.out.printf("클라이언트(%s:%d)가 연결되었음!\n", remoteAddr.getAddress(), remoteAddr.getPort());

      out = new ObjectOutputStream(socket.getOutputStream());
      in = new ObjectInputStream(socket.getInputStream());

      // 클라이언트 플레이어 이름 등록
      clientPlayerName = in.readUTF();
      clientPlayer = userDao.findByName(clientPlayerName);
      if(clientPlayer == null) {
        clientPlayer = new User(clientPlayerName);
        userDao.insert(clientPlayer);
      }

      ctx.setAttribute("out", out);
      ctx.setAttribute("in", in);

      // Rock-Paper-Scissors to decide the starting player
      playRPS();

      // 게임 시작
      Thread requestThread;
      while (true) {
        requestThread = new RequestHandler();
        requestThread.start();
        requestThread.join();

        String command = in.readUTF();
        if (command.equals("0")) {
          try {
            out.close();
            in.close();
            socket.close();
          } catch (Exception ignored) {

          }
          break;
        }

        if (command.equals("1")) {
          out.reset();
          GameCommand.start();
          turn = CLIENT_TURN;
          continue;
        }

        if (command.equals("2")) {
          List<History> list = historyDao.list(clientPlayerName);
          out.writeObject(list);
          out.flush();
          break;
        }
      }

    } catch (Exception e) {
      System.out.println("통신 중 오류 발생!");
      e.printStackTrace();
    }

    System.out.println("종료합니다.");

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
      serverMove = Prompt.input("가위, 바위, 보 중 하나를 입력하세요: ");
      out.writeUTF(serverMove);
      out.flush();

      clientMove = in.readUTF();
      System.out.println("클라이언트의 선택: " + clientMove);

      if (clientMove.equals(serverMove)) {
        System.out.println("비겼습니다. 다시 시도하세요.");
      } else if ((clientMove.equals("가위") && serverMove.equals("보")) ||
          (clientMove.equals("바위") && serverMove.equals("가위")) ||
          (clientMove.equals("보") && serverMove.equals("바위"))) {
        System.out.println("클라이언트가 이겼습니다. 클라이언트가 먼저 시작합니다.");
        turn = CLIENT_TURN;
        break;
      } else {
        System.out.println("서버가 이겼습니다. 서버가 먼저 시작합니다.");
        turn = SERVER_TURN;
        break;
      }
    }
  }


  class RequestHandler extends Thread {

    @Override
    public void run() {
      try {

        System.out.println("게임 시작!");
        Thread.sleep(1000);

        while (true) {

          printMap();

          if (turn == CLIENT_TURN) {
            clientTurn();
          } else {
            serverTurn();
          }

          result = GameCommand.check();
          if(Objects.equals(result, "game over")) {
            User winner = userDao.findByName(player);
            User loser = userDao.findByName(op);
            setWinLose(winner, loser);
            break;
          }

          if(Objects.equals(result, "draw")) {
            setDraw();
            break;
          }

          out.writeUTF("continue");
          out.flush();
        }

        gameOver();

      } catch (Exception e) {
        System.out.println("클라이언트 요청 처리 중 오류 발생!");
        e.printStackTrace();
      }
    }

    private void printMap() throws Exception {
      System.out.println(GameCommand.getMap());
      out.writeUTF(GameCommand.getMap());
      out.flush();
    }

    private void clientTurn() throws Exception {
      turn = SERVER_TURN;
      player = clientPlayerName;
      op = serverPlayerName;
      System.out.println("상대방 입력 대기 중...");

      while (true) {
        int clientMove = in.readInt();
        String message = GameCommand.validate(clientMove);
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
    }

    private void serverTurn() {
      turn = CLIENT_TURN;
      player = serverPlayerName;
      op = clientPlayerName;

      GameCommand.move("x");
    }

    private void setWinLose(User winner, User loser) {
      try {

        winner.setWin(winner.getWin() + 1);
        loser.setLose(loser.getLose() + 1);

        History history = new History();
        history.setPlayers(new String[] {winner.getName(), loser.getName()});
        history.setWinner(winner.getName());
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

    private void gameOver() throws Exception {
      out.writeUTF("game over");
      printMap();
      out.writeUTF(result);
      out.writeUTF(player);
      out.writeObject(clientPlayer);
      out.flush();

      if(Objects.equals(result, "draw")) {
        System.out.println("무승부입니다.");
      } else {
        System.out.printf("승자 : %s\n", player);
      }

      System.out.printf("내 전적 : %d승 %d무 %d패\n", serverPlayer.getWin(), serverPlayer.getDraw(), serverPlayer.getLose());
      System.out.println("게임 오버");
    }
  }
}
