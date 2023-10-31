import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Scanner;
import java.util.Vector;

class Parser {
    String commandName;
    String[] args;

    // This method will divide the input into commandName and args
    // where "input" is the string command entered by the user
    public boolean parse(String input) {
        String[] parsedValues = input.split(" ");
        commandName = parsedValues[0];
        if (parsedValues.length == 1) {
            args = null;
            return true;
        }
        if (parsedValues[1].equals("-r")) {
            commandName += "_r";
            args = new String[parsedValues.length - 2];
            for (int i = 2; i < parsedValues.length; i++) {
                args[i - 2] = parsedValues[i];
            }
            return true;
        }
        args = new String[parsedValues.length - 1];
        for (int i = 1; i < parsedValues.length; i++) {
            args[i - 1] = parsedValues[i];
        }
        return true;
    }

    public String getCommandName() {
        return commandName;
    }

    public String[] getArgs() {
        return args;
    }
}

public class Terminal {
    static Parser parser;
    static Path currentDirectoryPath;
    static Scanner scanner;
    static Vector<String> history;

    public static String echo(String str) {
        return str;
    }

    public static String pwd() {
        return currentDirectoryPath.toString();
    }

    public static void cd() {
        currentDirectoryPath = Paths.get(System.getProperty("user.home"));
    }

    public static void cd(String arg) {
        if (arg.equals("..")) {
            Path parentDirectoryPath = currentDirectoryPath.getParent();
            if (parentDirectoryPath == null) {
                throw new Error("You are already in the root directory");
            }
            currentDirectoryPath = parentDirectoryPath;
        } else {
            Path newDirectoryPath = Paths.get(arg);
            if (!newDirectoryPath.isAbsolute()) {
                newDirectoryPath = Paths.get(currentDirectoryPath.toString(), arg);
            }
            if (Files.exists(newDirectoryPath) && Files.isDirectory(newDirectoryPath)) {
                currentDirectoryPath = newDirectoryPath.normalize().toAbsolutePath();
            } else {
                throw new Error("Directory not found");
            }
        }
    }

    public static String[] ls() {
        File f = new File(currentDirectoryPath.toString());
        File[] matchingFiles = f.listFiles();
        String[] result = new String[matchingFiles.length];
        for (int i = 0; i < matchingFiles.length; i++)
            result[i] = matchingFiles[i].getName();
        return result;
    }

    public static void mkdir(String[] args) {
        for (String arg : args) {
            File directory = new File(arg);
            // Check if the argument is a valid directory name or path
            if (!directory.isAbsolute()) {
                // If it's not an absolute path, create the directory in the current directory
                directory = new File(currentDirectoryPath.toFile(), arg);
            }
            // check if the directory is created successfully.
            if (!directory.mkdirs()) {
                throw new Error("Failed to create directory: " + directory.getAbsolutePath());
            }
        }
    }

    public static void rmdir(String arg) {
        if (arg.equals("*")) {
            // Case 1: Remove all empty directories in the current directory
            File[] subdirectories = currentDirectoryPath.toFile().listFiles(File::isDirectory);
            if (subdirectories != null) {
                for (File directory : subdirectories) {
                    if (directory.isDirectory() && directory.list().length == 0) {
                        if (!directory.delete()) {
                            throw new Error("Failed to remove directory: " + directory.getAbsolutePath());
                        }
                    }
                }
            }
        } else {
            // Case 2: Remove a specific empty directory specified by the path
            File directory = new File(currentDirectoryPath.toFile(), arg);
            // Check if the directory is empty
            if (directory.isDirectory() && directory.list().length == 0) {
                if (!directory.delete()) {
                    throw new Error("Failed to remove directory: " + directory.getAbsolutePath());
                }
            } else {
                throw new Error("Directory is not empty or does not exist: " + directory.getAbsolutePath());
            }
        }
    }

    public static void touch(String arg) throws IOException {
        File f = new File(arg);
        if (!f.isAbsolute()) {
            f = new File(currentDirectoryPath.toString(), arg);
        }
        try {
            f.createNewFile();
        } catch (IOException error) {
            throw new IOException("Couldn't create file");
        }
    }

    public static void cp(String first, String second) {
        File source = new File(first);
        File destination = new File(second);
        if (!source.isAbsolute()) {
            source = new File(currentDirectoryPath.toString(), first);
        }
        if (!destination.isAbsolute()) {
            destination = new File(currentDirectoryPath.toString(), second);
        }
        try {
            Files.write(destination.toPath(), Files.readAllBytes(source.toPath()), StandardOpenOption.APPEND);
            System.out.println("> File appended successfully.");
        } catch (IOException e) {
            throw new Error("Error appending file: " + e.getMessage());
        }
    }

