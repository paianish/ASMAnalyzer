package ASMAnalyzer;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Runner {
    private static String PROJECT_DIR;
    private static List<String> WHITELIST;

    public static void main(String[] args) throws IOException {
        runCLI();

        // Parse project dir
        ProjectReader projectReader = new ProjectReader(PROJECT_DIR, WHITELIST);
        projectReader.findClassFiles();

        // Formatter
        Formatter formatter = new Formatter(projectReader.getClassNames());
        String umlCode = formatter.analyzeProject(projectReader.getClassPaths());
        try (PrintWriter out = new PrintWriter("output.txt")) {
            out.println(umlCode);
        }

        // SVG Generation
        Report report = new Report();
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
                System.out.println("Failed to open file " + outputImagePath);
            }
        } else {
            System.out.println("Open action is not supported on this system.");
        }
    }

    private static void runCLI() {
        Scanner scanner = new Scanner(System.in);

        //prompt for directory name
        System.out.print("Enter directory name: ");
        String directory = scanner.nextLine();

        //prompt for whitelist names
        System.out.print("Enter package names to whitelist (separated by spaces): ");
        String whitelistInput = scanner.nextLine();
        String[] whitelist = whitelistInput.trim().split("\\s+");  // \s for whitespace, + for once or more

        // Optionally, print the inputs to verify
        System.out.println("Directory: " + directory);
        System.out.println("Whitelist:");
        for (String name : whitelist) {
            System.out.println(" - " + name);
        }
        PROJECT_DIR = directory;
        if (whitelist[0].isEmpty()) {
            WHITELIST = new ArrayList<>();
        } else {
            WHITELIST = Arrays.asList(whitelist);
        }
    }
}