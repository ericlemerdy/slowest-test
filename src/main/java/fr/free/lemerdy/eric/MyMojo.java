package fr.free.lemerdy.eric;

import java.io.File;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

/**
 * @goal print
 * @phase verify
 */
public class MyMojo extends AbstractMojo {
  /**
   * @parameter expression="${project.build.directory}"
   * @required
   */
  private File outputDirectory;

  public void execute() throws MojoExecutionException {
    List<TestReport> readSlowestTests = new SlowestSurefireReports(outputDirectory.getAbsolutePath()).readSlowestTests();
    for (TestReport testReport : readSlowestTests) {
      getLog().info(testReport.toString());
    }
  }
}
