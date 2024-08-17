package guis;

import javax.swing.*;
import java.awt.*;

public class mainFrame extends JFrame {
    //Create main frame. So, I can add every other guis into that and prevent creating several frames.
    public mainFrame(){
        this.setTitle("Hotel Management");
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setSize(920,600);
        this.setVisible(false);
        this.setLayout(null);
        //Adjusting the frame's coordinates.
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = getSize();
        int x = (screenSize.width - frameSize.width) / 2;
        int y = (screenSize.height - frameSize.height) / 3;
        setLocation(x, y);
    }
}
