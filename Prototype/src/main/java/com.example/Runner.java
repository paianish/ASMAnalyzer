package com.example;

import java.io.IOException;
import java.util.ArrayList;

public class Runner {

    public static void main(String[] args) throws IOException {
        ArrayList<String> list = new ArrayList<>();
        list.add("lab2/DecryptionInputStream.class");
        list.add("lab2/EncryptionOutputStream.class");
        list.add("lab2/IDecryption.class");
        list.add("lab2/IEncryption.class");
        list.add("lab2/SubstitutionCipher.class");
        list.add("lab2/TextEditorApp.class");
        list.add("lab2/SingletonExample.class");

        list.add("target/classes/pizzaDecoratorDemo/PlainPizza.class");
        list.add("target/classes/pizzaDecoratorDemo/PizzaDecorator.class");
        list.add("target/classes/pizzaDecoratorDemo/CheeseDecorator.class");
        list.add("target/classes/pizzaDecoratorDemo/PepperoniDecorator.class");
        list.add("target/classes/pizzaDecoratorDemo/Pizza.class");


        Formatter formatter = new Formatter();

        System.out.println(formatter.analyzeProject(list));
        Report report = new Report();
        report.generateReport(formatter.analyzeProject(list));
    }
}
