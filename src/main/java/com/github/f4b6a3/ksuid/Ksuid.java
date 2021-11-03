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

import java.io.Serializable;
import java.time.Instant;

/**
 * This class represents a KSUID.
 * 
 * The KSUID has two parts:
 * 
 * - Time: a byte array of 32 bits that represent the creation time.
 * 
 * - Payload: a byte array of 128 bits generated a secure random generator.
 * 
 * Instances of this class are immutable.
 * 
 * Read: https://segment.com/blog/a-brief-history-of-the-uuid/
 */
public final class Ksuid implements Serializable, Comparable<Ksuid> {

	private static final long serialVersionUID = 3045351825700035803L;

	private final int seconds;
	private final byte[] payload;

	public static final int KSUID_CHARS = 27;
	public static final int KSUID_BYTES = 20;

	public static final int TIME_BYTES = 4;
	public static final int PAYLOAD_BYTES = 16;

	public static final long EPOCH_OFFSET = 1_400_000_000L; // 2014-05-13T16:53:20Z

	protected static final int BASE62_RADIX = 62;
	protected static final char[] BASE62_ALPHABET;
	protected static final int[] BASE62_MAP;

	static {

		BASE62_ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();
		BASE62_MAP = new int[128];

		// initialize the map with -1
		for (int i = 0; i < BASE62_MAP.length; i++) {
			BASE62_MAP[i] = -1;
		}
		// map the alphabets chars to values
		for (int i = 0; i < BASE62_ALPHABET.length; i++) {
			BASE62_MAP[BASE62_ALPHABET[i]] = i;
		}
	}

	protected static final int KSUID_INTS = KSUID_BYTES / Integer.BYTES;
	protected static final long INTEGER_MASK = 0x00000000ffffffffL;

	/**
	 * Create a new KSUID.
	 * 
	 * @param bytes a byte array
	 */
	protected Ksuid(byte[] bytes) {

		if (bytes == null || bytes.length != KSUID_BYTES) {
			throw new IllegalArgumentException("Invalid byte array length or null"); // null or wrong length!
		}

		// copy the seconds
		int secondz = 0;
		secondz |= (bytes[0x00] & 0xff) << 0x18;
		secondz |= (bytes[0x01] & 0xff) << 0x10;
		secondz |= (bytes[0x02] & 0xff) << 0x08;
		secondz |= (bytes[0x03] & 0xff) << 0x00;
		this.seconds = secondz;

		// copy the payload
		this.payload = new byte[PAYLOAD_BYTES];
		System.arraycopy(bytes, TIME_BYTES, this.payload, 0, PAYLOAD_BYTES);
	}

	/**
	 * Create a new KSUID.
	 * 
	 * @param bytes an integer array
	 */
	protected Ksuid(final int[] ints) {

		if (ints == null || ints.length != KSUID_INTS) {
			throw new IllegalArgumentException("Invalid integer array length or null"); // null or wrong length!
		}

		// copy the seconds
		this.seconds = ints[0];

		// copy the payload
		this.payload = new byte[PAYLOAD_BYTES];
		for (int i = 1, j = 0; i < ints.length; i++, j += 4) {
			this.payload[j + 0] = (byte) ((ints[i] >>> 0x18) & 0xff);
			this.payload[j + 1] = (byte) ((ints[i] >>> 0x10) & 0xff);
			this.payload[j + 2] = (byte) ((ints[i] >>> 0x08) & 0xff);
			this.payload[j + 3] = (byte) ((ints[i] >>> 0x00) & 0xff);
		}
	}

	/**
	 * Create a new KSUID.
	 * 
	 * Useful to make copies of KSUIDs.
	 * 
	 * @param ksuid a KSUID
	 */
	public Ksuid(Ksuid ksuid) {

		// copy the seconds
		this.seconds = ksuid.seconds;

		// copy the payload
		this.payload = new byte[PAYLOAD_BYTES];
		System.arraycopy(ksuid.payload, 0, this.payload, 0, PAYLOAD_BYTES);
	}

	/**
	 * Create a new KSUID.
	 * 
	 * @param seconds the Unix time in seconds since 1970-01-01
	 * @param payload the payload byte array
	 */
	public Ksuid(final long seconds, final byte[] payload) {

		if (payload == null || payload.length != PAYLOAD_BYTES) {
			throw new IllegalArgumentException("Invalid payload length or null"); // null or wrong length!
		}

		// copy the seconds
		this.seconds = (int) toKsuidTime(seconds);

		// copy the payload
		this.payload = new byte[PAYLOAD_BYTES];
		System.arraycopy(payload, 0, this.payload, 0, PAYLOAD_BYTES);
	}

