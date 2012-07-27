package fr.free.lemerdy.eric;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Lists.transform;
import static java.lang.String.format;

import java.io.File;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;

import com.google.common.base.Function;

/**
 * @phase reporting
 * @goal print
 * @aggregator true
 */
public class SlowestTestMojo extends AbstractMojo {
  
  /** @parameter expression="${project.collectedProjects}" */
  List<MavenProject> collectedProjects;

  public void execute() throws MojoExecutionException {
    MavenProject mavenProject = (MavenProject) getPluginContext().get("project");
    List<File> targetFiles = newArrayList(getTargetFolderFromMavenProject().apply(mavenProject));
    targetFiles.addAll(transform(collectedProjects, getTargetFolderFromMavenProject()));
    List<ExecutionTimeTestMethod> readSlowestTests = new SlowestSurefireReports(targetFiles).readSlowestTests();
    for (ExecutionTimeTestMethod testReport : readSlowestTests) {
      getLog().info(testReport.toString());
    }
  }

  private Function<MavenProject, File> getTargetFolderFromMavenProject() {
    return new Function<MavenProject, File>() {
      public File apply(MavenProject input) {
        getLog().debug(format("Scanning project %s...", input.getId()));
        return new File(format("%s%starget", input.getBasedir(), File.separator));
      }
    };
  }
}
