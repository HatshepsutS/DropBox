
import java.awt.event.*;
import java.awt.*;
import javax.swing.*;
import java.io.*;

public class Interface extends JFrame implements ActionListener 
{
    Color moradoFachero=new Color(102,0,153); 
    JButton newFile, refreshFile, deleteFile,downloadFile;
    static JList<String> files;
    static DefaultListModel<String> container;
    MouseListener mouseListener;
    JPanel panelButtons;
    JPanel panelLabel;
    JScrollPane scroll;
    Font myFont1 = new Font("Segoe UI Semilight", Font.PLAIN, 40);
    Font myFont2 = new Font("Tahoma", Font.PLAIN, 18);    
    public Interface() 
    {
          
        Container c = getContentPane();
    
        c.setLayout(new BoxLayout(c, BoxLayout.X_AXIS));
        c.setBackground(moradoFachero);
        files = new JList<String>();
        files.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        mouseListener = new MouseAdapter() 
        {
            public void mouseClicked(MouseEvent e) 
            {

                if (e.getClickCount() == 2) 
                {
                    int index = files.locationToIndex(e.getPoint());
                    String nombreSeleccion = container.getElementAt(index);

            
                    if (Client.allFiles[index] == 1) 
                    {
                        container.clear();
                        
                        Client.selectFile(index);
                    }
                }

            }
        };
       files.addMouseListener(mouseListener);

        container = new DefaultListModel<>();
        Client.Refresh();

       files.setModel(container);

        scroll = new JScrollPane(files);
        scroll.setMinimumSize(new Dimension(100, 200));

      

        panelButtons = new JPanel();
        panelButtons.setLayout(new BoxLayout(panelButtons, BoxLayout.Y_AXIS));

        panelButtons.setBackground(moradoFachero);
        newFile = new JButton();
        newFile.setText("  Nuevo  ");
        newFile.setBorderPainted(false);
        newFile.setBackground(moradoFachero);
        newFile.setForeground(Color.WHITE);
        newFile.setFont(myFont2);  
        
        refreshFile = new JButton();
        refreshFile.setText("  Refresh  ");
        refreshFile.setBorderPainted(false);
        refreshFile.setBackground(moradoFachero);
        refreshFile.setForeground(Color.WHITE);
        refreshFile.setFont(myFont2);

        downloadFile = new JButton();
        downloadFile.setText("  Descargar  ");
        downloadFile.setBorderPainted(false);
        downloadFile.setBackground(moradoFachero);
        downloadFile.setForeground(Color.WHITE);
        downloadFile.setFont(myFont2);        
        
        
        deleteFile = new JButton();
        deleteFile.setText("  Eliminar  ");
        deleteFile.setBorderPainted(false);
        deleteFile.setBackground(moradoFachero);
        deleteFile.setForeground(Color.WHITE);
        deleteFile.setFont(myFont2);
        
        panelButtons.add(newFile);
        panelButtons.add(refreshFile);
        panelButtons.add(deleteFile);
        panelButtons.add(downloadFile);
        
       panelLabel=new JPanel();
       JLabel jLabel1=new JLabel();
       JLabel ImageLabel=new JLabel();
       panelLabel.setLayout(new BoxLayout(panelLabel, BoxLayout.Y_AXIS));
       ImageLabel.setText("");
       ImageLabel.setIcon(new ImageIcon("src/main/java/Icons/open-box (5).png"));
      
       jLabel1.setText("Dropbox");
       jLabel1.setFont(myFont1);
       jLabel1.setForeground(Color.WHITE);
       panelLabel.setBackground(moradoFachero);
       panelLabel.add(ImageLabel);
       panelLabel.add(jLabel1);
       panelLabel.add(panelButtons);
        c.add(panelLabel); 
       
         c.add(scroll);
        newFile.addActionListener(this);
        refreshFile.addActionListener(this);
        deleteFile.addActionListener(this);
        downloadFile.addActionListener(this);
     
    }

    public void actionPerformed(ActionEvent e) 
    {
        JButton b = (JButton) e.getSource();
       
        if (b == newFile) 
        {
            Client.selectFiles();
        }
        else if (b == refreshFile) 
        {
            container.clear();
            Client.Refresh();
        } 
        
		else if(b == downloadFile) {
			if(!files.isSelectionEmpty()) {
				int[] indices = files.getSelectedIndices();	
				String[] nombreSeleccion = new String[indices.length];

				for(int i = 0; i < indices.length; i++) {
				    nombreSeleccion[i] = container.getElementAt(indices[i]);
				}

				Client.GetFiles(nombreSeleccion, indices.length);
			}   }     
        
        else if(b == deleteFile)
        {
            if (!files.isSelectionEmpty()) 
            {
                int[] indices = files.getSelectedIndices();
                String[] nombreSeleccion = new String[indices.length];

                for (int i = 0; i < indices.length; i++) 
                {
                    nombreSeleccion[i] = container.getElementAt(indices[i]);
                    System.out.println("Nombre del archivo:  " + nombreSeleccion[i]);
                }
                Client.deleteFile(nombreSeleccion, indices.length);
            }
           
        }
    }

    public static void main(String s[]) {
        Interface tab = new Interface();
        tab.setTitle("Dropbox");
        tab.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       
        tab.setSize(700, 500);
        tab.setVisible(true);
        tab.setLocationRelativeTo(null);
    
    }
}