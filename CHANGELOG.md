# Changelog

All notable changes to this project will be documented in this file.

## [Unreleased]

Nothing unreleaded.

## [4.1.0] - 2022-10-22

Add a fast method to generate identifiers. #16

## [4.0.2] - 2022-09-17

Rewrite docs. #15

## [4.0.1] - 2022-08-21

Optimized comparison. #14

## [4.0.0] - 2022-07-09

Add support for RandomGenerator in Java 17. #13

## [3.1.0] - 2022-04-21

Handle clock drift. #12

## [3.0.0] - 2022-01-30

Implemented KSUID with sub-second precision. #11

## [2.3.0] - 2021-12-11

Implemented Monotonic KSUID inspired on [Monotonic ULID](https://github.com/ulid/spec).

## [2.2.0] - 2021-11-03

Optimization: the `Ksuid.compareTo()` method is at least 25% faster.

The internal field `byte[20] bytes` was replaced by two fields: `int seconds` and `byte[16] payload`.

## [2.1.2] - 2021-10-24

Regular maintenance.

## [2.1.1] - 2021-10-03

Regular maintenance.

## [2.1.0] - 2021-09-04

Add OSGi entries to Manifest.MF #8

Module and bundle names are the same as the root package name.

The OSGi symbolic name is the same as the JPMS module name: `com.github.f4b6a3.ksuid`.

## [2.0.0] - 2021-08-08

Now you call `KsuidFactory.newInstance()` and it's variants to get a new `KsuidFactory`.

### Added

-   Added `KsuidCreator.getKsuid(Instant)`
-   Added `KsuidCreator.getMsKsuid(Instant)`
-   Added `KsuidCreator.getUsKsuid(Instant)`
-   Added `KsuidCreator.getNsKsuid(Instant)`
-   Added `KsuidFactory.create(Instant)`
-   Added `KsuidFactory.newInstance()`
-   Added `KsuidFactory.newInstance(Random)`
-   Added `KsuidFactory.newInstance(Supplier<byte[]>)`
-   Added `KsuidFactory.newMsInstance()`
-   Added `KsuidFactory.newMsInstance(Random)`
-   Added `KsuidFactory.newMsInstance(Supplier<byte[]>)`
-   Added `KsuidFactory.newUsInstance()`
-   Added `KsuidFactory.newUsInstance(Random)`
-   Added `KsuidFactory.newUsInstance(Supplier<byte[]>)`
-   Added benchmark code to compare KSUID with UUID

### Removed

-   Removed `KsuidCreator.getKsuid(long)`
-   Removed `KsuidCreator.getMsKsuid(long, int)`
-   Removed `KsuidCreator.getUsKsuid(long, int)`
-   Removed `KsuidCreator.getNsKsuid(long, int)`
-   Removed `KsuidFactory.createMs()`
-   Removed `KsuidFactory.createUs()`
-   Removed `KsuidFactory.createNs()`
-   Removed `KsuidFactory.create(long)`
-   Removed `KsuidFactory.createMs(long, int)`
-   Removed `KsuidFactory.createUs(long, int)`
-   Removed `KsuidFactory.createNs(long, int)`
-   Removed `RandomGenerator` interface

## [1.1.2] - 2021-07-18

Fix for Sonatype Lift analysis report

### Fixed

-   Fixed `int` to `long` conversion in method that use nanoseconds.

## [1.1.1] - 2021-07-18

Fix for Sonatype Lift analysis report

### Fixed

-   Fixed shifts in methods that use fractions of a second (ms, us, ns).

## [1.1.0] - 2021-07-17

Simplified the use of `KsuidFactory` with other random generators.

### Added

-   Added constructors in `KsuidFactory` for random generators.

## [1.0.1] - 2021-07-17

Creates a module name be used in Java 9+.

### Added

-   Added module name for Java 9+

## [1.0.0] - 2021-06-05

Project created as an alternative Java implementation of [K-Sortable Unique IDentifier](https://github.com/segmentio/ksuid).

### Added

-   Added `Ksuid`
-   Added `KsuidCreator`
-   Added `KsuidFactory`
-   Added `RandomGenerator`
-   Added `DefaultRandomGenerator`
-   Added test cases

[unreleased]: https://github.com/f4b6a3/ksuid-creator/compare/ksuid-creator-4.1.0...HEAD
[4.1.0]: https://github.com/f4b6a3/ksuid-creator/compare/ksuid-creator-4.0.2...ksuid-creator-4.1.0
[4.0.2]: https://github.com/f4b6a3/ksuid-creator/compare/ksuid-creator-4.0.1...ksuid-creator-4.0.2
[4.0.1]: https://github.com/f4b6a3/ksuid-creator/compare/ksuid-creator-4.0.0...ksuid-creator-4.0.1
[4.0.0]: https://github.com/f4b6a3/ksuid-creator/compare/ksuid-creator-3.1.0...ksuid-creator-4.0.0
[3.1.0]: https://github.com/f4b6a3/ksuid-creator/compare/ksuid-creator-3.0.0...ksuid-creator-3.1.0
[3.0.0]: https://github.com/f4b6a3/ksuid-creator/compare/ksuid-creator-2.3.0...ksuid-creator-3.0.0
[2.3.0]: https://github.com/f4b6a3/ksuid-creator/compare/ksuid-creator-2.2.0...ksuid-creator-2.3.0
[2.2.0]: https://github.com/f4b6a3/ksuid-creator/compare/ksuid-creator-2.1.2...ksuid-creator-2.2.0
[2.1.2]: https://github.com/f4b6a3/ksuid-creator/compare/ksuid-creator-2.1.1...ksuid-creator-2.1.2
[2.1.1]: https://github.com/f4b6a3/ksuid-creator/compare/ksuid-creator-2.1.0...ksuid-creator-2.1.1
[2.1.0]: https://github.com/f4b6a3/ksuid-creator/compare/ksuid-creator-2.0.0...ksuid-creator-2.1.0
[2.0.0]: https://github.com/f4b6a3/ksuid-creator/compare/ksuid-creator-1.1.2...ksuid-creator-2.0.0
[1.1.2]: https://github.com/f4b6a3/ksuid-creator/compare/ksuid-creator-1.1.1...ksuid-creator-1.1.2
[1.1.1]: https://github.com/f4b6a3/ksuid-creator/compare/ksuid-creator-1.1.0...ksuid-creator-1.1.1
[1.1.0]: https://github.com/f4b6a3/ksuid-creator/compare/ksuid-creator-1.0.1...ksuid-creator-1.1.0
[1.0.1]: https://github.com/f4b6a3/ksuid-creator/compare/ksuid-creator-1.0.0...ksuid-creator-1.0.1
[1.0.0]: https://github.com/f4b6a3/ksuid-creator/releases/tag/ksuid-creator-1.0.0
