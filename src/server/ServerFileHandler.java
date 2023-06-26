package server;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class ServerFileHandler {

    private Path logFilePath;

    public ServerFileHandler(String logFileName) {
        logFilePath = Path.of(logFileName);
        try {
            if (!Files.exists(logFilePath)) {
                Files.createFile(logFilePath);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String> readLog() {
        try {
            return Files.readAllLines(logFilePath);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void appendToLog(String message) {
        try {
            Files.writeString(logFilePath, message + System.lineSeparator(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
