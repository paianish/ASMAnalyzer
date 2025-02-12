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

        Formatter formatter = new Formatter();

        System.out.println(formatter.analyzeProject(list));
        Report report = new Report();
        report.generateReport(formatter.analyzeProject(list));
    }
}
