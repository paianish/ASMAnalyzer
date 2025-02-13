package ASMAnalyzer;

import org.objectweb.asm.ClassReader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class Parser {

    public Parser(){
    }

    public ClassReader parseFile(String path) throws IOException {
        InputStream inputStream = new FileInputStream(path);
        return new ClassReader(inputStream);
    }
}
