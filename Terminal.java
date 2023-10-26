import java.util.Scanner;

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

    // Implement each command in a method, for example:
    public String echo(String str) {
        return null;
    }

    public String pwd() {
        throw new Error("snbdfjsaf");
        // return null;
    }

    public void cd() {

    }

    public void cd(String arg) {

    }

    public String[] ls() {
        return null;
    }

    public String[] ls_r() {
        return null;
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

    public String[] history() {
        return null;
    }

    public void exit() {
    }

    // ...
    // This method will choose the suitable command method to be called
    public void chooseCommandAction() {

    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        parser = new Parser();
        parser.parse(input);
        try {
            switch (parser.getCommandName()) {
                case "echo":
                break;
                case "pwd":
                break;
                case "cd":
                break;
                case "ls":
                break;
                case "ls_r":
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
                break;
                case "exit":
                break;
                default:
                throw new Error("Command not found");
            }
        } catch (Error error) {
            System.out.println(error.getMessage());
        }
    }
}