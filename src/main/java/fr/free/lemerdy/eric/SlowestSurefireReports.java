package fr.free.lemerdy.eric;

import static com.google.common.base.Objects.firstNonNull;
import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Lists.newArrayList;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.util.Collections;
import java.util.List;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;

public class SlowestSurefireReports {

  private static final FilenameFilter ONLY_SUPPORTED_TEST_RESULTS = new FilenameFilter() {
    @Override
    public boolean accept(File dir, String name) {
      return name.equals("surefire-reports") || name.equals("failsafe-reports");
    }
  };
  
  private static final FilenameFilter ONLY_TEST_RESULTS_FILES = new FilenameFilter() {
    @Override
    public boolean accept(File dir, String name) {
      return name.startsWith("TEST-") && name.endsWith(".xml");
    }
  };

  private static final FileFilter ONLY_DIRECTORIES = new FileFilter() {
    @Override
    public boolean accept(File pathname) {
      return pathname.isDirectory();
    }
  };
  
  private String targetPathname;

  private ParseTestsInFileReport parseTestsInFileReport;

  public SlowestSurefireReports(String targetPathname) {
    this.targetPathname = targetPathname;
    this.parseTestsInFileReport = new ParseTestsInFileReport();
  }

  public List<TestReport> readSlowestTests() {
    Iterable<File> testReportFiles = findSupportedReports(targetPathname);
    Iterable<List<TestReport>> testReportByFile = transform(testReportFiles, parseTestsInFileReport);
    List<TestReport> allTestReports = newArrayList(concat(testReportByFile));
    Collections.sort(allTestReports);
    return allTestReports;
  }

  private Iterable<File> findSupportedReports(String pathname) {
    File targetDirectory = new File(pathname);
    List<File> supportedReportDirectories = newArrayList(firstNonNull(targetDirectory.listFiles(ONLY_SUPPORTED_TEST_RESULTS), new File[]{}));
    Function<File, List<File>> allTestReportDir = new Function<File, List<File>>(){
      @Override
      public List<File> apply(File input) {
        return newArrayList(input.listFiles(ONLY_TEST_RESULTS_FILES));
      }
    };
    Iterable<List<File>> allReports = Iterables.transform(supportedReportDirectories, allTestReportDir);
    Iterable<File> testReportFiles = Iterables.<File>concat(allReports);
    File[] otherDirectories = firstNonNull(targetDirectory.listFiles(ONLY_DIRECTORIES), new File[]{});
    for (File otherDirectory : otherDirectories) {
      Iterable<File> otherSupportedReports = findSupportedReports(otherDirectory.getAbsolutePath());
      testReportFiles = Iterables.concat(testReportFiles, otherSupportedReports);
    }
    return testReportFiles;
  }

}
