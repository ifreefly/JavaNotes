# StringBuilder真的比加号快很多吗

一般来说大家都极力推荐使用StringBuilder进行字符串拼接，一旦使用了加号"+"进行字符串拼接，仿佛罪大恶极一般，恨不得所有地方全部用StringBuilder进行字符拼接。

其实对于简单的字符串拼接场景，使用加号完全没有问题，在可读性上来说要更好。

## 加号、StringBuilder、StringBuffer的适用场景：

先上结论

* 适用加号做拼接的场景：
  * 简单的字符串拼接场景，不含循环
  * 常量拼接（常量拼接场景下，会被编译器在编译时直接优化为一个字符串）
  * IDE工具（比如IDEA、Eclipse）自动生成toString方法的场景

* 适用StringBuilder(Singce JDK 1.5)做拼接的场景：
  * 对字符串做循环拼接的场景
  * 在使用StringBuilder做拼接的时候，如果预先知道字符串可能的大小，建议设置其大小，这样拼接速度会更快[注1]。

* 适用StringBuffer(Since JDK 1.0)做拼接的场景：
  * 暂时未见到。


早在JDK 1.4的版本中，编译器就已经将加号的字符串拼接优化为StringBuffer的拼接了[注1]。现在则是将其优化为StringBuilder拼接了。简单拼接场景中，用加号是完全没有问题的。

StringBuffer相对于StringBuilder来说是线程的。但在实际使用场景中，真的没有见过需要线程安全的场景下进行字符串拼接，一般来说字符串拼接都是对一个局部变量进行拼接，不会存在对类成员或者全局变量进行拼接的，这种场景下，是不会存在线程安全问题的。

即使真的需要对类成员或者全局变量进行拼接，也实在是想不出一个拼接出来只有长度是确定，而内容是乱序的字符串有什么用。或许这也是为什么在先有了StringBuffer后，还要再搞一个StringBuilder的一个原因了。

## 源码分析

说完结论，再看代码

### 原始代码

```java

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
        for (int i = 0; i < LOOP_TIMES; i++) {
            for (int j = 0; j < SUB_LOOP_TIMES; j++) {
                StringBuilder tmpStringBuilder = new StringBuilder(stringBuilder.toString());
                tmpStringBuilder.append(j);
                stringBuilder = tmpStringBuilder;
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
```

运行结果

```shell
begin
stringBuilderTest001 consume is 15ms
stringBuilderTest002 consume is 53ms
stringBuilderTest003 consume is 129308ms
plusTest001 consume is 120754ms
end
```

* stringBuilderTest001是使用StringBuilder进行字符串拼接的场景，可以看到速度很快。

* stringBuilderTest002也是使用StringBuilder进行字符串拼接的场景，是另外一篇博文中的例子。
  * 在该博文中将该方法执行的速度与plusTest001比拼速度，证明StringBuilder的拼接速度要比加号快。这个结论是错误的，因为stringBuilderTest002拼接出来的字符串与plustTest001拼接得出来的字符串根本不是同一个。stringBuilderTest002的拼接的字符工作量要小得多。

  * 在stringBuilderTest002拼接出来的字符串，其长度最终只有SUB_LOOP_TIMES,而plusTest001拼接出来的字符串明显要比这大的多。

* stringBuilderTest003是使用StringBuilder进行字符串拼接的场景，其等效于plusTest001

* plusTest001 使用加号进行字符串拼接。

### 字节码

前文说到stringBuilderTest003与plusTest 003是等效的，这里从反编译的字节码来进行证明。

我们看下这两个方法的关键片段的字节码，即做字符串拼接的片段。

先看下stringBuilderTest002的字节码片段(完整字节码见附件):

