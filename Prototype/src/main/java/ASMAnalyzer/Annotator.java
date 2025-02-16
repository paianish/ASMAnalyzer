package ASMAnalyzer;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.HashMap;
import java.util.Map;

public class Annotator {
    private Map<String, PackageCounter> packageMap;
    public Annotator(){
        packageMap = new HashMap<>();
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

        //decorator pattern variables
        boolean isDecorator = false;
        String decoratorRelation = "";

        //decorator pattern check
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

        //gets actual decorating classes
        if(parent.endsWith("Decorator")){
            isDecorator = true;
            decoratorRelation = className + "-[#90D5FF]>" + parent + "\n";
        }

        if (isSingleton) {
            output.append(className).append(" -[#red]> ").append(className).append("\n");
            output.append("class ").append(className).append(" <<Singleton>> #red {\n");

            packageMap.get(packageName).incrementSingletonCount();

        } else if(isDecorator==true){
            output.append(decoratorRelation);
            output.append("class ").append(className).append(" #90D5FF {\n");

            packageMap.get(packageName).incrementDecoratorCount();
        }else{
            output.append("class ").append(className).append(" {\n");

        }

        return output.toString();
    }

    public String getPackageName(String className){
        int lastDotIndex = className.lastIndexOf('.');
        return (lastDotIndex == -1) ? "" : className.substring(0, lastDotIndex);
    }

    public String getNotes(){
        StringBuilder output = new StringBuilder();

        for(String packageName : packageMap.keySet()){
            output.append("note top of ").append(packageName).append("\n");
            output.append("    Decorator Count: ").append(packageMap.get(packageName).getDecoratorCount()).append("\n");
            output.append("    Singleton Count: ").append(packageMap.get(packageName).getSingletonCount()).append("\n");
            output.append("end note \n\n");
        }

        return output.toString();
    }
}
