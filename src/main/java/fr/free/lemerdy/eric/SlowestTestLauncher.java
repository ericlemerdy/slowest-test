package fr.free.lemerdy.eric;

import static java.lang.String.format;

import java.io.File;
import java.net.URISyntaxException;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PatternOptionBuilder;
import org.apache.commons.cli.PosixParser;

import com.google.common.base.Joiner;

public class SlowestTestLauncher {

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
      e.printStackTrace(System.err);
    }
  
    if (cmd.hasOption("help")) {
      printHelp(options);
    } else if (cmd.hasOption("f")) {
      File targetFilePath = (File) cmd.getParsedOptionValue("f");
      List<ExecutionTimeTestMethod> slowestTests = new SlowestSurefireReports(targetFilePath.getAbsolutePath()).readSlowestTests();
      if (slowestTests.size() == 0) {
        System.err.println(format("No report files found in '%s'.", targetFilePath.getAbsolutePath()));
      }
      System.out.println(Joiner.on('\n').join(slowestTests));
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
