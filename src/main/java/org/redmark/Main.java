package org.redmark;

import com.googlecode.lanterna.*;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.googlecode.lanterna.input.KeyType.*;

public class Main {

    static int mainInd = -1;
    static ArrayList<String> todos = new ArrayList<>(Arrays
            .asList("Do This", "Do that", "And finally This"));
    static ArrayList<String> dones = new ArrayList<>(Arrays
            .asList("Navigation", "Out Of Bound", "Git rebase", "OneMore to Test"));
    static boolean isInTodo = true;
    static int terminalWidth = 0;
    static String donePrefix = "[X]";
    static String todoPrefix = "[]";
    static int maxInd = Integer.max(todos.size(), dones.size());

    public static void main(String[] args) throws IOException {
        ;
        Screen screen = new DefaultTerminalFactory().createScreen();
        screen.startScreen();
        String heading = "Judu : A ToDo App";
        TextGraphics textGraphics = screen.newTextGraphics();
        textGraphics.enableModifiers(SGR.REVERSE);
        textGraphics.putString((screen.getTerminalSize().getColumns() - heading.length()) / 2, 1, heading);
        defaultText(textGraphics);
        terminalWidth = screen.getTerminalSize().getColumns();

        renderTodo(todos, textGraphics);
        //renderDones(dones, textGraphics);
        screen.refresh();


        int index = 0;
        // KeyStroke keyStroke = screen.readInput();
        while (true) {
            KeyStroke keyStroke = screen.readInput();
            if (!keyStroke.getKeyType().equals(EOF))
                switch (keyStroke.getKeyType()) {
                    case Escape: {
                        screen.stopScreen();
                        break;
                    }
                    case ArrowDown: {
                        if (isInTodo)
                            moveDown(textGraphics, todos, todoPrefix);
                        else
                            moveDown(textGraphics, dones, donePrefix);
                        System.out.println("Move Down Called : " + index++ + " " + mainInd);
                        defaultText(textGraphics);
                        screen.refresh();
                        break;
                    }
                    case ArrowUp: {
                        if (isInTodo)
                            moveUp(textGraphics, todos, todoPrefix);
                        else
                            moveUp(textGraphics, dones, donePrefix);

                        System.out.println("Move Up Called : " + --index + " " + mainInd);
                        screen.refresh();
                        break;
                    }
                    case Tab: {
                        System.out.println("Tab Called, changing section");
                        renderBlanks(textGraphics);
                        changeRenderer(textGraphics);
                        mainInd = -1;
                        screen.refresh();
                        break;
                    }
                    case Enter: {
                        System.out.println("Pressed Enter on item number: " + mainInd);
                        switchTasks();
                        renderBlanks(textGraphics);
                        changeRenderer(textGraphics);
                        mainInd = -1;
                        screen.refresh();
                        break;
                    }
                    case Insert: {
                        System.out.println("Insert Pressed");
                        if (isInTodo)
                            insertTodo(textGraphics, screen);

                        break;
                    }
                    default: {
                        System.out.println(keyStroke.getKeyType().name());
                        break;
                    }
                }
            else
                break;
        }
    }

    private static void insertTodo(TextGraphics textGraphics, Screen screen) throws IOException {

        textGraphics.enableModifiers(SGR.REVERSE);
        textGraphics.putString(1, screen.getTerminalSize().getRows() - 1,
                "Into Enter Mode");
        screen.refresh();
        defaultText(textGraphics);
        StringBuilder newTodo = new StringBuilder("[]");
        while (true) {
            KeyStroke keyStroke = screen.readInput();
            if (!keyStroke.equals(EOF)) {
                switch (keyStroke.getKeyType()) {
                    case Character: {
                        screen.refresh();
                        System.out.println("Inside the insert");
                        newTodo.append(keyStroke.getCharacter());
                        textGraphics.putString(2, 4 + todos.size(), newTodo.toString());
                        screen.refresh();
                        System.out.println(newTodo);
                        break;

                    }
                    case Backspace: {
                        if (newTodo.length() > 0) {
                            newTodo.deleteCharAt(newTodo.length() - 1);
                            textGraphics.putString(2, 4 + todos.size(), " ".repeat(terminalWidth));
                            textGraphics.putString(2, 4 + todos.size(), newTodo.toString());
                        }
                        screen.refresh();
                        break;
                    }
                    case Enter: {
                        if (newTodo.length() > 0) {
                            String message = "Task Added!";
                            todos.add(newTodo.substring(2));
                            textGraphics.putString(screen.getTerminalSize().getColumns() - message.length() - 1,
                                    screen.getTerminalSize().getRows() - 1, message);
                            screen.refresh();
                        }
                        break;
                    }
                    case Escape: {
                        textGraphics.putString(1, screen.getTerminalSize().getRows() - 1,
                                " ".repeat(terminalWidth));
                        textGraphics.putString(2, 4 + todos.size(), " ".repeat(terminalWidth));
                        screen.refresh();
                        return;
                    }
                }
            }
        }

    }

