package statics;

/**
 * synchronized static lock
 */
public class World {
    private static final Object lock = new Object();

    void sayWorld() {
        synchronized (lock) {
            System.out.println("world");
        }
    }

    void test1() {
        final long begin = System.currentTimeMillis();

        new Thread(() -> {
            quietSleep(1_000L);
            sayWorld();

            System.out.println("test1 consume " + (System.currentTimeMillis() - begin) / 1000 + " seconds");
        }).start();
    }

    void getLock(long millis) {
        synchronized (lock) {
            System.out.println("get lock for " + millis + " millis");
            quietSleep(10_000L);
        }
    }

    private void quietSleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        World world1 = new World();
        World world2 = new World();

        world1.test1();

        world2.getLock(10_000);
    }
}
