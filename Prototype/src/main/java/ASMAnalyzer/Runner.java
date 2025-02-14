package ASMAnalyzer;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.nio.file.*;

public class Runner {

    public static void main(String[] args) throws IOException {
        ArrayList<String> paths = new ArrayList<>();

        String directoryPath = "target/classes";

        paths = findClassFiles(directoryPath);

        Formatter formatter = new Formatter();
        Report report = new Report();

        String umlCode = formatter.analyzeProject(paths);
        System.out.println(umlCode);
        try (PrintWriter out = new PrintWriter("output.txt")) {
            out.println(umlCode);
        }
        report.generateReport(umlCode);

        String outputImagePath = "output.svg";

        Desktop desktop = Desktop.getDesktop();

        if (desktop.isSupported(Desktop.Action.OPEN)) {
            try {
                File svgFile = new File(outputImagePath);

                if (svgFile.exists()) {
                    desktop.open(svgFile); // Opens in default SVG viewer
                } else {
                    System.out.println("File does not exist.");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Open action is not supported on this system.");
        }
    }

    private static ArrayList<String> findClassFiles(String directoryPath){
        ArrayList<String> paths = new ArrayList<>();
        Path startPath = Paths.get(directoryPath);

        try{
            Files.walk(startPath).filter(path -> Files.isRegularFile(path) && path.toString().endsWith(".class")).forEach(path -> paths.add(path.toString()));
        }catch (Exception e){
            e.printStackTrace();
        }
        return paths;
    }
}
