package ASMAnalyzer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Formatter {

    private Analyzer analyzer;
    public Formatter(Set<String> classNames){
        analyzer = new Analyzer(classNames);
    }

    public String analyzeProject(ArrayList<String> paths) throws IOException {
        StringBuilder output = new StringBuilder();

        output.append("@startuml\n");

        for(String path : paths){
            output.append(analyzer.analyzeFile(path));
        }
        output.append(analyzer.getNotes());
        output.append("@enduml");

        return output.toString();
    }
}
