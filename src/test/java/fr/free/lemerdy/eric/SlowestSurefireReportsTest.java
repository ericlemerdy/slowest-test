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
    List<ExecutionTimeTestMethod> testReports = slowestSurefireReports.readSlowestTests();

    assertThat(testReports).hasSize(4);
    Iterator<ExecutionTimeTestMethod> testReportsIterator = testReports.iterator();
    assertThat(testReportsIterator.next().toString()).isEqualTo("    11,000ms fr.PlainTest.should_test_plain_1");
    assertThat(testReportsIterator.next().toString()).isEqualTo("    10,001ms fr.FullTest.should_test_full_1");
    assertThat(testReportsIterator.next().toString()).isEqualTo("     1,010ms fr.ITTest.should_test_it_1");
    assertThat(testReportsIterator.next().toString()).isEqualTo("     0,011ms fr.PlainTest.should_test_plain_2");
    assertThat(testReportsIterator.hasNext()).isFalse();
  }

  @Test
  public void with_folders_containing_surefire_test_report_should_order_test_results_by_time() throws URISyntaxException {
    File surefireTestFolder = new File(getResource("root_with_reports_deep_hierarchy/a-module/target").toURI());
    
    SlowestSurefireReports slowestSurefireReports = new SlowestSurefireReports(newArrayList(surefireTestFolder));
    List<ExecutionTimeTestMethod> testReports = slowestSurefireReports.readSlowestTests();

    assertThat(testReports).hasSize(2);
    Iterator<ExecutionTimeTestMethod> testReportsIterator = testReports.iterator();
    assertThat(testReportsIterator.next().toString()).isEqualTo("    10,001ms fr.FullTest.should_test_full_1");
    assertThat(testReportsIterator.next().toString()).isEqualTo("     1,010ms fr.ITTest.should_test_it_1");
    assertThat(testReportsIterator.hasNext()).isFalse();
  }
  
  @Test
  public void with_multimodule_project_should_produce_report() throws Exception {
    File module1SurefireReports = new File(getResource("root_with_maven_projects/module1/target").toURI());
    File module2SurefireReports = new File(getResource("root_with_maven_projects/module2/target").toURI());
    
    SlowestSurefireReports slowestSurefireReports = new SlowestSurefireReports(newArrayList(module1SurefireReports, module2SurefireReports));
    List<ExecutionTimeTestMethod> testReports = slowestSurefireReports.readSlowestTests();

    assertThat(testReports).hasSize(2);
    Iterator<ExecutionTimeTestMethod> testReportsIterator = testReports.iterator();
    assertThat(testReportsIterator.next().toString()).isEqualTo("     0,003ms App2Test.testApp");
    assertThat(testReportsIterator.next().toString()).isEqualTo("     0,002ms App1Test.testApp");
    assertThat(testReportsIterator.hasNext()).isFalse();
  }

}