    private static void switchTasks() {
        if (isInTodo) {
            dones.add(pop(todos, mainInd));
        } else {
            todos.add(pop(dones, mainInd));
        }
    }

    private static void renderBlanks(TextGraphics textGraphics) {
        for (int i = 4; i < 4 + maxInd(maxInd, todos.size(), dones.size()); i++) {
            textGraphics.putString(0, i, " ".repeat(terminalWidth));
        }
        System.out.println(maxInd(maxInd, todos.size(), dones.size()));
    }

    private static void changeRenderer(TextGraphics textGraphics) {

        if (isInTodo) {
            renderDones(dones, textGraphics);
            isInTodo = false;
        } else {
            renderTodo(todos, textGraphics);
            isInTodo = true;
        }

    }

    public static void defaultText(TextGraphics textGraphics) {
        textGraphics.disableModifiers(SGR.REVERSE);
        textGraphics.setForegroundColor(TextColor.ANSI.DEFAULT);
        textGraphics.setBackgroundColor(TextColor.ANSI.DEFAULT);
    }

    public static void moveDown(TextGraphics textGraphics, ArrayList<String> todos, String prefix) {
        textGraphics.enableModifiers(SGR.REVERSE);
        mainInd++;
        if (mainInd < todos.size()) {

            textGraphics.putString(2, 4 + mainInd, prefix + todos.get(mainInd));
            System.out.println("The Index Curr is : " + mainInd);
            if (mainInd > 0) {
                resetOld(textGraphics, todos);
            }
        } else {
            mainInd--;
            System.out.println("Out of Index, cant't move");
        }
    }

    public static void moveUp(TextGraphics textGraphics, ArrayList<String> todos, String prefix) {
        textGraphics.enableModifiers(SGR.REVERSE);

        mainInd--;
        if (mainInd > -1) {
            textGraphics.putString(2, 4 + mainInd, prefix + todos.get(mainInd));
            if (mainInd < todos.size() - 1) {
                resetOldUp(textGraphics, todos);
            }
        } else {
            mainInd++;
            System.out.println("Out of Index, can't move");
        }
    }

    public static void resetOld(TextGraphics textGraphics, ArrayList<String> todos) {
        textGraphics.disableModifiers(SGR.REVERSE);
        if (isInTodo)
            textGraphics.putString(2, 4 + mainInd - 1, todoPrefix + todos.get(mainInd - 1));
        else
            textGraphics.putString(2, 4 + mainInd - 1, donePrefix + todos.get(mainInd - 1));
    }

    public static void resetOldUp(TextGraphics textGraphics, ArrayList<String> todos) {
        textGraphics.disableModifiers(SGR.REVERSE);
        if (isInTodo)
            textGraphics.putString(2, 4 + mainInd + 1, todoPrefix + todos.get(mainInd + 1));
        else
            textGraphics.putString(2, 4 + mainInd + 1, donePrefix + todos.get(mainInd + 1));
    }

    public static void renderTodo(ArrayList<String> todos, TextGraphics textGraphics) {
        textGraphics.putString(2, 3, "TODOs");
        String prefix = "[]";

        int rows = 4;
        for (String todo : todos) {
            textGraphics.putString(2, rows++, prefix + todo);
        }
    }

    public static void renderDones(ArrayList<String> dones, TextGraphics textGraphics) {
        textGraphics.putString(2, 3, "Done");
        String prefix = "[X]";

        int rows = 4;
        for (String done : dones) {
            textGraphics.putString(2, rows++, prefix + done);
        }

    }

    public static <T> T pop(ArrayList<T> list, int index) {
        T item = list.get(index);
        list.remove(index);
        return item;
    }

    public static Integer maxInd(Integer... args) {
        ArrayList<Integer> argList = new ArrayList<>(List.of(args));
        maxInd = argList.stream().reduce(0, Integer::max);
        return maxInd;
    }
}
