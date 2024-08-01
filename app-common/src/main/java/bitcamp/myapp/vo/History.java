package bitcamp.myapp.vo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class History implements Serializable {

  private static final long serialVersionUID = 1L;

  int no;
  String[] players;
  String winner;
  Date date;

  public History() {

  }

  public History(int no) {
    this.no = no;
  }

  public History(String[] players, String winner) {
    this.players = players;
    this.winner = winner;
    this.date = new Date();
  }

  public int getNo() {
    return no;
  }

  public void setNo(int no) {
    this.no = no;
  }

  public String[] getPlayers() {
    return players;
  }

  public void setPlayers(String[] players) {
    this.players = players;
  }

  public String getWinner() {
    return winner;
  }

  public void setWinner(String winner) {
    this.winner = winner;
  }

  public Date getDate() {
    return date;
  }

  public void setDate(Date date) {
    this.date = date;
  }
}
