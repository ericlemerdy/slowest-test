package fr.free.lemerdy.eric;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Test;

public class TestReportTest {
  @Test
  public void natural_order_should_be_time_desc() throws Exception {
    TestReport testReportBefore = new TestReport();
    testReportBefore.time = 1d;
    TestReport testReportAfter = new TestReport();
    testReportAfter.time = 2d;
    
    int comparison = testReportBefore.compareTo(testReportAfter);
    
    assertThat(comparison).isLessThan(0);
  }
  @Test
  public void test_report_should_display_time_classname_and_test() throws Exception {
    TestReport testReport = new TestReport();
    testReport.time = 1.56d;
    testReport.classname = "fr.test.Test";
    testReport.name = "should_test_something";
    
    String display = testReport.toString();
    
    assertThat(display).isEqualTo("     1,560ms fr.test.Test.should_test_something");
  }
}
