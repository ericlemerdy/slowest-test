package fr.free.lemerdy.eric;

import static com.google.common.collect.Lists.newArrayList;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import com.google.common.base.Function;
import com.google.common.base.Throwables;
import com.google.common.io.Closeables;

public final class ParseTestsInFileReport implements Function<File, List<ExecutionTimeTestMethod>> {
  public List<ExecutionTimeTestMethod> apply(File inputFile) {
    List<ExecutionTimeTestMethod> testReports = newArrayList();
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
            ExecutionTimeTestMethod testReport = new ExecutionTimeTestMethod();
            testReport.time = new Double(element.getAttributeByName(QName.valueOf("time")).getValue());
            testReport.classname = element.getAttributeByName(QName.valueOf("classname")).getValue();
            testReport.methodName = element.getAttributeByName(QName.valueOf("name")).getValue();
            testReports.add(testReport);
          }
        }
      }
    } catch (XMLStreamException e) {
      Throwables.propagate(e);
    } catch (FileNotFoundException e) {
      Throwables.propagate(e);
    } finally {
      Closeables.closeQuietly(fileReader);
      closeQuietly(xmler);
    }
    return testReports;
  }

  private void closeQuietly(XMLEventReader xmler) {
    if (xmler != null) {
      try {
        xmler.close();
      } catch (XMLStreamException e) {
        // Swallowing
      }
    }
  }
}