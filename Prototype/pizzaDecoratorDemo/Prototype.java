package com.example;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

public class Prototype {
    public static void main(String[] args) throws Exception {
//        if (args.length != 1) {
//            System.out.println("Usage: java Analyzer <path-to-class-file>");
//            return;
//        }
        StringBuilder output = new StringBuilder();
        StringBuilder relations = new StringBuilder();

        output.append("@startuml\n");

        ArrayList<String> list = new ArrayList<>();
//        list.add("lab2/DecryptionInputStream.class");
//        list.add("lab2/EncryptionOutputStream.class");
//        list.add("lab2/IDecryption.class");
//        list.add("lab2/IEncryption.class");
//        list.add("lab2/SubstitutionCipher.class");
//        list.add("lab2/TextEditorApp.class");
//        list.add("target/classes/ExclamationDecorator.class");
//        list.add("target/classes/Main.class");
//        list.add("target/classes/PlainTextProcessor.class");
//        list.add("target/classes/TextDecorator.class");
//        list.add("target/classes/TextProcessor.class");
//        list.add("target/classes/UpperCaseDecorator.class");
        list.add("target/classes/PlainPizza.class");
        list.add("target/classes/PizzaDecorator.class");
        list.add("target/classes/CheeseDecorator.class");
        list.add("target/classes/PepperoniDecorator.class");
        list.add("target/classes/Pizza.class");

        for (int index = 0; index < list.size(); index++) {

            String classPath = list.get(index);
            InputStream inputStream = new FileInputStream(classPath);
            ClassReader reader = new ClassReader(inputStream);
            ClassNode classNode = new ClassNode();
            reader.accept(classNode, 0);

            String className = classNode.name.replace('/', '.');

            // output.append("class ").append(className).append(" {\n");

            //decorator pattern variables
            boolean isDecorator = false;
            String decoratorRelation = "";
            String decoratorAnnotation = "";

            //decorator pattern interface check
            for (String interfaces : classNode.interfaces) {
                interfaces = interfaces.replace('/', '.');
                for (FieldNode field : classNode.fields) {
                    String typeName = Type.getType(field.desc).getClassName();
                    if(typeName.equals(interfaces) || list.contains(typeName.replace('.', '/') + ".class")) {
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

            for (String interfaces : classNode.interfaces) {
                interfaces = interfaces.replace('/', '.');
                String newRelation = className + " ..|> " + interfaces + ": implements\n";
                if (!relations.toString().contains(newRelation)) {
                    relations.append(newRelation);
                }
            }

            for (FieldNode field : classNode.fields) {
                String typeName = Type.getType(field.desc).getClassName();
                output.append("    - ").append(field.name).append(" : ").append(typeName).append("\n");
                if (!typeName.startsWith("java.") && !typeName.startsWith("javax.") && !typeName.startsWith("char")
                        && !typeName.startsWith("int") && !typeName.startsWith("String")
                        && !typeName.startsWith("double") && !typeName.startsWith("float")
                        && !typeName.startsWith("long") && !typeName.startsWith("short")
                        && !typeName.startsWith("byte")) {
                    String newRelation = className + " --> " + typeName + "\n";
                    if (!relations.toString().contains(newRelation)) {
                        relations.append(newRelation);
                    }
                }
            }

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
                if (method.localVariables != null) {
                    for (LocalVariableNode localVars : method.localVariables) {
                        String typeName = Type.getType(localVars.desc).getClassName();
                        if (!localVars.name.equals("this") && !typeName.startsWith("java.")
                                && !typeName.startsWith("javax.") && !typeName.startsWith("char")
                                && !typeName.startsWith("int") && !typeName.startsWith("String")
                                && !typeName.startsWith("double") && !typeName.startsWith("float")
                                && !typeName.startsWith("long") && !typeName.startsWith("short")
                                && !typeName.startsWith("byte")) {
                            String newRelation = className + " --> " + typeName + "\n";
                            if (!relations.toString().contains(newRelation)) {
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

        }
        output.append(relations);
        output.append("@enduml");

        System.out.println(output);

        try (PrintWriter out = new PrintWriter("output.txt")) {
            out.println(output.toString());
        }
    }
}