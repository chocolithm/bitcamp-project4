package bitcamp.myapp.listener;

import bitcamp.context.ApplicationContext;
import bitcamp.listener.ApplicationListener;
import bitcamp.myapp.dao.ListUserDao;
import bitcamp.myapp.dao.UserDao;
import bitcamp.myapp.dao.skel.UserDaoSkel;

public class InitApplicationListener implements ApplicationListener {

  UserDao userDao;

  @Override
  public void onStart(ApplicationContext ctx) throws Exception {
    userDao = new ListUserDao("data.xlsx");

    UserDaoSkel userDaoSkel = new UserDaoSkel(userDao);

    ctx.setAttribute("userDao", userDao);
    ctx.setAttribute("userDaoSkel", userDaoSkel);
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
  }
}
