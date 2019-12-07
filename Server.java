import java.io.*;
import java.text.*;
import java.util.*;
import java.net.*;

public class Server
{
	public static void main(String[] args) throws IOException 
	{
		Scanner s1 = new Scanner(System.in);
    	System.out.println("Enter port number:");
    	int portno;
    	portno = s1.nextInt();
        int client_limit=Integer.parseInt(args[0]);
		ServerSocket ss = new ServerSocket(portno);
        HashMap<String, Vector<String>> chatroom_user_map = new HashMap<String, Vector<String>>();
        HashMap<String, Socket> user_socket_map = new HashMap<String, Socket>();
        Vector all_users= new Vector();
        int no_of_clients=0;

        while(client_limit>=no_of_clients) 
		//while (true) 
        {
            //System.out.println(no_of_clients);
            // if(client_limit==no_of_clients)
            // {
                
            //     break;
            // }
        	Socket s = null;
        	try
        	{
        		s = ss.accept();
                no_of_clients+=1;
                //System.out.println(no_of_clients);
                //System.out.println("client connected ");
                DataInputStream dis = new DataInputStream(s.getInputStream());
                DataOutputStream dos = new DataOutputStream(s.getOutputStream());
                String client_name = dis.readUTF();
                if(client_limit<no_of_clients)
                {
                    dos.writeUTF("Client limit exceeded. Cannot be connected");
                    //System.out.println("client connected ");
                    //s.close();
                    break;
                }


                all_users.add(client_name);
                user_socket_map.put(client_name, s);

                // create a new thread object
                Thread t = new ClientHandler(s, dis, dos,client_name,chatroom_user_map,user_socket_map,all_users);

                // Invoking the start() method
                t.start();

        	}
        	catch (Exception e){
                s.close();
                e.printStackTrace();
            }

            
            
            
        }
        System.out.println("Cannot add more clients ");
        //ss.close();
        
        
	}
    public static String FileTransferServer(String client_name,String chatroom_name,File file_to_send,HashMap<String, Vector<String>> chatroom_user_map,HashMap<String, Socket> user_socket_map) throws IOException 
        {
        Vector<String> users_to_send= new Vector<>();
        users_to_send=chatroom_user_map.get(chatroom_name);
        for (int i = 0; i < users_to_send.size(); i++)
        {
        if(!users_to_send.get(i).equals(client_name))
        {
        Socket s=user_socket_map.get(users_to_send.get(i));
        FileInputStream fis = new FileInputStream(file_to_send);
        BufferedInputStream bis = new BufferedInputStream(fis); 
          
        //Get socket's output stream
        OutputStream os = s.getOutputStream();
        byte[] contents;
        long fileLength = file_to_send.length(); 
        long current = 0;
         
        long start = System.nanoTime();
        while(current!=fileLength){ 
            int size = 100;
            if(fileLength - current >= size)
            {
                current += size;
            }   
            else{
                size = (int)(fileLength - current); 
                current = fileLength;
            } 
            contents = new byte[size];
            bis.read(contents, 0, size);
            //String string_byte = new String(contents);
            //System.out.println(os.write(contents));
            os.write(contents);
            os.flush();
            String message1="Sending file "+file_to_send+" "+(current*100)/fileLength+"% complete!";
            Server.Send_command_error_result(client_name,chatroom_name,message1,chatroom_user_map,user_socket_map);
        }
        String message1="Sent file";
        Server.Send_command_error_result(client_name,chatroom_name,message1,chatroom_user_map,user_socket_map);
        }   
        }
        return null;
        }
    
    public static String Send(String client_name,String chatroom_name,String received,HashMap<String, Vector<String>> chatroom_user_map,HashMap<String, Socket> user_socket_map) throws IOException 
        {
        System.out.println(chatroom_name);
        Vector<String> users_to_send= new Vector<>();

        users_to_send=chatroom_user_map.get(chatroom_name);
        for (int i = 0; i < users_to_send.size(); i++)
        {

        Socket s=user_socket_map.get(users_to_send.get(i));
        DataInputStream dis = new DataInputStream(s.getInputStream());
        DataOutputStream dos = new DataOutputStream(s.getOutputStream());
        if(!users_to_send.get(i).equals(client_name))
        {
        dos.writeUTF(received);
        }
        }
        return null;
        }
    public static String Send_no_chatroom(String client_name,String received,HashMap<String, Socket> user_socket_map) throws IOException 
        {
        Vector<String> users_to_send= new Vector<>();

        Socket s=user_socket_map.get(client_name);
        DataInputStream dis = new DataInputStream(s.getInputStream());
        DataOutputStream dos = new DataOutputStream(s.getOutputStream());
        
        dos.writeUTF(received);
        
        
        return null;
        }

