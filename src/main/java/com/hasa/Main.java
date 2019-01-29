package com.hasa;

import com.github.rholder.retry.RetryException;
import com.hasa.testmodule.DynamicTestModuleLoader;
import com.hasa.testmodule.TestModuleInfo;
import com.hasa.util.Environment;
import org.eclipse.aether.resolution.DependencyResolutionException;
import org.testng.TestNG;
import org.testng.xml.XmlSuite;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

/**
 * - MasterTestService -
 * @author Hasantha Alahakoon 
 */
public class Main
{
  public static void main(String args[])
  {
    try
    {
      String[] dependencyInfo = validateAndGetDependencyInfo(args);
      System.out.println("************ Starting Master Test Service for " + Arrays.toString(dependencyInfo) + " ************");
      TestModuleInfo testModuleInfo = DynamicTestModuleLoader.getInstance()
          .loadTestModuleWithDependencies(dependencyInfo[0], dependencyInfo[1], dependencyInfo[2]); //TODO
      Environment.getInstance().waitTillReady();
            new Main().runTestSuite(testModuleInfo, Environment.getInstance().getNumberOfSlaves());
    }
    catch (DependencyResolutionException e)
    {
      System.out.println("Error Loading Test Module from Maven dependency info.");
      e.printStackTrace();
    }
    catch (IOException e)
    {
      System.out.println("Error Extracting TestSuite XML File from Test Module.");
      e.printStackTrace();
    }
    catch (ExecutionException | RetryException e)
    {
      System.out.println("Error Setting up Test Environment. Test Suite Execution will be aborted.");
      e.printStackTrace();
    }
  }

  private void runTestSuite(TestModuleInfo testModuleInfo, int numberOfSlaves)
  {
    TestNG testNG = new TestNG();
    testNG.setTestSuites(Arrays.asList(testModuleInfo.getTestSuiteXmlFile()));
    testNG.setListenerClasses(Arrays.asList(MasterTestRunner.class));
    testNG.addClassLoader(testModuleInfo.getTestModuleClassLoader());
    if (numberOfSlaves > 1)
    {
      testNG.setParallel(XmlSuite.ParallelMode.METHODS);
      testNG.setThreadCount(numberOfSlaves);
    }
    testNG.run();
  }

  private static String[] validateAndGetDependencyInfo(String[] args)
  {
    String[] dependencyInfo;
    if (args.length == 1)
    {
      dependencyInfo = args[0].split(":");
      if (dependencyInfo.length != 3)
      {
        throw new RuntimeException("Test Module Dependency Information not provided. Need to be in Following Format: groupId:artifactid:version");
      }
    }
    else
    {
      throw new RuntimeException("Test Module Dependency Information not provided.");
    }
    return dependencyInfo;
  }
}