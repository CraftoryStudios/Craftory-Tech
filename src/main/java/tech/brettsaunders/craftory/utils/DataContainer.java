package tech.brettsaunders.craftory.utils;

public class DataContainer<T> {
  private T t;

  public T getT() {
    return t;
  }

  public void setT(T t) {
    this.t = t;
  }

  public DataContainer(T t) {
    this.t = t;
  }
}
