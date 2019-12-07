import java.io.*;
import java.net.*;
import java.util.Scanner;
 
// Client class
public class Client
{
    public static void main(String[] args) throws IOException 
    {
        try
        {
          Scanner scn = new Scanner(System.in);
		      System.out.println("Enter port number:");
          int portno;  
    	    portno = scn.nextInt();
          String client_name = args[0];  
            // getting localhost ip
          InetAddress ip = InetAddress.getByName("localhost");
            // establish the connection with server port 5056
          Socket s = new Socket(ip, portno);
          DataInputStream dis = new DataInputStream(s.getInputStream());
          DataOutputStream dos = new DataOutputStream(s.getOutputStream());
          dos.writeUTF(client_name);
          //*******FILE
          byte[] contents = new byte[10000];

          InputStream is = s.getInputStream();
//**********
          DatagramSocket ds = new DatagramSocket();
          
//********8
          Thread terminal = new Terminal_Handler(s,dos,ds,ip);
          terminal.start();
          Thread receive = new Receive_Handler(s,dis,is);
          receive.start();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }


}
class Terminal_Handler extends Thread //read from terminal.
{
    final DataOutputStream dos;
    final Socket s;
    final DatagramSocket ds;
    final InetAddress ip;
    Scanner scn = new Scanner(System.in);
    byte buf[] = null;
	// Constructor
    public Terminal_Handler(Socket s,DataOutputStream dos,DatagramSocket ds,InetAddress ip) throws IOException
    {
        this.s = s;
        this.dos = dos;
        this.ds=ds;
        this.ip=ip;
       // this.buf=buf;
    }
    public void run() 
    {
    	while (true) 
            {
            	try {
            	
                //System.out.println(dis.readUTF());
                String tosend = scn.nextLine();
                dos.writeUTF(tosend);
                String[] split_tosend = tosend.split("\\s+");
                int tokens=split_tosend.length;
                if(split_tosend[0].equals("Exit"))
                { 
                    System.out.println("Closing this connection : " + s);
                    s.close();
                    System.out.println("Connection closed");
                    break;
                }
                if(split_tosend[0].equals("reply") && tokens==3)
                {
                  if(split_tosend[2].equals("udp"))
                  {
                    System.out.println("Entering udp client : " + s);
                    File file_to_send=new File(split_tosend[1]);
                    FileInputStream fis = new FileInputStream(file_to_send);
                    BufferedInputStream bis = new BufferedInputStream(fis); 
          
                  //Get socket's output stream
                  //OutputStream os = s.getOutputStream();
                    byte[] contents;

                    long fileLength = file_to_send.length();
                    System.out.println("long filelength" + fileLength);
                    String filelength_str=Long.toString(fileLength); 
                    this.dos.writeUTF(filelength_str);
                    long current = 0;
         
                    //long start = System.nanoTime();
                    while(current<fileLength){ 
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
 
                    System.out.println("inp: " + contents);
                  
                    DatagramPacket dp = new DatagramPacket(contents, contents.length, InetAddress.getByName("localhost"), 7000);
 
                  // Step 3 : invoke the send call to actually send
                  // the data.
                    this.ds.send(dp);
                    //contents = new byte[size];//clear buffer
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
  	//closing resources
  		scn.close();
  		this.dos.close();
             
  		}
    	catch(IOException e){
        	e.printStackTrace();
    	}
	}   
    

}
class Receive_Handler extends Thread //read from terminal.
{
    final DataInputStream dis;
    final Socket s;
    final InputStream is;
    String received;


    Scanner scn = new Scanner(System.in);
  // Constructor
    public Receive_Handler(Socket s,DataInputStream dis,InputStream is) throws IOException
    {
        this.s = s;
        this.dis = dis;
        this.is=is;
    }
    public void run() 
    {
      while (true) 
            {
              try {
                
                received = dis.readUTF();
                System.out.println(received);
                String[] split_received = received.split("\\s+");
                int tokens=split_received.length;
                if(split_received[0].equals("Receiving"))
                {
                FileOutputStream fos = new FileOutputStream("A_out.txt");
                BufferedOutputStream bos = new BufferedOutputStream(fos);
                byte[] buffer = new byte[100];
                int filesize = Integer.parseInt(split_received[2]);
                int read = 0;
                int totalRead = 0;
                int remaining = filesize;
                while((read = is.read(buffer, 0, Math.min(buffer.length, remaining))) > 0) {
                    totalRead += read;
                    remaining -= read;
                    fos.write(buffer, 0, read);
                }
                
                bos.flush();
                System.out.println("Received " + split_received[1] + " from " + split_received[4]);
                }
                else
                {
                  while(true)
                  {
                  System.out.println(dis.readUTF());
                  }

                }
                
              }
              catch (IOException e) {
                e.printStackTrace();
              }
             }
    //   try
    //   {
    // //closing resources
    //   scn.close();
    //   this.dis.close();
             
    //   }
    //   catch(IOException e){
    //       e.printStackTrace();
    //   }
    }   
    

}