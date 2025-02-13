package ASMAnalyzer;
import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;

public class Report {
    public Report(){

    }

    public void generateReport(String umlCode) throws IOException {
        String outputPath = "output.svg";

        SourceStringReader reader = new SourceStringReader(umlCode);
        final ByteArrayOutputStream os = new ByteArrayOutputStream();
        // Write the first image to "os"
        String desc = reader.generateImage(os, new FileFormatOption(FileFormat.SVG));
        os.close();

        // The XML is stored into svg
        final String svg = new String(os.toByteArray(), Charset.forName("UTF-8"));
        try (PrintWriter out = new PrintWriter(outputPath)) {
            out.println(svg);
        }

    }

}
