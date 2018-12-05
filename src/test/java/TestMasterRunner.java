import org.testng.TestNG;
import org.testng.annotations.Test;

import java.util.Arrays;

/**
 * - SlaveTestService -
 * @author Hasantha Alahakoon 
 */
public class TestMasterRunner
{
  @Test
  public void test1()
  {
    TestNG testNG = new TestNG();
    testNG.setTestSuites(Arrays.asList("testng.xml"));
    testNG.run();
  }
}
