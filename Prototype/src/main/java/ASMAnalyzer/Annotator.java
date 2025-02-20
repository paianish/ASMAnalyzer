package ASMAnalyzer;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import java.lang.reflect.Array;
import java.util.*;

public class Annotator {
    private Map<String, PackageCounter> packageMap;
    private ArrayList<String> singletons;

    public Annotator(){
        packageMap = new HashMap<>();
        singletons = new ArrayList<>();
    }

    public String annotate(ClassNode classNode){
        StringBuilder output = new StringBuilder();

        //Class Name
        String className = classNode.name.replace('/', '.');
        String packageName = getPackageName(className);

        //Adds package to map if not seen before
        if(!packageMap.containsKey(packageName)){
            packageMap.put(packageName, new PackageCounter());
        }


        //Find singleton pattern
        boolean isSingleton = false;
        for (MethodNode method : classNode.methods) {
            if (Type.getReturnType(method.desc).getClassName().equals(className)) {
                isSingleton = true;
            }
        }


        //decorator pattern check
        //decorator pattern variables
        boolean isDecorator = false;
        String decoratorRelation = "";


        for (String interfaces : classNode.interfaces) {
            interfaces = interfaces.replace('/', '.');
            for (FieldNode field : classNode.fields) {
                String typeName = Type.getType(field.desc).getClassName();
                if(typeName.equals(interfaces)) {
                    isDecorator = true;
                    decoratorRelation = className + "-[#90D5FF]>" + typeName + ": decorates\n";
                }
            }
        }


        String parent;
        if(classNode.superName != null) {
            parent = classNode.superName.replace('/', '.');
        }
        else{
            parent = "";
        }

        //gets decorating classes
        if(parent.endsWith("Decorator")||(className.endsWith("Decorator"))){
            isDecorator = true;
            decoratorRelation = className + "-[#90D5FF]>" + parent + "\n";
        }






        if (isSingleton) {
            output.append(className).append(" -[#red]> ").append(className).append("\n");
            output.append("class ").append(className).append(" <<Singleton>> #red {\n");

            packageMap.get(packageName).incrementSingletonCount();

            singletons.add(className);

        } else if(isDecorator){
            System.out.println("Decorator!");
            output.append(decoratorRelation);
            System.out.println(decoratorRelation);
            output.append("class ").append(className).append(" #90D5FF {\n");
            packageMap.get(packageName).incrementDecoratorCount();
        }else{
            boolean myInterface = (Opcodes.ACC_INTERFACE&classNode.access) != 0;
            if(myInterface){
                output.append("interface ").append(className).append(" {\n");
            }
            else{
                output.append("class ").append(className).append(" {\n");
            }

        }



        return output.toString();
    }

    private String getPackageName(String className){
        int lastDotIndex = className.lastIndexOf('.');
        return (lastDotIndex == -1) ? "" : className.substring(0, lastDotIndex);
    }

    public String getNotes(Map<String, Integer> classNameToRelations){
        StringBuilder output = new StringBuilder();
        determineAbuse(classNameToRelations);
        for(String packageName : packageMap.keySet()){
            if (packageMap.get(packageName).getSingletonCount() == 0 && packageMap.get(packageName).getDecoratorCount() == 0) {
                continue;
            }
            output.append("note top of ").append(packageName).append("\n");

            output.append("    Decorator Count: ").append(packageMap.get(packageName).getDecoratorCount()).append("\n");

            if(packageMap.get(packageName).getAbuseCount() > 0){
                output.append("    SINGLETON ABUSE\n");
            }
            output.append("    Singleton Count: ").append(packageMap.get(packageName).getSingletonCount()).append("\n");

            output.append("end note \n\n");
        }

        return output.toString();
    }

    private void determineAbuse(Map<String, Integer> classNameToRelations){
        for(String singleton : singletons){
            if(classNameToRelations.get(singleton) < 2 || classNameToRelations.get(singleton) >= 4){
                packageMap.get(getPackageName(singleton)).incrementAbuseCount();
            }
        }
    }
}
