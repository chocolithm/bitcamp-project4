package bitcamp.listener;

import bitcamp.context.ApplicationContext;

public class StartApplicationListener implements ApplicationListener {

  private static String boldAnsi = "\033[1m";
  private static String blueAnsi = "\033[34m";
  private static String redAnsi = "\033[31m";
  private static String resetAnsi = "\033[0m";


  @Override
  public void onStart(ApplicationContext ctx) throws Exception {
    System.out.println();
    System.out.println("⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢰⣷⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣾⡆⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀");
    System.out.println("⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢸⣿⠀⠀⣠⣴⣾⢿⢿⣾⣦⣄⠀⠀⣿⡇⠀⠀⣠⣶⣷⢿⢿⣶⣦⣀⠀⠀⠀");
    System.out.println("⠀⠀⠀⠀⠀⠺⣿⣤⠀⠀⢀⣴⡿⠇⠀⠀⢸⣿⠀⣼⡿⠋⠀⠀⠀⠀⠙⢿⣦⠀⣿⡇⢀⣾⡿⠉⠀⠀⠀ ⠈⠙⣿⣦⠀");
    System.out.println("⠀⠀⠀⠀⠀⠀⠈⠻⣿⣴⣿⠏⠁⠀⠀⠀⢸⣿⢸⣿⡁⠀⠀⠀⠀⠀⠀⢘⣿⡅⣿⡇⢸⣿⠀⠀⠀⠀⠀  ⠀⠀⢸⣿⠄");
    System.out.println("⠀⠀⠀⠀⠀⠀⢀⣴⣿⠿⣷⣆⡀⠀⠀⠀⢸⣿⠐⣿⣆⠀⠀⠀⠀⠀⠀⣴⣿⠁⣿⡇⠸⣿⣆⠀⠀⠀⠀  ⠀⠀⣼⡿⠀⠀");
    System.out.println("⠀⠀⠀⠀⠀⢴⣿⠟⠁⠀⠈⠻⣷⡆⠀⠀⢸⣿⠀⠘⠿⣷⣤⣄⣤⣴⣾⠿⠁⠀⣿⡇⠀⠙⢿⣶⣤⣄⣤⣴⣿⠟⠁⠀⠀");
    System.out.println("⠀⠀⢀⣀⣈⣀⣀⣀⣀⣀⣀⣈⣀⣀⣀⣜⣿⣀⣀⣀⣉⣙⣙⣙⣩⣈⣀⣀⣠⣿⣇⣀⣀⣀⣈⣙⣙⣙⣩⣀⣀⣄⣀⡀");
    System.out.println("⠀⠀⠙⠛⠛⠛⠛⢛⣛⣛⠛⠛⠛⠛⠛⢻⣿⠛⠛⠛⠛⢛⣛⣛⠛⠛⠛⠛⠛⣿⡛⠛⠛⠛⠛⠛⠛⠛⠛⠛⠛⠛⠛⠋");
    System.out.println("⠀⠀⠀⠀⠀⢀⣴⡿⡟⠟⠻⢻⣷⣦⡀⠀⢸⣿⠀⢀⣴⣿⠟⠟⠻⠻⣷⣦⠀⠀⣿⡇⠀⠀⣴⣄⠀⠀⠀⠀⣠⣦⠄⠀⠀");
    System.out.println("⠀⠀⠀⠀⢀⣾⡟⠁⠀⠀⠀⠀⠈⢻⣷⡀⢸⣿⢀⣿⡟⠀⠀⠀⠀⠀⠈⢻⣷⡀⣿⡇⠀⠀⠉⢿⣷⣄⣠⣾⡿⠋⠀⠀⠀");
    System.out.println("⠀⠀⠀⠀⠸⣿⡁⠀⠀⠀⠀⠀⠀⢨⣿⡃⢸⣿⠸⣿⠄⠀⠀⠀⠀⠀⠀⠨⣿⡆⣿⡇⠀⠀⠀⠀⣸⣿⣿⣏⠀⠀⠀⠀⠀");
    System.out.println("⠀⠀⠀⠀⠈⢿⣧⡀⠀⠀⠀⠀⢀⣼⡿⠁⢸⣿⠈⢿⣧⡀⠀⠀⠀⠀⢀⣾⡿⠀⣿⡇⠀⠀⢠⣾⡿⠋⠘⢻⣷⣄⠀⠀⠀");
    System.out.println("⠀ ⠀⠀⠀⠈⠛⢿⣶⣦⣦⣾⡿⠋⠁⠀⢸⣿⠀⠈⠛⢿⣶⣦⣶⣾⠿⠛⠀⠀⣿⡇⠀⠀⠻⠋⠀⠀⠀⠀⠉⠟⠁⠀⠀");
    System.out.println("⠀⠀⣠⣤⣤⣤⣤⣦⣭⣥⣥⣤⣴⣤⣤⣮⣿⣤⣤⣤⣦⣤⣭⣥⣴⣴⣤⣦⣴⣿⣧⣤⣤⣤⣤⣤⣤⣤⣤⣤⣤⣤⣤⣄");
    System.out.println("⠀⠀⠈⠉⠉⠉⠁⠉⠉⠉⠉⠉⠈⠉⠁⢹⣿⠉⠉⠁⠉⠉⠉⠉⠈⠁⠉⠈⠉⣿⡍⠈⠉⠉⠉⠉⠉⠉⠉⠉⠉⠉⠉⠀");
    System.out.println("⠀⠀⠀⠀⠀⢴⣦⣀⠀⠀⠀⢠⣴⡆⠀⠀⢸⣿⠀⠀⣠⣶⡿⠿⠿⣿⣶⣄⠀⠀⣿⡇⠀⠀⣶⣆⡀⠀⠀⠀⣠⣶⠄⠀⠀");
    System.out.println("⠀⠀⠀⠀⠀⠀⠛⢿⣦⣄⣾⡿⠋⠀⠀⠀⢸⣿⠀⣾⡿⠉⠀⠀⠀⠀⠉⢿⣧⠐⣿⡅⠀⠀⠈⠻⣷⣆⣤⣾⠿⠉⠀⠀⠀");
    System.out.println("⠀⠀⠀⠀⠀⠀⠀⢠⣿⣿⣯⡄⠀⠀⠀⠀⢸⣿⢸⣿⡁⠀⠀⠀⠀⠀⠀⠨⣿⡇⣿⡇⠀⠀⠀⢀⣸⣿⣿⣇⡀⠀⠀⠀⠀");
    System.out.println("⠀⠀⠀⠀⠀⢠⣼⡿⠋⠀⠹⢿⣶⡀⠀⠀⢸⣿⠐⣿⣦⠀⠀⠀⠀⠀⠀⣼⣿⠀⣿⡇⠀⠀⣰⣾⠟⠁⠈⠻⣷⣦⠀⠀⠀");
    System.out.println("⠀⠀⠀⠀⠀⠙⠋⠀⠀⠀⠀⠀⠛⠃⠀⠀⢸⣿⠀⠈⠿⣷⣦⣤⣤⣴⣿⠟⠁⠀⣿⡇⠀⠀⠛⠁⠀⠀⠀⠀⠈⠛⠁⠀⠀");
    System.out.println(" ⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠸⡿⠀⠀⠀⠈⠈⠋⠙⠉⠀⠀⠀⠀⢿⠃⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀ ");
    System.out.println();
    System.out.println();
    System.out.println(boldAnsi + blueAnsi + "                    Welcome to TIC TAC TOE GAME!                " + resetAnsi);
    System.out.println();
    System.out.println();

  }

}
