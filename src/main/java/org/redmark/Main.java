package org.redmark;

import com.googlecode.lanterna.*;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.ansi.UnixLikeTTYTerminal;
import com.googlecode.lanterna.terminal.ansi.UnixTerminal;
import com.googlecode.lanterna.terminal.swing.AWTTerminal;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.googlecode.lanterna.input.KeyType.*;

public class Main {

    static int mainInd = 0;
    public static void main(String[] args) throws IOException {;
        Screen screen = new DefaultTerminalFactory().createScreen();
        screen.startScreen();
        String heading = "Judu : A ToDo App";
        TextGraphics textGraphics = screen.newTextGraphics();
        textGraphics.enableModifiers(SGR.REVERSE);
        textGraphics.putString((screen.getTerminalSize().getColumns()-heading.length())/2, 1, heading);
        defaultText(textGraphics);
        textGraphics.putString(2, 3, "TODOs");
        String prefix = "[]";
        String[] todos = {"Do This", "Do that", "And finally This"};
        int rows = 4;
        for(String todo : todos)
        {
            textGraphics.putString(2,rows++ , prefix+todo);
             screen.refresh();
        }

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
                            moveDown(textGraphics, todos, index++);
                            mainInd++;
                            System.out.println("Move Down Called : "+ index+ " "+mainInd);
                            defaultText(textGraphics);
                            screen.refresh();
                            break;
                        }
                        case ArrowUp:
                        {
                            --mainInd;
                            moveUp(textGraphics, todos, --index);

                            System.out.println("Move Up Called : "+ index + " "+mainInd);
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

    public static void defaultText(TextGraphics textGraphics)
    {
        textGraphics.disableModifiers(SGR.REVERSE);
        textGraphics.setForegroundColor(TextColor.ANSI.DEFAULT);
        textGraphics.setBackgroundColor(TextColor.ANSI.DEFAULT);
    }

    public static void moveDown(TextGraphics textGraphics, String [] todos, int rows)
    {
        textGraphics.enableModifiers(SGR.REVERSE);
        if(mainInd < todos.length) {
            textGraphics.putString(2, 4 + mainInd, "[]" + todos[mainInd]);
            if (mainInd > 0) {
                resetOld(textGraphics, todos, rows);
            }
        }
        else
        {
            mainInd--;
            System.out.println("Out of Index, cant't move");
        }

    }
    public static void moveUp(TextGraphics textGraphics, String[] todos, int rows)
    {
        textGraphics.enableModifiers(SGR.REVERSE);
        rows--;
        mainInd--;
        if(mainInd>-1)
        {
            textGraphics.putString(2, 4+mainInd, "[]"+todos[mainInd]);
            if(mainInd<todos.length-1)
            {resetOldUp(textGraphics, todos, rows);}
        }
        else
        { mainInd++;
            System.out.println("Out of Index, can't move");}

    }
    public static void resetOld(TextGraphics textGraphics, String[] todos, int rows) {
        textGraphics.disableModifiers(SGR.REVERSE);
        textGraphics.putString(2, 4+mainInd-1, "[]"+todos[mainInd-1]);

    }
    public static void resetOldUp(TextGraphics textGraphics, String[] todos, int rows)
    {
        textGraphics.disableModifiers(SGR.REVERSE);
        textGraphics.putString(2, 4+mainInd+1, "[]"+todos[mainInd+1]);
    }

}
