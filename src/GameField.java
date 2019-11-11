import javax.sound.sampled.*;
import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;




class Vector
{
    public int x;
    public int y;

    public Vector()
    {
        x = 0;
        y = 0;
    }
}

public class GameField extends JPanel implements ActionListener {


    private final int SIZE = 20;
    private final int DOT_SIZE = 16;
    private final int ALL_DOTS = 400;
    private Image dot;
    private Image head;
    private Image apple;
    private Image barrier;
    private Image background;
    private Image stas;
    private Image flash;
    private int flashX;
    private int flashY;

    int counter = 0;

    final int RIGHT = 0;
    final int LEFT = 1;
    final int DOWN = 2;
    final int UP = 3;

    private int appleX;
    private int appleY;

    protected ArrayList<Vector> barriers = new ArrayList<Vector>();

    private int[] x = new int[ALL_DOTS];
    private int[] y = new int[ALL_DOTS];

    private Random random = new Random();


    private int dots; //текущий размер змейки
    private Timer timer;

    public static boolean inGame = true;
    //текущее положение змейки
    private int direction = RIGHT;
    //желаемое положение змейки
    private int desiredDirection = RIGHT;


    private Clip backgroundMusic;
    private Clip gameOverSound;
    private Clip gettingAppleSound;
    private Clip gettingFlashSound;

    public GameField() {
        setBackground(Color.white);
        loadImages();
        initGame();
        addKeyListener(new FieldKeyListener());
        setFocusable(true); //фокус на игровом поле
        gameOverSound = initializeSound("GameOver.wav");
        backgroundMusic = initializeSound("fon.wav");
        gettingAppleSound = initializeSound("glotok.wav");
        gettingFlashSound = initializeSound("flash.wav");
    }


    public void initGame(){ //инициализация игры
        dots = 3;
        counter = 0;
        int startX = 3;
        int startY = 3;
        for(int i=0; i<dots; i++){
            x[i] = 3;
            y[i] = 3;
        }
        //new Timer(250, this);
        if (timer != null) {
            timer.stop();
        }
        timer = new Timer(200, this);
        timer.start();
        createApple();
//        createFlash();
        createBarrier();
        inGame = true;
        direction = RIGHT;
        desiredDirection = RIGHT;
        barriers.clear();
    }

    public void createApple(){
        Vector barrier = new Vector();
        boolean isPositionOkay = true;
        int iterations = 0;
        do {
            appleX = random.nextInt(SIZE);
            appleY = random.nextInt(SIZE);
            System.out.println("Apple generated: " + appleX + " " + appleY + " - it is generation " + iterations);

            for (Vector ass: barriers) {
                if ((appleX == barrier.x) && (appleY == barrier.y)) {
                    isPositionOkay = false;
                    break;
                }
            }

            if (isPositionOkay) {
                for (int i = 0; i < dots; i++) {
                    if ((Math.abs(appleY - y[i]) < 3) && (Math.abs(appleX - x[i]) < 3)) {
                        isPositionOkay = false;
                        break;
                    }
                }
            }
            iterations++;
        } while (!isPositionOkay && iterations < 50);

        if (iterations >= 50) {
            Vector slowSearch = slowFreeSpaceSearch();
            appleX = slowSearch.x;
            appleY = slowSearch.y;

        }
    }

    public void  createFlash(){
        flashX = random.nextInt(SIZE);
        flashY = random.nextInt(SIZE);
    }

    public void createBarrier(){
        Vector barrier = new Vector();

        int width = 5;
        boolean isPositionOkay = true;
        int iterations = 0;
        do {
            barrier.x = random.nextInt(20);
            barrier.y = random.nextInt(20);
            System.out.println("Barrier generated: " + barrier.x + " " + barrier.y + " - it is generation " + iterations);

            if ((Math.abs(barrier.y - y[0]) < width) && (Math.abs(barrier.x - x[0]) < width)) {
                isPositionOkay = false;
            }

            if (isPositionOkay) {
                if ((appleX == barrier.x) && (appleY == barrier.y)) {
                    isPositionOkay = false;
                }
            }

            if (isPositionOkay) {
                for (int i = 0; i < dots; i++) {
                    if ((barrier.y == y[i]) && (barrier.x == x[i])) {
                        isPositionOkay = false;
                        break;
                    }
                }
            }
            iterations++;
        } while (!isPositionOkay && iterations < 10);

        //за 20 итераций барьер мог сгенерироваться неудобно, не добавляем его
        if (iterations < 20) {
            barriers.add(barrier);
        }
    }

