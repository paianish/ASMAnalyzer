package com.example;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Analyzer {

    private Parser parser;
    public Analyzer(){
        parser = new Parser();
    }

    public String analyzeFile(String path) throws IOException {
        ClassNode classNode = new ClassNode();
        parser.parseFile(path).accept(classNode, 0);
        CodeAndRelations analyzedClassNode = analyzeClassNode(classNode);
        analyzedClassNode.code.append(analyzedClassNode.relations);
        return analyzedClassNode.code.toString();
    }

    private CodeAndRelations analyzeClassNode(ClassNode classNode){
        StringBuilder output = new StringBuilder();
        StringBuilder relations = new StringBuilder();

        //Class Name
        String className = classNode.name.replace('/', '.');

        boolean isSingleton = false;
        //Find singleton pattern
        for (MethodNode method : classNode.methods) {
          if (Type.getReturnType(method.desc).getClassName().equals(className)) {
            isSingleton = true;
          }
        }

        if (isSingleton) {
            output.append(className).append(" -[#red]> ").append(className).append("\n");
            output.append("class ").append(className).append(" <<Singleton>> #red {\n");
        } else {
            output.append("class ").append(className).append(" {\n");
        }

        //decorator pattern variables
        boolean isDecorator = false;
        String decoratorRelation = "";
        String decoratorAnnotation = "";

        //decorator pattern interface check
        for (String interfaces : classNode.interfaces) {
            interfaces = interfaces.replace('/', '.');
            for (FieldNode field : classNode.fields) {
                String typeName = Type.getType(field.desc).getClassName();
                if(typeName.equals(interfaces)) {
                    isDecorator = true;
                    decoratorRelation = className + "-[#90D5FF]>" + typeName + ": decorates\n";
//                        decoratorAnnotation = className + " #90D5FF : Decorator\n";
                }
            }
        }

        if(isDecorator==true){
            output.append("class ").append(className).append(" #90D5FF {\n");
        }else{
            output.append("class ").append(className).append(" {\n");

        }

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
            output.append("    - ").append(field.name).append(" : ").append(typeName).append("\n");
            if (!typeName.startsWith("java.") && !typeName.startsWith("javax.") && !typeName.startsWith("char") && !typeName.startsWith("int") && !typeName.startsWith("String") && !typeName.startsWith("double") && !typeName.startsWith("float")&& !typeName.startsWith("long") && !typeName.startsWith("short") && !typeName.startsWith("byte")) {
                String newRelation = className + " --> " + typeName + "\n";
                if(!relations.toString().contains(newRelation)){
                    relations.append(newRelation);
                }
            }
        }

        //Methods
        for (MethodNode method : classNode.methods) {
            output.append("    + ").append(method.name).append("(");
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
            if(method.localVariables != null) {
                for (LocalVariableNode localVars : method.localVariables) {
                    String typeName = Type.getType(localVars.desc).getClassName();
                    if (!localVars.name.equals("this") && !typeName.startsWith("java.") && !typeName.startsWith("javax.") && !typeName.startsWith("char") && !typeName.startsWith("int") && !typeName.startsWith("String") && !typeName.startsWith("double") && !typeName.startsWith("float") && !typeName.startsWith("long") && !typeName.startsWith("short") && !typeName.startsWith("byte")) {
                        String newRelation = className + " --> " + typeName + "\n";
                        if(!relations.toString().contains(newRelation)){
                            relations.append(newRelation);
                        }
                    }
                }
            }

            output.append(") : " + Type.getReturnType(method.desc).getClassName() + "\n");
        }

        if(isDecorator){
            relations.append(decoratorRelation);
            relations.append(decoratorAnnotation);
        }
        output.append("}\n");

        return new CodeAndRelations(output,relations);
    }
}
