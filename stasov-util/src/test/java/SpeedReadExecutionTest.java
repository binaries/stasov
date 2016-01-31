import com.pocketmath.stasov.util.sync.speedread.SpeedReadExecution;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.concurrent.Callable;

/**
 * Created by etucker on 1/31/16.
 */
@Test
public class SpeedReadExecutionTest {

    @Test(timeOut = 10000L)
    public void test1() throws Exception {
        final SpeedReadExecution exec = new SpeedReadExecution();
        exec.isFreeNow();
    }

    @Test //(timeOut = 10000L)
    public void test2() throws Exception {
        final SpeedReadExecution exec = new SpeedReadExecution(1000);
        final Callable callable = new Callable() {
            @Override
            public Object call() throws Exception {
                return null;
            }
        };
        exec.blockingExecuteCritical(callable);
    }

    @Test(timeOut = 10000L)
    public void test3() throws Exception {
        final SpeedReadExecution exec = new SpeedReadExecution(1000);
        final Callable callable = new Callable() {
            @Override
            public Object call() throws Exception {
                return null;
            }
        };
        exec.blockingExecuteCritical(callable);
        exec.isFreeNow();
    }

    @Test(timeOut = 10000L)
    public void test10() throws Exception {
        final SpeedReadExecution exec = new SpeedReadExecution(500);
        boolean pass = false;
        for (int i = 0; i < 10*500/100; i++) {
            pass = exec.isFreeNow();
            if (pass) break;
            Thread.sleep(100);
        }
        Assert.assertTrue(pass, "barf=" + exec);
    }

}
