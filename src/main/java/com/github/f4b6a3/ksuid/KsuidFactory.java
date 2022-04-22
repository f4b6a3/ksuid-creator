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
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A factory for generating KSUIDs.
 */
public final class KsuidFactory {

	private final Clock clock; // for tests
	private final Function<Instant, Ksuid> ksuidFunction;

	protected static final int PRECISION_MILLISECOND = 1;
	protected static final int PRECISION_MICROSECOND = 2;
	protected static final int PRECISION_NANOSECOND = 3;

	public KsuidFactory() {
		this(new KsuidFunction(getRandomSupplier(null)));
	}

	protected KsuidFactory(Function<Instant, Ksuid> ksuidFunction) {
		this(ksuidFunction, null);
	}

	protected KsuidFactory(Function<Instant, Ksuid> ksuidFunction, Clock clock) {
		this.ksuidFunction = ksuidFunction;
		this.clock = clock != null ? clock : Clock.systemUTC();
	}

	/**
	 * Returns a new Segment's KSUID factory.
	 * 
	 * It is equivalent to {@code new KsuidFactory()}.
	 * 
	 * @return {@link KsuidFactory}
	 */
	public static KsuidFactory newInstance() {
		return newInstance(getRandomSupplier(null));
	}

	/**
	 * Returns a new Segment's KSUID factory.
	 * 
	 * @param random a {@link Random} generator
	 * @return {@link KsuidFactory}
	 */
	public static KsuidFactory newInstance(Random random) {
		return newInstance(getRandomSupplier(random));
	}

	/**
	 * Returns a new Segment's KSUID factory.
	 * 
	 * @param randomSupplier a random supplier that returns an array of 16 bytes
	 * @return {@link KsuidFactory}
	 */
	public static KsuidFactory newInstance(Supplier<byte[]> randomSupplier) {
		return new KsuidFactory(new KsuidFunction(randomSupplier));
	}

	/**
	 * Returns a new Sub-second KSUID factory.
	 * 
	 * @return {@link KsuidFactory}
	 */
	public static KsuidFactory newSubsecondInstance() {
		return newSubsecondInstance(getRandomSupplier(null));
	}

	/**
	 * Returns a new Sub-second KSUID factory.
	 * 
	 * @param random a {@link Random} generator
	 * @return {@link KsuidFactory}
	 */
	public static KsuidFactory newSubsecondInstance(Random random) {
		return newSubsecondInstance(getRandomSupplier(random));
	}

	/**
	 * Returns a new Sub-second KSUID factory.
	 * 
	 * @param randomSupplier a random supplier that returns an array of 16 bytes
	 * @return {@link KsuidFactory}
	 */
	public static KsuidFactory newSubsecondInstance(Supplier<byte[]> randomSupplier) {
		return new KsuidFactory(getSubsecondFunction(randomSupplier));
	}

	/**
	 * Returns a new Monotonic KSUID factory.
	 * 
	 * @return {@link KsuidFactory}
	 */
	public static KsuidFactory newMonotonicInstance() {
		return newMonotonicInstance(getRandomSupplier(null));
	}

	/**
	 * Returns a new Monotonic KSUID factory.
	 * 
	 * @param random a {@link Random} generator
	 * @return {@link KsuidFactory}
	 */
	public static KsuidFactory newMonotonicInstance(Random random) {
		return newMonotonicInstance(getRandomSupplier(random));
	}

	/**
	 * Returns a new Monotonic KSUID factory.
	 * 
	 * @param randomSupplier a random supplier that returns an array of 16 bytes
	 * @return {@link KsuidFactory}
	 */
	public static KsuidFactory newMonotonicInstance(Supplier<byte[]> randomSupplier) {
		return new KsuidFactory(new MonotonicFunction(randomSupplier));
	}

	/**
	 * Returns a new Monotonic KSUID factory.
	 * 
	 * @param randomSupplier a random supplier that returns an array of 16 bytes
	 * @param clock          a custom clock instance for tests
	 * @return {@link KsuidFactory}
	 */
	protected static KsuidFactory newMonotonicInstance(Supplier<byte[]> randomSupplier, Clock clock) {
		return new KsuidFactory(new MonotonicFunction(randomSupplier), clock);
	}

	/**
	 * Create a KSUID.
	 * 
	 * @return a KSUID
	 */
	public Ksuid create() {
		return ksuidFunction.apply(clock.instant());
	}

	/**
	 * Create a KSUID with a given instant.
	 * 
	 * @param instant an instant
	 * @return a KSUID
	 */
	public Ksuid create(final Instant instant) {
		return ksuidFunction.apply(instant);
	}

