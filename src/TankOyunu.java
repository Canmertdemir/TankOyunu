import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import javax.imageio.ImageIO;

public class TankOyunu extends JPanel {
    // Tank 1 (Kullanıcı)
    private int tankX = 350;
    private int tankY = 250;
    private final int tankWidth = 50;
    private final int tankHeight = 50;
    private final int speed = 5;
    private int tankHealth = 3;

    // Tank 2 (Rakip)
    private int enemyTankX = 400;
    private int enemyTankY = 100;
    private int enemyTankHealth = 3;

    // Mermiler
    private List<Rectangle> bullets = new ArrayList<>();
    private List<Rectangle> enemyBullets = new ArrayList<>();
    private final int bulletWidth = 10;
    private final int bulletHeight = 5;

    // Görseller
    private Image tankImage;
    private Image enemyTankImage;
    private Image backgroundImage;

    public TankOyunu() {
        // Görselleri yükle
        try {
            tankImage = ImageIO.read(getClass().getResource("tank1.jpg"));
            enemyTankImage = ImageIO.read(getClass().getResource("enemy_tank.jpg"));
            backgroundImage = ImageIO.read(getClass().getResource("grass.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int keyCode = e.getKeyCode();

                if (keyCode == KeyEvent.VK_UP) {
                    tankY -= speed;
                } else if (keyCode == KeyEvent.VK_DOWN) {
                    tankY += speed;
                } else if (keyCode == KeyEvent.VK_LEFT) {
                    tankX -= speed;
                } else if (keyCode == KeyEvent.VK_RIGHT) {
                    tankX += speed;
                } else if (keyCode == KeyEvent.VK_SPACE) {
                    fireBullet();
                }
            }
        });
    }

    // Kullanıcı tankının ateşi
    private void fireBullet() {
        bullets.add(new Rectangle(tankX + tankWidth / 2 - bulletWidth / 2, tankY, bulletWidth, bulletHeight));
    }

    // Rakip tankının ateşi
    private void enemyFire() {
        enemyBullets.add(new Rectangle(enemyTankX + tankWidth / 2 - bulletWidth / 2, enemyTankY + tankHeight, bulletWidth, bulletHeight));
    }

    // Tankları hareket ettirme
    private void moveEnemyTank() {
        if (enemyTankY < getHeight() - tankHeight) {
            enemyTankY += 2; // Rakip tankını aşağıya doğru hareket ettir
        } else {
            enemyTankY = 100; // Başlangıç noktasına geri döner
        }
    }

    // Mermileri hareket ettirme
    private void moveBullets() {
        List<Rectangle> bulletsToRemove = new ArrayList<>();
        for (Rectangle bullet : bullets) {
            bullet.y -= 10;
            if (bullet.y < 0) {
                bulletsToRemove.add(bullet);
            }
        }
        bullets.removeAll(bulletsToRemove);
    }

    private void moveEnemyBullets() {
        List<Rectangle> enemyBulletsToRemove = new ArrayList<>();
        for (Rectangle bullet : enemyBullets) {
            bullet.y += 5;
            if (bullet.y > getHeight()) {
                enemyBulletsToRemove.add(bullet);
            }
        }
        enemyBullets.removeAll(enemyBulletsToRemove);
    }

    // Çarpışma kontrolü
    private void checkCollisions() {
        // Kullanıcı mermisinin rakibe çarpması
        for (Rectangle bullet : bullets) {
            if (bullet.intersects(new Rectangle(enemyTankX, enemyTankY, tankWidth, tankHeight))) {
                enemyTankHealth--;
                bullets.remove(bullet);
                break;
            }
        }

        // Rakip mermisinin kullanıcıya çarpması
        for (Rectangle bullet : enemyBullets) {
            if (bullet.intersects(new Rectangle(tankX, tankY, tankWidth, tankHeight))) {
                tankHealth--;
                enemyBullets.remove(bullet);
                break;
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Çimen arka planı
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);

        // Tank 1 (Kullanıcı)
        g.drawImage(tankImage, tankX, tankY, tankWidth, tankHeight, this);

        // Tank 2 (Rakip)
        g.drawImage(enemyTankImage, enemyTankX, enemyTankY, tankWidth, tankHeight, this);

        // Mermileri çiz
        g.setColor(Color.BLACK);
        for (Rectangle bullet : bullets) {
            g.fillRect(bullet.x, bullet.y, bullet.width, bullet.height);
        }
        for (Rectangle bullet : enemyBullets) {
            g.fillRect(bullet.x, bullet.y, bullet.width, bullet.height);
        }

        // Canları göster
        g.setColor(Color.WHITE);
        g.drawString("Kullanıcı Can: " + tankHealth, 10, 20);
        g.drawString("Rakip Can: " + enemyTankHealth, 10, 40);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Tank Oyunu");
        TankOyunu tankOyunu = new TankOyunu();
        frame.add(tankOyunu);
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        // Oyun döngüsü
        while (true) {
            tankOyunu.moveBullets();
            tankOyunu.moveEnemyBullets();
            tankOyunu.moveEnemyTank();
            tankOyunu.checkCollisions();
            tankOyunu.repaint();
            try {
                Thread.sleep(20); // 20 ms bekle
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
