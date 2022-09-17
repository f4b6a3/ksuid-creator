
KSUID Creator
======================================================

This is a Java implementation of [Segment's K-Sortable Globally Unique Identifiers](https://github.com/segmentio/ksuid).

In summary:

*   Sorted by generation time;
*   Can be stored as a string of 27 chars;
*   Can be stored as an array of 20 bytes;
*   String format is encoded to [base-62](https://en.wikipedia.org/wiki/Base62) (0-9A-Za-z);
*   String format is URL safe and has no hyphens.

This project contains a [micro benchmark](https://github.com/f4b6a3/ksuid-creator/tree/master/benchmark) and a good amount of [unit tests](https://github.com/f4b6a3/ksuid-creator/tree/master/src/test/java/com/github/f4b6a3/ksuid).

The jar file can be downloaded directly from [maven.org](https://repo1.maven.org/maven2/com/github/f4b6a3/ksuid-creator/).

Read the [Javadocs](https://javadoc.io/doc/com.github.f4b6a3/ksuid-creator).

Also read the [KSUID release post](https://segment.com/blog/a-brief-history-of-the-uuid).

How to Use
------------------------------------------------------

Create a KSUID:

```java
Ksuid ksuid = KsuidCreator.getKsuid();
```

Create a KSUID String:

```java
String string = KsuidCreator.getKsuid().toString();
```

### Maven dependency

Add these lines to your `pom.xml`.

```xml
<!-- https://search.maven.org/artifact/com.github.f4b6a3/ksuid-creator -->
<dependency>
  <groupId>com.github.f4b6a3</groupId>
  <artifactId>ksuid-creator</artifactId>
  <version>4.0.2</version>
</dependency>
```

See more options in [maven.org](https://search.maven.org/artifact/com.github.f4b6a3/ksuid-creator).

### Modularity

Module and bundle names are the same as the root package name.

*   JPMS module name: `com.github.f4b6a3.ksuid`
*   OSGi symbolic name: `com.github.f4b6a3.ksuid`

### Segment's KSUID

The Segment's KSUID is a 160 bit long identifier (20 bytes). It consists of a 32-bit timestamp and a 128-bit randomly generated payload. Its canonical string representation is 27 characters long.

```java
// Create a KSUID
Ksuid ksuid = KsuidCreator.getKsuid();
```

Sequence of KSUIDs:

```text
24QbW6CHZpeF5q3KvFxlQpSPuR7
24QbW75lYxOmr1KZP8gi0L7WZMW
24QbW4SpzjSBTs5PMTTo5y8z793
24QbW3RXcN0OFfjTURVCNmLqJch
24QbW3BCODy9IZAjvaAPH6qgr8U
24QbW5DlcuhMlZHEXJVAOddmkeZ
24QbW6PtsLVRl4Xi9rmPAjApl1k
24QbW0TRakXKk6bykUbCSu7BJAj
24QbVznAO74F0zMaOrIShGphXdT
24QbW5HqqFlMtRqiQ1h1BHjORxf
24QbW4bWYmhDe3mZLjWcCUUZyOh
24QbW3iL26DpJmxE31QSHLan4jB
24QbW1gwBP7yClo43pJHTTb1EMU
24QbW3pKgLubTlc8xRT380eDRXb
24QbW4lHWRuUhIY0twbkTaCbQmt
24QbW0rfP04tIdUfReOcFxKaI7o

|----|--------------------|
 time       payload
```

### Sub-second KSUID

The Sub-second KSUID is a variant of Segment's KSUID. A small number of its payload bits are traded for more timestamp bits. Its main advantage is the *precision*.

The number of sub-second bits depends on `Instant.now()` [resolution](https://stackoverflow.com/questions/11452597/precision-vs-accuracy-of-system-nanotime). In JDK-8, the usual resolution is millisecond. In JDK-9+, microsecond.

Three sub-second precisions are supported: millisecond, microsecond, and nanosecond. The precision is detected at runtime.

```java
// Create a Sub-second KSUID
Ksuid ksuid = KsuidCreator.getSubsecondKsuid();
```

Sequence of Sub-second KSUIDs:

```text
24QbW5oYmzZmRWVGuv0ANLv7NxH
24QbW5ofcCGe3QtYlS60qlBmbi5
24QbW5oPZzC4nOeOtzDphnwe9ud
24QbW5omwpeVZLZNATGzcO47FcR
24QbW5ogYyeN3MIKfllQaDl04Gr
24QbW5oZDMWf3uQwk3BgOOLKyza
24QbW5ol0wQ9s0mNcvhisQipr96
24QbW5obEClijP4R7UedowRf8bo
24QbW5oD3yDaFtpZdEsmNIFAg0x
24QbW5oF304l9mj8eSdMQv5ZdsP
24QbW5o1xxkjquiqhWqT6tP4feA
24QbW5oCDHZS6AQJDmM4p22j3PH
24QbW5oI9egLkvY3iQpQXZAZTbk
24QbW5owmgSDnATUZyE4b58wSNz
24QbW5ovHJtU9kM58kDZZGGgCQ5
24QbW5oBsHnmQseJs495DpSCCze

|------|------------------|
  time       payload
```

### Monotonic KSUID

The Monotonic KSUID is another variant of Segment's KSUID. Its payload is incremented by 1 whenever the current second is equal to the previous one. Its main advantage is *speed*.

This implementation is derived from [Monotonic ULID](https://github.com/ulid/spec). It's like Segment's [`sequence.go`](https://github.com/segmentio/ksuid/blob/master/sequence.go) generator, which generates sequential KSUIDs, but there's a difference. You must pass a seed to `sequence.go` generator. In Monotonic KSUID, the seed is regenerated every second.

```java
// Create a Monotonic KSUID
Ksuid ksuid = KsuidCreator.getMonotonicKsuid();
```

Sequence of Monotonic KSUIDs:

```text
24QcJGoCufA6t80z28wBpCWdE10
24QcJGoCufA6t80z28wBpCWdE11
24QcJGoCufA6t80z28wBpCWdE12
24QcJGoCufA6t80z28wBpCWdE13
24QcJGoCufA6t80z28wBpCWdE14
24QcJGoCufA6t80z28wBpCWdE15
24QcJGoCufA6t80z28wBpCWdE16
24QcJGoCufA6t80z28wBpCWdE17
24QcJRYPLFj3bIqFnNpoP7Rv6Hs < second changed
24QcJRYPLFj3bIqFnNpoP7Rv6Ht
24QcJRYPLFj3bIqFnNpoP7Rv6Hu
24QcJRYPLFj3bIqFnNpoP7Rv6Hv
24QcJRYPLFj3bIqFnNpoP7Rv6Hw
24QcJRYPLFj3bIqFnNpoP7Rv6Hx
24QcJRYPLFj3bIqFnNpoP7Rv6Hy
24QcJRYPLFj3bIqFnNpoP7Rv6Hz
     ^ look               ^ look

|----|--------------------|
 time       payload
```

### More Examples

---

Create a KSUID from a canonical string (27 chars, base-62):

```java
Ksuid ksuid = Ksuid.from("0123456789ABCDEFGHIJKLMNOPQ");
```

---

Get the creation instant of a KSUID:

```java
Instant instant = ksuid.getInstant(); // 2014-06-05T09:06:29Z
```

```java
// static method
Instant instant = Ksuid.getInstant("0123456789ABCDEFGHIJKLMNOPQ"); // 2014-06-05T09:06:29Z
```

---

A key generator that makes substitution easy if necessary:

```java
package com.example;

import com.github.f4b6a3.ksuid.KsuidCreator;

public class KeyGenerator {
    public static String next() {
        return KsuidCreator.getKsuid().toString();
    }
}
```
```java
String key = KeyGenerator.next();
```

---

A `KsuidFactory` with `java.util.Random`:

```java
// use a `java.util.Random` instance for fast generation
KsuidFactory factory = KsuidFactory.newInstance(new Random());

// use the factory
Ksuid ksuid = factory.create();
```

---

A `KsuidFactory` with `SplittableRandom`:

```java
// use a random function that returns a long value
SplittableRandom random = new SplittableRandom();
KsuidFactory factory = KsuidFactory.newInstance(() -> random.nextLong());

// use the factory
Ksuid ksuid = factory.create();
```

---

A `KsuidFactory` with `RandomGenerator` (JDk 17+):

```java
// use a random function that returns a long value
RandomGenerator random = RandomGenerator.getDefault();
KsuidFactory factory = KsuidFactory.newInstance(() -> random.nextLong());

// use the factory
Ksuid ksuid = factory.create();
```

---

A `KsuidFactory` with `ThreadLocalRandom`:

```java
// use a random function that returns a byte array
KsuidFactory factory = KsuidFactory.newInstance((length) -> {
    final byte[] bytes = new byte[length];
    ThreadLocalRandom.current().nextBytes(bytes);
    return bytes;
});


// use the factory
Ksuid ksuid = factory.create();
```


Benchmark
------------------------------------------------------

This section shows benchmarks comparing `KsuidCreator` to `UUID.randomUUID()`.

```
---------------------------------------------------------------------------------
THROUGHPUT (operations/msec)              Mode  Cnt     Score      Error   Units
---------------------------------------------------------------------------------
UUID_randomUUID                          thrpt    5   3387,205 ±  10,224  ops/ms
UUID_randomUUID_toString                 thrpt    5   2810,942 ±  58,598  ops/ms
-  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -
KsuidCreator_getKsuid                    thrpt    5   2932,173 ±  59,416  ops/ms
KsuidCreator_getKsuid_toString           thrpt    5   1626,184 ±  66,695  ops/ms
-  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -
KsuidCreator_getSubsecondKsuid           thrpt    5   2963,775 ±  38,594  ops/ms
KsuidCreator_getSubsecondKsuid_toString  thrpt    5   1550,485 ± 124,456  ops/ms
-  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -  -
KsuidCreator_getMonotonicKsuid           thrpt    5  16123,933 ± 226,742  ops/ms
KsuidCreator_getMonotonicKsuid_toString  thrpt    5   3020,113 ±  56,734  ops/ms
---------------------------------------------------------------------------------
Total time: 00:02:41
---------------------------------------------------------------------------------
```

System: CPU i7-8565U, 16G RAM, Ubuntu 22.04, JVM 11, rng-tools installed.

To execute the benchmark, run `./benchmark/run.sh`.

Other identifier generators
------------------------------------------------------

Check out the other ID generators from the same family:

*   [UUID Creator](https://github.com/f4b6a3/uuid-creator): Universally Unique Identifiers
*   [ULID Creator](https://github.com/f4b6a3/ulid-creator): Universally Unique Lexicographically Sortable Identifiers
*   [TSID Creator](https://github.com/f4b6a3/tsid-creator): Time Sortable Identifiers

License
------------------------------------------------------

This library is Open Source software released under the [MIT license](https://opensource.org/licenses/MIT).

