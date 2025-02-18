package exampleSingletonDemo;

import java.util.ArrayList;
import java.util.Map;

public class Test {
    private Test2[] testarray;
    private ArrayList<Test> testarraylist;

    private Map<String, Test> testMap;
    public void dosomething() {
        testarray = new Test2[5];
    }
}
