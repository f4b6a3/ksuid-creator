# Changelog

All notable changes to this project will be documented in this file.

## [Unreleased]

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

[unreleased]: https://github.com/f4b6a3/ksuid-creator/compare/ksuid-creator-1.1.2...HEAD
[1.1.2]: https://github.com/f4b6a3/ksuid-creator/compare/ksuid-creator-1.1.1...ksuid-creator-1.1.2
[1.1.1]: https://github.com/f4b6a3/ksuid-creator/compare/ksuid-creator-1.1.0...ksuid-creator-1.1.1
[1.1.0]: https://github.com/f4b6a3/ksuid-creator/compare/ksuid-creator-1.0.1...ksuid-creator-1.1.0
[1.0.1]: https://github.com/f4b6a3/ksuid-creator/compare/ksuid-creator-1.0.0...ksuid-creator-1.0.1
[1.0.0]: https://github.com/f4b6a3/ksuid-creator/releases/tag/ksuid-creator-1.0.0
