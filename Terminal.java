import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.Vector;

class Parser {
    // Take care of this case: echo -r
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
    static Path currentDirectory;

    // Implement each command in a method, for example:
    public static String echo(String str) {
        return str;
    }

    public static String pwd() {
        return currentDirectory.toString();
    }

    public static void cd() {
        currentDirectory = Paths.get(System.getProperty("user.home"));
    }

    public static void cd(String arg) {
        if (arg.equals("..")) {
            Path parentPath = currentDirectory.getParent();
            if (parentPath == null) {
                throw new Error("You are already in the root directory");
            }
            currentDirectory = parentPath;
        } else {
            Path tempPath;
            if (!arg.contains(":")) {
                // Absolute path
                tempPath = Paths.get(currentDirectory.toString(), arg);
            } else {
                // Relative path
                tempPath = Paths.get(arg);
            }
            if (Files.exists(tempPath) && Files.isDirectory(tempPath)) {
                currentDirectory = tempPath.normalize().toAbsolutePath();
            } else {
                throw new Error("Directory not found");
            }
        }
    }

    public static String[] ls() {
        File f = new File(currentDirectory.toString());
        File[] matchingFiles = f.listFiles();
        String[] result = new String[matchingFiles.length];
        for (int i = 0; i < matchingFiles.length; i++)
            result[i] = matchingFiles[i].toString();
        return result;
    }

    public void mkdir(String[] args) {
    }

    public void rmdir(String arg) {
    }

    public void touch(String arg) {
    }

    public void cp(String first, String second) {
    }

    public void cp_r(String first, String second) {
    }

    public void rm(String arg) {
    }

    public String cat(String arg) {
        return null;
    }

    public String cat(String first, String second) {
        return null;
    }

    public void exit() {
    }

    // ...
    // This method will choose the suitable command method to be called
    public void chooseCommandAction() {

    }

    public static void main(String[] args) {
        currentDirectory = Paths.get(System.getProperty("user.dir"));
        Scanner scanner = new Scanner(System.in);
        Vector<String> history = new Vector<String>();
        parser = new Parser();
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
                        System.out.println("> Current directory: " + currentDirectory.toString());
                        break;
                    case "ls":
                        String[] paths = ls();
                        for (String path : paths
                        ) {
                            System.out.println(path);
                        }
                        break;
                    case "ls_r":
                        paths = ls();
                        for (int i = paths.length-1; i >= 0; i--) {
                            System.out.println(paths[i]);
                        }
                        break;
                    case "mkdir":
                        break;
                    case "rmdir":
                        break;
                    case "touch":
                        break;
                    case "cp":
                        break;
                    case "cp_r":
                        break;
                    case "rm":
                        break;
                    case "cat":
                        break;
                    case "history":
                        for (int i = 0; i < history.size(); i++){
                            System.out.println(i+1+". "+history.get(i));
                        }
                        break;
                    case "exit":
                        scanner.close();
                        return;
                    default:
                        throw new Error("Command not found");
                }
            } catch (Error error) {
                System.out.println("> " + error.getMessage());
            }
        }
    }
}