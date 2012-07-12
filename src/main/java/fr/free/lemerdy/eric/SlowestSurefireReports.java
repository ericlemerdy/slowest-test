package fr.free.lemerdy.eric;

import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Lists.newArrayList;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PatternOptionBuilder;
import org.apache.commons.cli.PosixParser;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Throwables;

public class SlowestSurefireReports {

  private static FilenameFilter ONLY_TEST_RESULTS = new FilenameFilter() {
    public boolean accept(File dir, String name) {
      return name.startsWith("TEST-") && name.endsWith(".xml");
    }
  };
  private String surefirePathname;

  public SlowestSurefireReports(String surefirePathname) {
    this.surefirePathname = surefirePathname;
  }

  public List<TestReport> slowestTests() throws URISyntaxException {
    File testDir = new File(surefirePathname);
    File[] listFiles = Objects.firstNonNull(testDir.listFiles(ONLY_TEST_RESULTS),new File[]{});
    ArrayList<File> testReportFiles = newArrayList(listFiles);
    Iterable<List<TestReport>> testReportByFile = transform(testReportFiles, parseTestsInFile());
    ArrayList<TestReport> allTestReports = newArrayList(concat(testReportByFile));
    Collections.sort(allTestReports);
    return allTestReports;
  }

  private Function<File, List<TestReport>> parseTestsInFile() {
    return new Function<File, List<TestReport>>() {
      public List<TestReport> apply(File inputFile) {
        ArrayList<TestReport> testReports = newArrayList();
        XMLInputFactory xmlif = XMLInputFactory.newInstance();
        FileReader fileReader = null;
        XMLEventReader xmler = null;
        try {
          fileReader = new FileReader(inputFile);
          xmler = xmlif.createXMLEventReader(fileReader);
          XMLEvent event;
          while (xmler.hasNext()) {
            event = xmler.nextEvent();
            if (event.isStartElement()) {
              StartElement element = event.asStartElement();
              if (element.getName().equals(QName.valueOf("testcase"))) {
                TestReport testReport = new TestReport();
                testReport.time = new Double(element.getAttributeByName(QName.valueOf("time")).getValue());
                testReport.classname = element.getAttributeByName(QName.valueOf("classname")).getValue();
                testReport.name = element.getAttributeByName(QName.valueOf("name")).getValue();
                testReports.add(testReport);
              }
            }
          }
        } catch (XMLStreamException e) {
          Throwables.propagate(e);
        } catch (FileNotFoundException e) {
          Throwables.propagate(e);
        } finally {
          Optional.fromNullable(fileReader).transform(new Function<FileReader, Boolean>() {
            public Boolean apply(FileReader input) {
              try {
                input.close();
                return true;
              } catch (IOException e) {
                return false;
              }
            };
          });
          Optional.fromNullable(xmler).transform(new Function<XMLEventReader, Boolean>() {
            public Boolean apply(XMLEventReader input) {
              try {
                input.close();
                return true;
              } catch (XMLStreamException e) {
                return false;
              }
            };
          });
        }
        return testReports;
      };
    };
  }

  public static void main(String[] args) throws ParseException, URISyntaxException {
    Options options = new Options();
    options.addOption(OptionBuilder.withLongOpt("help").withDescription("print this message").create('h'));
    options.addOption(OptionBuilder.hasArg().withLongOpt("targetFilepath")
        .withDescription("The target path. Should contains surefire-reports xml reports.")
        .withType(PatternOptionBuilder.FILE_VALUE).create('f'));

    CommandLineParser parser = new PosixParser();
    CommandLine cmd = null;
    try {
      cmd = parser.parse(options, args, true);
    } catch (ParseException e) {
      e.printStackTrace();
    }

    if (cmd.hasOption("help")) {
      printHelp(options);
    } else if (cmd.hasOption("f")) {
      File targetFilePath = (File) cmd.getParsedOptionValue("f");
      System.out.println(targetFilePath.getAbsolutePath());
      List<TestReport> slowestTests = new SlowestSurefireReports(targetFilePath.getAbsolutePath() + File.separator
          + "surefire-reports").slowestTests();
      if (slowestTests.size() == 0) {
        System.out.println("No report files found.");
      }
      for (TestReport testReport : slowestTests) {
        System.out.println(testReport.time + "ms " + testReport.classname + "." + testReport.name);
      }
    } else {
      System.err.println("Missing target filepath option");
      printHelp(options);
    }
  }

  private static void printHelp(Options options) {
    HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp("slowest-tests", options);
  }

}
