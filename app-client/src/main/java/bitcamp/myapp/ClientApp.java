package bitcamp.myapp;

import bitcamp.context.ApplicationContext;
import bitcamp.listener.ApplicationListener;
import bitcamp.myapp.listener.InitApplicationListener;
import bitcamp.myapp.vo.User;
import bitcamp.util.Prompt;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ClientApp {

  List<ApplicationListener> listeners = new ArrayList<>();
  ApplicationContext appCtx = new ApplicationContext();

  public static void main(String[] args) {
    ClientApp app = new ClientApp();

    // 애플리케이션이 시작되거나 종료될 때 알림 받을 객체의 연락처를 등록한다.
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
      System.out.println("Welcome to TicTacToe");
      String playerName = Prompt.input("플레이어 :");

      Socket socket = new Socket("127.0.0.1", 8888);

      ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
      ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

      appCtx.setAttribute("inputStream", in);
      appCtx.setAttribute("outputStream", out);

      // 애플리케이션이 시작될 때 리스너에게 알린다.
      for (ApplicationListener listener : listeners) {
        try {
          listener.onStart(appCtx);
        } catch (Exception e) {
          System.out.println("리스너 실행 중 오류 발생!");
        }
      }
      
//      appCtx.getMainMenu().execute();

      out.writeUTF(playerName);
      out.flush();

      // 게임 실행
      while (true) {

        System.out.println("게임 시작!");
        Thread.sleep(1000);

        int turn = 1;
        String message;
        while (true) {

          System.out.println(in.readUTF());

          if (turn == 1) {
            turn = 2;

            while (true) {
              try {
                out.writeInt(Prompt.inputInt("다음 수를 입력하세요(1~9) :"));
                out.flush();
                message = in.readUTF();

                if (message.equals("OK")) {
                  break;
                } else {
                  System.out.println(message);
                }

              } catch (Exception e) {
                System.out.println("오류 발생!");
              }
            }

          } else {
            turn = 1;
            System.out.println("상대방 입력 대기 중...");
          }

          if (in.readUTF().equals("game over")) {
            break;
          }
        }


        System.out.println(in.readUTF());
        String player = in.readUTF();
        if (Objects.equals(player, "draw")) {
          System.out.println("무승부입니다.");
        } else {
          System.out.printf("승자 : %s\n", player);
        }
        User clientPlayer = (User) in.readObject();
        System.out.printf("내 전적 : %d승 %d무 %d패\n", clientPlayer.getWin(), clientPlayer.getDraw(),
            clientPlayer.getLose());
        System.out.println("게임 오버");


        System.out.println("[0] 종료  [1] 다시하기  [2] 전적보기");
        String command = Prompt.input("선택>");
        out.writeUTF(command);
        out.flush();
        if (command.equals("0")) {
          break;
        }

        if (command.equals("1")) {
          System.out.println("게임을 다시 시작합니다.");
          continue;
        }

        if (command.equals("2")) {
          System.out.println("준비중");
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
        listener.onShutdown(appCtx);
      } catch (Exception e) {
        System.out.println("리스너 실행 중 오류 발생!");
      }
    }
  }
}