	/**
	 * Convert a byte array into a KSUID.
	 * 
	 * @param bytes a byte array
	 * @return a KSUID
	 */
	public static Ksuid from(byte[] bytes) {
		return new Ksuid(bytes);
	}

	/**
	 * Convert a canonical string into a KSUID.
	 * 
	 * The input string must be 27 characters long and must contain only characters
	 * from base-62 alphabet.
	 * 
	 * @param string a canonical string
	 * @return a KSUID
	 */
	public static Ksuid from(String string) {
		return fromBase62(string);
	}

	/**
	 * Convert the KSUID into a byte array.
	 * 
	 * @return a byte array.
	 */
	public byte[] toBytes() {

		byte[] bytes = new byte[KSUID_BYTES];

		// copy the seconds
		bytes[0] = (byte) ((this.seconds >>> 0x18) & 0xff);
		bytes[1] = (byte) ((this.seconds >>> 0x10) & 0xff);
		bytes[2] = (byte) ((this.seconds >>> 0x08) & 0xff);
		bytes[3] = (byte) ((this.seconds >>> 0x00) & 0xff);

		// copy the payload
		System.arraycopy(this.payload, 0, bytes, TIME_BYTES, PAYLOAD_BYTES);

		return bytes;
	}

	/**
	 * Convert the KSUID into a canonical string.
	 * 
	 * The output string is 27 characters long and contains only characters from
	 * base-62 alphabet.
	 * 
	 * @return a string
	 */
	@Override
	public String toString() {
		return toBase62(this);
	}

	/**
	 * Return the instant of creation.
	 * 
	 * The instant of creation is extracted from the creation time.
	 * 
	 * @return {@link Instant}
	 */
	public Instant getInstant() {
		return Instant.ofEpochSecond(getTime());
	}

	/**
	 * Return the instant of creation.
	 * 
	 * The instant of creation is extracted from the creation time.
	 * 
	 * @param string a canonical string
	 * @return {@link Instant}
	 */
	public static Instant getInstant(String string) {
		return Instant.ofEpochSecond(getTime(string));
	}

	/**
	 * Return the creation time.
	 * 
	 * The number returned is equivalent to the number of seconds since 1970-01-01
	 * (Unix epoch).
	 * 
	 * @return a number of seconds
	 */
	public long getTime() {
		return toUnixTime(this.seconds);
	}

	/**
	 * Return the creation time.
	 * 
	 * The number returned is equivalent to the count of seconds since 1970-01-01
	 * (Unix epoch).
	 * 
	 * @param string a canonical string
	 * @return a number of seconds
	 */
	public static long getTime(String string) {
		return from(string).getTime();
	}

	/**
	 * Return the payload.
	 * 
	 * The payload is an array of 16 bytes (128 bits).
	 * 
	 * @return a byte array
	 */
	public byte[] getPayload() {
		final byte[] copy = new byte[PAYLOAD_BYTES];
		System.arraycopy(this.payload, 0, copy, 0, PAYLOAD_BYTES);
		return copy;
	}

	/**
	 * Return the payload.
	 * 
	 * The payload is an array of 16 bytes (128 bits).
	 * 
	 * @param string a canonical string
	 * @return a byte array
	 */
	public static byte[] getPayload(String string) {
		return from(string).getPayload();
	}

	/**
	 * Check if the input string is valid.
	 * 
	 * The input string must be 27 characters long and must contain only characters
	 * from base-62 alphabet.
	 * 
	 * @param string a string
	 * @return true if valid
	 */
	public static boolean isValid(String string) {
		return string != null && isValid(string.toCharArray());
	}

	@Override
	public int hashCode() {

		final int prime = 31;
		int result = 1;

		result = prime * result + seconds;

		for (int i = 0; i < PAYLOAD_BYTES; i++) {
			result = prime * result + payload[i];
		}

		return result;
	}

	@Override
	public boolean equals(Object obj) {

		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;

		Ksuid other = (Ksuid) obj;

		if (this.seconds != other.seconds)
			return false;

		for (int i = 0; i < PAYLOAD_BYTES; i++) {
			if (this.payload[i] != other.payload[i]) {
				return false;
			}
		}

		return true;
	}

	@Override
	public int compareTo(Ksuid other) {

		// unsigned comparison of seconds
		if ((this.seconds & INTEGER_MASK) > (other.seconds & INTEGER_MASK)) {
			return 1;
		} else if ((this.seconds & INTEGER_MASK) < (other.seconds & INTEGER_MASK)) {
			return -1;
		}

		// unsigned comparison of payload bytes
		for (int i = 0; i < PAYLOAD_BYTES; i++) {
			if ((this.payload[i] & 0xff) > (other.payload[i] & 0xff)) {
				return 1;
			} else if ((this.payload[i] & 0xff) < (other.payload[i] & 0xff)) {
				return -1;
			}
		}

		return 0;
	}