	/**
	 * Function that creates Segment's KSUIDs.
	 */
	protected static final class KsuidFunction implements Function<Instant, Ksuid> {

		// a function that must return 16 bytes
		private Supplier<byte[]> randomSupplier;

		public KsuidFunction(Supplier<byte[]> randomSupplier) {
			this.randomSupplier = randomSupplier;
		}

		@Override
		public Ksuid apply(final Instant instant) {
			return new Ksuid(instant.getEpochSecond(), randomSupplier.get());
		}
	}

	/**
	 * Function that creates Monotonic KSUIDs.
	 */
	protected static final class MonotonicFunction implements Function<Instant, Ksuid> {

		private long lastTime;
		private Ksuid lastKsuid;

		// Used to preserve monotonicity when the system clock is
		// adjusted by NTP after a small clock drift or when the
		// system clock jumps back by 1 second due to leap second.
		protected static final long CLOCK_DRIFT_TOLERANCE = 10;

		// a function that must return 16 bytes
		private Supplier<byte[]> randomSupplier;

		public MonotonicFunction(Supplier<byte[]> randomSupplier) {
			this.randomSupplier = randomSupplier;

			// initialize internal state
			this.lastTime = Instant.now().getEpochSecond();
			this.lastKsuid = new Ksuid(lastTime, randomSupplier.get());
		}

		@Override
		public Ksuid apply(final Instant instant) {

			long time = instant.getEpochSecond();

			// Check if the current time is the same as the previous time or has moved
			// backwards after a small system clock adjustment or after a leap second.
			// Drift tolerance = (previous_time - 10s) < current_time <= previous_time
			if ((time > this.lastTime - CLOCK_DRIFT_TOLERANCE) && (time <= this.lastTime)) {
				// increment the previous payload
				lastKsuid = lastKsuid.increment();
			} else {
				lastTime = time;
				lastKsuid = new Ksuid(time, randomSupplier.get());
			}

			return new Ksuid(lastKsuid);
		}
	}

	/**
	 * Returns a payload function with SUB-SECOND precision.
	 * 
	 * @param randomSupplier a random supplier
	 * @return a function that returns 16 bytes
	 */
	protected static Function<Instant, Ksuid> getSubsecondFunction(Supplier<byte[]> randomSupplier) {

		// try to detect the sub-second precision
		final int precision = getSubsecondPrecision(Clock.systemUTC());

		switch (precision) {
		case PRECISION_MILLISECOND:
			return new MillisecondFunction(randomSupplier);
		case PRECISION_MICROSECOND:
			return new MicrosecondFunction(randomSupplier);
		case PRECISION_NANOSECOND:
			return new NanosecondFunction(randomSupplier);
		default:
			return new MillisecondFunction(randomSupplier);
		}
	}

	/**
	 * Function that creates Sub-second KSUIDs with MILLISECOND precision.
	 */
	protected static final class MillisecondFunction implements Function<Instant, Ksuid> {

		// a function that must return 16 bytes
		private Supplier<byte[]> randomSupplier;

		public MillisecondFunction(Supplier<byte[]> randomSupplier) {
			this.randomSupplier = randomSupplier;
		}

		@Override
		public Ksuid apply(final Instant instant) {

			// fill the payload with random bytes
			final byte[] payload = randomSupplier.get();

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
	protected static final class MicrosecondFunction implements Function<Instant, Ksuid> {

		// a function that must return 16 bytes
		private Supplier<byte[]> randomSupplier;

		public MicrosecondFunction(Supplier<byte[]> randomSupplier) {
			this.randomSupplier = randomSupplier;
		}

		@Override
		public Ksuid apply(final Instant instant) {

			// fill the payload with random bytes
			final byte[] payload = randomSupplier.get();

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
	protected static final class NanosecondFunction implements Function<Instant, Ksuid> {

		// a function that must return 16 bytes
		private Supplier<byte[]> randomSupplier;

		public NanosecondFunction(Supplier<byte[]> randomSupplier) {
			this.randomSupplier = randomSupplier;
		}

		@Override
		public Ksuid apply(final Instant instant) {

			// fill the payload with random bytes
			final byte[] payload = randomSupplier.get();

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
	protected static int getSubsecondPrecision(Clock clock) {

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

	/**
	 * Returns a random supplier.
	 * 
	 * @param random a {@link Random} generator
	 * @return a function that returns 16 bytes
	 */
	protected static Supplier<byte[]> getRandomSupplier(Random random) {
		Random entropy = random != null ? random : new SecureRandom();
		return () -> {
			byte[] payload = new byte[Ksuid.PAYLOAD_BYTES];
			entropy.nextBytes(payload);
			return payload;
		};
	}
}
