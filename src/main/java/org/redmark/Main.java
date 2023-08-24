package org.redmark;

import com.googlecode.lanterna.*;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;

import java.io.IOException;

import static com.googlecode.lanterna.input.KeyType.*;

public class Main {

    static int mainInd = 0;
    static String[] todos = {"Do This", "Do that", "And finally This"};
    static String[] dones = {"Navigation", "Out Of Bound", "Git rebase", "OneMore to Test"};
    static boolean isInTodo = true;
    static int terminalWidth = 0;
    static String donePrefix = "[X]";
    static String todoPrefix = "[]";
    public static void main(String[] args) throws IOException {;
        Screen screen = new DefaultTerminalFactory().createScreen();
        screen.startScreen();
        String heading = "Judu : A ToDo App";
        TextGraphics textGraphics = screen.newTextGraphics();
        textGraphics.enableModifiers(SGR.REVERSE);
        textGraphics.putString((screen.getTerminalSize().getColumns()-heading.length())/2, 1, heading);
        defaultText(textGraphics);
        terminalWidth = screen.getTerminalSize().getColumns();


        renderTodo(todos, textGraphics);
        //renderDones(dones, textGraphics);
        screen.refresh();


        int index = 0;
       // KeyStroke keyStroke = screen.readInput();
        while (true){
            KeyStroke keyStroke = screen.readInput();
            if(!keyStroke.getKeyType().equals(EOF))
                {
                    switch (keyStroke.getKeyType()){
                        case Escape:{screen.stopScreen(); break;}
                        case ArrowDown:
                        {
                            if(isInTodo)
                                moveDown(textGraphics, todos, todoPrefix);
                            else
                                moveDown(textGraphics, dones, donePrefix);
                            System.out.println("Move Down Called : "+ index++ + " "+mainInd);
                            defaultText(textGraphics);
                            screen.refresh();
                            break;
                        }
                        case ArrowUp:
                        {
                            if(isInTodo)
                                moveUp(textGraphics, todos, todoPrefix);
                            else
                                moveUp(textGraphics, dones,donePrefix);

                            System.out.println("Move Up Called : "+ --index + " "+mainInd);
                            screen.refresh();
                            break;
                        }
                        case Tab:
                        {
                            System.out.println("Tab Called, changing section");
                            renderBlanks(textGraphics);
                            changeRenderer(textGraphics);
                            mainInd =0;
                            screen.refresh();
                            break;
                        }
                        default:
                        {
                            System.out.println(keyStroke.getKeyType().name());
                            break;
                        }
                    }
                }
            else
                break;

        }
    }


    private static void renderBlanks(TextGraphics textGraphics) {
        int rows = 4;
        if(isInTodo)
        {
            for(String todo : todos)
            {
                textGraphics.putString(0, rows++ , " ".repeat(terminalWidth));
            }
        }
        else {
            for(String done : dones)
            {
                textGraphics.putString(0, rows++ , " ".repeat(terminalWidth));
            }
        }

    }

    private static void changeRenderer(TextGraphics textGraphics) {

        if(isInTodo)
        {
            renderDones(dones, textGraphics);
            isInTodo = false;
        }
        else {
            renderTodo(todos, textGraphics);
            isInTodo = true;
        }

    }

    public static void defaultText(TextGraphics textGraphics)
    {
        textGraphics.disableModifiers(SGR.REVERSE);
        textGraphics.setForegroundColor(TextColor.ANSI.DEFAULT);
        textGraphics.setBackgroundColor(TextColor.ANSI.DEFAULT);
    }

    public static void moveDown(TextGraphics textGraphics, String [] todos, String prefix)
    {
        textGraphics.enableModifiers(SGR.REVERSE);
        if(mainInd < todos.length) {
            textGraphics.putString(2, 4 + mainInd, prefix + todos[mainInd]);
            if (mainInd > 0) {
                resetOld(textGraphics, todos);
            }
            mainInd++;
        }
        else
        {
            mainInd--;
            System.out.println("Out of Index, cant't move");
        }

    }
    public static void moveUp(TextGraphics textGraphics, String[] todos, String prefix)
    {
        textGraphics.enableModifiers(SGR.REVERSE);
        mainInd--;
        if(mainInd>-1)
        {
            textGraphics.putString(2, 4+mainInd, prefix+todos[mainInd]);
            if(mainInd<todos.length-1)
            {resetOldUp(textGraphics, todos);}
        }
        else
        { mainInd++;
            System.out.println("Out of Index, can't move");}

    }
    public static void resetOld(TextGraphics textGraphics, String[] todos){
        textGraphics.disableModifiers(SGR.REVERSE);
        if(isInTodo)
            textGraphics.putString(2, 4+mainInd-1, todoPrefix+todos[mainInd-1]);
        else
            textGraphics.putString(2, 4+mainInd-1, donePrefix+todos[mainInd-1]);
    }
    public static void resetOldUp(TextGraphics textGraphics, String[] todos)
    {
        textGraphics.disableModifiers(SGR.REVERSE);
        if(isInTodo)
            textGraphics.putString(2, 4+mainInd+1, todoPrefix+todos[mainInd+1]);
        else
            textGraphics.putString(2, 4+mainInd+1, donePrefix+todos[mainInd+1]);
    }

    public static void renderTodo(String[] todos, TextGraphics textGraphics)
    {
        textGraphics.putString(2, 3, "TODOs");
        String prefix = "[]";

        int rows = 4;
        for(String todo : todos)
        {

            textGraphics.putString(2,rows++ , prefix+todo);
        }

    }
    public static void renderDones(String[] dones, TextGraphics textGraphics)
    {
        textGraphics.putString(2, 3, "Done");
        String prefix = "[X]";

        int rows = 4;
        for(String done : dones)
        {
            textGraphics.putString(2,rows++ , prefix+done);
        }

    }

}
