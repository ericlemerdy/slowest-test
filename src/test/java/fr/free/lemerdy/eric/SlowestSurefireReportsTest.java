package fr.free.lemerdy.eric;

import static com.google.common.io.Resources.getResource;
import static org.fest.assertions.Assertions.assertThat;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

public class SlowestSurefireReportsTest {
  @Test
  public void with_target_containing_supported_test_report_should_order_test_results_by_time() throws URISyntaxException {
    String targetWithReports = new File(getResource("target_with_reports").toURI()).getAbsolutePath();
    
    SlowestSurefireReports slowestSurefireReports = new SlowestSurefireReports(targetWithReports);
    List<TestReport> testReports = slowestSurefireReports.readSlowestTests();

    assertThat(testReports.size()).isEqualTo(4);
    Iterator<TestReport> testReportsIterator = testReports.iterator();
    TestReport testReport = testReportsIterator.next();
    assertThat(testReport.time).isEqualTo(11d);
    assertThat(testReport.classname).isEqualTo("fr.PlainTest");
    assertThat(testReport.name).isEqualTo("should_test_plain_1");
    testReport = testReportsIterator.next();
    assertThat(testReport.time).isEqualTo(10.001d);
    assertThat(testReport.classname).isEqualTo("fr.FullTest");
    assertThat(testReport.name).isEqualTo("should_test_full_1");
    testReport = testReportsIterator.next();
    assertThat(testReport.time).isEqualTo(1.01d);
    assertThat(testReport.classname).isEqualTo("fr.ITTest");
    assertThat(testReport.name).isEqualTo("should_test_it_1");
    testReport = testReportsIterator.next();
    assertThat(testReport.time).isEqualTo(0.011d);
    assertThat(testReport.classname).isEqualTo("fr.PlainTest");
    assertThat(testReport.name).isEqualTo("should_test_plain_2");
    assertThat(testReportsIterator.hasNext()).isFalse();
  }

}
