package nl.han.ica.datastructures;

public class HANStack<T> implements IHANStack<T> {

	private final IHANLinkedList<T> list = new HANLinkedList<>();

	@Override
	public void push(T value) {
		list.addFirst(value);
	}

	@Override
	public T pop() {
		var v = list.getFirst();
		list.removeFirst();
		return v;
	}

	@Override
	public T peek() {
		return list.getFirst();
	}

}