    public Vector slowFreeSpaceSearch()
    {
        System.out.println("Slow search activated");
        int size = SIZE;
        boolean[][] field = new boolean[size + 1][size + 1];

        for (Vector barrier: barriers) {
            int coordX = barrier.x;
            int coordY = barrier.y;
            field[coordX][coordY] = true;
        }

        for (int i = 0; i < dots; i++) {
            int coordX = x[i];
            int coordY = y[i];

            field[coordX][coordY] = true;
        }


        int coordX = appleX;
        int coordY = appleY;
        field[coordX][coordY] = true;

        //вниз вправо
        for (int i = size/2; i < size; i++) {
            for (int j = size/2; j < size; j++) {
                if (field[i][j] == false) {
                    Vector result = new Vector();
                    result.x = i;
                    result.y = j;

                    return result;
                }
            }
        }

        //вниз влево
        for (int i = size/2; i < size; i++) {
            for (int j = size/2; j >= 0; j--) {
                if (field[i][j] == false) {
                    Vector result = new Vector();
                    result.x = i;
                    result.y = j;

                    return result;
                }
            }
        }

        //вверх влево
        for (int i = size/2; i >= 0; i--) {
            for (int j = size/2; j >= 0; j--) {
                if (field[i][j] == false) {
                    Vector result = new Vector();
                    result.x = i;
                    result.y = j;

                    return result;
                }
            }
        }

        //вверх вправо
        for (int i = size/2; i >= 0; i--) {
            for (int j = size/2; j < size; j++) {
                if (field[i][j] == false) {
                    Vector result = new Vector();
                    result.x = i;
                    result.y = j;

                    return result;
                }
            }
        }

        inGame = false;
        System.out.println("NO PLACE LEFT");
        return new Vector();
    }



    public void loadImages(){
        ImageIcon iconApple = new ImageIcon("apple.png");
        apple = iconApple.getImage();

        ImageIcon iconFlash = new ImageIcon("flash.png");
        flash = iconFlash.getImage();

        ImageIcon iconBarrier = new ImageIcon("barrier.png");
        barrier = iconBarrier.getImage();

        ImageIcon iconDot = new ImageIcon("dot.png");
        dot =  iconDot.getImage();

        ImageIcon iconHead = new ImageIcon("head.png");
        head =  iconHead.getImage();

        ImageIcon iconBackground = new ImageIcon("background.png");
        background =  iconBackground.getImage();

        ImageIcon iconStas = new ImageIcon("stas.png");
        stas =  iconStas.getImage();
    }

//перерисовка
    @Override
    protected void paintComponent(Graphics g) {
        //перерисовка текущего компонента
        super.paintComponent(g);

        String SCORE = String.valueOf(counter);

        if (inGame) {
            g.drawImage(background, 0, 0, this);


            int shift = 50;
            int fieldRectSize = 336;
            g.fillRect(0 + shift, 0 + shift, fieldRectSize, fieldRectSize);
            g.drawImage(apple, appleX * DOT_SIZE + shift, appleY * DOT_SIZE + shift, this);
            g.drawImage(flash, flashX * DOT_SIZE + shift, flashY * DOT_SIZE + shift, this);
            for (Vector ass: barriers) {
                g.drawImage(barrier, ass.x * DOT_SIZE + shift, ass.y * DOT_SIZE + shift, this);
            }

            g.drawString("Очки: " + SCORE, 460, 110);




            for (int i = 1; i < dots; i++) {
                g.drawImage(dot, x[i] * DOT_SIZE + shift, y[i] * DOT_SIZE + shift, this);
            }
            g.drawImage(head, x[0] * DOT_SIZE + shift, y[0] * DOT_SIZE + shift, this);

        } else {
            timer.setDelay(1);
            String str = "Game over";
            g.setColor(Color.BLACK);
            g.drawString(str, 125, SIZE / 2);
            g.drawString("Ваш результат: " + SCORE,110, 180 );
            g.drawString("Нажмите Enter, чтобы начать сначала.", 110, 200);

            Graphics2D g2 = (Graphics2D)g;

            long kek = java.lang.System.currentTimeMillis();
            double rotation = Math.sin(kek * 0.001) * 0.5;

            int stasX = 380;
            int stasWidth = 200;
            int stasY = 100;
            int stasHeight = 200;

            g2.rotate(rotation, stasX + stasWidth * 0.5, stasY + stasHeight * 0.9);
            g2.drawImage(stas, stasX, stasY, this);
            g2.rotate(-rotation, stasX + stasWidth * 0.5, stasY + stasHeight * 0.9);
        }
    }

