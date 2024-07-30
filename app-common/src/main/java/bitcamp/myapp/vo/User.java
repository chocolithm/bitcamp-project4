package bitcamp.myapp.vo;

import java.io.Serializable;
import java.util.Objects;

public class User implements Serializable {

  private static final long serialVersionUID = 1L;

  private int no;
  private String name;
  private int win;
  private int lose;

  public User() {
  }

  public User(int no) {
    this.no = no;
  }

  public User(String name) {
    this.name = name;
    this.win = 0;
    this.lose = 0;
  }

  @Override
  public String toString() {
    return "User{" +
        "no=" + no +
        ", name='" + name + '\'' +
        ", win=" + win +
        ", lose=" + lose +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    User user = (User) o;
    return no == user.no;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(no);
  }

  public int getNo() {
    return no;
  }

  public void setNo(int no) {
    this.no = no;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getWin() {
    return win;
  }

  public void setWin(int win) {
    this.win = win;
  }

  public int getLose() {
    return lose;
  }

  public void setLose(int lose) {
    this.lose = lose;
  }
}
