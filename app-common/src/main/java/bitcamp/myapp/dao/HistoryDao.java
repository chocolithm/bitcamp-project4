package bitcamp.myapp.dao;

import bitcamp.myapp.vo.History;
import bitcamp.myapp.vo.User;
import java.util.List;

public interface HistoryDao {

  boolean insert(History history) throws Exception;

  List<History> list(String userName) throws Exception;
}
