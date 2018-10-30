
public class StringTest {
    private static final int LOOP_TIMES = 10000;

    private static final int SUB_LOOP_TIMES = 50;

    private static final String a = "1";
    private static final String b = "2";

    private static void stringBuilderTest001() {
        long begin = System.currentTimeMillis();

        StringBuilder sb = new StringBuilder("");

        for (int i = 0; i < LOOP_TIMES; i++) {
            for (int j = 0; j < SUB_LOOP_TIMES; j++) {
                sb.append(j);
            }
        }

        long end = System.currentTimeMillis();

        System.out.println("stringBuilderTest001 consume is " + (end - begin) + "ms");
    }

    private static void stringBuilderTest002() {
        long begin = System.currentTimeMillis();

        for (int i = 0; i < LOOP_TIMES; i++) {
            StringBuilder stringBuilder = new StringBuilder("");
            for (int j = 0; j < SUB_LOOP_TIMES; j++) {
                StringBuilder tmpStringBuilder = new StringBuilder(stringBuilder.toString());
                tmpStringBuilder.append(j);
                stringBuilder = tmpStringBuilder;
            }
        }

        long end = System.currentTimeMillis();

        System.out.println("stringBuilderTest002 consume is " + (end - begin) + "ms");
    }

    private static void stringBuilderTest003() {
        long begin = System.currentTimeMillis();

        StringBuilder stringBuilder = new StringBuilder("");
        int count = 0;
        for (int i = 0; i < LOOP_TIMES; i++) {
            for (int j = 0; j < SUB_LOOP_TIMES; j++) {
                StringBuilder tmpStringBuilder = new StringBuilder(stringBuilder.toString());
                tmpStringBuilder.append(j);
                stringBuilder = tmpStringBuilder;
                count++;
            }
        }

        long end = System.currentTimeMillis();

        System.out.println("stringBuilderTest003 consume is " + (end - begin) + "ms");
    }

    private static void plusTest001() {
        long begin = System.currentTimeMillis();

        String str = "";
        for (int i = 0; i < LOOP_TIMES; i++) {
            for (int j = 0; j < SUB_LOOP_TIMES; j++) {
                str += j;
            }
        }

        long end = System.currentTimeMillis();

        System.out.println("plusTest001 consume is " + (end - begin) + "ms");
    }

    public static void main(String[] args) {
        System.out.println("begin");

        stringBuilderTest001();
        stringBuilderTest002();
        stringBuilderTest003();

        plusTest001();

        System.out.println(a + " " +b);

        System.out.println("end");
    }
}