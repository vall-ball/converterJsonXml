package converter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class FileHandler {
    String fileName;

    FileHandler(String fileName) {
        this.fileName = fileName;
    }
    public String load() throws FileNotFoundException {
        Scanner scanner = new Scanner(new File(fileName));
        StringBuilder builder = new StringBuilder();
        while (scanner.hasNext()) {
            builder.append(scanner.nextLine());
        }
        scanner.close();
        return builder.toString();
    }

    public void save(String s) throws IOException {
        FileWriter writer = new FileWriter("D:\\programming\\1.txt");
        writer.write(s);
        writer.close();
    }
}
