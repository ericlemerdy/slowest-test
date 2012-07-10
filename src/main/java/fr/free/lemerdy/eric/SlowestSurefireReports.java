package fr.free.lemerdy.eric;

import static com.google.common.collect.Lists.newArrayList;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.io.Resources;

public class SlowestSurefireReports {

  public SlowestSurefireReports() {
  }

  public List<TestReport> slowestTests() throws URISyntaxException {
    FilenameFilter onlyTestResults = new FilenameFilter() {
      public boolean accept(File dir, String name) {
        return name.startsWith("TEST-") && name.endsWith(".xml");
      }
    };
    File testDir = new File(Resources.getResource("surefire-reports").toURI());
    File[] files = testDir.listFiles(onlyTestResults);
    ArrayList<File> testReportFiles = newArrayList(files);
    Iterable<List<TestReport>> allTestReports = Iterables.transform(testReportFiles,
        new Function<File, List<TestReport>>() {
          public List<TestReport> apply(File inputFile) {
            ArrayList<TestReport> testReports = Lists.newArrayList();
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
                  StartElement asStartElement = event.asStartElement();
                  if (QName.valueOf("testcase").equals(asStartElement.getName())) {
                    TestReport testReport = new TestReport();
                    testReport.time = Double.valueOf(asStartElement.getAttributeByName(QName.valueOf("time"))
                        .getValue());
                    testReport.classname = asStartElement.getAttributeByName(QName.valueOf("classname")).getValue();
                    testReport.name = asStartElement.getAttributeByName(QName.valueOf("name")).getValue();
                    testReports.add(testReport);
                  }
                }
              }
            } catch (XMLStreamException e) {
              e.printStackTrace();
            } catch (FileNotFoundException e) {
              e.printStackTrace();
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
        });
    return newArrayList(Iterables.concat(allTestReports));
  }

}
