package ASMAnalyzer;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

public class Annotator {

    public Annotator(){

    }
    public String annotate(ClassNode classNode){
        StringBuilder output = new StringBuilder();

        //Class Name
        String className = classNode.name.replace('/', '.');


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

        if (isSingleton) {
            output.append(className).append(" -[#red]> ").append(className).append("\n");
            output.append("class ").append(className).append(" <<Singleton>> #red {\n");
        } else if(isDecorator==true){
            output.append(decoratorRelation);
            output.append("class ").append(className).append(" #90D5FF {\n");
        }else{
            output.append("class ").append(className).append(" {\n");

        }

        return output.toString();
    }
}
