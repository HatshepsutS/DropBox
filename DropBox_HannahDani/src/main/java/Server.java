
import java.net.*;
import java.io.*;
import java.util.zip.*;

public class Server 
{
    public static String separator1 = System.getProperty("file.separator");
    private static String serverPath = "." + separator1 + "DropBoxServer" + separator1;
    private static File[] list;
    private static String actualPath = "";
    private static int Times = 0;
    
    public static void receiveFiles(DataInputStream dis, String name) throws IOException 
    {
        long size = dis.readLong();
        String desPath = dis.readUTF();
        name = serverPath + desPath;
        System.out.println("\nRecibiendo archivos " + name + " -> " + size + "bytes");
        DataOutputStream dos = new DataOutputStream(new FileOutputStream(name)); 
        long processed = 0;
        int y = 0, percentage = 0;
        byte[] bytes = new byte[2000];

        while (processed < size) 
        {
            y = dis.read(bytes);
            dos.write(bytes, 0, y);
            dos.flush();
            processed += y;
            percentage = (int) ((processed * 100) / size);
            System.out.println("\r" + percentage + "% procesado  -> " + processed + " de " + size + " bytes");
        }

        System.out.println("\nArchivo recibido " + name + " -> " + size + " bytes");
        dos.close();
        dis.close();
    }
    
    public static void refreshClient(Socket client, DataInputStream dis, String path, int flag) throws IOException 
    {
        File filesPath = new File(path);
        if (!filesPath.exists()) 
        {
            filesPath.mkdir();
        }
        if (flag == 1) 
        {
            actualPath = actualPath + separator1 + filesPath.getName();
          
        }
        list = filesPath.listFiles();
        DataOutputStream dos = new DataOutputStream(client.getOutputStream()); 
        dos.writeInt(list.length);
        dos.flush();
        String info = "";
        int type = 0;

        for (File f : list) 
        {
            if (f.isDirectory()) 
            {
                type = 1;
                if (flag == 0)
                {
                    info = "." + separator1 + f.getName();
                } 
                else 
                {
                    info = "." + actualPath + separator1 + f.getName();
                }
            }
            else 
            {
                type = 2;
                if (flag == 0) 
                {
                    info = f.getName();
                } 
                else 
                {
                    info = "." + actualPath + separator1 + f.getName();
                }
            }
            dos.writeUTF(info);
            dos.flush();
            dos.writeInt(type);
            dos.flush();
            type = 0;
        }
        dos.close();
        System.out.println("Directorio actualizado");
    }
    
    public static void sendFile(DataOutputStream dos, File f) 
    {
        try {
            String name = f.getName();
            long size = f.length();
            String path = f.getAbsolutePath();
            DataInputStream disFile = new DataInputStream(new FileInputStream(path)); 
            dos.writeUTF(name);
            dos.flush();
            dos.writeLong(size);
            dos.flush();

            long processed = 0;
            int y = 0, percentage = 0;
            byte[] bytes = new byte[2000];

            while (processed < size) 
            {
                y = disFile.read(bytes);
                dos.write(bytes, 0, y);
                dos.flush();
                processed += y;
                percentage = (int) ((processed * 100) / size);
                System.out.println("\r" + percentage + "% procesado -> " + processed + "de" + size + " bytes");
            } 
            System.out.println("\nARCHIVO ENVIADO");
  
            disFile.close();
            dos.close();
        }
        catch (Exception e) 
        {
            e.printStackTrace();
        }
    }
  
