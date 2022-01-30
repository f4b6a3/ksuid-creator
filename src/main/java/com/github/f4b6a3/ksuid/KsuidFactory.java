/*
 * MIT License
 * 
 * Copyright (c) 2021 Fabio Lima
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
import java.time.Instant;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A factory for generating KSUIDs.
 */
public final class KsuidFactory {

	// it must return an array of 16 bytes
	private final Function<Instant, byte[]> payloadFunction;

	protected static final int PRECISION_MILLISECOND = 1;
	protected static final int PRECISION_MICROSECOND = 2;
	protected static final int PRECISION_NANOSECOND = 3;

	public KsuidFactory() {
		this(getPayloadFunction(getRandomSupplier(new SecureRandom())));
	}

	protected KsuidFactory(Function<Instant, byte[]> payloadFunction) {
		this.payloadFunction = payloadFunction;
	}

	/**
	 * Returns a new KSUID factory.
	 * 
	 * It is equivalent to {@code new KsuidFactory()}.
	 * 
	 * @return {@link KsuidFactory}
	 */
	public static KsuidFactory newInstance() {
		return newInstance(new SecureRandom());
	}

	/**
	 * Returns a new KSUID factory.
	 * 
	 * @param random a {@link Random} generator
	 * @return {@link KsuidFactory}
	 */
	public static KsuidFactory newInstance(Random random) {
		return newInstance(getRandomSupplier(random));
	}

	/**
	 * Returns a new KSUID factory.
	 * 
	 * @param randomSupplier a random supplier that returns an array of 16 bytes
	 * @return {@link KsuidFactory}
	 */
	public static KsuidFactory newInstance(Supplier<byte[]> randomSupplier) {
		return new KsuidFactory(getPayloadFunction(randomSupplier));
	}

	/**
	 * Returns a new KSUID factory with sub-second precision.
	 * 
	 * @return {@link KsuidFactory}
	 */
	public static KsuidFactory newSubsecondInstance() {
		return newSubsecondInstance(new SecureRandom());
	}

	/**
	 * Returns a new KSUID factory with sub-second precision.
	 * 
	 * @param random a {@link Random} generator
	 * @return {@link KsuidFactory}
	 */
	public static KsuidFactory newSubsecondInstance(Random random) {
		return newSubsecondInstance(getRandomSupplier(random));
	}

	/**
	 * Returns a new KSUID factory with sub-second precision.
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
		return newMonotonicInstance(new SecureRandom());
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
		return new KsuidFactory(getMonotonicFunction(randomSupplier));
	}

	/**
	 * Create a KSUID.
	 * 
	 * @return a KSUID
	 */
	public Ksuid create() {
		final Instant instant = Instant.now();
		return new Ksuid(instant.getEpochSecond(), payloadFunction.apply(instant));
	}

	/**
	 * Create a KSUID with a given instant.
	 * 
	 * @param instant an instant
	 * @return a KSUID
	 */
	public Ksuid create(final Instant instant) {
		return new Ksuid(instant.getEpochSecond(), payloadFunction.apply(instant));
	}

	/**
	 * Returns a random payload function.
	 * 
	 * @param randomSupplier a random supplier
	 * @return a function that returns 16 bytes
	 */
	protected static Function<Instant, byte[]> getPayloadFunction(Supplier<byte[]> randomSupplier) {
		return (final Instant instant) -> {
			return randomSupplier.get();
		};
	}

	/**
	 * Returns a monotonic payload function.
	 * 
	 * @param randomSupplier a random supplier
	 * @return a function that returns 16 bytes
	 */
	protected static Function<Instant, byte[]> getMonotonicFunction(Supplier<byte[]> randomSupplier) {
		return new Function<Instant, byte[]>() {

			private long lastSeconds = -1;
			private Ksuid lastKsuid = null;

			@Override
			public synchronized byte[] apply(final Instant instant) {

				final long seconds = instant.getEpochSecond();

				if (seconds == lastSeconds) {
					// increment the previous payload
					lastKsuid = lastKsuid.increment();
				} else {
					// get a brand new random payload
					byte[] payload = randomSupplier.get();
					lastKsuid = new Ksuid(seconds, payload);
				}

				lastSeconds = seconds;
				return lastKsuid.getPayload();
			}
		};
	}

