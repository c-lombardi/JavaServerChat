/**
 * Created by Christopher on 4/13/2014.
 */
// Ex27Client.java
// Simple chat client for Ex27Server

import java.util.*;
import java.io.*;
import java.net.*;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

public class Assign6Client extends JFrame implements Runnable {

    public static final int PORT = 5002;


    ObjectInputStream myInputStream;
    ObjectOutputStream myOutputStream;
    JTextArea outputArea;
    String myName, serverName;
    JButton enter, viewMine, viewAll, editMine, deleteMine, quit;


    // Log on to server and set up GUI
    public  Assign6Client ()
    {
        try {
            myName = JOptionPane.showInputDialog(this, "Enter your user name: ");
            serverName = JOptionPane.showInputDialog(this, "Enter the server name: ");
            InetAddress addr = InetAddress.getByName(serverName);
            Socket socket = new Socket(addr, PORT);

            this.setTitle(myName);

            outputArea = new JTextArea();
            outputArea.setEditable(false);
            outputArea.append(myName +", your connection to the sales server: " + serverName + ", has been established.\n");
            JScrollPane scroller = new JScrollPane(outputArea);
            enter = new JButton("Enter Sale");
            viewMine = new JButton("View My Sale");
            viewAll = new JButton("View All Sale");
            editMine = new JButton("Edit A Sale");
            deleteMine = new JButton("Delete A Sale");
            quit = new JButton("Quit");
            super.setLayout(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = 1;
            c.gridy = 0;
            c.ipady = 575;
            c.ipadx = 700;
            c.weightx = 0.0;
            c.gridwidth = 3;
            add(scroller, c);

            c.ipady = 0;
            c.ipadx = 0;
            c.gridx = 1;
            c.gridy = 1;
            c.weightx = 0.5;
            c.gridwidth = 1;
            add(enter, c);

            c.gridx = 2;
            c.gridy = 1;
            add(viewMine, c);

            c.gridx = 3;
            c.gridy = 1;
            add(viewAll, c);

            c.gridx = 1;
            c.gridy = 2;
            add(editMine, c);
            editMine.setEnabled(false);

            c.gridx = 2;
            c.gridy = 2;
            add(deleteMine, c);
            deleteMine.setEnabled(false);

            c.gridx = 3;
            c.gridy = 2;
            add(quit, c);

            myOutputStream = new ObjectOutputStream(socket.getOutputStream());
            myOutputStream.flush();
            myOutputStream.writeObject(myName);
            myOutputStream.flush();
            myInputStream = new ObjectInputStream(socket.getInputStream());
            Thread outputThread = new Thread(this);
            outputThread.start();

            setSize(700,675);
            setVisible(true);
            setResizable(false);

            enter.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    viewMine.setEnabled(false);
                    viewAll.setEnabled(false);
                    enter.setEnabled(false);
                    editMine.setEnabled(false);
                    deleteMine.setEnabled(false);
                    String rep = myName;
                    String location = JOptionPane.showInputDialog("Enter the location it was purchased:");
                    try {
                        Integer amount = Integer.parseInt(JOptionPane.showInputDialog("Enter the amount you purchased"));
                        Record r = new Record(rep, location, amount);
                        r.setId(-1);
                        myOutputStream.writeObject(r);
                        myOutputStream.flush();
                    }
                    catch (NumberFormatException ex)
                    {
                        JOptionPane.showMessageDialog(null, "You must enter a valid amount.");
                        System.out.println("Number was not entered");
                    }
                    catch (IOException ex)
                    {
                        System.out.println("Problem sending message");
                    }
                    viewMine.setEnabled(true);
                    viewAll.setEnabled(true);
                    enter.setEnabled(true);
                }
            });
            viewMine.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    viewMine.setEnabled(false);
                    viewAll.setEnabled(false);
                    enter.setEnabled(false);
                    editMine.setEnabled(false);
                    deleteMine.setEnabled(false);
                    try {
                        myOutputStream.writeObject("Mine");
                        myOutputStream.flush();
                    }
                    catch (IOException ex)
                    {
                        System.out.println("Problem sending message");
                    }
                    viewMine.setEnabled(true);
                    viewAll.setEnabled(true);
                    enter.setEnabled(true);
                    editMine.setEnabled(true);
                    deleteMine.setEnabled(true);
                }
            });
            viewAll.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    viewMine.setEnabled(false);
                    viewAll.setEnabled(false);
                    enter.setEnabled(false);
                    editMine.setEnabled(false);
                    deleteMine.setEnabled(false);
                    try {
                        myOutputStream.writeObject("All");
                        myOutputStream.flush();
                    }
                    catch (IOException ex)
                    {
                        System.out.println("Problem sending message");
                    }
                    viewMine.setEnabled(true);
                    viewAll.setEnabled(true);
                    enter.setEnabled(true);
                }
            });
            editMine.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    viewMine.setEnabled(false);
                    viewAll.setEnabled(false);
                    enter.setEnabled(false);
                    editMine.setEnabled(false);
                    deleteMine.setEnabled(false);
                    try {
                        Integer i = -1;
                        while (i < 0) {
                            i = Integer.parseInt(JOptionPane.showInputDialog("Enter the id number of the sale to edit:"));
                        }
                        String l = JOptionPane.showInputDialog("Enter the new Location:");
                        Integer a = Integer.parseInt(JOptionPane.showInputDialog("Enter the new amount:"));
                        Record r = new Record(i, myName, l, a);
                        myOutputStream.writeObject(r);
                        myOutputStream.flush();
                    }
                    catch (NumberFormatException ex)
                    {
                        JOptionPane.showMessageDialog(null, "You must enter a valid id.");
                        System.out.println("Number was not entered");
                    }
                    catch (IOException ex)
                    {
                        System.out.println("Problem sending message");
                    }
                    viewMine.setEnabled(true);
                    viewAll.setEnabled(true);
                    enter.setEnabled(true);
                    editMine.setEnabled(false);
                    deleteMine.setEnabled(false);
                }
            });
            deleteMine.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    viewMine.setEnabled(false);
                    viewAll.setEnabled(false);
                    enter.setEnabled(false);
                    editMine.setEnabled(false);
                    deleteMine.setEnabled(false);
                    try {
                        Integer i = Integer.parseInt(JOptionPane.showInputDialog("Enter the id number of the sale to edit:"));
                        myOutputStream.writeObject(i);
                        myOutputStream.flush();
                    }
                    catch (NumberFormatException ex)
                    {
                        JOptionPane.showMessageDialog(null, "You must enter a valid id.");
                        System.out.println("Number was not entered");
                    }
                    catch (IOException ex)
                    {
                        System.out.println("Problem sending message");
                    }
                    viewMine.setEnabled(true);
                    viewAll.setEnabled(true);
                    enter.setEnabled(true);
                    editMine.setEnabled(false);
                    deleteMine.setEnabled(false);
                }
            });
            quit.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    { System.exit(0); }
                }
            });
        }
        catch (Exception e)
        {
            System.out.println("Problem starting client");
        }
    }

    public void run()
    {
        while (true)
        {
            Object obj = null;
            try {
                obj = myInputStream.readObject();
            } catch (IOException e) {
                System.out.println("Problem reading OBJ");
                break;
            } catch (ClassNotFoundException e) {
                System.out.println("Class not found for OBJ");
                break;
            }
            if(obj instanceof ArrayList)
            {
                ArrayList<Record> Records = (ArrayList<Record>) obj;
                String format = "%-20s %04d %20s %20d\n";
                outputArea.append("\n"+String.format("%-20s %-20s %-20s %-20s", "Rep", "Id", "Location", "Amount")+"\n");
                if (Records.size() > 0) {
                    for (Record r : Records) {
                        outputArea.append(String.format(format, r.getRep(), r.getId(), r.getLocation(), r.getAmount()));
                    }
                }
                else
                {
                    outputArea.append("You have no sales to display.\n");
                }
                outputArea.setCaretPosition(outputArea.getDocument().getLength());
            }
            else if(obj instanceof String)
            {
                String currMsg = (String) obj;
                outputArea.append(currMsg+"\n");
                outputArea.setCaretPosition(outputArea.getDocument().getLength());
            }
        }
    }

    public static void main(String [] args)
    {
        Assign6Client Client = new  Assign6Client();

        Client.addWindowListener(
                new WindowAdapter()
                {
                    public void windowClosing(WindowEvent e)
                    { System.exit(0); }
                }
        );
    }
}

