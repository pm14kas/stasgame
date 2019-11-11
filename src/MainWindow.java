import java.io.File;
import javax.sound.sampled.*;
import javax.swing.*;
import java.io.IOException;



public class MainWindow extends JFrame {

    public MainWindow() {
        setTitle("Змейка");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(640,480);
        setState(2);
        //setLocation(0,0);
        add(new GameField());
        setVisible(true);


    }

    public static void main(String[] args) {
        MainWindow mw = new MainWindow();

     //  playMusic("fon.wav");



       //playMusic("GameOver.wav");


    }



}

