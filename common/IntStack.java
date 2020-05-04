package common;

import java.util.Arrays;

public final class IntStack {
  private int _size;
  private int _available;
  private int _data[];

  public IntStack(final int data[], final int size) {
    _data = data;
    _available = _data.length;
    _size = Math.min(Math.max(size, 0), _available);
  }

  public IntStack(final int data[]) {
    this(data, 0);
  }

  /**
   * Creates an empty Stack.
   */
  public IntStack() {
    this(new int[16]);
  }

  /**
   * Tests if this stack is empty.
   * 
   * @return {@code true} if the stack is empty; {@code false} otherwise.
   */
  public boolean isEmpty() {
    return _size == 0;
  }

  /**
   * Looks at the top of this stack without removing it from the stack.
   * 
   * @return the top of the stack
   */
  public int peek() {
    return _data[_size - 1];
  }

  /**
   * Removes the top of this stack and returns it as the value of this function.
   * 
   * @return the top of the stack
   */
  public int pop() {
    return _data[--_size];
  }

  /**
   * Pushes an item onto the top of this stack.
   * 
   * @param value an item to push.
   */
  public void push(final int value) {
    if (_size == _available) {
      _available *= 2;
      _data = Arrays.copyOf(_data, _available);
    }
    _data[_size++] = value;
  }

  /**
   * Removes all of the elements from the stack. It will be empty after this call returns.
   */
  public void clear() {
    _size = 0;
  }

  /**
   * Returns the element at the specified position in the stack.
   * @param index - index of the element to return
   * @return the element at the specified index
   */
  public int get(final int index) {
    return _data[index];
  }

  /**
   * Returns an array containing all of the elements in this stack.
   * @return an array
   */
  public int[] toArray() {
    return Arrays.copyOf(_data, _size);
  }

  public int size() {
    return _size;
  }

  public int available() {
    return _available;
  }

  /**
   * Returns a string representation of this stack.
   * @return a string
   */
  @Override
  public String toString() {
    final var buf = new StringBuilder();
    var prefix = "";
    buf.append('[');
    for (int i = 0; i < _size; i++) {
      buf.append(prefix);
      buf.append(Integer.toString(_data[i]));
      prefix = ", ";
    }
    buf.append(']');
    return buf.toString();
  }
}