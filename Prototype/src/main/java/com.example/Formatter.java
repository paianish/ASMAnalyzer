package com.example;

import java.io.IOException;
import java.util.ArrayList;

public class Formatter {

    private Analyzer analyzer;
    public Formatter(){
        analyzer = new Analyzer();
    }

    public String analyzeProject(ArrayList<String> list) throws IOException {
        StringBuilder output = new StringBuilder();

        output.append("@startuml\n");

        for(String path : list){
            output.append(analyzer.analyzeFile(path));
        }

        output.append("@enduml");

        return output.toString();
    }
}
