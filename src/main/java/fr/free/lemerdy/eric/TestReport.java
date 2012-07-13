package fr.free.lemerdy.eric;

import static java.lang.String.format;

public class TestReport implements Comparable<TestReport> {

  public Double time;
  public String classname;
  public String name;

  @Override
  public int compareTo(TestReport o) {
    return time.compareTo(o.time);
  }
  
  @Override
  public String toString() {
    return format("%10.3fms %s.%s", time, classname, name);
  }

}
