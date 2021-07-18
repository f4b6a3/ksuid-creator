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

package com.github.f4b6a3.ksuid.factory;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Random;

import com.github.f4b6a3.ksuid.Ksuid;
import com.github.f4b6a3.ksuid.random.RandomGenerator;

/**
 * A factory for generating KSUIDs.
 */
public final class KsuidFactory {

	private RandomGenerator randomGenerator;

	/**
	 * Use the default {@link java.security.SecureRandom}.
	 */
	public KsuidFactory() {
		this(new SecureRandom()::nextBytes);
	}

	/**
	 * Use a random generator that inherits from {@link Random}.
	 * 
	 * @param random a {@link Random} instance
	 */
	public KsuidFactory(Random random) {
		this.randomGenerator = random::nextBytes;
	}

	/**
	 * Use a random generator that inherits from {@link RandomGenerator}.
	 * 
	 * @param randomGenerator a {@link RandomGenerator} instance
	 */
	public KsuidFactory(RandomGenerator randomGenerator) {
		this.randomGenerator = randomGenerator;
	}

	/**
	 * Create a KSUID.
	 * 
	 * @return a KSUID
	 */
	public Ksuid create() {
		return create((System.currentTimeMillis() / 1000));
	}

	/**
	 * Create a KSUID with millisecond precision.
	 * 
	 * @return a KSUID
	 */
	public Ksuid createMs() {
		final long t = System.currentTimeMillis();
		final long s = t / 1000;
		final int ms = (int) t % 1000;
		return createMs(s, ms);
	}

	/**
	 * Create a KSUID with microsecond precision.
	 * 
	 * Check if the target runtime provides microsecond precision:
	 * {@code System.out.println(Instant.now().getNano() / 1000);}
	 * 
	 * @return a KSUID
	 */
	public Ksuid createUs() {
		Instant instant = Instant.now();
		final long s = instant.getEpochSecond();
		final int us = instant.getNano() / 1000;
		return createUs(s, us);
	}

	/**
	 * Create a KSUID with nanosecond precision.
	 * 
	 * Check if the target runtime provides nanosecond precision:
	 * {@code System.out.println(Instant.now().getNano());}
	 *
	 * 
	 * @return a KSUID
	 */
	public Ksuid createNs() {
		Instant instant = Instant.now();
		final long s = instant.getEpochSecond();
		final int ns = instant.getNano();
		return createNs(s, ns);
	}

	/**
	 * Create a KSUID.
	 * 
	 * @param seconds the Unix time in seconds
	 * @return a KSUID
	 */
	public Ksuid create(final long seconds) {
		final byte[] payload = getRandomPayload();
		return new Ksuid(seconds, payload);
	}

	/**
	 * Create a KSUID with millisecond precision.
	 * 
	 * @param seconds      the Unix time in seconds
	 * @param milliseconds the milliseconds
	 * @return a KSUID
	 */
	public Ksuid createMs(final long seconds, final int milliseconds) {

		final byte[] payload = getRandomPayload();

		// insert milliseconds into payload
		final int subsecs = (milliseconds << 6) | (payload[1] & 0b00111111);
		payload[0] = (byte) ((subsecs >>> 0x08) & 0xff);
		payload[1] = (byte) ((subsecs >>> 0x00) & 0xff);

		return new Ksuid(seconds, payload);
	}

	/**
	 * Create a KSUID with microsecond precision.
	 * 
	 * @param seconds      the Unix time in seconds
	 * @param microseconds the microseconds
	 * @return a KSUID
	 */
	public Ksuid createUs(final long seconds, final int microseconds) {

		final byte[] payload = getRandomPayload();

		// insert microseconds into payload
		final int subsecs = (microseconds << 4) | (payload[2] & 0b00001111);
		payload[0] = (byte) ((subsecs >>> 0x10) & 0xffL);
		payload[1] = (byte) ((subsecs >>> 0x08) & 0xffL);
		payload[2] = (byte) ((subsecs >>> 0x00) & 0xffL);

		return new Ksuid(seconds, payload);
	}

	/**
	 * Create a KSUID with nanosecond precision.
	 * 
	 * @param seconds     the Unix time in seconds
	 * @param nanoseconds the nanoseconds
	 * @return a KSUID
	 */
	public Ksuid createNs(final long seconds, final int nanoseconds) {

		final byte[] payload = getRandomPayload();

		// insert nanoseconds into payload
		final long subsecs = (nanoseconds << 2) | (payload[3] & 0b00000011);
		payload[0] = (byte) ((subsecs >>> 0x18) & 0xffL);
		payload[1] = (byte) ((subsecs >>> 0x10) & 0xffL);
		payload[2] = (byte) ((subsecs >>> 0x08) & 0xffL);
		payload[3] = (byte) ((subsecs >>> 0x00) & 0xffL);

		return new Ksuid(seconds, payload);
	}

	/**
	 * Generate a random byte array for payload.
	 * 
	 * @return a random byte array
	 */
	private byte[] getRandomPayload() {
		final byte[] payload = new byte[Ksuid.PAYLOAD_BYTES];
		randomGenerator.nextBytes(payload);
		return payload;
	}
}
