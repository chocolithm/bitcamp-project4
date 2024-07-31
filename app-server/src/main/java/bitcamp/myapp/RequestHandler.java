package bitcamp.myapp;

import bitcamp.command.PracticeGame;
import bitcamp.myapp.dao.UserDao;
import bitcamp.myapp.vo.User;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class RequestHandler extends Thread {

  private Socket socket;
  private UserDao userDao;

  public RequestHandler(Socket socket, UserDao userDao) {
    this.socket = socket;
    this.userDao = userDao;
  }

  @Override
  public void run() {
    try (Socket socket2 = socket) {
      ObjectOutputStream out = new ObjectOutputStream(socket2.getOutputStream());
      ObjectInputStream in = new ObjectInputStream(socket2.getInputStream());

      // 클라이언트 플레이어 이름 등록
      String clientPlayerName = in.readUTF();
      User clientPlayer = userDao.findByName(clientPlayerName);
      if(clientPlayer == null) {
        clientPlayer = new User(clientPlayerName);
        userDao.insert(clientPlayer);
      }

      System.out.println("게임 시작!");

      while (true) {

        System.out.println(PracticeGame.getMap());
        out.writeUTF(PracticeGame.getMap());
        out.writeObject(PracticeGame.gameMap);
        out.flush();

        int clientMove = in.readInt();
        PracticeGame.set(clientMove, "o");
        if(PracticeGame.check()) {
          out.writeUTF("game over");
          out.writeUTF(PracticeGame.getMap());
          out.flush();
          System.out.println(PracticeGame.getMap());
          System.out.println("게임 오버");
          break;
        }
        out.writeUTF("continue");
        out.flush();


        System.out.println(PracticeGame.getMap());
        out.writeUTF(PracticeGame.getMap());
        out.writeObject(PracticeGame.gameMap);
        out.flush();

        PracticeGame.move("x");
        if(PracticeGame.check()) {
          out.writeUTF("game over");
          out.writeUTF(PracticeGame.getMap());
          out.flush();
          System.out.println(PracticeGame.getMap());
          System.out.println("게임 오버");
          break;
        }
        out.writeUTF("continue");
        out.flush();
      }
    } catch (Exception e) {
      System.out.println("클라이언트 요청 처리 중 오류 발생!");

    } finally {
      System.out.println("클라이언트 연결 종료!");
    }
  }
}


