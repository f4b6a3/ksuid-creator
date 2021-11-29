KSUID Creator
======================================================

A Java library for generating [KSUIDs](https://segment.com/blog/a-brief-history-of-the-uuid) (K-Sortable Unique Identifier).

*   Sorted by generation time;
*   Can be stored as a string of 27 chars;
*   Can be stored as an array of 20 bytes;
*   String format is encoded to [base-62](https://en.wikipedia.org/wiki/Base62) (0-9A-Za-z);
*   String format is URL safe and has no hyphens.

Read the [reference implementation](https://github.com/segmentio/ksuid).

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
  <version>2.2.0</version>
</dependency>
```

See more options in [maven.org](https://search.maven.org/artifact/com.github.f4b6a3/ksuid-creator).

### Modularity

Module and bundle names are the same as the root package name.

*   JPMS module name: `com.github.f4b6a3.ksuid`
*   OSGi symbolic name: `com.github.f4b6a3.ksuid`

### KSUID

The KSUID is a 160 bit long identifier (20 bytes). Its consists of a 32-bit timestamp and a 128-bit randomly generated payload.

```java
// Create a KSUID
Ksuid ksuid = KsuidCreator.getKsuid();
```

```java
// Create a KSUID with a given instant
Ksuid ksuid = KsuidCreator.getKsuid(Instant.now());
```

Sequence of KSUIDs:

```text
1tVNDMPsoc59fugBaCLAn9zfbpz
1tVNDMLGoOGqXhR6wSmGCGTuvpw
1tVNDJ9mTTvJO0TqYGC3fTbL73U
1tVNDM39pmWUFhPFSoeNKNpZrsW
1tVNDIfXtt01uVAOvHPdiLunz6N
1tVNDJPRPpPu8qMFk9cmu3b4TLw
1tVNDNP3YfqCOH7wKXStcEc61UP
1tVNDIiEnf9sQAhxcCKTJejJLab
1tVNDIHTknh2fUN74Fb5Hrgo3iy
1tVNDIrcOykL0pWQELgMQ8dZmlV
1tVNDLVdzGm1dL1KeVsekwBySXI
1tVNDHpAvyP7o4xCpKBetE0mn3p
1tVNDHKi79Eaf8uymdQZIZrCG7j
1tVNDMlWdoyH1xnxFYI9UubeIqB
1tVNDHE2n8HOAnMB5bO8X4eEFTD
1tVNDNHR0sZx5d6NE5SkyVbmIzB

|----|--------------------|
 time       payload
```

### More Examples

Create a KSUID with subsecond precision:

```java
// Create a KSUID with millisecond precision
Ksuid ksuid = KsuidCreator.getKsuidMs();
```

```java
// Create a KSUID with microsecond precision
Ksuid ksuid = KsuidCreator.getKsuidUs();
```

```java
// Create a KSUID with nanosecond precision
Ksuid ksuid = KsuidCreator.getKsuidNs();
```

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

A `KsuidFactory` with `ThreadLocalRandom` inside of a `Supplier<byte[]>`:

```java
// use a random supplier that returns an array of 16 bytes
KsuidFactory factory = KsuidFactory.newInstance(() -> {
    final byte[] bytes = new byte[Ksuid.PAYLOAD_BYTES];
    ThreadLocalRandom.current().nextBytes(bytes);
    return bytes;
});

// use the factory
Ksuid ksuid = factory.create();
```

Benchmark
------------------------------------------------------

This section shows benchmarks comparing `KsuidCreator` to `java.util.UUID`.

```
--------------------------------------------------------------------------------
THROUGHPUT (operations/msec)            Mode  Cnt     Score    Error   Units
--------------------------------------------------------------------------------
UUID_randomUUID                        thrpt    5  2035,533 ± 39,739  ops/ms
UUID_randomUUID_toString               thrpt    5  1177,259 ± 32,038  ops/ms
KsuidCreator_getKsuid                  thrpt    5  1927,256 ± 33,542  ops/ms
KsuidCreator_getKsuid_toString         thrpt    5  1012,974 ± 18,003  ops/ms
--------------------------------------------------------------------------------
Total time: 00:05:21
--------------------------------------------------------------------------------
```

System: JVM 8, Ubuntu 20.04, CPU i5-3330, 8G RAM.

To execute the benchmark, run `./benchmark/run.sh`.

Other identifier generators
------------------------------------------------------

Check out the other ID generators.

*   [UUID Creator](https://github.com/f4b6a3/uuid-creator)
*   [ULID Creator](https://github.com/f4b6a3/ulid-creator)
*   [TSID Creator](https://github.com/f4b6a3/tsid-creator)
