
import javax.swing.JFileChooser;
import java.io.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

public class Client 
{
    private static String filepath = "";
    public static String separator = System.getProperty("file.separator");
    public static int[] allFiles;
    
    public static void selectFile (int index)
    {
        try 
        {
            Socket client = new Socket ("11.111.11.111", 1234); //Change  "11.111.11.111" for your IP adress 
            DataOutputStream dos = new DataOutputStream(client.getOutputStream());
            dos.writeInt(3);
            dos.flush();
            dos.writeInt(index);
            dos.flush();
            DataInputStream dis = new DataInputStream(client.getInputStream());
            int noFiles = dis.readInt();
            allFiles = new int[noFiles];
            
            for (int i = 0; i < noFiles; i++) 
            {
                String fileReceived = dis.readUTF();
                Interface.container.addElement(fileReceived);
                allFiles[i] = dis.readInt();
            }
            
            dis.close();
            dos.close();
            client.close();

        } 
        catch (IOException ex) 
        {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    public static void sendFile(File File1, String Sender, String Adressee)
    {
        try
        {
             if (File1.isFile())
             {
                Socket client = new Socket("11.111.11.111", 1234);
                DataOutputStream dos = new DataOutputStream(client.getOutputStream());
                String fileName = File1.getName();
                long size = File1.length();
                System.out.println("\nArchivo: " + Sender + " tamanio " + size + " bytes");
                DataInputStream dis = new DataInputStream(new FileInputStream(Sender));
                dos.writeInt(0);
                dos.flush();
                dos.writeUTF(fileName);
                dos.flush();
                dos.writeLong(size);
                dos.flush();
                dos.writeUTF(Adressee);
                dos.flush();
                long processed = 0;
                int x = 0;
                int y = 0, percentage = 0;
                byte[] bytes = new byte[2000];
                
                while (processed < size) 
                {
                    y = dis.read(bytes);
                    dos.write(bytes, 0, y);
                    dos.flush();
                    processed += y;
                    percentage = (int) ((processed * 100) / size);
                    System.out.println("\r" + percentage + "% procesado -> " + processed + " de " + size + " bytes");

                }
                    System.out.println("\nEl archivo se subio al server ");

                    dis.close();
                    dos.close();
                    client.close();

             }
             else
             {
                Socket client = new Socket("11.111.11.111", 1234);
                DataOutputStream dos = new DataOutputStream(client.getOutputStream());
                String fileName2 = File1.getName();
                String path1 = File1.getAbsolutePath();
              
                String aux = filepath;
                filepath = filepath + separator + fileName2;
                dos.writeInt(4);
                dos.flush();
                dos.writeUTF(filepath);
                dos.flush();
                File folder = new File(path1);
                File[] files = folder.listFiles();
                
                for (File file : files) 
                {
                    String path = filepath + separator + file.getName();
     
                    sendFile(file, file.getAbsolutePath(), path);
                }
                
                filepath = aux;
                dos.close();
                client.close();
             }
            
        }
        catch (Exception e) 
        {
            e.printStackTrace();
        }
    }
    
    public static void selectFiles() 
    {
        try 
        {
            JFileChooser jf = new JFileChooser();
            jf.setMultiSelectionEnabled(true);
            jf.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            int r = jf.showOpenDialog(null);

            if (r == JFileChooser.APPROVE_OPTION) 
            {
                filepath = "";
                File[] files = jf.getSelectedFiles();
                for (File file : files) 
                {
                    String rutaOrigen = file.getAbsolutePath();
                    sendFile(file, rutaOrigen, file.getName());
                }
                Interface.container.clear();
                Refresh();
            }  
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
    }
    
    public static void Refresh() 
    {
        try 
        {
            Socket client = new Socket("11.111.11.111", 1234);
            DataOutputStream dos = new DataOutputStream(client.getOutputStream());
            dos.writeInt(1);
            dos.flush();
            DataInputStream dis = new DataInputStream(client.getInputStream()); 
            int noFiles1 = dis.readInt();
            allFiles = new int[noFiles1];

            for (int i = 0; i < noFiles1; i++) 
            {
                String fRec = dis.readUTF();
                Interface.container.addElement(fRec);
                allFiles[i] = dis.readInt();
            }

            dis.close();
            dos.close();
            client.close();
            System.out.println("Directorio actualizado");

        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
    }
    
    
    
    public static void deleteFile(String[] fileNames2, int size4) 
    {
        try 
        {
            Socket client = new Socket("11.111.11.111", 1234);
            DataOutputStream dos = new DataOutputStream(client.getOutputStream()); 
            DataInputStream dis = new DataInputStream(client.getInputStream()); 
            dos.writeInt(5);
            dos.flush();
            dos.writeInt(size4);
            dos.flush();
            String aux = "";
            for (int i = 0; i < size4; i++) 
            {
                aux = fileNames2[i];
                dos.writeUTF(aux);
                dos.flush();
            }

            String name3 = dis.readUTF();
            long size5 = dis.readLong();
            System.out.println("\nArchivo eliminado ");

            dos.close();
            dis.close();
            client.close();

        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
    } 
    
    
    public static void GetFiles(String[] nombresArchivos, int tama) {
		try {
    		Socket client = new Socket("11.111.11.111", 1234); //Change for your IP adress
			DataOutputStream dos = new DataOutputStream(client.getOutputStream()); 
			DataInputStream dis = new DataInputStream(client.getInputStream()); 

			dos.writeInt(2); 
			dos.flush();

			dos.writeInt(tama); 
			dos.flush();
			
			String aux = "";

			for(int i = 0; i < tama; i++) {
				aux = nombresArchivos[i];
				dos.writeUTF(aux);
				dos.flush();
			}
                        String nombre="";
			
		
                                nombre=  "src/main/java/Client_Downloads" + separator + dis.readUTF();
			

			long tam = dis.readLong();
		

			DataOutputStream dosArchivo = new DataOutputStream(new FileOutputStream(nombre)); 
			
			long recibidos = 0;
			int n = 0, porciento = 0;
			byte[] b = new byte[2000];

			while(recibidos < tam) {
				n = dis.read(b);
				dosArchivo.write(b, 0, n);
				dosArchivo.flush();
				recibidos += n;
				porciento = (int)((recibidos * 100) / tam);
				System.out.println("\r" + porciento + "% procesado -> " + recibidos + " de " + tam + " bytes");
			} 

			System.out.println("\nEl archivo se ha descargado con éxito");
                      
			dos.close();
			dis.close();
			dosArchivo.close();
			client.close();

    	}catch(Exception e) {
    		e.printStackTrace();
    	}
	}
    
}
