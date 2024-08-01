package bitcamp.context;

import java.util.HashMap;
import java.util.Map;

public class ApplicationContext {

  Map<String, Object> objContainer = new HashMap<>();

  public void setAttribute(String name, Object value) {
    objContainer.put(name, value);
  }

  public Object getAttribute(String name) {
    return objContainer.get(name);
  }


}
