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

	private final Function<Instant, Ksuid> ksuidFunction;

	public KsuidFactory() {
		this.ksuidFunction = new KsuidFunction();
	}

	private KsuidFactory(Function<Instant, Ksuid> ksuidFunction) {
		this.ksuidFunction = ksuidFunction;
	}

	/**
	 * Returns a new KSUID factory.
	 * 
	 * It is equivalent to {@code new KsuidFactory()}.
	 * 
	 * @return {@link KsuidFactory}
	 */
	public static KsuidFactory newInstance() {
		return new KsuidFactory(new KsuidFunction());
	}

	/**
	 * Returns a new KSUID factory.
	 * 
	 * @param random a {@link Random} generator
	 * @return {@link KsuidFactory}
	 */
	public static KsuidFactory newInstance(Random random) {
		return new KsuidFactory(new KsuidFunction(random));
	}

	/**
	 * Returns a new KSUID factory.
	 * 
	 * The given random supplier must return an array of 16 bytes.
	 * 
	 * @param randomSupplier a random supplier that returns 16 bytes
	 * @return {@link KsuidFactory}
	 */
	public static KsuidFactory newInstance(Supplier<byte[]> randomSupplier) {
		return new KsuidFactory(new KsuidFunction(randomSupplier));
	}

	/**
	 * Returns a new KSUID factory with millisecond precision.
	 * 
	 * @return {@link KsuidFactory}
	 */
	public static KsuidFactory newMsInstance() {
		return new KsuidFactory(new MsKsuidFunction());
	}

	/**
	 * Returns a new KSUID factory with millisecond precision.
	 * 
	 * @param random a {@link Random} generator
	 * @return {@link KsuidFactory}
	 */
	public static KsuidFactory newMsInstance(Random random) {
		return new KsuidFactory(new MsKsuidFunction(random));
	}

	/**
	 * Returns a new KSUID factory with millisecond precision.
	 * 
	 * The given random supplier must return an array of 16 bytes.
	 * 
	 * @param randomSupplier a random supplier that returns 16 bytes
	 * @return {@link KsuidFactory}
	 */
	public static KsuidFactory newMsInstance(Supplier<byte[]> randomSupplier) {
		return new KsuidFactory(new MsKsuidFunction(randomSupplier));
	}

	/**
	 * Returns a new KSUID factory with microsecond precision (JDK 9+).
	 * 
	 * @return {@link KsuidFactory}
	 */
	public static KsuidFactory newUsInstance() {
		return new KsuidFactory(new UsKsuidFunction());
	}

	/**
	 * Returns a new KSUID factory with microsecond precision (JDK 9+).
	 * 
	 * @param random a {@link Random} generator
	 * @return {@link KsuidFactory}
	 */
	public static KsuidFactory newUsInstance(Random random) {
		return new KsuidFactory(new UsKsuidFunction(random));
	}

	/**
	 * Returns a new KSUID factory with microsecond precision (JDK 9+).
	 * 
	 * The given random supplier must return an array of 16 bytes.
	 * 
	 * @param randomSupplier a random supplier that returns 16 bytes
	 * @return {@link KsuidFactory}
	 */
	public static KsuidFactory newUsInstance(Supplier<byte[]> randomSupplier) {
		return new KsuidFactory(new UsKsuidFunction(randomSupplier));
	}

	/**
	 * Returns a new KSUID factory with nanosecond precision (JDK 9+).
	 * 
	 * Check if the target runtime provides nanosecond precision:
	 * {@code System.out.println(Instant.now().getNano());}
	 * 
	 * Read: https://stackoverflow.com/questions/1712205
	 * 
	 * @return {@link KsuidFactory}
	 */
	public static KsuidFactory newNsInstance() {
		return new KsuidFactory(new NsKsuidFunction());
	}

	/**
	 * Returns a new KSUID factory with nanosecond precision (JDK 9+).
	 * 
	 * Check if the target runtime provides nanosecond precision:
	 * {@code System.out.println(Instant.now().getNano());}
	 * 
	 * Read: https://stackoverflow.com/questions/1712205
	 * 
	 * @param random a {@link Random} generator
	 * @return {@link KsuidFactory}
	 */
	public static KsuidFactory newNsInstance(Random random) {
		return new KsuidFactory(new NsKsuidFunction(random));
	}

	/**
	 * Returns a new KSUID factory with nanosecond precision (JDK 9+).
	 * 
	 * The given random supplier must return an array of 16 bytes.
	 * 
	 * Check if the target runtime provides nanosecond precision:
	 * {@code System.out.println(Instant.now().getNano());}
	 * 
	 * Read: https://stackoverflow.com/questions/1712205
	 * 
	 * @param randomSupplier a random supplier that returns 16 bytes
	 * @return {@link KsuidFactory}
	 */
	public static KsuidFactory newNsInstance(Supplier<byte[]> randomSupplier) {
		return new KsuidFactory(new NsKsuidFunction(randomSupplier));
	}

	/**
	 * Create a KSUID.
	 * 
	 * @return a KSUID
	 */
	public Ksuid create() {
		return this.ksuidFunction.apply(null);
	}

	/**
	 * Create a KSUID with a given instant.
	 * 
	 * @param instant an instant
	 * @return a KSUID
	 */
	public Ksuid create(Instant instant) {
		return this.ksuidFunction.apply(instant);
	}

	/**
	 * Function that creates KSUIDs.
	 */
	protected static final class KsuidFunction implements Function<Instant, Ksuid> {

		// it must return an array of 16 bytes
		private Supplier<byte[]> randomSupplier;

		public KsuidFunction() {
			this(new SecureRandom());
		}

		public KsuidFunction(Random random) {
			this(getRandomSupplier(random));
		}

		public KsuidFunction(Supplier<byte[]> randomSupplier) {
			this.randomSupplier = randomSupplier;
		}

		@Override
		public Ksuid apply(Instant instant) {

			final long seconds;

			if (instant == null) {
				seconds = System.currentTimeMillis() / 1000L;
			} else {
				seconds = instant.getEpochSecond();
			}

			return new Ksuid(seconds, randomSupplier.get());
		}
	}

	/**
	 * Function that creates KSUIDs with millisecond precision.
	 */
	protected static final class MsKsuidFunction implements Function<Instant, Ksuid> {

		// it must return an array of 16 bytes
		private Supplier<byte[]> randomSupplier;

		public MsKsuidFunction() {
			this(new SecureRandom());
		}

		public MsKsuidFunction(Random random) {
			this(getRandomSupplier(random));
		}

		public MsKsuidFunction(Supplier<byte[]> randomSupplier) {
			this.randomSupplier = randomSupplier;
		}

		@Override
		public Ksuid apply(Instant instant) {

			final long seconds;
			final int milliseconds;

			if (instant == null) {
				final long time = System.currentTimeMillis();
				seconds = time / 1000L;
				milliseconds = (int) (time % 1000L);
			} else {
				seconds = instant.getEpochSecond();
				milliseconds = instant.getNano() / 1000000;
			}

			final byte[] payload = randomSupplier.get();

			// insert milliseconds into payload
			final int subsecs = (milliseconds << 6) | (payload[1] & 0b00111111);
			payload[0] = (byte) ((subsecs >>> 0x08) & 0xff);
			payload[1] = (byte) ((subsecs >>> 0x00) & 0xff);

			return new Ksuid(seconds, payload);
		}
	}

	/**
	 * Function that creates KSUIDs with microsecond precision.
	 */
	protected static final class UsKsuidFunction implements Function<Instant, Ksuid> {

		// it must return an array of 16 bytes
		private Supplier<byte[]> randomSupplier;

		public UsKsuidFunction() {
			this(new SecureRandom());
		}

		public UsKsuidFunction(Random random) {
			this(getRandomSupplier(random));
		}

		public UsKsuidFunction(Supplier<byte[]> randomSupplier) {
			this.randomSupplier = randomSupplier;
		}

		@Override
		public Ksuid apply(Instant instant) {

			if (instant == null) {
				instant = Instant.now();
			}

			final long seconds = instant.getEpochSecond();
			final int microseconds = instant.getNano() / 1000;

			final byte[] payload = randomSupplier.get();

			// insert microseconds into payload
			final int subsecs = (microseconds << 4) | (payload[2] & 0b00001111);
			payload[0] = (byte) ((subsecs >>> 0x10) & 0xff);
			payload[1] = (byte) ((subsecs >>> 0x08) & 0xff);
			payload[2] = (byte) ((subsecs >>> 0x00) & 0xff);

			return new Ksuid(seconds, payload);
		}
	}

	/**
	 * Function that creates KSUIDs with nanosecond precision.
	 */
	protected static final class NsKsuidFunction implements Function<Instant, Ksuid> {

		// it must return an array of 16 bytes
		private Supplier<byte[]> randomSupplier;

		public NsKsuidFunction() {
			this(new SecureRandom());
		}

		public NsKsuidFunction(Random random) {
			this(getRandomSupplier(random));
		}

		public NsKsuidFunction(Supplier<byte[]> randomSupplier) {
			this.randomSupplier = randomSupplier;
		}

		@Override
		public Ksuid apply(Instant instant) {

			if (instant == null) {
				instant = Instant.now();
			}

			final long seconds = instant.getEpochSecond();
			final int nanoseconds = instant.getNano();

			final byte[] payload = randomSupplier.get();

			// insert nanoseconds into payload
			final int subsecs = (nanoseconds << 2) | (payload[3] & 0b00000011);
			payload[0] = (byte) ((subsecs >>> 0x18) & 0xff);
			payload[1] = (byte) ((subsecs >>> 0x10) & 0xff);
			payload[2] = (byte) ((subsecs >>> 0x08) & 0xff);
			payload[3] = (byte) ((subsecs >>> 0x00) & 0xff);

			return new Ksuid(seconds, payload);
		}
	}

	/**
	 * It instantiates a supplier that returns an array of 16 bytes.
	 * 
	 * @param random a {@link Random} generator
	 * @return a random supplier that returns 16 bytes
	 */
	protected static Supplier<byte[]> getRandomSupplier(Random random) {
		return () -> {
			byte[] payload = new byte[Ksuid.PAYLOAD_BYTES];
			random.nextBytes(payload);
			return payload;
		};
	}
}
