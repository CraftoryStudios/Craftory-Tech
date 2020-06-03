package tech.brettsaunders.craftory.utils;

public class VariableContainer<T> {
  private T t;

  public T getT() {
    return t;
  }

  public void setT(T t) {
    this.t = t;
  }

  public VariableContainer(T t) {
    this.t = t;
  }
}