	/**
	 * Returns a payload function with SUB-SECOND precision.
	 * 
	 * @param randomSupplier a random supplier
	 * @return a function that returns 16 bytes
	 */
	protected static Function<Instant, byte[]> getSubsecondFunction(Supplier<byte[]> randomSupplier) {

		// try to detect the sub-second precision
		final int precision = getSubsecondPrecision(Instant::now);

		switch (precision) {
		case PRECISION_MILLISECOND:
			return getMillisecondFunction(randomSupplier);
		case PRECISION_MICROSECOND:
			return getMicrosecondFunction(randomSupplier);
		case PRECISION_NANOSECOND:
			return getNanosecondFunction(randomSupplier);
		default:
			return getMillisecondFunction(randomSupplier);
		}
	}

	/**
	 * Returns a payload function with MILLISECOND precision.
	 * 
	 * @param randomSupplier a random supplier
	 * @return a function that returns 16 bytes
	 */
	protected static Function<Instant, byte[]> getMillisecondFunction(Supplier<byte[]> randomSupplier) {
		return (final Instant instant) -> {

			// fill the payload with random bytes
			final byte[] payload = randomSupplier.get();

			// insert milliseconds into the payload
			final int milliseconds = instant.getNano() / 1000000;
			final int subsecs = (milliseconds << 6) | (payload[1] & 0b00111111);
			payload[0] = (byte) ((subsecs >>> 0x08) & 0xff);
			payload[1] = (byte) ((subsecs >>> 0x00) & 0xff);

			return payload;
		};
	}

	/**
	 * Returns a payload function with MICROSECOND precision.
	 * 
	 * @param randomSupplier a random supplier
	 * @return a function that returns 16 bytes
	 */
	protected static Function<Instant, byte[]> getMicrosecondFunction(Supplier<byte[]> randomSupplier) {
		return (final Instant instant) -> {

			// fill the payload with random bytes
			final byte[] payload = randomSupplier.get();

			// insert microseconds into the payload
			final int microseconds = instant.getNano() / 1000;
			final int subsecs = (microseconds << 4) | (payload[2] & 0b00001111);
			payload[0] = (byte) ((subsecs >>> 0x10) & 0xff);
			payload[1] = (byte) ((subsecs >>> 0x08) & 0xff);
			payload[2] = (byte) ((subsecs >>> 0x00) & 0xff);

			return payload;
		};
	}

	/**
	 * Returns a payload function with NANOSECOND precision.
	 * 
	 * @param randomSupplier a random supplier
	 * @return a function that returns 16 bytes
	 */
	protected static Function<Instant, byte[]> getNanosecondFunction(Supplier<byte[]> randomSupplier) {
		return (final Instant instant) -> {

			// fill the payload with random bytes
			final byte[] payload = randomSupplier.get();

			// insert nanoseconds into the payload
			final int nanoseconds = instant.getNano();
			final int subsecs = (nanoseconds << 2) | (payload[3] & 0b00000011);
			payload[0] = (byte) ((subsecs >>> 0x18) & 0xff);
			payload[1] = (byte) ((subsecs >>> 0x10) & 0xff);
			payload[2] = (byte) ((subsecs >>> 0x08) & 0xff);
			payload[3] = (byte) ((subsecs >>> 0x00) & 0xff);

			return payload;
		};
	}

	/**
	 * Returns the instant precision detected.
	 * 
	 * @param instantSupplier an instant supplier (used for tests).
	 * @return the precision
	 */
	protected static int getSubsecondPrecision(Supplier<Instant> instantSupplier) {

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
			int nanosecond = instantSupplier.get().getNano();

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
		return () -> {
			byte[] payload = new byte[Ksuid.PAYLOAD_BYTES];
			random.nextBytes(payload);
			return payload;
		};
	}
}
