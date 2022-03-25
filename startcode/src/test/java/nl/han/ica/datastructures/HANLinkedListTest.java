package nl.han.ica.datastructures;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class HANLinkedListTest {

	private HANLinkedList<Integer> sut;

	@BeforeEach
	void setUp() {
		sut = new HANLinkedList<>();
	}

	@Test
	void addFirst_addsToFrontAndShiftsElementsToRight() {
		var list = List.of(1, 2);
		var expected = list.size();

		list.forEach(sut::addFirst);

		var actual = sut.getSize();
		assertEquals(expected, actual);
	}

	@Test
	void clear_removesAllElements() {
		var list = List.of(1, 2);
		var expected = new HANLinkedList<>();
		list.forEach(sut::addFirst);

		sut.clear();

		var actual = sut;
		assertEquals(expected, actual);
	}

	@Test
	void insert_insertsAtPositionAndShiftsElementsToRight() {
		var list = List.of(1, 2, 3, 4);
		var expected = new HANLinkedList<>();

		// (value:index): (4:0) -> (3:1:) -> (2:2) -> (1:3)
		list.forEach(expected::addFirst);
		// (value:index): (4:0) -> (3:1) -> (1:2)
		sut.addFirst(list.get(0));
		sut.addFirst(list.get(list.size() - 2));
		sut.addFirst(list.get(list.size() - 1));

		sut.insert(2, 2);

		var actual = sut;
		assertEquals(expected, sut);
	}

	@Test
	void delete_deletesAtPositionAndShiftsElementsToLeft() {
		var list = List.of(1, 2, 3, 4);
		var expected = new HANLinkedList<>();

		// (value:index): (4:0) -> (3:1) -> (1:2)
		expected.addFirst(list.get(0));
		expected.addFirst(list.get(list.size() - 2));
		expected.addFirst(list.get(list.size() - 1));
		// (value:index): (4:0) -> (3:1:) -> (2:2) -> (1:3)
		list.forEach(sut::addFirst);

		sut.delete(2);

		var actual = sut;
		assertEquals(expected, sut);
	}

	void get_givesCorrectValueAtSameIndex() {
		var list = Arrays.asList(1, 2, 3, 4);
		var expectedIndex = 3;
		var expected = list.get(expectedIndex);

		/*
		 * HANLinkedList's addFirst will result in a reverse of the original list
		 * in terms of structure, so by reversing the original list before calling
		 * `HANLinkedList#addFirst`, we can expect the same result from the same index.
		 */
		Collections.reverse(list);
		list.forEach(sut::addFirst);

		var actual = sut.get(expectedIndex);
		assertEquals(expected, actual);
	}

	@Test
	void removeFirst_removesFirstAndShiftsElementsToLeft() {
		var list = List.of(1, 2, 3, 4);
		var expected = new HANLinkedList<>();

		// (value:index): (3:0) -> (3:1) -> (1:2)
		expected.addFirst(list.get(0));
		expected.addFirst(list.get(1));
		expected.addFirst(list.get(2));
		// (value:index): (4:0) -> (3:1:) -> (2:2) -> (1:3)
		list.forEach(sut::addFirst);

		sut.removeFirst();

		var actual = sut;
		assertEquals(expected, actual);
	}

	@Test
	void getFirst_givesCorrectValue() {
		var expected = 1;
		sut.addFirst(expected);

		var actual = sut.getFirst();
		assertEquals(expected, actual);
	}

}
