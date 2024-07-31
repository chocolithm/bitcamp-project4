package bitcamp.myapp.listener;

import bitcamp.command.history.HistoryListCommand;
import bitcamp.context.ApplicationContext;
import bitcamp.listener.ApplicationListener;
import bitcamp.menu.MenuGroup;
import bitcamp.menu.MenuItem;
import bitcamp.myapp.dao.HistoryDao;
import bitcamp.myapp.dao.UserDao;
import bitcamp.myapp.dao.stub.HistoryDaoStub;
import bitcamp.myapp.dao.stub.UserDaoStub;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class InitApplicationListener implements ApplicationListener {

  UserDao userDao;
  HistoryDao historyDao;

  @Override
  public void onStart(ApplicationContext ctx) throws Exception {

    ObjectInputStream in = (ObjectInputStream) ctx.getAttribute("inputStream");
    ObjectOutputStream out = (ObjectOutputStream) ctx.getAttribute("outputStream");
    String playerName = (String) ctx.getAttribute("playerName");

    userDao = new UserDaoStub(in, out, "users");
    historyDao = new HistoryDaoStub(in, out, "histories");

    MenuGroup mainMenu = ctx.getMainMenu();
    mainMenu.add(new MenuItem("게임하기", null));
    mainMenu.add(new MenuItem("전적보기", new HistoryListCommand(historyDao, playerName)));

    mainMenu.setExitMenuTitle("종료");
  }
}
