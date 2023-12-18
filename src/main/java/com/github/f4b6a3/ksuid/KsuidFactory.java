/*
 * MIT License
 * 
 * Copyright (c) 2021-2022 Fabio Lima
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.github.f4b6a3.ksuid;

import java.security.SecureRandom;
import java.time.Clock;
import java.time.Instant;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

/**
 * A class that actually generates KSUIDs.
 * <p>
 * This class is used by {@link KsuidCreator}.
 * <p>
 * You can use this class if you need to use a specific random generator
 * strategy. However, most people just need {@link KsuidCreator}.
 * <p>
 * Instances of this class can behave in one of three ways: non-monotonic
 * (Segment's), monotonic and sub-second.
 * <p>
 * If the factory is monotonic, the random component is incremented by 1 If more
 * than one KSUID is generated within the same second.
 * <p>
 * If the factory is sub-second, the time precision is detected at runtime.
 * <p>
 * The maximum KSUID that can be generated per second is 2^128.
 */
public final class KsuidFactory {

	private final Supplier<Instant> instantFunction;
	private final Function<Instant, Ksuid> ksuidFunction;
	private final ReentrantLock lock = new ReentrantLock();

	static final int PRECISION_MILLISECOND = 1;
	static final int PRECISION_MICROSECOND = 2;
	static final int PRECISION_NANOSECOND = 3;

	// ******************************
	// Constructors
	// ******************************

	/**
	 * Default constructor.
	 */
	public KsuidFactory() {
		this(new KsuidFunction(IRandom.newInstance()));
	}

	KsuidFactory(Function<Instant, Ksuid> ksuidFunction) {
		this(ksuidFunction, null);
	}

	KsuidFactory(Function<Instant, Ksuid> ksuidFunction, Supplier<Instant> instantFunction) {
		this.ksuidFunction = ksuidFunction;
		this.instantFunction = instantFunction != null ? instantFunction : () -> Clock.systemUTC().instant();
	}

	/**
	 * Returns a new Segment's KSUID factory.
	 * <p>
	 * It is equivalent to {@code new KsuidFactory()}.
	 * 
	 * @return {@link KsuidFactory}
	 */
	public static KsuidFactory newInstance() {
		return new KsuidFactory();
	}

	/**
	 * Returns a new Segment's KSUID factory.
	 * 
	 * @param random a {@link Random} generator
	 * @return {@link KsuidFactory}
	 */
	public static KsuidFactory newInstance(Random random) {
		return new KsuidFactory(new KsuidFunction(IRandom.newInstance(random)));
	}

	/**
	 * Returns a new Segment's KSUID factory.
	 * 
	 * @param randomFunction a random function that returns a long value
	 * @return {@link KsuidFactory}
	 */
	public static KsuidFactory newInstance(LongSupplier randomFunction) {
		return new KsuidFactory(new KsuidFunction(IRandom.newInstance(randomFunction)));
	}

	/**
	 * Returns a new Segment's KSUID factory.
	 * 
	 * @param randomFunction a random function that returns a byte array
	 * @return {@link KsuidFactory}
	 */
	public static KsuidFactory newInstance(IntFunction<byte[]> randomFunction) {
		return new KsuidFactory(new KsuidFunction(IRandom.newInstance(randomFunction)));
	}

	/**
	 * Returns a new Sub-second KSUID factory.
	 * 
	 * @return {@link KsuidFactory}
	 */
	public static KsuidFactory newSubsecondInstance() {
		return new KsuidFactory(getSubsecondFunction(new ByteRandom()));
	}

	/**
	 * Returns a new Sub-second KSUID factory.
	 * 
	 * @param random a {@link Random} generator
	 * @return {@link KsuidFactory}
	 */
	public static KsuidFactory newSubsecondInstance(Random random) {
		return new KsuidFactory(getSubsecondFunction(IRandom.newInstance(random)));
	}

	/**
	 * Returns a new Sub-second KSUID factory.
	 * 
	 * @param randomFunction a random function that returns a long value
	 * @return {@link KsuidFactory}
	 */
	public static KsuidFactory newSubsecondInstance(LongSupplier randomFunction) {
		return new KsuidFactory(getSubsecondFunction(new LongRandom(randomFunction)));
	}

	/**
	 * Returns a new Sub-second KSUID factory.
	 * 
	 * @param randomFunction a random function that returns a byte array
	 * @return {@link KsuidFactory}
	 */
	public static KsuidFactory newSubsecondInstance(IntFunction<byte[]> randomFunction) {
		return new KsuidFactory(getSubsecondFunction(new ByteRandom(randomFunction)));
	}

