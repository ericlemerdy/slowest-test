package fr.free.lemerdy.eric;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Test;

public class ExecutionTimeTestMethodTest {
  @Test
  public void natural_order_should_be_time_desc() throws Exception {
    ExecutionTimeTestMethod testReportFaster = new ExecutionTimeTestMethod();
    testReportFaster.time = 1d;
    ExecutionTimeTestMethod testReportLonger = new ExecutionTimeTestMethod();
    testReportLonger.time = 2d;

    int comparison = testReportFaster.compareTo(testReportLonger);

    assertThat(comparison).isGreaterThan(0);
  }

  @Test
  public void should_display_time_classname_and_test() throws Exception {
    ExecutionTimeTestMethod testReport = new ExecutionTimeTestMethod(1.56d, "fr.test.Test", "should_test_something");

    String display = testReport.toString();

    assertThat(display).isEqualTo("     1,560ms fr.test.Test.should_test_something");
  }

  @Test
  public void constructor_should_set_time_classname_and_test() throws Exception {
    ExecutionTimeTestMethod testReport = new ExecutionTimeTestMethod(42.1d, "exemple.com.TestClass", "testMethod");

    assertThat(testReport.time).isEqualTo(42.1d);
    assertThat(testReport.classname).isEqualTo("exemple.com.TestClass");
    assertThat(testReport.methodName).isEqualTo("testMethod");
  }
}
