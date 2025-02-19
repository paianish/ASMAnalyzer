package ASMAnalyzer;

public class PackageCounter {
    private int decoratorCount;
    private int singletonCount;

    private int abuseCount;
    public PackageCounter(){
        decoratorCount = 0;
        singletonCount = 0;
        abuseCount = 0;
    }

    public void incrementSingletonCount(){
        singletonCount+=1;
    }

    public void incrementDecoratorCount(){
        decoratorCount+=1;
    }

    public void incrementAbuseCount(){abuseCount +=1;}

    public int getDecoratorCount(){
        return decoratorCount;
    }

    public int getSingletonCount(){
        return singletonCount;
    }

    public int getAbuseCount(){return abuseCount;}
}
