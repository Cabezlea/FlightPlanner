public class Stack<T> {
    private LinkedList<T> list;

    public Stack() {
        list = new LinkedList<>();
    }

    public void push(T data) {
        LinkedList<T> newList = new LinkedList<>();
        newList.add(data);
        Node<T> current = list.getHead();
        while (current != null) {
            newList.add(current.data);
            current = current.next;
        }
        list = newList;
    }

    public T pop() {
        if (isEmpty()) {
            return null;
        }
        T data = list.getHead().data;
        LinkedList<T> newList = new LinkedList<>();
        Node<T> current = list.getHead().next;
        while (current != null) {
            newList.add(current.data);
            current = current.next;
        }
        list = newList;
        return data;
    }

    public boolean isEmpty() {
        return list.getHead() == null;
    }

    public LinkedList<T> getList() {
        return list;
    }
}