	/**
	 * Convert the Unix time to KSUID time.
	 * 
	 * The 4 fist bytes of contain an unsigned time since epoch 14e8.
	 * 
	 * @param unixTime the time in seconds since 1970-01-01
	 * @return the KSUID time
	 */
	protected static long toKsuidTime(final long unixTime) {
		return (unixTime - EPOCH_OFFSET) & INTEGER_MASK;
	}

	/**
	 * Convert the KSUID time to Unix time.
	 * 
	 * The 4 fist bytes of contain an unsigned time since epoch 14e8.
	 * 
	 * @param ksuidTime the KSUID time
	 * @return the time in seconds since 1970-01-01
	 */
	protected static long toUnixTime(final long ksuidTime) {
		return (ksuidTime & INTEGER_MASK) + EPOCH_OFFSET;
	}

	/**
	 * Encode a KSUID into a base-62 string.
	 * 
	 * @param ksuid a KSUID
	 * @return a string
	 */
	protected static String toBase62(final Ksuid ksuid) {

		int[] number = ksuid.toInts();

		int b = KSUID_CHARS; // buffer index
		char[] buffer = new char[KSUID_CHARS];

		while (!isZero(number)) {
			final int[] quotient = new int[KSUID_INTS]; // division output
			final int remainder = remainder(number, BASE62_RADIX, quotient);
			buffer[--b] = BASE62_ALPHABET[remainder];
			number = quotient;
		}

		// add padding left
		while (b > 0) {
			buffer[--b] = '0';
		}

		return new String(buffer);
	}

	/**
	 * Decode a base-62 string into a KSUID.
	 * 
	 * @param string a string
	 * @return a KSUID
	 */
	protected static Ksuid fromBase62(final String string) {

		int[] number = new int[KSUID_INTS];
		char[] chars = toCharArray(string);

		for (int i = 0; i < chars.length; i++) {
			final int remainder = BASE62_MAP[chars[i]];
			final int[] product = multiply(number, BASE62_RADIX, remainder, true);
			number = product;
		}

		return new Ksuid(number);
	}

	protected static int remainder(int[] number, int divisor, int[] quotient /* division output */) {

		long temporary = 0;
		long remainder = 0;

		for (int i = 0; i < number.length; i++) {
			temporary = (remainder << 32) | (number[i] & INTEGER_MASK);
			quotient[i] = (int) (temporary / divisor);
			remainder = temporary % divisor;
		}

		return (int) remainder;
	}

	protected static int[] multiply(int[] number, int multiplier, int addend, boolean validate) {

		long temporary = 0;
		long overflow = addend;
		final int[] product = new int[KSUID_INTS];

		for (int i = KSUID_INTS - 1; i >= 0; i--) {
			temporary = ((number[i] & INTEGER_MASK) * multiplier) + overflow;
			product[i] = (int) temporary;
			overflow = (temporary >>> 32);
		}

		if (validate && overflow != 0) {
			throw new IllegalArgumentException("Invalid KSUID (overflow)");
		}

		return product;
	}

	protected int[] toInts() {
		int[] ints = new int[KSUID_INTS];

		// copy the seconds
		ints[0] = this.seconds;

		// copy the payload
		for (int i = 1, j = 0; i < ints.length; i++, j += 4) {
			ints[i] |= (this.payload[j + 0] & 0xff) << 0x18;
			ints[i] |= (this.payload[j + 1] & 0xff) << 0x10;
			ints[i] |= (this.payload[j + 2] & 0xff) << 0x08;
			ints[i] |= (this.payload[j + 3] & 0xff) << 0x00;
		}

		return ints;
	}

	private static boolean isZero(int[] number) {
		return number[0] == 0 && number[1] == 0 && number[2] == 0 && number[3] == 0 && number[4] == 0;
	}

	private static char[] toCharArray(String string) {
		if (string == null) {
			throw new IllegalArgumentException("Invalid KSUID: null");
		}
		char[] chars = string.toCharArray();
		if (!isValid(chars)) {
			throw new IllegalArgumentException(String.format("Invalid KSUID: \"%s\"", string));
		}
		return chars;
	}

	private static boolean isValid(final char[] chars) {
		if (chars == null || chars.length != KSUID_CHARS) {
			return false; // null or wrong size!
		}
		for (int i = 0; i < chars.length; i++) {
			if (BASE62_MAP[chars[i]] == -1) {
				return false; // invalid character!
			}
		}
		return true; // It seems to be OK.
	}
}
