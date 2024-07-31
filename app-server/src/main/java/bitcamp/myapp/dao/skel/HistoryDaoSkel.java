package bitcamp.myapp.dao.skel;

import bitcamp.myapp.dao.HistoryDao;
import bitcamp.myapp.vo.History;
import bitcamp.myapp.vo.User;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import static bitcamp.net.ResponseStatus.ERROR;
import static bitcamp.net.ResponseStatus.SUCCESS;

public class HistoryDaoSkel {

  private HistoryDao historyDao;

  public HistoryDaoSkel(HistoryDao historyDao) {
    this.historyDao = historyDao;
  }

  public void service(ObjectInputStream in, ObjectOutputStream out) throws Exception {
    String command = in.readUTF();

    History history = null;
    int no = 0;

    switch (command) {
      case "insert":
        history = (History) in.readObject();
        historyDao.insert(history);
        out.writeUTF(SUCCESS);
        break;
      case "list":
        List<History> list = historyDao.list(in.readUTF());
        out.writeUTF(SUCCESS);
        out.writeObject(list);
        break;
      default:
        out.writeUTF(ERROR);
        out.writeUTF("무효한 명령입니다.");
    }

    out.flush();
  }

}