        public static String Send_command_error_result(String client_name,String chatroom_name,String received,HashMap<String, Vector<String>> chatroom_user_map,HashMap<String, Socket> user_socket_map) throws IOException 
        {
        Vector<String> users_to_send= new Vector<>();
        users_to_send=chatroom_user_map.get(chatroom_name);

        for (int i = 0; i < users_to_send.size(); i++)
        {
        if(users_to_send.get(i).equals(client_name))
        {
        Socket s=user_socket_map.get(users_to_send.get(i));
        DataInputStream dis = new DataInputStream(s.getInputStream());
        DataOutputStream dos = new DataOutputStream(s.getOutputStream());
        dos.writeUTF(received);
        }
        else
        {
            //System.out.println( users_to_send.get(i)+"did not request list users/chatrooms");
        }
        }
        return null;
        }
    public static Boolean check_userinchatroom(String client_name,String chatroom_name,HashMap<String, Vector<String>> chatroom_user_map) throws IOException
    {
        Vector<String> list;
        Boolean check_userinchatroom=false;
        Iterator itr=chatroom_user_map.values().iterator();
        Vector vector=null;
  
        while (itr.hasNext())
            {
                vector=(Vector)itr.next();
                if(vector.contains(client_name))
                {
                check_userinchatroom=true; //User in chatroom
                break;
                }

            }
        return check_userinchatroom;
    } 
    public static String FileTransferServer_UDP(String client_name,String chatroom_name,byte[] contents,String file_to_send,HashMap<String, Vector<String>> chatroom_user_map,HashMap<String, Socket> user_socket_map) throws IOException 
        {
        Vector<String> users_to_send= new Vector<>();
        users_to_send=chatroom_user_map.get(chatroom_name);
        for (int i = 0; i < users_to_send.size(); i++)
        {
            if(!users_to_send.get(i).equals(client_name))
            {
            Socket s=user_socket_map.get(users_to_send.get(i));
            OutputStream os = s.getOutputStream();
            os.write(contents);
            os.flush();
            String message1="Sending file "+file_to_send;
            Server.Send_command_error_result(client_name,chatroom_name,message1,chatroom_user_map,user_socket_map);
            }
            //String message1="Sent file";
            //Server.Send_command_error_result(client_name,chatroom_name,message1,chatroom_user_map,user_socket_map);
        }
        return null;  
        }
        
        
     
    
}
// ClientHandler class
class ClientHandler extends Thread 
{
	final DataInputStream dis;
    final DataOutputStream dos;
    final Socket s;
    final String client_name;
    final HashMap<String, Vector<String>> chatroom_user_map;
    final HashMap<String, Socket> user_socket_map;
    final Vector all_users;
	// Constructor
    public ClientHandler(Socket s, DataInputStream dis, DataOutputStream dos,String client_name, HashMap<String, Vector<String>> chatroom_user_map,HashMap<String, Socket> user_socket_map,Vector all_users) 
    {
        this.s = s;
        this.dis = dis;
        this.dos = dos;
        this.client_name=client_name;
        this.chatroom_user_map=chatroom_user_map;
        this.user_socket_map=user_socket_map;
        this.all_users=all_users;
    }
    public void run() 
    {
        String received;
        String toreturn;
        String chatroom_name=new String("");
        Vector<String> users_in_chatroom= new Vector<>();
        String error="";

        while (true) 
        {
            try {
                // receive the answer from client
                received = dis.readUTF();
                String[] split_received = received.split("\\s+");
                int tokens=split_received.length;
               
                if(split_received[0].equals("Exit"))
                { 
                    System.out.println("Client " + this.s + " sends exit...");
                    System.out.println("Closing this connection.");
                    this.s.close();
                    System.out.println("Connection closed");
                    break;
                }
                else if(split_received[0].equals("create"))
                { 
                    if(split_received[1].equals("chatroom") && tokens==3){    
                        //System.out.println(this.client_name);
                        Boolean check_usercreatechatroom=true;
                        Iterator itr=this.chatroom_user_map.values().iterator();
                        Vector vector=null;
  
                        while (itr.hasNext())
                        {
                        vector=(Vector)itr.next();
                        if(vector.contains(this.client_name))
                        {
                            check_usercreatechatroom=false; //User cant's join chatroom
                            break;
                        }

                        }

                        chatroom_name=split_received[2].toString();
                        Vector<String> list;
                        if(check_usercreatechatroom)
                        {
                            if(chatroom_user_map.containsKey(chatroom_name)) {

                                list = this.chatroom_user_map.get(chatroom_name);
                                list.add(this.client_name);
                                error="Chatroom already active";
                                Server.Send_command_error_result(this.client_name,chatroom_name,error,chatroom_user_map,user_socket_map);
                            }
                            else
                            {
                            //create a new vector
                                Vector v1 = new Vector();
                                v1.addElement(new String(this.client_name));
                                this.chatroom_user_map.put(split_received[2], v1);
                            }
                        }
                        else
                        {
                            error="Cannot create "+ chatroom_name + ":User already in some other chatroom";
                            Server.Send_command_error_result(this.client_name,chatroom_name,error,chatroom_user_map,user_socket_map);
                        }
                    }
                    else
                    {
                      error="Error: Usage:create chatroom chatroom_name";
                      Server.Send_command_error_result(this.client_name,chatroom_name,error,chatroom_user_map,user_socket_map);

                    }
                System.out.println(chatroom_user_map); 
                    
                }
                else if(split_received[0].equals("list"))
                { 
                    if(split_received[1].equals("chatrooms") && tokens==2)
                    {
                    Set<String> keys= new HashSet<>();
                    keys = this.chatroom_user_map.keySet();
                    
                    for(String key: keys){
                        //System.out.println("sending chatrooms to server");
                        //System.out.println("client requsting chtroom :" + this.client_name);
                        Server.Send_command_error_result(this.client_name,chatroom_name,key,chatroom_user_map,user_socket_map);
                        //System.out.println(key);
                    }
                    }
                    else if(split_received[1].equals("users") && tokens==2)
                    {
                    Vector<String> users_in_listchatroom= new Vector<>();
                    users_in_listchatroom=this.chatroom_user_map.get(chatroom_name);
                    //System.out.println("chatroom_name: " + chatroom_name);
                    //System.out.println("cliet requsting user list :" + this.client_name);
                    //System.out.println("users_in_listchatroom"+ users_in_listchatroom);
                    for (int i = 0; i < users_in_listchatroom.size(); i++)
                    {

                      //  System.out.println("Sending result to server:" + users_in_listchatroom.get(i));
                        //System.out.println("chatroom_user_map: " + this.chatroom_user_map);
                        
                        Server.Send_command_error_result(this.client_name,chatroom_name,users_in_listchatroom.get(i),chatroom_user_map,user_socket_map);
                    }

                    }
                    else
                    {
                    error="Error: Usage: list chatrooms OR Usage: list users";
                    Server.Send_command_error_result(this.client_name,chatroom_name,error,chatroom_user_map,user_socket_map);
                    }
                }
                else if(split_received[0].equals("join"))
                { 
                    Vector<String> list;
                    Boolean check_userjoinchatroom=true;
                    Iterator itr=this.chatroom_user_map.values().iterator();
                    Vector vector=null;
  
                    while (itr.hasNext())
                    {
                    //System.out.println("here in itr.hasnext()");
                    vector=(Vector)itr.next();
                    if(vector.contains(this.client_name))
                    {
                        check_userjoinchatroom=false; //User cant's join chatroom
                        //System.out.println("user can't join chat room");
                        break;
                    }

                    }
                    
                    if(check_userjoinchatroom)
                    {    
                    chatroom_name=split_received[1];
                    list = this.chatroom_user_map.get(split_received[1]);
                    list.add(this.client_name);
                    System.out.println("join: "+chatroom_user_map);
                    }
                    else
                    {
                      chatroom_name=split_received[1];
                      error="Cannot join "+ split_received[1] + ":User already in some other chatroom";
                      Server.Send_command_error_result(this.client_name,chatroom_name,error,chatroom_user_map,user_socket_map);
                    } 
                }
                else if(split_received[0].equals("leave"))
                { 
                    Vector<String> select_leave_users = this.chatroom_user_map.get(chatroom_name);
                    select_leave_users.remove(this.client_name);
                    Iterator<String> iterator = this.chatroom_user_map.keySet().iterator();
                    while(iterator.hasNext()){
                        String key = iterator.next();
                        if(key.contains(chatroom_name)){
                            iterator.remove();
                            }
                    }
                    this.chatroom_user_map.put(chatroom_name,select_leave_users);
                    System.out.println("Client "+this.client_name+" has left the chatroom " + chatroom_name);

                }
                else if(split_received[0].equals("add"))
                {
                    Boolean check_userinchatroom=false;
                    Iterator itr=this.chatroom_user_map.values().iterator();
                    Vector vector=null;
  
                    while (itr.hasNext())
                    {
                    vector=(Vector)itr.next();
                    if(vector.contains(split_received[1]))
                    {
                        check_userinchatroom=true;
                        break;
                    }

                    }
  
                    if(this.all_users.contains(split_received[1]) && !(check_userinchatroom))
                    {
                        Vector<String> list = new Vector<>();

                        
                        list = this.chatroom_user_map.get(chatroom_name);
                        //System.out.println("chatroom_name"+chatroom_name);
                        list.add(split_received[1]);
                        //System.out.println("Adding user HashMap : " + this.chatroom_user_map);
                    }
                    else if(!this.all_users.contains(split_received[1]) || (check_userinchatroom))
                    {
                        if(!this.all_users.contains(split_received[1]))
                        {
                            error="User does not exist";
                            Server.Send_command_error_result(this.client_name,chatroom_name,error,chatroom_user_map,user_socket_map);
                        }
                        else
                        {
                            error="User associated with other chatroom";
                            Server.Send_command_error_result(this.client_name,chatroom_name,error,chatroom_user_map,user_socket_map);
                        }
                    }
                System.out.println("add: " + chatroom_user_map); 
                }
                else{
                    Boolean check_userinchatroom=true;
                    Iterator itr=this.chatroom_user_map.values().iterator();
                    Vector vector=null;
  
                    while (itr.hasNext())
                    {
                    vector=(Vector)itr.next();
                    if(vector.contains(this.client_name))
                    {
                        check_userinchatroom=false; //User cant's join chatroom
                        break;
                    }

                    }

                    if(this.all_users.contains(this.client_name) && (check_userinchatroom))
                    {
                        error="Error: Join/Create/Add in a chatroom";
                        Server.Send_no_chatroom(this.client_name,error,user_socket_map);

                    }
                    else if(this.all_users.contains(this.client_name) && !(check_userinchatroom))//reply
                    {
                    Vector<String> check_users = new Vector<>();
                    check_users = this.chatroom_user_map.get(chatroom_name);
                    Boolean flag = false;
                    flag=check_users.contains(this.client_name);
                    //System.out.println("flag"+flag);

                    
                    if(flag){

                         //System.out.println(split_received[1].charAt(0));
                         
                         if(split_received[0].equals("reply"))
                         {
                          if (split_received[2].equals("tcp") && tokens==3)
                         {
                            File file_to_send = new File(split_received[1]);
                            String message="Receiving " + file_to_send +" "+ file_to_send.length() + " from " + this.client_name;
                            System.out.println(message);
                            String filelength_str = Long.toString(file_to_send.length());
                            //System.out.println("Begin to send file: "+ file_to_send);
                            Server.Send(this.client_name,chatroom_name,message,chatroom_user_map,user_socket_map);
                            Server.FileTransferServer(this.client_name,chatroom_name,file_to_send,chatroom_user_map,user_socket_map);
                         }
                         else if(split_received[2].equals("udp") && tokens==3)
                         {
                            String filelength_str = this.dis.readUTF();
                            System.out.println("filelength_str"+filelength_str);
                            int fileLength = Integer.parseInt(filelength_str);

                            System.out.println("Sending file by udp");
                            DatagramSocket ds = new DatagramSocket(7000);
                            String message="Receiving " + split_received[1];
                            Server.Send(this.client_name,chatroom_name,message,chatroom_user_map,user_socket_map);
                            DatagramPacket dp = null;
                            while (fileLength > 0)
                            {
 
                                byte[] receive;

                                if(fileLength > 1000) {
                                    System.out.println("here in if");
                                    receive = new byte[1000];
                                }
                                else
                                {
                                    System.out.println("here in while");
                                    receive = new byte[fileLength];
                                }

                            // Step 2 : create a DatgramPacket to receive the data.
                                dp = new DatagramPacket(receive, receive.length);
                                System.out.println("receive 1"+receive);
 
                            // Step 3 : revieve the data in byte buffer.
                                ds.receive(dp);
                                fileLength-=dp.getLength();
 
                                //sSystem.out.println(":-");

                                System.out.println("receive 2");
                                Server.FileTransferServer_UDP(this.client_name,chatroom_name,receive,split_received[1],chatroom_user_map,user_socket_map);

                            }
                         }

                         else
                         {

                            //if(split_received[0].equals("reply"))//add condition that inverted commas 
                            //{
                                System.out.println(received.contains(""));
                                System.out.println("reply normal");
                                String client_who_sentmsg="";
                                String[] split_received_1 = received.split("\"");
                                System.out.println("split_received_1[1]"+split_received_1[1]);
                                client_who_sentmsg=(this.client_name+":").toString();
                                Server.Send(this.client_name,chatroom_name,client_who_sentmsg,chatroom_user_map,user_socket_map);
                                Server.Send(this.client_name,chatroom_name,split_received_1[1],chatroom_user_map,user_socket_map);


                            //}


                            // System.out.println("reply error");
                            // error="Error: Sending message/file Usage: reply \"message content\" OR reply filename.txt tcp OR reply filename udp";
                            // Server.Send_command_error_result(this.client_name,chatroom_name,error,chatroom_user_map,user_socket_map);
                         }
                     }
                    }
                    }

                }
			}
			catch (IOException e) {
                e.printStackTrace();
            }
		}
		try
        {
            // closing resources
            this.dis.close();
            this.dos.close();
             
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

}
	