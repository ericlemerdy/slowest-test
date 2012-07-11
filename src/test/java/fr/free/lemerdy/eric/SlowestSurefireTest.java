package fr.free.lemerdy.eric;

import static org.fest.assertions.Assertions.assertThat;

import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

public class SlowestSurefireTest {
	@Test
    public void with_several_surefire_test_report_should_order_test_results_by_time() throws URISyntaxException
    {
		SlowestSurefireReports slowestSurefireReports = new SlowestSurefireReports();
		List<TestReport> testReports = slowestSurefireReports.slowestTests();
		
		assertThat(testReports.size()).isEqualTo(3);
		Iterator<TestReport> testReportsIterator = testReports.iterator();
		TestReport testReport = testReportsIterator.next();
        assertThat(testReport.time).isEqualTo(10.01d);
        assertThat(testReport.classname).isEqualTo("PlainTest");
        assertThat(testReport.name).isEqualTo("should_test_plain_1");
        testReport = testReportsIterator.next();
        assertThat(testReport.time).isEqualTo(1.001d);
        assertThat(testReport.classname).isEqualTo("PlainTest");
        assertThat(testReport.name).isEqualTo("should_test_plain_2");
        testReport = testReportsIterator.next();
        assertThat(testReport.time).isEqualTo(5.001d);
        assertThat(testReport.classname).isEqualTo("FullTest");
        assertThat(testReport.name).isEqualTo("should_test_full_1");
        assertThat(testReportsIterator.hasNext()).isFalse();
    }
	
}
