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

  private static final FilenameFilter ONLY_SUPPORTED_TEST_DIRECTORY = new FilenameFilter() {
    @Override
    public boolean accept(File dir, String name) {
      return name.equals("surefire-reports") || name.equals("failsafe-reports");
    }
  };

  private static final FilenameFilter ONLY_SUPPORTED_TEST_REPORT_FILE = new FilenameFilter() {
    @Override
    public boolean accept(File dir, String name) {
      return name.startsWith("TEST-") && name.endsWith(".xml");
    }
  };

  private static final FileFilter ONLY_DIRECTORY = new FileFilter() {
    @Override
    public boolean accept(File pathname) {
      return pathname.isDirectory();
    }
  };

  private final ParseTestsInFileReport parseTestsInFileReport = new ParseTestsInFileReport();

  private final Iterable<File> targetsFolder;

  public SlowestSurefireReports(String targetPathname) {
    this.targetsFolder = findAllSupportedReportsInTarget(targetPathname);
  }

  public SlowestSurefireReports(Iterable<File> targetsFolders) {
    this.targetsFolder = findOnlySupportedTestsFolders(targetsFolders);
  }

  private Iterable<File> findOnlySupportedTestsFolders(Iterable<File> targetDirectories) {
    return concat(transform(targetDirectories, new Function<File, Iterable<File>>() {
    public Iterable<File> apply(File input) {
      return newArrayList(firstNonNull(
        input.listFiles(ONLY_SUPPORTED_TEST_DIRECTORY), new File[] {}));
    }}));
  }

  public List<ExecutionTimeTestMethod> readSlowestTests() {
    Function<File, List<File>> allTestReportDir = new Function<File, List<File>>() {
      @Override
      public List<File> apply(File input) {
        return newArrayList(input.listFiles(ONLY_SUPPORTED_TEST_REPORT_FILE));
      }
    };
    Iterable<List<File>> allReports = Iterables.transform(targetsFolder, allTestReportDir);
    Iterable<File> testReportFiles = Iterables.<File> concat(allReports);
    Iterable<List<ExecutionTimeTestMethod>> testReportByFile = transform(testReportFiles, parseTestsInFileReport);
    List<ExecutionTimeTestMethod> allTestReports = newArrayList(concat(testReportByFile));
    Collections.sort(allTestReports);
    return allTestReports;
  }

  private Iterable<File> findAllSupportedReportsInTarget(String targetPathname) {
    File targetDirectory = new File(targetPathname);
    Iterable<File> supportedReportDirectories = findOnlySupportedTestsFolders(newArrayList(targetDirectory));
    File[] otherDirectories = firstNonNull(targetDirectory.listFiles(ONLY_DIRECTORY), new File[] {});
    for (File otherDirectory : otherDirectories) {
      Iterable<File> otherSupportedReports = findAllSupportedReportsInTarget(otherDirectory.getAbsolutePath());
      supportedReportDirectories = Iterables.concat(supportedReportDirectories, otherSupportedReports);
    }
    return supportedReportDirectories;
  }

}
