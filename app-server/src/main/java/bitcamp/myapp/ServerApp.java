package bitcamp.myapp;

import bitcamp.myapp.command.GameCommand;
import bitcamp.context.ApplicationContext;
import bitcamp.listener.ApplicationListener;
import bitcamp.myapp.dao.HistoryDao;
import bitcamp.myapp.dao.UserDao;
import bitcamp.myapp.dao.skel.HistoryDaoSkel;
import bitcamp.myapp.dao.skel.UserDaoSkel;
import bitcamp.myapp.listener.InitApplicationListener;
import bitcamp.myapp.vo.User;
import bitcamp.util.Prompt;
import java.io.ObjectInputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServerApp {

  List<ApplicationListener> listeners = new ArrayList<>();
  ApplicationContext appCtx = new ApplicationContext();

  public static void main(String[] args) {
    ServerApp app = new ServerApp();

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
        listener.onStart(appCtx);
      } catch (Exception e) {
        System.out.println("리스너 실행 중 오류 발생!");
      }
    }

    // 서버에서 사용할 Dao Skeloton 객체를 준비한다.
    UserDaoSkel userDaoSkel = (UserDaoSkel) appCtx.getAttribute("userDaoSkel");
    UserDao userDao = (UserDao) appCtx.getAttribute("userDao");
    HistoryDaoSkel historyDaoSkel = (HistoryDaoSkel) appCtx.getAttribute("historyDaoSkel");
    HistoryDao historyDao = (HistoryDao) appCtx.getAttribute("historyDao");

    System.out.println("Welcome to TicTacToe");
    String playerName = Prompt.input("플레이어 :");
    User serverPlayer;

    // 서버 플레이어 이름 등록
    try {
      serverPlayer = userDao.findByName(playerName);

      if(serverPlayer == null) {
        serverPlayer = new User(playerName);
        userDao.insert(serverPlayer);
      }

      appCtx.setAttribute("serverPlayer", playerName);

    } catch (Exception e) {
      System.out.println("플레이어 등록 중 오류 발생!");
    }


    try (ServerSocket serverSocket = new ServerSocket(8888);) {

        GameCommand.start();
        System.out.println("게임 시작 대기 중...");

        System.out.println("클라이언트의 연결을 기다림!");
        Socket socket = serverSocket.accept();
        InetSocketAddress remoteAddr = (InetSocketAddress) socket.getRemoteSocketAddress();
        System.out.printf("클라이언트(%s:%d)가 연결되었음!\n", //
            remoteAddr.getAddress(), remoteAddr.getPort());

        RequestHandler requestHandler = new RequestHandler(socket, userDao, historyDao, appCtx);
        requestHandler.start();
        requestHandler.join();



    } catch (Exception e) {
      System.out.println("통신 중 오류 발생!");
      e.printStackTrace();
    }

    System.out.println("종료합니다.");

    for (ApplicationListener listener : listeners) {
      try {
        listener.onShutdown(appCtx);
      } catch (Exception e) {
        System.out.println("리스너 실행 중 오류 발생!");
      }
    }
  }
}
