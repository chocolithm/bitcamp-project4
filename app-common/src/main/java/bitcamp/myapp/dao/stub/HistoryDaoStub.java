package bitcamp.myapp.dao.stub;

import bitcamp.myapp.dao.HistoryDao;
import bitcamp.myapp.dao.UserDao;
import bitcamp.myapp.vo.History;
import bitcamp.myapp.vo.User;
import bitcamp.net.ResponseStatus;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

public class HistoryDaoStub implements HistoryDao {

  private ObjectInputStream in;
  private ObjectOutputStream out;
  private String dataName;

  public HistoryDaoStub(ObjectInputStream in, ObjectOutputStream out, String dataName)
      throws Exception {
    this.in = in;
    this.out = out;
    this.dataName = dataName;
  }

  @Override
  public boolean insert(History history) throws Exception {
    out.writeUTF(dataName);
    out.writeUTF("insert");
    out.writeObject(history);
    out.flush();

    if (in.readUTF().equals(ResponseStatus.SUCCESS)) {
      return true;
    }

    return false;
  }

  @Override
  public List<History> list(String userName) throws Exception {
    out.writeUTF(dataName);
    out.writeUTF("list");
    out.writeUTF(userName);
    out.flush();

    if (in.readUTF().equals(ResponseStatus.SUCCESS)) {
      return (List<History>) in.readObject();
    }

    return null;
  }
}