    public static void cp_r(String first, String second) {
        File sourceDir = new File(first);
        File destinationDir = new File(second);
        if (!sourceDir.isAbsolute()) {
            sourceDir = new File(currentDirectoryPath.toString(), first);
        }
        if (!destinationDir.isAbsolute()) {
            destinationDir = new File(currentDirectoryPath.toString(), second);
        }
        try {
            copyDirectory(sourceDir, destinationDir);
            System.out.println("> Directory copied successfully.");
        } catch (IOException e) {
            throw new Error("Error copying directory: " + e.getMessage());
        }
    }

    private static void copyDirectory(File source, File destination) throws IOException {
        if (source.isDirectory()) {
            if (!destination.exists()) {
                destination.mkdir();
            }

            String[] children = source.list();
            for (String child : children) {
                copyDirectory(new File(source, child), new File(destination, child));
            }
        } else {
            InputStream in = null;
            OutputStream out = null;
            try {
                in = new FileInputStream(source);
                out = new FileOutputStream(destination);

                byte[] buffer = new byte[1024];
                int length;
                while ((length = in.read(buffer)) > 0) {
                    out.write(buffer, 0, length);
                }
            } finally {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
            }
        }
    }

    public static void rm(String arg) {
        File file = new File(currentDirectoryPath.toFile(), arg);
        if (file.exists() && file.isFile()) {
            if (!file.delete()) {
                throw new Error("Failed to remove file: " + file.getAbsolutePath());
            }
        } else {
            throw new Error("File does not exist: " + file.getAbsolutePath());
        }
    }

    public static void cat(String arg) {
        File file = new File(arg);
        if (!file.isAbsolute()) {
            file = new File(currentDirectoryPath.toString(), arg);
        }
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            reader.close();
        } catch (IOException e) {
            throw new Error("Error reading file: " + e.getMessage());
        }
    }

    public static void cat(String first, String second) {
        File firstFile = new File(first);
        File secondFile = new File(second);
        if (!firstFile.isAbsolute()) {
            firstFile = new File(currentDirectoryPath.toString(), first);
        }
        if (!secondFile.isAbsolute()) {
            secondFile = new File(currentDirectoryPath.toString(), second);
        }
        try {
            BufferedReader reader1 = new BufferedReader(new FileReader(firstFile));
            BufferedReader reader2 = new BufferedReader(new FileReader(secondFile));
            String line;
            while ((line = reader1.readLine()) != null) {
                System.out.println(line);
            }
            while ((line = reader2.readLine()) != null) {
                System.out.println(line);
            }
            reader1.close();
            reader2.close();
        } catch (IOException e) {
            throw new Error("Error reading file: " + e.getMessage());
        }
    }

    public static void exit() {
        scanner.close();
    }

    // This method will choose the suitable command method to be called
    public static void chooseCommandAction() {
        while (true) {
            String input = scanner.nextLine();
            history.add(input);
            parser.parse(input);
            try {
                switch (parser.getCommandName()) {
                    case "echo":
                        if (parser.getArgs() == null) {
                            throw new Error("Missing argument");
                        }
                        System.out.println("> " + echo(parser.getArgs()[0]));
                        break;
                    case "pwd":
                        System.out.println("> " + pwd());
                        break;
                    case "cd":
                        if (parser.getArgs() == null) {
                            cd();
                        } else {
                            cd(parser.getArgs()[0]);
                        }
                        System.out.println("> Current directory: " + currentDirectoryPath.toString());
                        break;
                    case "ls":
                        String[] paths = ls();
                        for (String path : paths) {
                            System.out.println(path);
                        }
                        break;
                    case "ls_r":
                        paths = ls();
                        for (int i = paths.length - 1; i >= 0; i--) {
                            System.out.println(paths[i]);
                        }
                        break;
                    case "mkdir":
                        mkdir(parser.getArgs());
                        break;
                    case "rmdir":
                        rmdir(parser.getArgs()[0]);
                        break;
                    case "touch":
                        touch(parser.getArgs()[0]);
                        break;
                    case "cp":
                        cp(parser.getArgs()[0], parser.getArgs()[1]);
                        break;
                    case "cp_r":
                        cp_r(parser.getArgs()[0], parser.getArgs()[1]);
                        break;
                    case "rm":
                        rm(parser.getArgs()[0]);
                        break;
                    case "cat":
                        if (parser.getArgs().length == 1) {
                            cat(parser.getArgs()[0]);
                        } else {
                            cat(parser.getArgs()[0], parser.getArgs()[1]);
                        }
                        break;
                    case "history":
                        for (int i = 0; i < history.size(); i++) {
                            System.out.println(i + 1 + ". " + history.get(i));
                        }
                        break;
                    case "exit":
                        exit();
                        return;
                    default:
                        throw new Error("Command not found");
                }
            } catch (Error | IOException error) {
                System.out.println("> " + error.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        currentDirectoryPath = Paths.get(System.getProperty("user.dir"));
        scanner = new Scanner(System.in);
        history = new Vector<String>();
        parser = new Parser();
        chooseCommandAction();
    }
}