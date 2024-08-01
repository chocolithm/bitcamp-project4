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

public class ServerApp {

  List<ApplicationListener> listeners = new ArrayList<>();
  ApplicationContext ctx = new ApplicationContext();

  ObjectOutputStream out;
  ObjectInputStream in;
  private String serverPlayerName;
  private String clientPlayerName;
  private User serverPlayer;
  private User clientPlayer;

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

    // 서버에서 사용할 Dao Skeloton 객체를 준비한다.
    UserDao userDao = (UserDao) ctx.getAttribute("userDao");
    HistoryDao historyDao = (HistoryDao) ctx.getAttribute("historyDao");


    // 서버 플레이어 이름 등록
    try {
      System.out.println("Welcome to TicTacToe");
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

    try (ServerSocket serverSocket = new ServerSocket(8888);) {
      GameCommand.start();
      System.out.println("게임 시작 대기 중...");

      System.out.println("클라이언트의 연결을 기다림!");
      Socket socket = serverSocket.accept();
      InetSocketAddress remoteAddr = (InetSocketAddress) socket.getRemoteSocketAddress();
      System.out.printf("클라이언트(%s:%d)가 연결되었음!\n", remoteAddr.getAddress(), remoteAddr.getPort());

      out = new ObjectOutputStream(socket.getOutputStream());
      in = new ObjectInputStream(socket.getInputStream());

      clientPlayerName = in.readUTF();
      clientPlayer = userDao.findByName(clientPlayerName);
      if(clientPlayer == null) {
        clientPlayer = new User(clientPlayerName);
        userDao.insert(clientPlayer);
      }

      ctx.setAttribute("out", out);
      ctx.setAttribute("in", in);

      Thread requestThread;
      while (true) {
        requestThread = new RequestHandler(userDao, historyDao);
        requestThread.start();
        requestThread.join();

        String command = in.readUTF();
        if (command.equals("0")) {
          try {
            socket.close();
          } catch (Exception ignored) {

          }
          break;
        }

        if (command.equals("1")) {
          GameCommand.start();
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

  class RequestHandler extends Thread {

    private UserDao userDao;
    private HistoryDao historyDao;

    public RequestHandler(UserDao userDao, HistoryDao historyDao) {
      this.userDao = userDao;
      this.historyDao = historyDao;
    }

    @Override
    public void run() {
      try {

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
              setWinLose(clientPlayer, serverPlayer);
              break;
            }

          } else {
            turn = 1;

            GameCommand.move("x");
            player = GameCommand.check(serverPlayerName);
            if(Objects.equals(player, serverPlayerName)) {
              setWinLose(serverPlayer, clientPlayer);
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
  }
}

