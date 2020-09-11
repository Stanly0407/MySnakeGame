import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;


public class GameField extends JPanel implements ActionListener {
    private final int SIZE = 320; // пикселей
    private final int DOT_SIZE = 16; // размер пикслеей - сколько будет пикселей на одну ячейку змейки или яблочка
    private final int ALL_DOTS = 400;
    private Image dot;
    private Image apple;
    private int appleX;
    private int appleY;
    private int[] x = new int[ALL_DOTS];   //массив, чтобы хранить все положения змейки в определенный момент
    private int[] y = new int[ALL_DOTS];
    private int dots; // размер змейки в данный момент времени
    private Timer timer;

    // поля которые будут отвечать за текузее положение змейки
    private boolean left = false;
    private boolean right = true;
    private boolean up = false;
    private boolean down = false;
    private boolean inGame = true;


    public GameField() {
        setBackground(Color.BLACK);
        loadImages();
        initGame();
        addKeyListener(new FieldKeyListener());
        // добавим обработчик событий еще
        setFocusable(true); //чтобы взаимодействие было с игровым полем
    }

    // метод, который инициализирует начало игры начальные значения для змейки
    public void initGame() {
        dots = 3; // инициализируем начальное кол-во точек через цикл
        for (int i = 0; i < dots; i++) {
            x[i] = 48 - i * DOT_SIZE; // 48 потому что оно кратно 16, первое звено в змейке будет 48
            y[i] = 48;
        }
        //таймер, частота инициализации
        timer = new Timer(250, this);
        timer.start();
        createApple();

    }

    private void createApple() {
        appleX = new Random().nextInt(21)* DOT_SIZE; // от 20, т.к. 20 16-ти пиксельных квадратиков м. поместиться на игровом поле/
        // 20 не входит
        appleY = new Random().nextInt(21) * DOT_SIZE;
    }


    // метод для загрузки картинок, через image icon
    public void loadImages() {
        ImageIcon iiApple = new ImageIcon("apple.png");
        apple = iiApple.getImage();
        ImageIcon iiDot = new ImageIcon("rectangle.png");
        dot = iiDot.getImage();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // перерисовка всего стандартного (за кулисами вся техническая перерисовка)
        // здесь перерисовка всего того, что касается игры
        if(inGame) {
            g.drawImage(apple, appleX, appleY, this); // рисуем яблоко
            for (int i = 0; i < dots; i++){
                g.drawImage(dot, x[i], y[i], this);
            }
        } else {
            String gameOver = "Game Over!";
            Font f = new Font("Arial",  Font.BOLD, 14);
            g.setColor(Color.white);
            g.setFont(f);
            g.drawString(gameOver, 125, SIZE/2);
        }


    }

    public void move() {
        // здесь будет происходить логическая перерисовка точек, т.е. они будут сдвигаться в том массиве х и у, кот. мы задали для хранения
        for (int i = dots; i > 0; i--) {
            x[i] = x[i - 1]; // т.е. мы сдвигаем, вторая точка, становится на позицию третей, четвертая на позицию пятой и так движение
            y[i] = y[i - 1];
            // т.е. все точки которые не голова мы переместили на предыдущие позиции
        }
        // а для головы: мы поместим точку туда куда указано направление
        if (left) {
            x[0] -= DOT_SIZE;
        }
        if (right) {
            x[0] += DOT_SIZE;
        }
        if (up) {
            y[0] -= DOT_SIZE;
        }
        if (down) {
            y[0] += DOT_SIZE;
        }

    }

    private void checkApple() {
        if(x[0] == appleX && y[0] == appleY){ // если голова змейки (это ее координатики) равны координатам яблока,
            dots++; // тогда мы двинемся и создадим яблоко
            createApple(); // создаем яблоко
        }
    }

    private void checkCollisions() {
        // проверка не столкнулась ли змейка сама с собой
        // это возможно если размер змейки больше 4 ячеек
        for (int i = dots; i > 0; i--) {
            if(i > 4 && x[0] == x[i] && y[0] == y[i]){
                inGame = false; // проигрыш
            }
        }
        // далее проверка на выход за пределы размеров поля
        if(x[0]>SIZE){
            inGame = false;
        }
        if(x[0]<0){
            inGame = false;
        }
        if(y[0]>SIZE){
            inGame = false;
        }
        if(y[0]<0){
            inGame = false;
        }

    }

    public void actionPerformed(ActionEvent actionEvent) {
        // будет вызываться каждый раз когда будет тикать таймер
        if (inGame) {

            // если встретили яблоко
            checkApple();

            //проверка на столкновение с бордюром (краями игрового поля)
            checkCollisions();

            //после проверки на столкновение с бордюрами, рамками, встречу с яблоком... и .т.д.
            // перерисовываваем поле через repaint(); (использует paint component) - используется в свинге
            move();

            repaint();
        }
    }


// Обработка нажатия клавиш
    class FieldKeyListener extends KeyAdapter {
    @Override
    public void keyPressed(KeyEvent e) {
        super.keyPressed(e);
        int key  = e.getKeyCode();
        // если вы двигаетесь вправо вы не можете двигаться влево и т.п.логика.
        if(key == KeyEvent.VK_LEFT && ! right){
            left = true;
            up = false;
            down = false;
        }

        if(key == KeyEvent.VK_RIGHT && ! left){
            right = true;
            up = false;
            down = false;
        }

        if(key == KeyEvent.VK_UP && ! down){
            up = true;
            right = false;
            left = false;
        }

        if(key == KeyEvent.VK_DOWN && ! up){
            down = true;
            right = false;
            left = false;
        }

    }
}

}
