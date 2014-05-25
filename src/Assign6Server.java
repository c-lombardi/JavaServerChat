/**
 * Created by Christopher on 4/13/2014.
 */
// Ex27Server.java
// Simple implementation of a server for a chat group


import java.util.*;
import java.io.*;
import java.net.*;

public class Assign6Server implements Serializable{

    public ArrayList<Record> Records = new ArrayList<Record>();

    public static final int PORT = 5002;

    private int MaxUsers;
    private Socket [] users;
    private UserThread [] threads;
    private int numUsers;

    public Assign6Server(int m)
    {
        MaxUsers = m;
        users = new Socket[MaxUsers];
        threads = new UserThread[MaxUsers];
        numUsers = 0;

        try
        {
            runServer();
        }
        catch (Exception e)
        {
            System.out.println("Problem with server");
        }
    }

    public synchronized void SendMsg(String msg)
    {
        for (int i = 0; i < numUsers; i++)
        {
            ObjectOutputStream out = threads[i].getOutputStream();
            try {
                out.writeObject(msg);
                out.flush();
            }
            catch (IOException e)  {
                System.out.println("Problem sending message");
            }
        }
    }

    public synchronized void removeClient(int id, String name)
    {
        try
        {
            users[id].close();
        }
        catch (IOException e)
        {
            System.out.println("Already closed");
        }
        users[id] = null;
        threads[id] = null;
        // fill up "gap" in arrays
        for (int i = id; i < numUsers-1; i++)
        {
            users[i] = users[i+1];
            threads[i] = threads[i+1];
            threads[i].setId(i);
        }
        numUsers--;
    }

    private void runServer() throws IOException
    {
        ServerSocket s = new ServerSocket(PORT);
        System.out.println("Started: " + s);

        try {
            while(true) {
                if (numUsers < MaxUsers)
                {
                    try {
                        // wait for client
                        Socket newSocket = s.accept();
                        // set up streams and thread for client
                        synchronized (this)
                        {
                            users[numUsers] = newSocket;
                            ObjectInputStream in = new ObjectInputStream(
                                    newSocket.getInputStream());
                            String newName = (String)in.readObject();

                            threads[numUsers] = new UserThread(newSocket, numUsers,
                                    newName, in);
                            threads[numUsers].start();
                            System.out.println("Connection " + numUsers + users[numUsers]);
                            numUsers++;

                        }
                    }
                    catch (Exception e)
                    {
                        System.out.println("Problem with connection...terminating");
                    }
                }  // end if

            }  // end while
        }   // end try
        finally
        {
            System.out.println("Server shutting down");
        }
    }

    private class UserThread extends Thread
    {
        private Socket mySocket;
        private ObjectInputStream myInputStream;
        private ObjectOutputStream myOutputStream;
        private int myId;
        private String myName;
        private UserThread(Socket newSocket, int id, String newName,
                           ObjectInputStream in) throws IOException
        {
            mySocket = newSocket;
            myId = id;
            myName = newName;
            myInputStream = in;
            myOutputStream = new ObjectOutputStream(newSocket.getOutputStream());
        }

        public ObjectInputStream getInputStream()
        {
            return myInputStream;
        }

        public ObjectOutputStream getOutputStream()
        {
            return myOutputStream;
        }

        public synchronized void setId(int newId)
        {
            myId = newId;
        }


        public String addRec(Record r)
        {
            try {
                if (!r.getLocation().isEmpty()) {
                    r.setId(Records.size());
                    Records.add(r);
                }
                else
                {
                    return ("Error: You must enter a location");
                }
            }
            catch(Exception ex)
            {
                return ("Error: There was an error adding your record.");
            }
            return ("Sale successfully added");
        }
        public String removeRecordById(Integer i, String r)
        {
            try {
                if (Records.get(i).getRep() == r)
                {
                    Records.remove(Records.get(i));
                }
                else
                {
                    return ("Error: This sale does not belong to you.");
                }
            }
            catch (Exception ex)
            {
                return ("Error: Could not remove that object.");
            }
            return ("Successfully deleted item at index:" + i);
        }
        public String editRecord(Record r)
        {
            try{
                if (Records.get(r.getId()).getRep() != myName) {
                    return("Error: This sale does not belong to you.");
                } else if (r.getId() < Records.size()) {
                    Records.set(r.getId(), r);
                } else if (r.getId() >= Records.size()) {
                    return ("Error: This sale does not exist.");
                } else {
                    return ("Error: Problem editing sale.");
                }
            }
            catch (Exception ex)
            {
                return("Error: Problem editing sale");
            }
            return("Successfully edited item at index: "+r.getId());
        }
        public void sendMsgToAUser(String msg)
        {
            try {
                myOutputStream.writeObject(msg);
                myOutputStream.flush();
            }
            catch(IOException ex)
            {
                System.out.println("Problem sending message");
            }
        }
        public void run()
        {
            boolean alive = true;
            while (alive)
            {
                Record newRec = null;
                String newMsg = null;
                try {
                    Object obj  = myInputStream.readObject();
                    if (obj instanceof Record)
                    {
                        newRec = (Record) obj;
                        if (newRec.getId() == -1) {
                            synchronized (this) {
                                sendMsgToAUser(addRec(newRec));
                            }
                        }
                        else
                        {
                            synchronized (this) {
                                sendMsgToAUser(editRecord(newRec));
                            }
                        }
                    }
                    else if (obj instanceof String)
                    {
                        newMsg = (String) obj;
                        synchronized(this)
                        {
                            if (newMsg.contains("Mine"))
                            {
                                ArrayList<Record> userRecords = new ArrayList<Record>();
                                for(Record r: Records)
                                {
                                    if (r.getRep() == myName) {
                                        userRecords.add(r);
                                    }
                                }
                                myOutputStream.reset();
                                myOutputStream.writeObject(userRecords);
                                myOutputStream.flush();
                            }
                            else if (newMsg.contains("All"))
                            {
                                myOutputStream.reset();
                                myOutputStream.writeObject(Records);
                                myOutputStream.flush();
                            }
                        }
                    }
                    else if(obj instanceof Integer)
                    {
                        Integer id = (Integer) obj;
                        synchronized (this)
                        {
                            sendMsgToAUser(removeRecordById(id, myName));
                        }
                    }
                }
                catch (ClassNotFoundException e)  {
                    System.out.println("Error receiving message....shutting down");
                    alive = false;
                }
                catch (IOException e)
                {
                    System.out.println("Client closing!!");
                    alive = false;
                }
            }
            removeClient(myId, myName);
        }
    }

    public static void main(String [] args)
    {
        Assign6Server Server = new Assign6Server(5);
    }
}