```
              15: istore_3        /* i */
              16: goto            62
                  linenumber      48
              19: iconst_0       
              20: istore          j
              22: goto            52
                  linenumber      49
              25: new             Ljava/lang/StringBuilder;
              28: dup            
              29: aload_2         /* stringBuilder */
              30: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
              33: invokespecial   java/lang/StringBuilder.<init>:(Ljava/lang/String;)V
              36: astore          tmpStringBuilder
                  linenumber      50
              38: aload           tmpStringBuilder
              40: iload           j
              42: invokevirtual   java/lang/StringBuilder.append:(I)Ljava/lang/StringBuilder;
              45: pop            
                  linenumber      51
              46: aload           tmpStringBuilder
              48: astore_2        /* stringBuilder */
                  linenumber      48
              49: iinc            j, 1
              52: iload           j
              54: bipush          50
              56: if_icmplt       25
                  linenumber      47
              59: iinc            i, 1
              62: iload_3         /* i */
```

可以看30到行与33行，代码把stringBuilder.toString的结果作为构造参数传入了新的tmpStringBuilder中，然后将tmpStringBuilder赋值给stringBuilder。

再看plusTest001字节码片段(完整字节码见附件)：

```
               8: istore_3        /* i */
               9: goto            51
                  linenumber      65
              12: iconst_0       
              13: istore          j
              15: goto            41
                  linenumber      66
              18: new             Ljava/lang/StringBuilder;
              21: dup            
              22: aload_2         /* str */
              23: invokestatic    java/lang/String.valueOf:(Ljava/lang/Object;)Ljava/lang/String;
              26: invokespecial   java/lang/StringBuilder.<init>:(Ljava/lang/String;)V
              29: iload           j
              31: invokevirtual   java/lang/StringBuilder.append:(I)Ljava/lang/StringBuilder;
              34: invokevirtual   java/lang/StringBuilder.toString:()Ljava/lang/String;
              37: astore_2        /* str */
                  linenumber      65
              38: iinc            j, 1
              41: iload           j
              43: bipush          50
              45: if_icmplt       18
                  linenumber      64
              48: iinc            i, 1
              51: iload_3         /* i */
              52: sipush          10000
```

看标号为23与26的行数，可以很清楚的看到，编译器先new了一个StringBuilder出来，然后通过String.valueOf()方法把字符串传入，然后使用StringBuilder.append进行拼接，最后再使用StringBuilder.toString方法把字符串返回给str。

从字节码来看，stringBuilderTest003与plusTest001的代码大体是差不多的，主要是new的时候使用valueOf以及重新赋值时采用的toString的差异，这种差异在此处是可以忽略的。基本上，从上述字节码来看，stringBuilderTest003与plusTest001就是等价的。

### 使用加号拼接常量的字节码格式

看下主要字节码，可以看到常量拼接直接在编译的时候就优化为常量了，这种情况下放心大胆用加号就是了。
完整字节码看附件。

```
              20: getstatic       java/lang/System.out:Ljava/io/PrintStream;
              23: ldc             "1 2"
```

## 为什么在循环中使用加号进行拼接要比StringBuilder慢

从上述stringBuilderTest003与plusTest001来看，并不是使用了StringBuilder进行拼接就快了，关键还得看是怎么使用的。

从上述例子来看，只有把StringBuilder放在循环的外层，才会有较好的速度。

综上，在循环中使用加号进行拼接速度慢的原因主要有2个：

* 每次循环都需要new StringBuilder对象，这是一笔开销
* 每次new StringBuilder对象的时候都需要拷贝原有的字符串，这是另一笔开销，当字符串很大时，这个开销将会很大。

## 其他

* 在写代码时，放心大胆的启用语言的新特性，不需要老是想着该代码会在很老的版本上运行，没有那么多古老的环境。如果真有这种古老的环境，或许要把环境的JDK版本升级了。

* 写代码的时候优先保证代码的可读性，如果真遇到了性能瓶颈了，易于阅读的代码也是很容易优化的。

* 代码是给人看的，不是给机器看的。

参考链接：

* 注1 https://www.javaspecialists.eu/archive/Issue068.html
* 本文采用的反编译工具是 ng-jd-gui，version 0.6.2