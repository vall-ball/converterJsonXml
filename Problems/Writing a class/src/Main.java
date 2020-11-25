//write code of the Shelf class here
class Shelf<T extends Book> {
    T element;

    public void setElement(T element) {
        this.element = element;
    }

    public T getElement() {
        return element;
    }
}