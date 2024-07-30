package bitcamp.command;

import bitcamp.myapp.vo.User;
import bitcamp.util.Ansi;
import bitcamp.util.Prompt;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

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

  ///////////////////////////////////////////////////////////////////////
  ///////////////////////////////////////////////////////////////////////
  ///////////////////////////////////////////////////////////////////////
  ///////////////////////////////////////////////////////////////////////


  public static void start() {
    for (int i = 1; i <= 9; i++) {
      gameMap.put(i, String.valueOf(i));
    }
  }

  public static String getMap() {
    StringBuilder str = new StringBuilder();

    str.append("┏━━━━━┳━━━━━┳━━━━━┓\n");
    str.append(String.format("┃  %s  ┃  %s  ┃  %s  ┃\n", gameMap.get(1), gameMap.get(2), gameMap.get(3)));
    str.append("┣━━━━━╋━━━━━╋━━━━━┫\n");
    str.append(String.format("┃  %s  ┃  %s  ┃  %s  ┃\n", gameMap.get(4), gameMap.get(5), gameMap.get(6)));
    str.append("┣━━━━━╋━━━━━╋━━━━━┫\n");
    str.append(String.format("┃  %s  ┃  %s  ┃  %s  ┃\n", gameMap.get(7), gameMap.get(8), gameMap.get(9)));
    str.append("┗━━━━━┻━━━━━┻━━━━━┛");

    return str.toString()
        .replaceAll("x", Ansi.BLUE + "x" + Ansi.RESET)
        .replaceAll("o", Ansi.RED + "o" + Ansi.RESET);
  }

  public static Integer move(String marker) {
    while (true) {

      try {
        int number = Prompt.inputInt("다음 수를 입력하세요(1~9) :");

        if(number < 1 || number > 9) {
          System.out.println("1~9 사이 숫자를 입력하세요.");
          continue;
        }

        if (gameMap.get(number).equals("x") || gameMap.get(number).equals("o")) {
          System.out.println("이미 입력한 수입니다.");
          continue;
        }
        gameMap.put(number, marker);
        return number;

      } catch (Exception e) {
        System.out.println("오류 발생!");
      }
    }
  }

  public static void set(Integer number, String marker) {
    gameMap.put(number, marker);
  }

  public static boolean check() {
    int[][] conditions = {
        {1, 2, 3},
        {4, 5, 6},
        {7, 8, 9},
        {1, 4, 7},
        {2, 5, 8},
        {3, 6, 9},
        {1, 5, 9},
        {3, 5, 7}
    };
    String regex = "[1-9]"; // 1-9 숫자 하나에 해당하는 정규 표현식
    Pattern pattern = Pattern.compile(regex);
    boolean draw = true;

    for (String value : gameMap.values()) {
      if (pattern.matcher(value).matches()) {
        draw = false;
        break;
      }
    }

    for(int[] condition : conditions) {
      String cell1 = gameMap.get(condition[0]);
      String cell2 = gameMap.get(condition[1]);
      String cell3 = gameMap.get(condition[2]);
      if (cell1.equals(cell2) && cell2.equals(cell3)) {
        return true;
      }
    }

    if(draw) {
      System.out.println("동점입니다");
      return true;
    }

    return false;
  }

}
