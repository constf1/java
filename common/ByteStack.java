package common;

import java.util.Arrays;

public final class ByteStack implements Comparable<ByteStack> {
  private int _size;
  private int _available;
  private byte _data[];

  public ByteStack(final byte data[], final int size) {
    _data = data;
    _available = _data.length;
    _size = Math.min(Math.max(size, 0), _available);
  }

  public ByteStack(final byte data[]) {
    this(data, 0);
  }

  /**
   * Creates an empty Stack.
   */
  public ByteStack() {
    this(new byte[64]);
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
  public byte peek() {
    return _data[_size - 1];
  }

  /**
   * Removes the top of this stack and returns it as the value of this function.
   * 
   * @return the top of the stack
   */
  public byte pop() {
    return _data[--_size];
  }

  /**
   * Pushes an item onto the top of this stack.
   * 
   * @param value an item to push.
   */
  public void push(final byte value) {
    if (_size == _available) {
      _available *= 2;
      _data = Arrays.copyOf(_data, _available);
    }
    _data[_size++] = value;
  }

  public void push(final ByteStack values) {
    final int S = values._size;
    if (_size + S > _available) {
      _available += Math.max(_available, S);
      _data = Arrays.copyOf(_data, _available);
    }
    final byte D[] = values._data;
    for (int i = 0; i < S; i++) {
      _data[_size++] = D[i];
    }
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
  public byte get(final int index) {
    return _data[index];
  }

  /**
   * Returns an array containing all of the elements in this stack.
   * @return an array
   */
  public byte[] toArray​() {
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
    return new String(_data, 0, _size);
  }

  @Override
  public int compareTo(ByteStack other) {
    // Since: 9
    // int cmp = Byte.compare(_data, 0, _size, other._data, 0, other._size);
    if (_size != other._size) {
      return _size - other._size;
    }
    for (int i = 0; i < _size; i++) {
      if (_data[i] != other._data[i]) {
        return _data[i] - other._data[i];
      }
    }
    return 0;
  }

  public static void main(String[] args) {
    var stack = new ByteStack();
    for (byte b = 32; b < 127; b++) {
      stack.push(b);
    }

    System.out.println("ASCII Printables: " + stack);

    var reversal = new ByteStack();
    while (stack.size() > 0) {
      reversal.push(stack.pop());
    }

    System.out.println("In opposite direction: " + reversal);
  }
}
