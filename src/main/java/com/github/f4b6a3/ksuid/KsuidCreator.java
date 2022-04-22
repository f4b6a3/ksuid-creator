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

import java.time.Instant;

/**
 * A class for generating KSUIDs.
 */
public final class KsuidCreator {

	private KsuidCreator() {
	}

	/**
	 * Create a Segment's KSUID.
	 * 
	 * @return a KSUID
	 */
	public static Ksuid getKsuid() {
		return FactoryHolder.INSTANCE.create();
	}

	/**
	 * Create a KSUID with sub-second precision.
	 * 
	 * Three sub-second precisions are supported: millisecond, microsecond, and
	 * nanosecond. The precision is detected at runtime.
	 * 
	 * @return a KSUID
	 */
	public static Ksuid getSubsecondKsuid() {
		return SubsecondHolder.INSTANCE.create();
	}

	/**
	 * Create a Monotonic KSUID.
	 * 
	 * The payload is reset to a new value whenever the second changes.
	 * 
	 * If more than one KSUID is generated within the same second, the payload is
	 * incremented by one.
	 * 
	 * @return a KSUID
	 */
	public static Ksuid getMonotonicKsuid() {
		return MonotonicHolder.INSTANCE.create();
	}

	/**
	 * Create a Segment's KSUID.
	 * 
	 * @param instant an instant
	 * @return a KSUID
	 */
	public static Ksuid getKsuid(Instant instant) {
		return FactoryHolder.INSTANCE.create(instant);
	}

	/**
	 * Create a KSUID with sub-second precision.
	 * 
	 * Three sub-second precisions are supported: millisecond, microsecond, and
	 * nanosecond. The precision is detected at runtime.
	 * 
	 * @param instant an instant
	 * @return a KSUID
	 */
	public static Ksuid getSubsecondKsuid(Instant instant) {
		return SubsecondHolder.INSTANCE.create(instant);
	}

	/**
	 * Create a Monotonic KSUID.
	 * 
	 * The payload is reset to a new value whenever the second changes.
	 * 
	 * If more than one KSUID is generated within the same second, the payload is
	 * incremented by one.
	 * 
	 * @param instant an instant
	 * @return a KSUID
	 */
	public static Ksuid getMonotonicKsuid(Instant instant) {
		return MonotonicHolder.INSTANCE.create(instant);
	}

	private static class FactoryHolder {
		static final KsuidFactory INSTANCE = KsuidFactory.newInstance();
	}

	private static class SubsecondHolder {
		static final KsuidFactory INSTANCE = KsuidFactory.newSubsecondInstance();
	}

	private static class MonotonicHolder {
		static final KsuidFactory INSTANCE = KsuidFactory.newMonotonicInstance();
	}
}
