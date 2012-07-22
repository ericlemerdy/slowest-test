package fr.free.lemerdy.eric;

import static com.google.common.collect.Lists.newArrayList;
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
    String targetWithReports = new File(getResource("root_with_reports_deep_hierarchy").toURI()).getAbsolutePath();
    
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

  @Test
  public void with_folders_containing_surefire_test_report_should_order_test_results_by_time() throws URISyntaxException {
    File surefireTestFolder = new File(getResource("root_with_reports_deep_hierarchy/a-module/target").toURI());
    
    SlowestSurefireReports slowestSurefireReports = new SlowestSurefireReports(newArrayList(surefireTestFolder));
    List<TestReport> testReports = slowestSurefireReports.readSlowestTests();

    assertThat(testReports.size()).isEqualTo(2);
    Iterator<TestReport> testReportsIterator = testReports.iterator();
    TestReport testReport = testReportsIterator.next();
    assertThat(testReport.time).isEqualTo(10.001d);
    assertThat(testReport.classname).isEqualTo("fr.FullTest");
    assertThat(testReport.name).isEqualTo("should_test_full_1");
    testReport = testReportsIterator.next();
    assertThat(testReport.time).isEqualTo(1.01d);
    assertThat(testReport.classname).isEqualTo("fr.ITTest");
    assertThat(testReport.name).isEqualTo("should_test_it_1");
    assertThat(testReportsIterator.hasNext()).isFalse();
  }
  
  @Test
  public void with_multimodule_project_should_produce_report() throws Exception {
    File module1SurefireReports = new File(getResource("root_with_maven_projects/module1/target").toURI());
    File module2SurefireReports = new File(getResource("root_with_maven_projects/module2/target").toURI());
    
    SlowestSurefireReports slowestSurefireReports = new SlowestSurefireReports(newArrayList(module1SurefireReports, module2SurefireReports));
    List<TestReport> testReports = slowestSurefireReports.readSlowestTests();

    assertThat(testReports).hasSize(2);
    Iterator<TestReport> testReportsIterator = testReports.iterator();
    TestReport testReport = testReportsIterator.next();
    assertThat(testReport.time).isEqualTo(0.003d);
    assertThat(testReport.classname).isEqualTo("App2Test");
    assertThat(testReport.name).isEqualTo("testApp");
    testReport = testReportsIterator.next();
    assertThat(testReport.time).isEqualTo(0.002d);
    assertThat(testReport.classname).isEqualTo("App1Test");
    assertThat(testReport.name).isEqualTo("testApp");
    assertThat(testReportsIterator.hasNext()).isFalse();
  }

}
