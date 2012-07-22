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
  
  private ParseTestsInFileReport parseTestsInFileReport;

  private Iterable<File> foldersWithTestReports;
  
  private SlowestSurefireReports() {
    this.parseTestsInFileReport = new ParseTestsInFileReport();
    }

  public SlowestSurefireReports(String targetPathname) {
    this();
    this.foldersWithTestReports = findAllSupportedReports(targetPathname);
  }

  public SlowestSurefireReports(Iterable<File> foldersWithReports) {
    this();
    this.foldersWithTestReports = foldersWithReports;
  }

  public List<TestReport> readSlowestTests() {
    Iterable<File> supportedReports = filterSupportedReports(foldersWithTestReports);
    Iterable<List<TestReport>> testReportByFile = transform(supportedReports, parseTestsInFileReport);
    List<TestReport> allTestReports = newArrayList(concat(testReportByFile));
    Collections.sort(allTestReports);
    return allTestReports;
  }

  private Iterable<File> filterSupportedReports(Iterable<File> foldersWithTestReport) {
    Function<File, List<File>> allTestReportDir = new Function<File, List<File>>(){
      @Override
      public List<File> apply(File input) {
        return newArrayList(input.listFiles(ONLY_TEST_RESULTS_FILES));
      }
    };
    Iterable<List<File>> allReports = Iterables.transform(foldersWithTestReport, allTestReportDir);
    Iterable<File> testReportFiles = Iterables.<File>concat(allReports);
    return testReportFiles;
  }

  private Iterable<File> findAllSupportedReports(String pathname) {
    File targetDirectory = new File(pathname);
    Iterable<File> supportedReportDirectories = newArrayList(firstNonNull(targetDirectory.listFiles(ONLY_SUPPORTED_TEST_RESULTS), new File[]{}));
    File[] otherDirectories = firstNonNull(targetDirectory.listFiles(ONLY_DIRECTORIES), new File[]{});
    for (File otherDirectory : otherDirectories) {
      Iterable<File> otherSupportedReports = findAllSupportedReports(otherDirectory.getAbsolutePath());
      supportedReportDirectories = Iterables.concat(supportedReportDirectories, otherSupportedReports);
    }
    return supportedReportDirectories;
  }

}
