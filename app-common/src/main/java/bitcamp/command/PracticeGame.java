package bitcamp.command;

import bitcamp.myapp.vo.User;
import bitcamp.util.Prompt;

import java.util.HashMap;
import java.util.Map;

public class PracticeGame {

  public static Map<Integer, String> gameMap = new HashMap<>();


  public static void main(String[] args) {

    for (int i = 1; i <= 9; i++) {
      gameMap.put(i, String.valueOf(i));
    }

    print();
    while (true) {

      String my = Prompt.input("다음 수를 입력하세요(1~9) :");
      if (my.equals("x")) {
        break;
      }

      try {
        int number = Integer.parseInt(my);
        if (gameMap.get(number).equals("x")) {
          System.out.println("이미 입력한 수입니다.");
          continue;
        }
        gameMap.put(number, "x");
        print();

      } catch (Exception e) {
        System.out.println("오류 발생!");
      }
    }



  }

  public static void print() {
    System.out.println("┏━━━━━┳━━━━━┳━━━━━┓");
    System.out.printf("┃  %s  ┃  %s  ┃  %s  ┃\n", gameMap.get(1), gameMap.get(2), gameMap.get(3));
    System.out.println("┣━━━━━╋━━━━━╋━━━━━┫");
    System.out.printf("┃  %s  ┃  %s  ┃  %s  ┃\n", gameMap.get(4), gameMap.get(5), gameMap.get(6));
    System.out.println("┣━━━━━╋━━━━━╋━━━━━┫");
    System.out.printf("┃  %s  ┃  %s  ┃  %s  ┃\n", gameMap.get(7), gameMap.get(8), gameMap.get(9));
    System.out.println("┗━━━━━┻━━━━━┻━━━━━┛");
  }



}
