slowest-test
============

A maven plugin to get the test methods ordered by execution time.

usage:
  in the plugin:
    mvn clean install
  in your project pom:
    <project>
      ...
      <build>
        ...
        <plugins>
          ...
          <plugin>
    			  <groupId>fr.free.lemerdy.eric</groupId>
  				  <artifactId>slowest-test-maven-plugin</artifactId>
  			  </plugin>
  from your project:
    mvn clean test slowest-test:print

expected output:
...
[INFO] ------------------------------------------------------------------------
[INFO] Building <my-project> Maven pom 1.0-SNAPSHOT
[INFO] ------------------------------------------------------------------------
[INFO] 
[INFO] --- slowest-test-maven-plugin:1.0-SNAPSHOT:print (default-cli) @ <my-project> ---
[INFO]      0,002ms fr.free.lemerdy.eric.AppTest.testApp
[INFO]      0,002ms fr.free.lemerdy.eric.AppTest2.testApp
[INFO] ------------------------------------------------------------------------
[INFO] Reactor Summary:
...
  
  
  