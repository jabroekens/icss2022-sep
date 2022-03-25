package nl.han.ica.datastructures;

import java.util.Objects;

public class HANLinkedList<T> implements IHANLinkedList<T> {

	private Node<T> head = new Node<>(null, null);

	@Override
	public void addFirst(T value) {
		insert(0, value);
	}

	@Override
	public void clear() {
		head.next = null;
	}

	@Override
	public void insert(int index, T value) {
		var n = getNode(index - 1);
		if (n != null) {
			n.next = new Node<>(value, n.next);
		}
	}

	@Override
	public void delete(int pos) {
		var n = getNode(pos - 1);
		if (n != null && n.next != null) {
			n.next = n.next.next;
		}
	}

	@Override
	public T get(int pos) {
		var n = getNode(pos);
		return n != null ? n.value : null;
	}

	@Override
	public void removeFirst() {
		delete(0);
	}

	@Override
	public T getFirst() {
		return head.next != null ? head.next.value : null;
	}

	@Override
	public int getSize() {
		var i = 0;
		for (var n = head; n.next != null; n = n.next) {
			i++;
		}
		return i;
	}

	private Node<T> getNode(int index) {
		var n = head;
		for (var i = 0; i <= index && n != null; i++) {
			n = n.next;
		}
		return n;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		HANLinkedList<?> that = (HANLinkedList<?>) o;
		return Objects.equals(head, that.head);
	}

	@Override
	public int hashCode() {
		return Objects.hash(head);
	}

	private static final class Node<T> {

		T value;
		Node<T> next;

		public Node(T value, Node<T> next) {
			this.value = value;
			this.next = next;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (o == null || getClass() != o.getClass()) {
				return false;
			}
			Node<?> node = (Node<?>) o;
			return Objects.equals(value, node.value) && Objects.equals(next, node.next);
		}

		@Override
		public int hashCode() {
			return Objects.hash(value, next);
		}

	}

}
