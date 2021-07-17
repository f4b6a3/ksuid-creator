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

import com.github.f4b6a3.ksuid.factory.KsuidFactory;

/**
 * A class for generating KSUID.
 * 
 * It uses a static {@link KsuidFactory}.
 */
public final class KsuidCreator {

	private static final KsuidFactory KSUID_FACTORY = new KsuidFactory();

	private KsuidCreator() {
	}

	/**
	 * Create a KSUID.
	 * 
	 * @return a KSUID
	 */
	public static Ksuid getKsuid() {
		return KSUID_FACTORY.create();
	}

	/**
	 * Create a KSUID with millisecond precision.
	 * 
	 * @return a KSUID
	 */
	public static Ksuid getKsuidMs() {
		return KSUID_FACTORY.createMs();
	}

	/**
	 * Create a KSUID with microsecond precision.
	 * 
	 * Check if the target runtime provides microsecond precision:
	 * {@code System.out.println(Instant.now().getNano() / 1000);}
	 * 
	 * @return a KSUID
	 */
	public static Ksuid getKsuidUs() {
		return KSUID_FACTORY.createUs();
	}

	/**
	 * Create a KSUID with nanosecond precision.
	 * 
	 * Check if the target runtime provides nanosecond precision:
	 * {@code System.out.println(Instant.now().getNano());}
	 * 
	 * @return a KSUID
	 */
	public static Ksuid getKsuidNs() {
		return KSUID_FACTORY.createNs();
	}

	/**
	 * Create a KSUID.
	 * 
	 * @param seconds the Unix time in seconds
	 * @return a KSUID
	 */
	public static Ksuid getKsuid(final long seconds) {
		return KSUID_FACTORY.create(seconds);
	}

	/**
	 * Create a KSUID with millisecond precision.
	 * 
	 * @param seconds      the Unix time in seconds
	 * @param milliseconds the milliseconds
	 * @return a KSUID
	 */
	public static Ksuid getKsuidMs(final long seconds, final int milliseconds) {
		return KSUID_FACTORY.createMs(seconds, milliseconds);
	}

	/**
	 * Create a KSUID with microsecond precision.
	 * 
	 * @param seconds      the Unix time in seconds
	 * @param microseconds the microseconds
	 * @return a KSUID
	 */
	public static Ksuid getKsuidUs(final long seconds, final int microseconds) {
		return KSUID_FACTORY.createUs(seconds, microseconds);
	}

	/**
	 * Create a KSUID with nanosecond precision.
	 * 
	 * @param seconds     the Unix time in seconds
	 * @param nanoseconds the nanoseconds
	 * @return a KSUID
	 */
	public static Ksuid getKsuidNs(final long seconds, final int nanoseconds) {
		return KSUID_FACTORY.createNs(seconds, nanoseconds);
	}
}
