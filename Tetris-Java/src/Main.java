import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;

public class Main extends JFrame implements KeyListener {
    public static int[][] map = new int[23][10];
    public static int[][] newmap = new int[23][10];
    private Map<Integer, int[][]> figures = new HashMap<>();
    private Figure currentFigure;
    public static boolean newgame = false;
    public static int rotationcounter = 0;
    public Main() {
        setTitle("Tetris");
        setSize(240, 460);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        addKeyListener(this);
        setVisible(true);
        //writing figures in a dict
        figures.put(0, new int[][]{{1,1},{1,1}}); // []
        figures.put(1, new int[][]{{1,0},{1,0},{1,0},{1,0}}); // |
        figures.put(2, new int[][]{{0,1,1},{1,1,0}}); // reverbed z
        figures.put(3, new int[][]{{1,1,0},{0,1,1}}); // z
        figures.put(4, new int[][]{{1,1,1},{0,0,1}}); // reverbed L
        figures.put(5, new int[][]{{1,1,1},{1,0,0}}); // L
        figures.put(6, new int[][]{{1,1,1},{0,1,0}}); // T


        //creating figure and starting the game
        currentFigure = newfigure(figures);
        Timer gameTimer = new Timer(200, e -> {
            if(newgame) {
                newgame = false;
                map = new int[23][10];
                newmap = new int[23][10];
            }
            repaint();
            currentFigure.down(figures);

        });
        gameTimer.start();

    }

