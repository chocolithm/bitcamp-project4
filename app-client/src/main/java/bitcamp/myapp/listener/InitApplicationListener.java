package bitcamp.myapp.listener;

import bitcamp.context.ApplicationContext;
import bitcamp.listener.ApplicationListener;
import bitcamp.menu.MenuGroup;
import bitcamp.menu.MenuItem;
import bitcamp.myapp.command.user.UserAddCommand;
import bitcamp.myapp.command.user.UserDeleteCommand;
import bitcamp.myapp.command.user.UserListCommand;
import bitcamp.myapp.command.user.UserUpdateCommand;
import bitcamp.myapp.command.user.UserViewCommand;
import bitcamp.myapp.dao.UserDao;
import bitcamp.myapp.dao.stub.UserDaoStub;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class InitApplicationListener implements ApplicationListener {

  UserDao userDao;

  @Override
  public void onStart(ApplicationContext ctx) throws Exception {

    ObjectInputStream in = (ObjectInputStream) ctx.getAttribute("inputStream");
    ObjectOutputStream out = (ObjectOutputStream) ctx.getAttribute("outputStream");

    userDao = new UserDaoStub(in, out, "users");

    MenuGroup mainMenu = ctx.getMainMenu();

    MenuGroup userMenu = new MenuGroup("회원");
    userMenu.add(new MenuItem("등록", new UserAddCommand(userDao)));
    userMenu.add(new MenuItem("목록", new UserListCommand(userDao)));
    userMenu.add(new MenuItem("조회", new UserViewCommand(userDao)));
    userMenu.add(new MenuItem("변경", new UserUpdateCommand(userDao)));
    userMenu.add(new MenuItem("삭제", new UserDeleteCommand(userDao)));
    mainMenu.add(userMenu);

    mainMenu.setExitMenuTitle("종료");
  }
}
