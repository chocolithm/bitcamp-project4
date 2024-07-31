package bitcamp.myapp.listener;

import bitcamp.context.ApplicationContext;
import bitcamp.listener.ApplicationListener;
import bitcamp.myapp.dao.HistoryDao;
import bitcamp.myapp.dao.ListHistoryDao;
import bitcamp.myapp.dao.ListUserDao;
import bitcamp.myapp.dao.UserDao;
import bitcamp.myapp.dao.skel.HistoryDaoSkel;
import bitcamp.myapp.dao.skel.UserDaoSkel;
import bitcamp.myapp.vo.History;

public class InitApplicationListener implements ApplicationListener {

  UserDao userDao;
  HistoryDao historyDao;

  @Override
  public void onStart(ApplicationContext ctx) throws Exception {
    userDao = new ListUserDao("data.xlsx");
    historyDao = new ListHistoryDao("data.xlsx");

    UserDaoSkel userDaoSkel = new UserDaoSkel(userDao);
    HistoryDaoSkel historyDaoSkel = new HistoryDaoSkel(historyDao);

    ctx.setAttribute("userDao", userDao);
    ctx.setAttribute("userDaoSkel", userDaoSkel);
    ctx.setAttribute("historyDao", historyDao);
    ctx.setAttribute("historyDaoSkel", historyDaoSkel);
  }

  @Override
  public void onShutdown(ApplicationContext ctx) throws Exception {
    try {
      ((ListUserDao) userDao).save();
    } catch (Exception e) {
      System.out.println("회원 데이터 저장 중 오류 발생!");
      e.printStackTrace();
      System.out.println();
    }

    try {
      ((ListHistoryDao) historyDao).save();
    } catch (Exception e) {
      System.out.println("전적 데이터 저장 중 오류 발생!");
      e.printStackTrace();
      System.out.println();
    }
  }
}
