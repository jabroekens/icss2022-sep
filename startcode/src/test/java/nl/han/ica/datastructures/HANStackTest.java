package nl.han.ica.datastructures;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.ArrayList;
import java.util.LinkedList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class HANStackTest {

	private HANStack<Integer> sut;

	@BeforeEach
	void setUp() {
		sut = new HANStack<>();
	}

	@Test
	void push_addsToTopAndShiftsElementsDown() {
		var expected = 2;

		for (int i = 0; i < 3; i++) {
			sut.push(i);
		}

		var actual = sut.peek();
		assertEquals(expected, actual);
	}

	@Test
	void pop_removesFromTopAndShiftsElementsUp() {
		var expected = new LinkedList<>();
		for (int i = 0; i < 3; i++) {
			expected.addFirst(i);
			sut.push(i);
		}

		var actual = new ArrayList<>();
		for (int i = 0; i < expected.size() - 1; i++) {
			actual.add(sut.pop());
		}

		assertAll(
			() -> assertIterableEquals(expected.subList(0, expected.size() - 1), actual),
			() -> assertEquals(expected.get(expected.size() - 1), sut.peek())
		);
	}

	@Test
	void peek_returnsElementAtTop() {
		var expected = 3;

		assertAll(
			() -> assertNull(sut.peek()),
			() -> {
				sut.push(3);
				assertEquals(expected, sut.peek());
			}
		);
	}

}
