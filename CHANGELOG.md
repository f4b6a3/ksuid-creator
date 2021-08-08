# Changelog

All notable changes to this project will be documented in this file.

## [Unreleased]

Nothing unreleaded.

## [2.0.0] - 2021-08-08

Now you call `KsuidFactory.newInstance()` and it's variants to get a new `KsuidFactory`.

### Added

- Added `KsuidCreator.getKsuid(Instant)`
- Added `KsuidCreator.getMsKsuid(Instant)`
- Added `KsuidCreator.getUsKsuid(Instant)`
- Added `KsuidCreator.getNsKsuid(Instant)`
- Added `KsuidFactory.create(Instant)`
- Added `KsuidFactory.newInstance()`
- Added `KsuidFactory.newInstance(Random)`
- Added `KsuidFactory.newInstance(Supplier<byte[]>)`
- Added `KsuidFactory.newMsInstance()`
- Added `KsuidFactory.newMsInstance(Random)`
- Added `KsuidFactory.newMsInstance(Supplier<byte[]>)`
- Added `KsuidFactory.newUsInstance()`
- Added `KsuidFactory.newUsInstance(Random)`
- Added `KsuidFactory.newUsInstance(Supplier<byte[]>)`
- Added benchmark code to compare KSUID with UUID

### Removed

- Removed `KsuidCreator.getKsuid(long)`
- Removed `KsuidCreator.getMsKsuid(long, int)`
- Removed `KsuidCreator.getUsKsuid(long, int)`
- Removed `KsuidCreator.getNsKsuid(long, int)`
- Removed `KsuidFactory.createMs()`
- Removed `KsuidFactory.createUs()`
- Removed `KsuidFactory.createNs()`
- Removed `KsuidFactory.create(long)`
- Removed `KsuidFactory.createMs(long, int)`
- Removed `KsuidFactory.createUs(long, int)`
- Removed `KsuidFactory.createNs(long, int)`
- Removed `RandomGenerator` interface

## [1.1.2] - 2021-07-18

Fix for Sonatype Lift analysis report

### Fixed

- Fixed `int` to `long` conversion in method that use nanoseconds.

## [1.1.1] - 2021-07-18

Fix for Sonatype Lift analysis report

### Fixed

- Fixed shifts in methods that use fractions of a second (ms, us, ns).

## [1.1.0] - 2021-07-17

Simplified the use of `KsuidFactory` with other random generators.

### Added

- Added constructors in `KsuidFactory` for random generators.

## [1.0.1] - 2021-07-17

Creates a module name be used in Java 9+.

### Added

- Added module name for Java 9+

## [1.0.0] - 2021-06-05

Project created as an alternative Java implementation of [K-Sortable Unique IDentifier](https://github.com/segmentio/ksuid).

### Added

- Added `Ksuid`
- Added `KsuidCreator`
- Added `KsuidFactory`
- Added `RandomGenerator`
- Added `DefaultRandomGenerator`
- Added test cases

[unreleased]: https://github.com/f4b6a3/ksuid-creator/compare/ksuid-creator-2.0.0...HEAD
[2.0.0]: https://github.com/f4b6a3/ksuid-creator/compare/ksuid-creator-1.1.2...ksuid-creator-2.0.0
[1.1.2]: https://github.com/f4b6a3/ksuid-creator/compare/ksuid-creator-1.1.1...ksuid-creator-1.1.2
[1.1.1]: https://github.com/f4b6a3/ksuid-creator/compare/ksuid-creator-1.1.0...ksuid-creator-1.1.1
[1.1.0]: https://github.com/f4b6a3/ksuid-creator/compare/ksuid-creator-1.0.1...ksuid-creator-1.1.0
[1.0.1]: https://github.com/f4b6a3/ksuid-creator/compare/ksuid-creator-1.0.0...ksuid-creator-1.0.1
[1.0.0]: https://github.com/f4b6a3/ksuid-creator/releases/tag/ksuid-creator-1.0.0
