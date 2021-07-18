
# KSUID Creator

A Java library for generating [KSUIDs](https://segment.com/blog/a-brief-history-of-the-uuid) (K-Sortable Unique Identifier).

* Sorted by generation time;
* Can be stored as a string of 27 chars;
* Can be stored as an array of 20 bytes;
* String format is encoded to [base-62](https://en.wikipedia.org/wiki/Base62) (0-9A-Za-z);
* String format is URL safe and has no hyphens.

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
  <version>1.1.1</version>
</dependency>
```

See more options in [maven.org](https://search.maven.org/artifact/com.github.f4b6a3/ksuid-creator).

Module name: `com.github.f4b6a3.ksuid`.

### KSUID

The KSUID is a 160 bit long identifier (20 bytes). Its consists of a 32-bit timestamp and a 128-bit randomly generated payload.

```java
// Generate a KSUID
Ksuid ksuid = KsuidCreator.getKsuid();
```

```java
// Generate a KSUID with a specific time
Ksuid ksuid = KsuidCreator.getKsuid(1234567890);
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

### Other usage examples

Create a KSUID with subsecond precision:

```java
// Generate a KSUID with millisecond precision
Ksuid ksuid = KsuidCreator.getKsuidMs();
```

```java
// Generate a KSUID with microsecond precision
Ksuid ksuid = KsuidCreator.getKsuidUs();
```

```java
// Generate a KSUID with nanosecond precision
Ksuid ksuid = KsuidCreator.getKsuidNs();
```

```java
// Generate a KSUID with given time and millisecond
Ksuid ksuid = KsuidCreator.getKsuidMs(1234567890, 999);
```

```java
// Generate a KSUID with given time and microsecond
Ksuid ksuid = KsuidCreator.getKsuidUs(1234567890, 999999);
```

```java
// Generate a KSUID with given time and nanosecond
Ksuid ksuid = KsuidCreator.getKsuidNs(1234567890, 999999999);
```

Create a KSUID from a canonical string (27 chars, base-62):

```java
Ksuid ksuid = Ksuid.from("0123456789ABCDEFGHIJKLMNOPQ");
```

Convert a KSUID into a canonical string (27 chars, base-62):

```java
String string = ksuid.toString(); // 0123456789ABCDEFGHIJKLMNOPQ
```

Convert a KSUID into a byte array:

```java
byte[] bytes = ksuid.toBytes(); // 20 bytes (160 bits)
```

Get the creation instant of a KSUID:

```java
Instant instant = ksuid.getInstant(); // 2014-06-05T09:06:29Z
```

```java
// static method
Instant instant = Ksuid.getInstant("0123456789ABCDEFGHIJKLMNOPQ"); // 2014-06-05T09:06:29Z
```

Get the time component of a KSUID:

```java
long time = ksuid.getTime(); // 1401959189
```

```java
// static method
long time = Ksuid.getTime("0123456789ABCDEFGHIJKLMNOPQ"); // 1401959189
```

Get the payload component of a KSUID:

```java
byte[] payload = ksuid.getPayload(); // 16 bytes (128 bits)
```

```java
// static method
byte[] payload = Ksuid.getPayload("0123456789ABCDEFGHJKMNPQRST"); // 16 bytes (128 bits)
```

Use a `KsuidFactory` with `java.util.Random`:

```java
// use a `Random` instance
KsuidFactory factory = new KsuidFactory(new Random());
Ksuid ksuid = factory.create();
```

Use a `KsuidFactory` with a random generator of your choice:

```java
// use a method of any RNG with this signature: `void nextBytes(byte[])`
import com.github.niceguy.random.AwesomeRandom; // a hypothetical RNG
KsuidFactory factory = new KsuidFactory(new AwesomeRandom()::nextBytes);
Ksuid ksuid = factory.create();
```

Benchmark
------------------------------------------------------

This section shows benchmarks comparing `KsuidCreator` to `java.util.UUID`.

```
==============================================================================
THROUGHPUT (operations/msec)            Mode  Cnt     Score     Error   Units
==============================================================================
Throughput.UUID_randomUUID             thrpt    5  2050,995 ±  21,636  ops/ms
Throughput.UUID_randomUUID_toString    thrpt    5  1178,970 ±  24,739  ops/ms
------------------------------------------------------------------------------
Throughput.KsuidCreator_Ksuid          thrpt    5  1948,716 ±  34,251  ops/ms
Throughput.KsuidCreator_Ksuid_toString thrpt    5  1025,416 ±  41,976  ops/ms
==============================================================================
Total time: 00:05:20
==============================================================================
```

The method `KsuidCreator.getKsuid()` is almost as fast as `UUID.randomUUID()`.

System: JVM 8, Ubuntu 20.04, CPU i5-3330, 8G RAM.

See: [uuid-creator-benchmark](https://github.com/fabiolimace/uuid-creator-benchmark)

Other generators
-------------------------------------------
* [UUID Creator](https://github.com/f4b6a3/uuid-creator): for generating UUID
* [ULID Creator](https://github.com/f4b6a3/ulid-creator): for generating ULID
* [TSID Creator](https://github.com/f4b6a3/tsid-creator): for generating Time Sortable ID