    public void move(){
        for (int i = dots; i >0; i--) {
            x[i] = x[i-1];
            y[i]=y[i-1];
        }

        if(desiredDirection == LEFT) {
            x[0] -= 1;
        }
        if(desiredDirection == RIGHT) {
            x[0] += 1;
        }

        if(desiredDirection == UP) {
            y[0] -= 1;
        }
        if(desiredDirection == DOWN) {
            y[0] += 1;
        }
        direction = desiredDirection;
    }

    public void checkApple() {
        if (x[0] == appleX && y[0] == appleY) {
            dots++;
            counter++;
            createApple();
            createBarrier();
            if(dots == 10 || dots == 15 || dots == 25) {
                createFlash();
            }

        }
    }


    public void checkFlash(){
        if(x[0]==flashX && y[0] == flashY){
            timer = new Timer(100, this);
            timer.start();


        }
    }


    //препятствие в виде стены
    public void checkCollisions(){

        for (int i = dots; i >0; i--) {
            //сама с собой
            if (i != 0 && dots > 3 && x[0] == x[i] && y[0] == y[i]) {
                inGame = false;
            }
        }

        for (Vector ass: barriers ) {
            if (ass.x == x[0] && ass.y == y[0]) {
                inGame = false;
            }
        }

        //стенки
        if(x[0] > SIZE) {
            inGame = false;
        } else if(y[0] > SIZE) {
            inGame = false;
        } else if(x[0] < 0) {
            inGame = false;
        } else if(y[0] < 0) {
            inGame = false;
        }

        if (inGame == false) {

            if (gameOverSound != null) {
                gameOverSound.setFramePosition(0);
                gameOverSound.start();
            }
        }


        if (gettingAppleSound != null && !gettingAppleSound.isRunning()) {
            if (x[0] == appleX && y[0] == appleY) {
                gettingAppleSound.setFramePosition(0);
                gettingAppleSound.start();
            }}

        if (gettingFlashSound != null && !gettingFlashSound.isRunning()) {
            if (x[0] == flashX && y[0] == flashY) {
                gettingFlashSound.setFramePosition(0);
                gettingFlashSound.start();
            }}
    }



    @Override
    public void actionPerformed(ActionEvent e) {
        if(inGame){
            checkApple(); //встретили яблоко
            checkFlash();
            move();
            checkCollisions();
            if ((backgroundMusic != null) && (!backgroundMusic.isRunning())) {
                backgroundMusic.setFramePosition(0);
                backgroundMusic.start();


            }
        } else {
            backgroundMusic.stop();
        }
        repaint();
    }


    class FieldKeyListener extends KeyAdapter{
        @Override
        public void keyPressed(KeyEvent e) {

            super.keyPressed(e);
            int key = e.getKeyCode(); //клавиша которая была нажата
            if((key == KeyEvent.VK_LEFT || key==KeyEvent.VK_A) && (direction != RIGHT)){
                desiredDirection = LEFT;
            }

            if((key == KeyEvent.VK_RIGHT || key==KeyEvent.VK_D) && (direction != LEFT)){
                desiredDirection = RIGHT;
            }



            if((key == KeyEvent.VK_UP || key==KeyEvent.VK_W) && (direction != DOWN)){
                desiredDirection = UP;

            }

            if((key == KeyEvent.VK_DOWN || key==KeyEvent.VK_S) && (direction != UP)){
                desiredDirection = DOWN;

            }

            if(inGame==false && key==KeyEvent.VK_ENTER){
              initGame();
            }


        }
    }

    public Clip initializeSound(String filepath){
        AudioInputStream music;
        Clip clip = null;
        try{
            File musicFile = new File(filepath);
            System.out.println(musicFile.exists() ? "FILE EXISTS" : "FILE NOT EXISTS");
            music = AudioSystem.getAudioInputStream(musicFile);

            clip = AudioSystem.getClip();

            clip.open(music);


            //AudioStream audios = new AudioStream(music);
            //AudioPlayer.player.start(audios);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }

        return clip;
    }
}


