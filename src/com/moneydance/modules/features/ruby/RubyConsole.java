/************************************************************\
 *      Copyright (C) 2008 Reilly Technologies, L.L.C.      *
 \************************************************************/

package com.moneydance.modules.features.ruby;

import com.moneydance.awt.*;
import com.moneydance.apps.md.controller.Common;
import com.moneydance.apps.md.controller.Util;

import java.io.*;
import java.util.*;
import javax.swing.*;
import java.text.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

/**
 * Window used for Ruby interface
 */

public class RubyConsole
        extends JFrame
        implements ActionListener {
    private Main extension;
    private JTextArea rubyArea;
    private JTextField inputArea;
    private JButton clearButton;
    private JButton closeButton;
    private JButton readFile;
    private RubyEngine ruby;

    public RubyConsole(Main extension, RubyEngine ruby) {
        super("Ruby Console");
        this.extension = extension;
        this.ruby = ruby;

        rubyArea = new JTextArea();
        rubyArea.setEditable(false);
        inputArea = new JTextField();
        inputArea.setEditable(true);
        clearButton = new JButton("Clear");
        closeButton = new JButton("Close");
        readFile = new JButton("File");

        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(new EmptyBorder(10, 10, 10, 10));
        p.add(new JScrollPane(rubyArea), AwtUtil.getConstraints(0, 0, 1, 1, 4, 1, true, true));
        p.add(inputArea, AwtUtil.getConstraints(0, 1, 1, 0, 5, 1, true, true));
        p.add(Box.createVerticalStrut(8), AwtUtil.getConstraints(0, 2, 0, 0, 1, 1, false, false));
        p.add(clearButton, AwtUtil.getConstraints(0, 3, 1, 0, 1, 1, false, true));
        p.add(closeButton, AwtUtil.getConstraints(1, 3, 1, 0, 1, 1, false, true));
        p.add(new JLabel(""), AwtUtil.getConstraints(2, 3, 1, 0, 1, 1, true, true));
        p.add(readFile, AwtUtil.getConstraints(3, 3, 1, 0, 1, 1, false, true));
        getContentPane().add(p);

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        enableEvents(WindowEvent.WINDOW_CLOSING);
        closeButton.addActionListener(this);
        clearButton.addActionListener(this);
        readFile.addActionListener(this);
        inputArea.addActionListener(this);

        //Redirect System.out to the rubyArea
        PrintStream c = new PrintStream(new ConsoleStream());

        //Instantiate the interpreter.
//        interpreter = new InteractiveConsole();
//        interpreter.setOut(c);
//        interpreter.setErr(c);
//
//        //Give it the context
//        interpreter.set("moneydance", extension.getUnprotectedContext());

        pack();
        setSize(500, 400);
        AwtUtil.centerWindow(this);
    }

    public void actionPerformed(ActionEvent evt) {
        Object src = evt.getSource();
        if (src == closeButton) {
            extension.closeConsole();
        }
        if (src == clearButton) {
            rubyArea.setText("");
        }
        if (src == inputArea) {
            processInputCommand();
        }
        if (src == readFile) {
            addLine("\n");
            readAFile();
            addLine(">> ");
        }
    }

    private void readAFile() {
//        JFileChooser fc = new JFileChooser();
//        fc.setDialogTitle("Choose Ruby File");
//        int returnVal = fc.showOpenDialog(this);
//        if (returnVal == JFileChooser.APPROVE_OPTION) {
//            extension.runFile(fc.getSelectedFile(), ruby);
//        }
    }

    private void processInputCommand() {
        String command = inputArea.getText();
        inputArea.setText("");
        addLine(command + "\n");
        executeRubyStatement(command);
    }

    public void executeRubyStatement(String command) {
        ruby.eval(command);
//        if (interpreter.push(command))
//            addLine("... ");
//        else
            addLine(">> ");
    }

    public final void processEvent(AWTEvent evt) {
        if (evt.getID() == WindowEvent.WINDOW_CLOSING) {
            extension.closeConsole();
            return;
        }
        if (evt.getID() == WindowEvent.WINDOW_OPENED) {
            addLine(">>> ");
            inputArea.requestFocus();
        }
        super.processEvent(evt);
    }

    void goAway() {
        setVisible(false);
        dispose();
    }

    private class ConsoleStream
            extends OutputStream
            implements Runnable {
        public void write(int b)
                throws IOException {
            rubyArea.append(String.valueOf((char) b));
            repaint();
        }

        public void write(byte[] b)
                throws IOException {
            rubyArea.append(new String(b));
            repaint();
        }

        public void run() {
            rubyArea.repaint();
        }
    }

    void addLine(String line) {
        rubyArea.append(line);
        rubyArea.repaint();
    }
}
