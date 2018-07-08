package statics;

/**
 * synchronized (this) / synchronized (instance) / synchronized method
 */
public class Test {
    synchronized void sayTest() {
        System.out.println("test");
    }

    static void quietSleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Test test = new Test();

        final long begin = System.currentTimeMillis();

        new Thread(() -> {
            synchronized (test) {
                    quietSleep(10_000);
            }
        }).start();

        quietSleep(1_000);

        test.sayTest();

        System.out.println("test1 consume " + (System.currentTimeMillis() - begin) / 1000 + " seconds");
    }
}
