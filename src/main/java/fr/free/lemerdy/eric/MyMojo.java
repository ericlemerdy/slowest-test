package fr.free.lemerdy.eric;

import java.io.File;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

/**
 * @goal print
 * @aggregator true
 */
public class MyMojo extends AbstractMojo {
  /**
   * @parameter expression="${basedir}"
   * @required
   */
  private File basedir;

  public void execute() throws MojoExecutionException {
    List<TestReport> readSlowestTests = new SlowestSurefireReports(basedir.getAbsolutePath()).readSlowestTests();
    for (TestReport testReport : readSlowestTests) {
      getLog().info(testReport.toString());
    }
  }
}
