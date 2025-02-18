package ASMAnalyzer;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.nio.file.*;
import java.util.HashSet;
import java.util.Set;

public class Runner {

    public static void main(String[] args) throws IOException {
        Set<String> classNames = new HashSet<>();
        ArrayList<String> paths = new ArrayList<>();

        String directoryPath = "target/classes";

        paths = findClassFiles(directoryPath, classNames);

        Formatter formatter = new Formatter(classNames);
        Report report = new Report();

        String umlCode = formatter.analyzeProject(paths);
        System.out.println(umlCode);
        try (PrintWriter out = new PrintWriter("output.txt")) {
            // out.println(umlCode);
        }
        report.generateReport(umlCode);

        String outputImagePath = "output.svg";

        Desktop desktop = Desktop.getDesktop();

        if (desktop.isSupported(Desktop.Action.OPEN)) {
            try {
                File svgFile = new File(outputImagePath);

                if (svgFile.exists()) {
                    desktop.open(svgFile);
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

    private static ArrayList<String> findClassFiles(String directoryPath, Set<String> classNames){
        ArrayList<String> paths = new ArrayList<>();
        Path startPath = Paths.get(directoryPath);

        try{
            Files.walk(startPath).filter(path -> Files.isRegularFile(path) && path.toString().endsWith(".class")).forEach(path -> {
                paths.add(path.toString());

                String[] pathParts;
                if (System.getProperty("os.name").equals("Mac OS X")) {
                    pathParts = path.toString().split("/");
                } else {
                    pathParts = path.toString().split("\\\\");
                }

                StringBuilder classNameBuilder = new StringBuilder();
                for (int i = 2; i < pathParts.length - 1; i++) {
                    classNameBuilder.append(pathParts[i]).append('.');
                }
                String className = pathParts[pathParts.length-1];
                className = className.substring(0, className.indexOf("."));
                classNameBuilder.append(className);
                classNames.add(classNameBuilder.toString());
            });
        }catch (Exception e){
            e.printStackTrace();
        }
        return paths;
    }
}
