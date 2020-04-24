package fosalgo;

import java.awt.EventQueue;
import javax.swing.JFrame;

public class Main extends JFrame{
    
    public Main(){
        initialize();
    }

    private void initialize() {
        add(new PapanPermainan());
        setResizable(false);
        pack();
        setTitle("Game Ular");
        //setLocationRelativeTo(null);
        setLocation(640,120);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
    public static void main(String[]fosalgo){
        EventQueue.invokeLater(() ->{
            JFrame myFrame = new Main();
            myFrame.setVisible(true);
        });
    }
    
}
