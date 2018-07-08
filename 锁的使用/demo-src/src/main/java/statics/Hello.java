package statics;

/**
 * static synchronized method / synchronized(*.class)
 * the lock is same
 *
 * reference:
 * https://stackoverflow.com/questions/28659350/scope-of-synchronization-on-class-objects-in-the-jvm
 */
public class Hello {
    static synchronized void sayHello() {
        System.out.println("say hello");
    }

    static void test1() {
        final long begin = System.currentTimeMillis();

        new Thread(()->{
            quietSleep(1_000L);
            sayHello();

            System.out.println("test1 consume " + (System.currentTimeMillis() - begin) / 1000 + " seconds");
        }).start();

        synchronized (World.class) {
            quietSleep(20_000L);
        }
    }

    static void test2() {
        final long begin = System.currentTimeMillis();

        new Thread(()->{
            quietSleep(1_000L);
            sayHello();

            System.out.println("test2 consume " + (System.currentTimeMillis() - begin) / 1000 + " seconds");
        }).start();

        synchronized (Hello.class) {
            quietSleep(10_000L);
        }
    }

    static void quietSleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        test1();
        test2();
    }
}
