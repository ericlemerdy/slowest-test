package fr.free.lemerdy.eric;

import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

import org.junit.Test;

public class SlowestTestLauncherTest {
  @Test
  public void should_launch_main_with_a_folder_containing_reports() throws Exception {
    List<String> args = newArrayList("-f", "src/test/resources/root_with_reports_deep_hierarchy");

    SlowestTestLauncher.main(args.toArray(new String[]{}));
  }
}
