package bitcamp.myapp.listener;

import bitcamp.context.ApplicationContext;
import bitcamp.listener.ApplicationListener;
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

    userDao = new UserDaoStub(in, out, "users");
    historyDao = new HistoryDaoStub(in, out, "histories");
  }
}
