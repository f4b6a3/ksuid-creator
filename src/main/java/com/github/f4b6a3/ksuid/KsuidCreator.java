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

import java.time.Instant;

/**
 * A class for generating KSUIDs.
 */
public final class KsuidCreator {

	private KsuidCreator() {
	}

	/**
	 * Create a KSUID.
	 * 
	 * @return a KSUID
	 */
	public static Ksuid getKsuid() {
		return FactoryHolder.INSTANCE.create();
	}

	/**
	 * Create a KSUID with millisecond precision.
	 * 
	 * @return a KSUID
	 */
	public static Ksuid getKsuidMs() {
		return MsFactoryHolder.INSTANCE.create();
	}

	/**
	 * Create a KSUID with microsecond precision (JDK 9+).
	 * 
	 * @return a KSUID
	 */
	public static Ksuid getKsuidUs() {
		return UsFactoryHolder.INSTANCE.create();
	}

	/**
	 * Create a KSUID with nanosecond precision (JDK 9+).
	 * 
	 * Check if the target runtime provides nanosecond precision:
	 * {@code System.out.println(Instant.now().getNano());}
	 * 
	 * Read: https://stackoverflow.com/questions/1712205
	 * 
	 * @return a KSUID
	 */
	public static Ksuid getKsuidNs() {
		return NsFactoryHolder.INSTANCE.create();
	}

	/**
	 * Create a KSUID.
	 * 
	 * @param instant an instant
	 * @return a KSUID
	 */
	public static Ksuid getKsuid(Instant instant) {
		return FactoryHolder.INSTANCE.create(instant);
	}

	/**
	 * Create a KSUID with millisecond precision.
	 * 
	 * @param instant an instant
	 * @return a KSUID
	 */
	public static Ksuid getKsuidMs(Instant instant) {
		return MsFactoryHolder.INSTANCE.create(instant);
	}

	/**
	 * Create a KSUID with microsecond precision (JDK 9+).
	 * 
	 * @param instant an instant
	 * @return a KSUID
	 */
	public static Ksuid getKsuidUs(Instant instant) {
		return UsFactoryHolder.INSTANCE.create(instant);
	}

	/**
	 * Create a KSUID with nanosecond precision (JDK 9+).
	 * 
	 * Check if the target runtime provides nanosecond precision:
	 * {@code System.out.println(Instant.now().getNano());}
	 * 
	 * Read: https://stackoverflow.com/questions/1712205
	 * 
	 * @param instant an instant
	 * @return a KSUID
	 */
	public static Ksuid getKsuidNs(Instant instant) {
		return NsFactoryHolder.INSTANCE.create(instant);
	}

	private static class FactoryHolder {
		static final KsuidFactory INSTANCE = KsuidFactory.newInstance();
	}

	private static class MsFactoryHolder {
		static final KsuidFactory INSTANCE = KsuidFactory.newMsInstance();
	}

	private static class UsFactoryHolder {
		static final KsuidFactory INSTANCE = KsuidFactory.newUsInstance();
	}

	private static class NsFactoryHolder {
		static final KsuidFactory INSTANCE = KsuidFactory.newNsInstance();
	}
}
