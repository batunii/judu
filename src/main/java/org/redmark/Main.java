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
                            System.out.println("Move Down Called : "+ index);
                            defaultText(textGraphics);
                            screen.refresh();
                            break;
                        }
                        case ArrowUp:
                        {
                            moveUp(textGraphics, todos, --index);
                            System.out.println("Move Up Called : "+ index);
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
        textGraphics.putString(2, 4+rows, "[]"+todos[rows]);
        if(rows>0)
        {resetOld(textGraphics, todos, rows);}

    }
    public static void moveUp(TextGraphics textGraphics, String[] todos, int rows)
    {
        textGraphics.enableModifiers(SGR.REVERSE);
        rows--;
        if(rows>-1)
        {
            textGraphics.putString(2, 4+rows, "[]"+todos[rows]);
            if(rows<todos.length-1)
            {resetOldUp(textGraphics, todos, rows);}
        }
        else
            System.out.println("Out of Index, can't move");

    }
    public static void resetOld(TextGraphics textGraphics, String[] todos, int rows) {
        textGraphics.disableModifiers(SGR.REVERSE);
        textGraphics.putString(2, 4+rows-1, "[]"+todos[rows-1]);

    }
    public static void resetOldUp(TextGraphics textGraphics, String[] todos, int rows)
    {
        textGraphics.disableModifiers(SGR.REVERSE);
        textGraphics.putString(2, 4+rows+1, "[]"+todos[rows+1]);
    }

}
