package util;

public class ListStack<E> {
    private StackLayer<E> headPointer;

    public ListStack() {
        headPointer = null;
    }

    public E peek() {
        return (headPointer == null) ? null : headPointer.element;
    }

    public E pop() {
        StackLayer<E> oldHead = headPointer;
        if (oldHead == null)
            return null;
        headPointer = oldHead.lower;
        return oldHead.element;

    }

    public void push(E e) {
        headPointer = new StackLayer<>(e, headPointer);
    }

    private record StackLayer<E>(E element, StackLayer<E> lower) {}
}
