package fr.free.lemerdy.eric;

public class TestReport implements Comparable<TestReport> {

  public Double time;
  public String classname;
  public String name;

  @Override
  public int compareTo(TestReport o) {
    return time.compareTo(o.time);
  }

}
