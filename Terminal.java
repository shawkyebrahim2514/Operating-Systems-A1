import java.io.*;
import java.nio.file.*;
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
            }else {
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
            result[i] = matchingFiles[i].getName();
        return result;
    }

    public void mkdir(String[] args) {
    }

    public void rmdir(String arg) {
    }

    public static void touch(String arg) throws IOException {
        if(!arg.contains(":")){
            arg = Paths.get(currentDirectory.toString(), arg).toString();
        }
        File f = new File(arg);
        try {
            f.createNewFile();
        }
        catch (IOException error){
            throw new IOException("Couldn't create file");
        }
    }

    public static void cp(String first, String second) {
        try {
            File source = new File(first);
            File destination = new File(second);
            Files.write(destination.toPath(), Files.readAllBytes(source.toPath()), StandardOpenOption.APPEND);
            System.out.println("File appended successfully.");
        } catch (IOException e) {
            System.out.println("Error appending file: " + e.getMessage());
        }
    }
    
    public static void cp_r(String first, String second) {
        try {
            File sourceDir = new File(first);
            File destinationDir = new File(second);
            copyDirectory(sourceDir, destinationDir);
            System.out.println("Directory copied successfully.");
        } catch (IOException e) {
            System.out.println("Error copying directory: " + e.getMessage());
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



    public void rm(String arg) {
    }

    public static void cat(String arg) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(arg));
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

        try {
            BufferedReader reader1 = new BufferedReader(new FileReader(first));
            BufferedReader reader2 = new BufferedReader(new FileReader(second));
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
                        touch(parser.getArgs()[0]);
                        break;
                    case "cp":
                        cp(parser.getArgs()[0],parser.getArgs()[1]);
                        break;
                    case "cp_r":
                        cp_r(parser.getArgs()[0],parser.getArgs()[1]);
                        break;
                    case "rm":
                        break;
                    case "cat":
                        if(parser.getArgs().length == 1) {
                            cat(parser.getArgs()[0]);
                        }else{
                            cat(parser.getArgs()[0], parser.getArgs()[1]);
                        }
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
            } catch (Error | IOException error) {
                System.out.println("> " + error.getMessage());
            }
        }
    }
}