	public static void crearZIP(DataInputStream dis, int tam) {
		try {
			
			String[] nombreArchivos = new String[tam];
			String aux = "";
			int i, j;
			for(i = 0; i < tam; i++) {
				nombreArchivos[i] = dis.readUTF();
			
			}

		
			char aux1 = ' ', aux2 = ' ';
			String nombre = ""; 
			for(i = 0; i < tam; i++) {
				aux1 = nombreArchivos[i].charAt(0);
				if( aux1 == '.') {
					for(j = 2; j < nombreArchivos[i].length(); j++)
						nombre = nombre + Character.toString(nombreArchivos[i].charAt(j));
					nombreArchivos[i] = nombre;
					nombre = "";
				}
			}
		    String destino = serverPath + "DropBoxDownload" + Times + ".zip";
		    FileOutputStream fos = new FileOutputStream(destino);
		    ZipOutputStream zipOut = new ZipOutputStream(fos);
			String sourceFile = "";
			for(i = 0; i < tam; i++) {
				
				sourceFile = serverPath + nombreArchivos[i];
				File fileToZip = new File(sourceFile);
		    	zipFile(fileToZip, fileToZip.getName(), zipOut);
		    	sourceFile = " ";
			}
			zipOut.close();
		    fos.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}


	public static void zipFile(File fileToZip, String fileName, ZipOutputStream zipOut) throws IOException {
        if (fileToZip.isHidden()) {
            return;
        }

        if (fileToZip.isDirectory()) {
            if (fileName.endsWith(separator1)) {
                zipOut.putNextEntry(new ZipEntry(fileName));
                zipOut.closeEntry();
            } 
            else {
                zipOut.putNextEntry(new ZipEntry(fileName + separator1));
                zipOut.closeEntry();
            }
            File[] children = fileToZip.listFiles();
            for (File childFile : children) {
                zipFile(childFile, fileName + separator1 + childFile.getName(), zipOut);
            }
            return;
        }

        FileInputStream fis = new FileInputStream(fileToZip);
        ZipEntry zipEntry = new ZipEntry(fileName);
        zipOut.putNextEntry(zipEntry);
        byte[] bytes = new byte[1024];
        int length;

        while ((length = fis.read(bytes)) >= 0) {
            zipOut.write(bytes, 0, length);
        }

        fis.close();
        System.out.println("Archivo(s) Comprimido!");
    }
        
        
    public static void deleteDir(File file) 
    {
        File[] contents = file.listFiles();
        if (contents != null) 
        {
            for (File f : contents) 
            {
                deleteDir(f);
            }
        }
        file.delete();
    }
    
    public static void deleteFile(DataInputStream dis, int size, DataOutputStream dos) 
    {
        try 
        {
            String[] nameFiles = new String[size];
            String aux = "";
            int i, j;
            
            for (i = 0; i < size; i++) 
            {
                nameFiles[i] = dis.readUTF();
                String name = nameFiles[i];
                boolean flag = false;
                if (name.indexOf(".") == 0) 
                {
                    name = name.substring(2,name.length());
                    flag = true;
                }
                
                File f = new File(serverPath + name);
                if (flag) 
                {
                    deleteDir(f);
                    System.out.println("Archivo eliminado");
                } 
                else 
                {
                    if (f.delete()) 
                    {
                        System.out.println("\n" + name + " -> " + size + " eliminado");
                    } 
                    else 
                    {
                        System.out.println("\n" + name + " -> " + size + " no se pudo eliminar");

                    }
                }
                dos.writeUTF(name);
                dos.flush();
                dos.writeLong(size);
                dos.flush();
            }

        } 
        catch (Exception e) {
            e.printStackTrace();
        }
    } 


    public static void main(String[] args) 
    {
       try 
       {
            ServerSocket server = new ServerSocket(1234);
            server.setReuseAddress(true);
            System.out.println("Esperando una conexión");

            for (;;) {
                Socket client = server.accept();
                System.out.println("\nCliente conectado desde:  " + client.getInetAddress() + " " + client.getPort());
                DataOutputStream dos = new DataOutputStream(client.getOutputStream()); 
                DataInputStream dis = new DataInputStream(client.getInputStream()); 

                int flag = dis.readInt();

                if (flag == 0) 
                {
                    String name = dis.readUTF();
                    receiveFiles(dis, name);
                } 
                else if (flag == 1) 
                {
                    actualPath = "";
                    refreshClient(client, dis, serverPath, 0);

                } 
     else if (flag == 2) {
					
					int tam = dis.readInt();
					String path = "DropBoxDownload" + Times + ".zip";
					path = serverPath + path;
					
					File archivoZip = new File(path);
					
					
					crearZIP(dis, tam);

					if(archivoZip.exists()) {
						
						System.out.println("El archivo se comprimira en formato zip con el nombre -> " + archivoZip.getName());
						sendFile(dos, archivoZip);
					if(archivoZip.delete()) 
							System.out.println(".....");
					}

					Times++;

				}
                else if (flag == 3) 
                {
                    int source = dis.readInt();
                    String newPath = "" + list[source].getAbsoluteFile();
                    refreshClient(client, dis, newPath, 1);
                } 
                else if (flag == 4) 
                {
                    String folderPath = dis.readUTF();
                    String path = serverPath + folderPath;
                    File filesPath = new File(path);
                    if (!filesPath.exists()) 
                    {
                        filesPath.mkdir();
                    }
                } 
                else if (flag == 5) 
                {
                    int size = dis.readInt();
                    deleteFile(dis, size, dos);
                } 
                else 
                {
                    System.out.println("Ha ocurrido un error...:");
                }
                dis.close();
                client.close();
            }
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
    }
    
}
