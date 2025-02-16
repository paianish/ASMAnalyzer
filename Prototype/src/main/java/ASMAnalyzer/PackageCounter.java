package ASMAnalyzer;

public class PackageCounter {
    private int decoratorCount;
    private int singletonCount;

    public PackageCounter(){
        decoratorCount = 0;
        singletonCount = 0;
    }

    public void incrementSingletonCount(){
        singletonCount+=1;
    }

    public void incrementDecoratorCount(){
        decoratorCount+=1;
    }

    public int getDecoratorCount(){
        return decoratorCount;
    }

    public int getSingletonCount(){
        return singletonCount;
    }
}
