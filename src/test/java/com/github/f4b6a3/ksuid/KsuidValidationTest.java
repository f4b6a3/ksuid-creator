package com.github.f4b6a3.ksuid;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class KsuidValidationTest {

	@Parameterized.Parameters(name = "{0}")
	public static Collection<Object[]> invalidStrings() {
		Collection<Object[]> inputs = new ArrayList<>();
		for (char character : new char[] { '\u00ff', '\u0100', '\ud800', '\udc00', '\uffff' }) {
			for (int position : new int[] { 0, 13, 26 }) {
				char[] chars = "000000000000000000000000000".toCharArray();
				chars[position] = character;
				String name = String.format("U+%04X at %d", (int) character, position);
				inputs.add(new Object[] { name, new String(chars) });
			}
		}
		return inputs;
	}

	private final String input;

	public KsuidValidationTest(String name, String input) {
		this.input = input;
	}

	@Test
	public void testIsValidRejectsNonBase62Characters() {
		assertFalse(Ksuid.isValid(input));
	}

	@Test
	public void testFromRejectsNonBase62Characters() {
		assertThrows(IllegalArgumentException.class, () -> Ksuid.from(input));
	}

	@Test
	public void testAccessorsRejectNonBase62Characters() {
		assertThrows(IllegalArgumentException.class, () -> Ksuid.getTime(input));
		assertThrows(IllegalArgumentException.class, () -> Ksuid.getInstant(input));
		assertThrows(IllegalArgumentException.class, () -> Ksuid.getPayload(input));
	}
}
