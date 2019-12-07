import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;


public class FileTransferClient { 
    
    public static void main(String[] args) throws Exception{
        
        //Initialize socket
        Socket socket = new Socket(InetAddress.getByName("localhost"), 5000);
        byte[] contents = new byte[10000];
        
        //Initialize the FileOutputStream to the output file's full path.
        FileOutputStream fos = new FileOutputStream("server_new.sh");
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        InputStream is = socket.getInputStream();
        
        //No of bytes read in one read() call
        int bytesRead = 0; 
        System.out.println("is.read(contenst)"+is.read(contents));
        while((bytesRead=is.read(contents))!=-1)
        {
            bos.write(contents, 0, bytesRead);
            System.out.println("contents written");
        } 
        
        bos.flush(); 
        socket.close(); 
        
        System.out.println("File saved successfully!");
    }
}

