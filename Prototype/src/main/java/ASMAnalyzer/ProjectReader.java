package ASMAnalyzer;

import org.objectweb.asm.ClassReader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public class ProjectReader {
    private final String BUILD_DIR;
    private final List<String> WHITELIST;
    private final Set<String> classNames;
    private final ArrayList<String> classPaths;

    public ProjectReader(String BUILD_DIR, List<String> WHITELIST) {
        this.BUILD_DIR = BUILD_DIR;
        this.WHITELIST = WHITELIST;
        classNames = new HashSet<>();
        classPaths = new ArrayList<>();
    }

    // Scans through BUILD_DIR and populates classNames and classPaths with correct info
    public void findClassFiles() {
        Path startPath = Paths.get(BUILD_DIR);

        try (Stream<Path> pathStream = Files.walk(startPath)) {
            List<String> paths;
            paths = pathStream.map(Path::toString).filter(pathString -> pathString.endsWith(".class")).toList();
            for (String path : paths) {
                ClassReader classReader = new Parser().parseFile(path);
                String className = classReader.getClassName().replace('/', '.');
                System.out.println("CLASS NAME: " + className);
                String packageName = className.contains(".") ? className.substring(0, className.lastIndexOf(".")) : className;
                System.out.println("PACKAGE NAME: " + packageName);
                if (WHITELIST.isEmpty() || WHITELIST.contains(packageName)) {
                    classNames.add(className);
                    classPaths.add(path);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Set<String> getClassNames() {
        return classNames;
    }

    public ArrayList<String> getClassPaths() {
        return classPaths;
    }
}