	/**
	 * Returns a new Sub-second KSUID factory.
	 * 
	 * @param random          a {@link Random} generator
	 * @param instantFunction an {@link Instant} supplier
	 * @return {@link KsuidFactory}
	 */
	public static KsuidFactory newSubsecondInstance(Random random, Supplier<Instant> instantFunction) {
		return new KsuidFactory(getSubsecondFunction(IRandom.newInstance(random)), instantFunction);
	}

	/**
	 * Returns a new Sub-second KSUID factory.
	 * 
	 * @param randomFunction  a random function that returns a long value
	 * @param instantFunction an {@link Instant} supplier
	 * @return {@link KsuidFactory}
	 */
	public static KsuidFactory newSubsecondInstance(LongSupplier randomFunction, Supplier<Instant> instantFunction) {
		return new KsuidFactory(getSubsecondFunction(new LongRandom(randomFunction)), instantFunction);
	}

	/**
	 * Returns a new Sub-second KSUID factory.
	 * 
	 * @param randomFunction  a random function that returns a byte array
	 * @param instantFunction a custom clock instance for tests
	 * @return {@link KsuidFactory}
	 */
	public static KsuidFactory newSubsecondInstance(IntFunction<byte[]> randomFunction,
			Supplier<Instant> instantFunction) {
		return new KsuidFactory(getSubsecondFunction(new ByteRandom(randomFunction)), instantFunction);
	}

	/**
	 * Returns a new Monotonic KSUID factory.
	 * 
	 * @return {@link KsuidFactory}
	 */
	public static KsuidFactory newMonotonicInstance() {
		return new KsuidFactory(new MonotonicFunction(IRandom.newInstance()));
	}

	/**
	 * Returns a new Monotonic KSUID factory.
	 * 
	 * @param random a {@link Random} generator
	 * @return {@link KsuidFactory}
	 */
	public static KsuidFactory newMonotonicInstance(Random random) {
		return new KsuidFactory(new MonotonicFunction(IRandom.newInstance(random)));
	}

	/**
	 * Returns a new Monotonic KSUID factory.
	 * 
	 * @param randomFunction a random function that returns a long value
	 * @return {@link KsuidFactory}
	 */
	public static KsuidFactory newMonotonicInstance(LongSupplier randomFunction) {
		return new KsuidFactory(new MonotonicFunction(IRandom.newInstance(randomFunction)));
	}

	/**
	 * Returns a new Monotonic KSUID factory.
	 * 
	 * @param randomFunction a random function that returns a byte array
	 * @return {@link KsuidFactory}
	 */
	public static KsuidFactory newMonotonicInstance(IntFunction<byte[]> randomFunction) {
		return new KsuidFactory(new MonotonicFunction(IRandom.newInstance(randomFunction)));
	}

	/**
	 * Returns a new Monotonic KSUID factory.
	 * 
	 * @param random          a {@link Random} generator
	 * @param instantFunction an {@link Instant} supplier
	 * @return {@link KsuidFactory}
	 */
	public static KsuidFactory newMonotonicInstance(Random random, Supplier<Instant> instantFunction) {
		return new KsuidFactory(new MonotonicFunction(IRandom.newInstance(random)), instantFunction);
	}

	/**
	 * Returns a new Monotonic KSUID factory.
	 * 
	 * @param randomFunction  a random function that returns a long value
	 * @param instantFunction an {@link Instant} supplier
	 * @return {@link KsuidFactory}
	 */
	public static KsuidFactory newMonotonicInstance(LongSupplier randomFunction, Supplier<Instant> instantFunction) {
		return new KsuidFactory(new MonotonicFunction(IRandom.newInstance(randomFunction)), instantFunction);
	}

	/**
	 * Returns a new Monotonic KSUID factory.
	 * 
	 * @param randomFunction  a random function that returns a byte array
	 * @param instantFunction an {@link Instant} supplier
	 * @return {@link KsuidFactory}
	 */
	public static KsuidFactory newMonotonicInstance(IntFunction<byte[]> randomFunction,
			Supplier<Instant> instantFunction) {
		return new KsuidFactory(new MonotonicFunction(IRandom.newInstance(randomFunction)), instantFunction);
	}

	// ******************************
	// Public methods
	// ******************************

	/**
	 * Create a KSUID.
	 * 
	 * @return a KSUID
	 */
	public Ksuid create() {
		return create(instantFunction.get());
	}

	/**
	 * Create a KSUID with a given instant.
	 * 
	 * @param instant an instant
	 * @return a KSUID
	 */
	public Ksuid create(final Instant instant) {
		lock.lock();
		try {
			return ksuidFunction.apply(instant);
		} finally {
			lock.unlock();
		}
	}

