package ASMAnalyzer;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.io.IOException;
import java.util.*;

public class Analyzer {

    private Parser parser;
    private Annotator annotator;
    private Set<String> classNames;

    private Map<String, Integer> classNameToRelations;
    public Analyzer(Set<String> classNames){
        parser = new Parser();
        annotator = new Annotator();
        this.classNames = classNames;
        classNameToRelations = new HashMap<>();
    }

    public String analyzeFile(String path) throws IOException {
        ClassNode classNode = new ClassNode();
        parser.parseFile(path).accept(classNode, 0);
        String analyzedClassCode = annotator.annotate(classNode)+analyzeClassNode(classNode);
        return analyzedClassCode;
    }

    public String getNotes(){
        return annotator.getNotes(classNameToRelations);
    }

    private String analyzeClassNode(ClassNode classNode){
        StringBuilder output = new StringBuilder();
        StringBuilder relations = new StringBuilder();


        //Class Name
        String className = classNode.name.replace('/', '.');

        //Class Name to Relations map logic
        if(!classNameToRelations.keySet().contains(className)){
            classNameToRelations.put(className,0);
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
            String access = "";
            if(field.access%2 == 1){
                access = "    + ";
            }else{
                access = "    - ";
            }
            output.append(access).append(field.name).append(" : ").append(typeName).append("\n");

            if (typeName.contains("[")) {
                String cleanName = typeName.substring(0, typeName.indexOf("["));
                if (classNames.contains(cleanName)) {
                    String newRelation = className + " \"1\"-->\"*\" " + cleanName + "\n";
                    if (!relations.toString().contains(newRelation)) {
                        relations.append(newRelation);
                        createOrIncrement(cleanName);
                    }
                }
            } else if(field.desc.contains("ArrayList")){
                String signature = field.signature;
                signature = signature.substring(signature.indexOf("<")+2);
                signature = signature.substring(0,signature.length()-3);
                signature.replace("/",".");
                if(classNames.contains(signature)){
                    String newRelation = className + " \"1\"-->\"*\" " + signature + "\n";
                    if (!relations.toString().contains(newRelation)) {
                        relations.append(newRelation);
                        createOrIncrement(signature);
                    }
                }

            }else if(field.desc.contains("Map")){
                String signature = field.signature;
                signature = signature.substring(signature.indexOf("<")+2);
                signature = signature.substring(0,signature.length()-3);
                String type1 = signature.substring(0,signature.indexOf(";")).replace("/",".");
                String type2 = signature.substring(signature.indexOf(";")+2).replace("/",".");
                if(classNames.contains(type1)){
                    String newRelation = className + " \"1\"-->\"*\" " + type1 + "\n";
                    if (!relations.toString().contains(newRelation)) {
                        relations.append(newRelation);
                        createOrIncrement(type1);

                    }
                }

                if(classNames.contains(type2)){
                    String newRelation = className + " \"1\"-->\"*\" " + type2 + "\n";
                    if (!relations.toString().contains(newRelation)) {
                        relations.append(newRelation);
                        createOrIncrement(type2);
                    }
                }

            } else {
                if (classNames.contains(typeName)) {
                    String newRelation = className + " \"1\"-->\"1\" " + typeName + "\n";
                    if (!relations.toString().contains(newRelation)) {
                        relations.append(newRelation);
                        createOrIncrement(typeName);
                    }
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
                        if(typeName.contains("[")){
                            String cleanName = typeName.substring(0, typeName.indexOf("["));
                            if(!localVars.name.equals("this") && classNames.contains(cleanName)){
                                String newRelation = className + " \"1\"-->\"1\" " + className + "\n";
                                if (!relations.toString().contains(newRelation)) {
                                    relations.append(newRelation);
                                    createOrIncrement(className);
                                }

                            }
                        }else {
                            if (!localVars.name.equals("this") && classNames.contains(typeName)) {
                                String newRelation = className + " \"1\"-->\"1\" " + typeName + "\n";
                                if (!relations.toString().contains(newRelation)) {
                                    relations.append(newRelation);
                                    createOrIncrement(typeName);
                                }
                            }
                        }
                    }
                }

                output.append(") : " + Type.getReturnType(method.desc).getClassName() + "\n");
            }
        }

        if (classNode.superName != null && !classNode.superName.equals("java/lang/Object")) {
            String superClass = classNode.superName.replace('/', '.');
            String inheritanceRelation = className + " --|>" + superClass + "\n";
            if (!relations.toString().contains(inheritanceRelation)) {
                relations.append(inheritanceRelation);
            }
        }

        output.append("}\n");

        output.append(relations);

        return output.toString();
    }

    private void createOrIncrement(String className){
        if(!classNameToRelations.keySet().contains(className)){
            classNameToRelations.put(className,1);
        }else {
            classNameToRelations.put(className,classNameToRelations.get(className)+1);
        }
    }
}
