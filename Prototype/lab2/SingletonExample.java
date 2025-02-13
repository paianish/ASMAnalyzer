public class SingletonExample {
  private SingletonExample() {
  }
  public SingletonExample getInstance() {
    return new SingletonExample();
  }
}