	// ******************************
	// Package-private inner classes
	// ******************************

	/**
	 * Function that creates Segment's KSUIDs.
	 */
	static final class KsuidFunction implements Function<Instant, Ksuid> {

		private final IRandom random;

		public KsuidFunction(IRandom random) {
			this.random = random;
		}

		@Override
		public Ksuid apply(final Instant instant) {
			return new Ksuid(instant.getEpochSecond(), random.nextBytes(Ksuid.PAYLOAD_BYTES));
		}
	}

	/**
	 * Function that creates Monotonic KSUIDs.
	 */
	static final class MonotonicFunction implements Function<Instant, Ksuid> {

		private Ksuid lastKsuid;

		private final IRandom random;

		// Used to preserve monotonicity when the system clock is
		// adjusted by NTP after a small clock drift or when the
		// system clock jumps back by 1 second due to leap second.
		protected static final long CLOCK_DRIFT_TOLERANCE = 10;

		protected MonotonicFunction(IRandom random) {
			this.random = random;
			// initialize internal state
			this.lastKsuid = new Ksuid(0L, random.nextBytes(Ksuid.PAYLOAD_BYTES));
		}

		@Override
		public Ksuid apply(final Instant instant) {

			final long time = instant.getEpochSecond();
			final long lastTime = lastKsuid.getTime();

			// Check if the current time is the same as the previous time or has moved
			// backwards after a small system clock adjustment or after a leap second.
			// Drift tolerance = (previous_time - 10s) < current_time <= previous_time
			if ((time > lastTime - CLOCK_DRIFT_TOLERANCE) && (time <= lastTime)) {
				// increment the previous payload
				lastKsuid = lastKsuid.increment();
			} else {
				lastKsuid = new Ksuid(time, random.nextBytes(Ksuid.PAYLOAD_BYTES));
			}

			return new Ksuid(lastKsuid);
		}
	}

	/**
	 * Returns a payload function with SUB-SECOND precision.
	 * 
	 * @param random a random generator
	 * @return a function that returns a byte array
	 */
	static Function<Instant, Ksuid> getSubsecondFunction(IRandom random) {

		// try to detect the sub-second precision
		final int precision = getSubsecondPrecision(Clock.systemUTC());

		switch (precision) {
		case PRECISION_MILLISECOND:
			return new MillisecondFunction(random);
		case PRECISION_MICROSECOND:
			return new MicrosecondFunction(random);
		case PRECISION_NANOSECOND:
			return new NanosecondFunction(random);
		default:
			return new MillisecondFunction(random);
		}
	}

	/**
	 * Function that creates Sub-second KSUIDs with MILLISECOND precision.
	 */
	static final class MillisecondFunction implements Function<Instant, Ksuid> {

		private final IRandom random;

		public MillisecondFunction(IRandom random) {
			this.random = random;
		}

		@Override
		public Ksuid apply(final Instant instant) {

			// fill the payload with random bytes
			final byte[] payload = random.nextBytes(Ksuid.PAYLOAD_BYTES);

			// insert milliseconds into the payload
			final int milliseconds = instant.getNano() / 1000000;
			final int subsecs = (milliseconds << 6) | (payload[1] & 0b00111111);
			payload[0] = (byte) ((subsecs >>> 0x08) & 0xff);
			payload[1] = (byte) ((subsecs >>> 0x00) & 0xff);

			return new Ksuid(instant.getEpochSecond(), payload);
		}
	}

	/**
	 * Function that creates Sub-second KSUIDs with MICROSECOND precision.
	 */
	static final class MicrosecondFunction implements Function<Instant, Ksuid> {

		private final IRandom random;

		public MicrosecondFunction(IRandom random) {
			this.random = random;
		}

		@Override
		public Ksuid apply(final Instant instant) {

			// fill the payload with random bytes
			final byte[] payload = random.nextBytes(Ksuid.PAYLOAD_BYTES);

			// insert microseconds into the payload
			final int microseconds = instant.getNano() / 1000;
			final int subsecs = (microseconds << 4) | (payload[2] & 0b00001111);
			payload[0] = (byte) ((subsecs >>> 0x10) & 0xff);
			payload[1] = (byte) ((subsecs >>> 0x08) & 0xff);
			payload[2] = (byte) ((subsecs >>> 0x00) & 0xff);

			return new Ksuid(instant.getEpochSecond(), payload);
		}
	}

