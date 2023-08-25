package org.redmark;

import com.googlecode.lanterna.*;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import netscape.javascript.JSObject;
import org.json.simple.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;

import static com.googlecode.lanterna.input.KeyType.*;

public class Main {

    final static Path filePath = Paths.get("/home/shreyansh/Documents/Todos.txt");
    static int mainInd = -1;
    static ArrayList<String> todos = new ArrayList<>();
    static ArrayList<String> dones = new ArrayList<>();
    static boolean isInTodo = true;
    static int terminalWidth = 0;
    static String donePrefix = "[X]";
    static String todoPrefix = "[]";
    static int maxInd = 0;

    public static void main(String[] args) throws IOException {
        Screen screen = new DefaultTerminalFactory().createScreen();
        screen.startScreen();
        String heading = "Judu : A ToDo App";
        TextGraphics textGraphics = screen.newTextGraphics();
        textGraphics.enableModifiers(SGR.REVERSE);
        textGraphics.putString((screen.getTerminalSize().getColumns() - heading.length()) / 2, 1, heading);
        defaultText(textGraphics);
        terminalWidth = screen.getTerminalSize().getColumns();
        getItems();
        maxInd = Integer.max(todos.size(), dones.size());
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
                        saveTodos();
                        textGraphics.putString(1, screen.getTerminalSize().getRows() - 1,
                                "Press Esc Again to Exit");
                        screen.refresh();
                        if(screen.readInput().getKeyType().equals(Escape))
                            screen.stopScreen();
                        else
                            textGraphics.putString(1, screen.getTerminalSize().getRows() - 1,
                                    " ".repeat(terminalWidth));
                        screen.refresh();

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
                        if(-1<mainInd)
                        {   switchTasks();
                            renderBlanks(textGraphics);
                            changeRenderer(textGraphics);
                            mainInd = -1;
                            screen.refresh();}
                        break;
                    }
                    case Insert: {
                        System.out.println("Insert Pressed");
                        if (isInTodo)
                            insertTodo(textGraphics, screen);

                        break;
                    }
                    case Delete:
                    {
                        deleteTasks();
                        renderBlanks(textGraphics);
                        if(isInTodo)
                            renderTodo(todos, textGraphics);
                        else
                            renderDones(dones, textGraphics);
                        screen.refresh();
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

    private static void deleteTasks() {
        if(isInTodo)
            todos.remove(mainInd);
        else
            dones.remove(mainInd);
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
                            maxInd(maxInd, todos.size());
                            screen.refresh();
                            newTodo.delete(2, newTodo.length());
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
        for (String done : dones)
        {
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
    public static void saveTodos()throws IOException
    {
        StringJoiner savedItems = new StringJoiner("\n");
        todos.stream().forEach(todo->savedItems.add("Todo:"+ todo));
        dones.stream().forEach(done -> savedItems.add("Done:"+ done));
        System.out.println(savedItems);

        try(
            final BufferedWriter writer = Files.newBufferedWriter(filePath, StandardCharsets.UTF_8,
                    StandardOpenOption.TRUNCATE_EXISTING))
        { writer.write(savedItems.toString());
            writer.flush();
        }
        catch (Exception e)
        {
            System.out.println(e.getStackTrace());
        }
    }

    public static void getItems() throws IOException {
        try(
                final BufferedReader reader = Files.newBufferedReader(filePath);
                )
        {
            reader.lines().forEach(ele->
                    {
                        System.out.println(ele);
                        if (ele.startsWith("Todo"))
                            todos.add(ele.substring(5));
                        else
                            dones.add(ele.substring(5));
                    }
            );
        }
        catch (Exception e)
        {
            System.out.println(e.getStackTrace());
        }
    }
}