    @Override
    public void paint(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());
        g.setColor(Color.RED);
        //paint figures
        for (int i = 3; i < 23; i++) {
            for (int j = 0; j < 10; j++) {
                if (newmap[i][j] == 1) {
                    g.fillRect(20 * j + 20, 20 * (i-3) + 40, 20, 20);
                }
            }
        }
    }
    //key press
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_A) {
            currentFigure.changepos(-1);
        }
        if (e.getKeyCode() == KeyEvent.VK_D) {
            currentFigure.changepos(1);
        }
        if (e.getKeyCode() == KeyEvent.VK_R) {
            currentFigure.rotatefigure();
        }
        repaint();
    }

    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}

    public static void main(String[] args) {
        new Main();
    }
    //class Figure
    public static class Figure {
        int[][] figure;
        int[] pos;
        public Figure(int[][] figure, int[] pos) {this.figure = figure; this.pos = pos; }
        // writing figure on a newmap
        void writenewmap(){
            for(int i = 0; i < newmap.length; i++) {
                System.arraycopy(map[i], 0, newmap[i], 0, map[i].length);
            }
            int fig_i = 0;
            for(int i = pos[0]; i < pos[0] + figure.length; i++){
                int fig_j = 0;
                for(int j = pos[1]; j < pos[1]+ figure[0].length; j++){
                    if (map[i][j] == 0) {
                        newmap[i][j] = figure[fig_i][fig_j];
                    }
                    fig_j++;
                }
                fig_i++;
            }
        }
        //func of rotation of the figure
        void rotatefigure() {
            int[][] rotatedfigure = new int[figure[0].length][figure.length];
            //making rotated copy of figure in rotatedfigure
            for (int i = 0; i < figure[0].length; i++) {
                for (int j = 0; j < figure.length; j++) {
                    rotatedfigure[i][j] = figure[figure.length - 1 - j][i];
                }
            }
            //checking the limits
            if (pos[1] + rotatedfigure[0].length > map[0].length) {
                this.pos[1] = map[0].length - rotatedfigure[0].length;
            }
            else if(pos[1] < 0) {
                this.pos[1] = 0;
            }
            //checking collision
            boolean canRotate = true;
            int fig_i = 0;
            if (pos[0] + rotatedfigure.length > map.length || pos[1] < 0) canRotate = false;
            for (int i = pos[0]; i < pos[0] + rotatedfigure.length; i++) {
                int fig_j = 0;
                for (int j = pos[1]; j < pos[1] + rotatedfigure[0].length; j++) {
                    if (rotatedfigure[fig_i][fig_j] == 1 && map[i][j] == 1) {
                        canRotate = false;
                        break;
                    }
                    fig_j++;
                }
                if (!canRotate) break;
                fig_i++;

            }
            //finally rotating
            if (canRotate) {
                this.figure = rotatedfigure;
            }
            writenewmap();
        }
        //moving figures down
        void down(Map<Integer, int[][]> figures) {
            boolean canMoveDown = true;
            //checking ground
            if (pos[0] >= 23 - figure.length) {
                canMoveDown = false;
                //checking ground for |
                if (figure.length == 2 && figure[0].length == 4 && figure[1][0] == 0 ) {
                    this.pos[0] -= 1;
                    this.figure = new int[][]{{0,0,0,0}, {1,1,1,1,}};
                    canMoveDown = true;
                }

            }
            //checking collision
            if(canMoveDown) {
                int fig_i = 0;
                for(int i = pos[0]; i < pos[0] + figure.length; i++){
                    int fig_j = 0;
                    for(int j = pos[1]; j < pos[1]+ figure[0].length; j++){
                        if (figure[fig_i][fig_j] == 1 && map[i+1][j] == 1) {
                            canMoveDown = false;
                            break;
                        }
                        fig_j++;
                    }
                    if (!canMoveDown) break;
                    fig_i++;
                }
            }
            //finally move down
            if (canMoveDown) {
                pos[0]++;
            //if cant move down creating new figure checking a line and checking a death
            } else {
                for (int i = 0; i < map.length; i++) {
                    System.arraycopy(newmap[i], 0, map[i], 0, map[i].length);
                }
                Figure f = newfigure(figures);
                this.figure = f.figure;
                this.pos = f.pos;
                checkline();
                newgame = checkdeath();
            }

            writenewmap();
        }
        //func death
        boolean checkdeath() {
            for(int i = 0; i < map[0].length; i++) {
                if(map[3][i] == 1) {
                    return  true;
                }
            }
            return false;
        }
        //fung for figure moving
        void changepos(int changeposx) {
            boolean test = true;
            boolean doReverb = false;
            //checking limits for |
            if(figure.length == 4 && figure[0].length == 2) {
                if (pos[1] + changeposx == map[0].length - 1 && figure[0][1] == 0) {
                    this.pos[1] -= changeposx;
                    this.figure = new int[][]{{0, 1}, {0, 1}, {0, 1}, {0, 1}};
                    doReverb = true;
                }
                else if(pos[1] + changeposx == 0 && figure[0][0] == 0) {
                    this.pos[1] -= changeposx;
                    this.figure = new int[][]{{1, 0}, {1, 0}, {1, 0}, {1, 0}};
                    doReverb = true;
                }

            }
            //collision
            int fig_i = 0;
            for(int i = pos[0]; i < pos[0] + figure.length; i++){
                if (test) {
                    int fig_j = 0;
                    for (int j = pos[1]; j < pos[1] + figure[0].length; j++) {
                        if (j + changeposx >= 0 && j + changeposx < map[0].length){
                            if (figure[fig_i][fig_j] == 1 && map[i][j + changeposx] == 1) {
                                test = false;
                                break;
                            }
                        }
                        fig_j++;
                    }
                    fig_i++;
                }
                else break;
            }
            //if not collision test making | back
            if (!test && doReverb) {
                if (figure[0][0] == 0) {
                    this.figure = new int[][] {{1,0},{1,0},{1,0},{1,0}};
                }
                else {
                    this.figure = new int[][]{{0, 1}, {0, 1}, {0, 1}, {0, 1}};
                }
                this.pos[1] += changeposx;
            }
            //if everything is ok moving figure
            if(pos[1] + changeposx >= 0 && pos[1] + changeposx <= map[0].length - figure[0].length && test){
                this.pos[1] = pos[1] + changeposx;}
            writenewmap();
        }
        //func for checking the line to clear it
        void checkline() {
            int[] line = new int[map[0].length];
            Arrays.fill(line, 1);
            for(int i = 0; i < map.length; i++) {
                if(Arrays.equals(map[i], line)){
                    for (int j = i; j > 0; j--) {
                        map[j] = Arrays.copyOf(map[j-1], map[j-1].length);
                    }
                }
            }
        }
    }
    //func for creating a new figure
    public static Figure newfigure(Map<Integer, int[][]> figures) {
        Random rand = new Random();
        int[][] figure = figures.get(rand.nextInt(7));
        int[] pos = {4 - figure.length, 4 - (figure[0].length - 1)/2};
        return new Figure(figure, pos);
    }

    //class for a timer
    public class TimerExample {
        public static void main(String[] args) {
            Timer timer = new Timer(500, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.out.println("Tik!");
                }
            });
            timer.start();

            JOptionPane.showMessageDialog(null, "Закрыть?");
        }
    }
}