	/**
	 * Function that creates Sub-second KSUIDs with NANOSECOND precision.
	 */
	static final class NanosecondFunction implements Function<Instant, Ksuid> {

		private final IRandom random;

		public NanosecondFunction(IRandom random) {
			this.random = random;
		}

		@Override
		public Ksuid apply(final Instant instant) {

			// fill the payload with random bytes
			final byte[] payload = random.nextBytes(Ksuid.PAYLOAD_BYTES);

			// insert nanoseconds into the payload
			final int nanoseconds = instant.getNano();
			final int subsecs = (nanoseconds << 2) | (payload[3] & 0b00000011);
			payload[0] = (byte) ((subsecs >>> 0x18) & 0xff);
			payload[1] = (byte) ((subsecs >>> 0x10) & 0xff);
			payload[2] = (byte) ((subsecs >>> 0x08) & 0xff);
			payload[3] = (byte) ((subsecs >>> 0x00) & 0xff);

			return new Ksuid(instant.getEpochSecond(), payload);
		}
	}

	/**
	 * Returns the instant precision detected.
	 * 
	 * @param clock a custom clock instance
	 * @return the precision
	 */
	static int getSubsecondPrecision(Clock clock) {

		int best = 0;
		int loop = 3; // the best of 3

		for (int i = 0; i < loop; i++) {

			if (i > 0) {
				try {
					// try to wait 1ns
					Thread.sleep(0, 1);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}

			int x = 0;
			int nanosecond = clock.instant().getNano();

			if (nanosecond % 1000 != 0) {
				x = PRECISION_NANOSECOND; // nanosecond
			} else if (nanosecond % 1_000_000 != 0) {
				x = PRECISION_MICROSECOND; // microsecond
			} else {
				x = PRECISION_MILLISECOND; // millisecond
			}

			best = Math.max(best, x);
		}

		return best;
	}

	static interface IRandom {

		public long nextLong();

		public byte[] nextBytes(int length);

		static IRandom newInstance() {
			return new ByteRandom();
		}

		static IRandom newInstance(Random random) {
			if (random == null) {
				return new ByteRandom();
			} else {
				if (random instanceof SecureRandom) {
					return new ByteRandom(random);
				} else {
					return new LongRandom(random);
				}
			}
		}

		static IRandom newInstance(LongSupplier randomFunction) {
			return new LongRandom(randomFunction);
		}

		static IRandom newInstance(IntFunction<byte[]> randomFunction) {
			return new ByteRandom(randomFunction);
		}
	}

	static class LongRandom implements IRandom {

		private final LongSupplier randomFunction;

		public LongRandom() {
			this(newRandomFunction(null));
		}

		public LongRandom(Random random) {
			this(newRandomFunction(random));
		}

		public LongRandom(LongSupplier randomFunction) {
			this.randomFunction = randomFunction != null ? randomFunction : newRandomFunction(null);
		}

		@Override
		public long nextLong() {
			return randomFunction.getAsLong();
		}

		@Override
		public byte[] nextBytes(int length) {

			int shift = 0;
			long random = 0;
			final byte[] bytes = new byte[length];

			for (int i = 0; i < length; i++) {
				if (shift < Byte.SIZE) {
					shift = Long.SIZE;
					random = randomFunction.getAsLong();
				}
				shift -= Byte.SIZE; // 56, 48, 40...
				bytes[i] = (byte) (random >>> shift);
			}

			return bytes;
		}

		protected static LongSupplier newRandomFunction(Random random) {
			final Random entropy = random != null ? random : new SecureRandom();
			return entropy::nextLong;
		}
	}

	static class ByteRandom implements IRandom {

		private final IntFunction<byte[]> randomFunction;

		public ByteRandom() {
			this(newRandomFunction(null));
		}

		public ByteRandom(Random random) {
			this(newRandomFunction(random));
		}

		public ByteRandom(IntFunction<byte[]> randomFunction) {
			this.randomFunction = randomFunction != null ? randomFunction : newRandomFunction(null);
		}

		@Override
		public long nextLong() {
			long number = 0;
			byte[] bytes = this.randomFunction.apply(Long.BYTES);
			for (int i = 0; i < Long.BYTES; i++) {
				number = (number << 8) | (bytes[i] & 0xff);
			}
			return number;
		}

		@Override
		public byte[] nextBytes(int length) {
			return this.randomFunction.apply(length);
		}

		protected static IntFunction<byte[]> newRandomFunction(Random random) {
			final Random entropy = random != null ? random : new SecureRandom();
			return (final int length) -> {
				final byte[] bytes = new byte[length];
				entropy.nextBytes(bytes);
				return bytes;
			};
		}
	}
}
