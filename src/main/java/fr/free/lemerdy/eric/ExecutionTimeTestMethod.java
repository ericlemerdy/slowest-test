package fr.free.lemerdy.eric;

import static java.lang.String.format;

public class ExecutionTimeTestMethod implements Comparable<ExecutionTimeTestMethod> {

  public Double time;
  public String classname;
  public String methodName;

  public ExecutionTimeTestMethod(double time, String classname, String methodName) {
    this.time = time;
    this.classname = classname;
    this.methodName = methodName;
  }

  public ExecutionTimeTestMethod() {
  }

  @Override
  public int compareTo(ExecutionTimeTestMethod o) {
    return o.time.compareTo(time);
  }
  
  @Override
  public String toString() {
    return format("%10.3fms %s.%s", time, classname, methodName);
  }

}
