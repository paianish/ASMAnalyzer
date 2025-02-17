package ASMAnalyzer;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Analyzer {

    private Parser parser;
    private Annotator annotator;
    private Set<String> classNames;
    public Analyzer(Set<String> classNames){
        parser = new Parser();
        annotator = new Annotator();
        this.classNames = classNames;
    }

    public String analyzeFile(String path) throws IOException {
        ClassNode classNode = new ClassNode();
        parser.parseFile(path).accept(classNode, 0);
        String analyzedClassCode = annotator.annotate(classNode)+analyzeClassNode(classNode);
        return analyzedClassCode;
    }

    public String getNotes(){
        return annotator.getNotes();
    }

    private String analyzeClassNode(ClassNode classNode){
        StringBuilder output = new StringBuilder();
        StringBuilder relations = new StringBuilder();

        //Class Name
        String className = classNode.name.replace('/', '.');

        //Interfaces
        for(String interfaces: classNode.interfaces){
            interfaces = interfaces.replace('/', '.');
            String newRelation = className + " ..|> " + interfaces + ": implements\n";
            if(!relations.toString().contains(newRelation)){
                relations.append(newRelation);
            }
        }

        //Fields
        for (FieldNode field : classNode.fields) {
            String typeName = Type.getType(field.desc).getClassName();
            String access = "";
            if(field.access%2 == 1){
                access = "    + ";
            }else{
                access = "    - ";
            }
            output.append(access).append(field.name).append(" : ").append(typeName).append("\n");
            if (classNames.contains(typeName)) {
                String newRelation = className + " --> " + typeName + "\n";
                if(!relations.toString().contains(newRelation)){
                    relations.append(newRelation);
                }
            }
        }

        //Methods
        for (MethodNode method : classNode.methods) {
            if(!method.name.startsWith("lambda$") && !method.name.startsWith("<")) {
                String access = "";
                if (method.access % 2 == 1) {
                    access = "    + ";
                } else {
                    access = "    - ";
                }
                output.append(access).append(method.name).append("(");
                List<String> out = new ArrayList<>();
                Type[] argTypes = Type.getArgumentTypes(method.desc);
                List<ParameterNode> parameters = method.parameters;
                if (parameters != null && parameters.size() > 0 && argTypes.length > 0) {
                    int parametersIndex = 0;
                    for (int i = 0; i < argTypes.length; i++) {
                        ParameterNode var = parameters.get(parametersIndex++);
                        String[] classNames = argTypes[i].getClassName().split("\\.");
                        out.add(classNames[classNames.length - 1] + " " + var.name);
                    }
                }
                output.append(String.join(", ", out));
                if (method.localVariables != null) {
                    for (LocalVariableNode localVars : method.localVariables) {
                        String typeName = Type.getType(localVars.desc).getClassName();
                        if (!localVars.name.equals("this") && classNames.contains(typeName)) {
                            String newRelation = className + " --> " + typeName + "\n";
                            if (!relations.toString().contains(newRelation)) {
                                relations.append(newRelation);
                            }
                        }
                    }
                }

                output.append(") : " + Type.getReturnType(method.desc).getClassName() + "\n");
            }
        }



        output.append("}\n");

        output.append(relations);

        return output.toString();
    }
}
