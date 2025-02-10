package sample;
public class HelloWorld {

    public int testInstanceVariable;
    public static void main(String[] args) {
        String[] test = args;
        Test test1 = new Test();
        System.out.println("Hello, World!");
    }
    public void test(int test){
        boolean in = false;
        if(getTrue(in)) {
            test++;
        }
    }
    public boolean getTrue(boolean out){
        out = true;
        return out;
    }

}