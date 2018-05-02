import org.junit.*;

import java.io.*;
import java.util.List;

public class Main {
    public Main() {
        new File("src/test/output/").mkdirs();
    }

    private void baseTest(String out, int n, boolean shouldFail) {
        baseTest(out, n, 60000, 60000, shouldFail);
    }

    private void baseTest(String out, int n, int readTimeout, int connectionTimeout, boolean shouldFail) {
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(out), "UTF-8"))) {
            try {
                StepikRanker ranker = new StepikRanker();
                List<String> rank = ranker.getCourseRank(n, readTimeout, connectionTimeout);
                Assert.assertFalse("Should not fail", shouldFail);
                for (String s : rank) {
                    writer.write(s + "\n");
                }
            } catch (RankerException e) {
                Assert.assertTrue("Should fail", shouldFail);
                writer.write("Failed with RankerException : " + e.getMessage());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test1() {
        baseTest("src/test/output/test1.txt", 10, false);
    }

    @Test
    public void test2_zero() {
        baseTest("src/test/output/test2.txt", 0, false);
    }

    @Test
    public void test3_large() {
        baseTest("src/test/output/test3.txt", 1000, false);
    }

    @Test
    public void test4_moreThanHave() {
        baseTest("src/test/output/test4.txt", 100000, false);
    }

    @Test
    public void test5_negativeArgument() {
        baseTest("src/test/output/test5.txt", -1, true);
    }

    @Test
    public void test6_lowTimeout() {
        baseTest("src/test/output/test6.txt", 10, 10, 10, true);
    }
}
