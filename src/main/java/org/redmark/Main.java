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

import static com.googlecode.lanterna.input.KeyType.ArrowDown;
import static com.googlecode.lanterna.input.KeyType.Escape;

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
//        while (true){
//        switch (screen.readInput().getKeyType()){
//            case Escape:{screen.stopScreen(); break;}
//            case ArrowDown: {moveDown(textGraphics, todos, index++); defaultText(textGraphics); screen.refresh(); break;}
//            case ArrowUp: {moveUp(textGraphics, todos, index--); System.out.println("Move Up Called");screen.refresh();break;}
//            default: System.out.println(screen.readInput().getKeyType().name());break;}
//
//    }
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
        textGraphics.putString(2, 4+rows, "[]"+todos[rows]);
        if(rows<todos.length-1)
        {resetOld(textGraphics, todos, rows);}
    }
    public static void resetOld(TextGraphics textGraphics, String[] todos, int rows) {
        {textGraphics.disableModifiers(SGR.REVERSE);
        textGraphics.putString(2, 4+rows-1, "[]"+todos[rows-1]);}

    }

}

class KeyListenerCustom implements java.awt.event.KeyListener{

    @Override
    public void keyTyped(KeyEvent keyEvent) {

    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {

    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {

    }
}