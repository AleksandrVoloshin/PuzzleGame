import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Game extends JFrame {

    private final int DESIRED_WIDTH = 400;
    private final int NUMBER_OF_BUTTONS = 12;

    private JPanel panel;
    private BufferedImage source;
    private BufferedImage resized;
    private Image image;
    private PuzzleMyButton lastButton;
    private int width, height;

    private List<Point> solution;
    private List<PuzzleMyButton> buttons;

    public Game() {
        initUI();
    }

    public void initUI() {
        solution = new ArrayList<>();
        solution.add(new Point(0, 0));
        solution.add(new Point(0, 1));
        solution.add(new Point(0, 2));
        solution.add(new Point(1, 0));
        solution.add(new Point(1, 1));
        solution.add(new Point(1, 2));
        solution.add(new Point(2, 0));
        solution.add(new Point(2, 1));
        solution.add(new Point(2, 2));
        solution.add(new Point(3, 0));
        solution.add(new Point(3, 1));
        solution.add(new Point(3, 2));

        buttons = new ArrayList<>();

        panel = new JPanel();
        panel.setBorder(BorderFactory.createLineBorder(Color.gray));
        panel.setLayout(new GridLayout(4, 3, 0, 0));

        try {
            source = loadImage();
            int h = getNewHeight(source.getWidth(), source.getHeight());
            resized = resizeImage(source, DESIRED_WIDTH, h, BufferedImage.TYPE_INT_ARGB);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Could not load image", "Error", JOptionPane.ERROR_MESSAGE);
        }

        width = resized.getWidth(null);
        height = resized.getHeight(null);

        add(panel, BorderLayout.CENTER);

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 3; j++) {
                image = createImage(new FilteredImageSource(resized.getSource(), new CropImageFilter(j * width / 3, i * height / 4, width / 3, height / 4)));

                var button = new PuzzleMyButton(image);
                button.putClientProperty("position", new Point(i, j));

                if (i == 3 && j == 2) {
                    lastButton = new PuzzleMyButton();
                    lastButton.setBorderPainted(false);
                    lastButton.setContentAreaFilled(false);
                    lastButton.setLastButton();
                    lastButton.putClientProperty("position", new Point(i, j));
                } else {
                    buttons.add(button);
                }
            }
        }

        Collections.shuffle(buttons);
        buttons.add(lastButton);

        for (int i = 0; i < NUMBER_OF_BUTTONS; i++) {

            var btn = buttons.get(i);
            panel.add(btn);
            btn.setBorder(BorderFactory.createLineBorder(Color.gray));
            btn.addActionListener(new ClickAction());
        }

        pack();

        setTitle("Puzzle");
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private int getNewHeight(int w, int h) {

        double ratio = DESIRED_WIDTH / (double) w;
        int newHeight = (int) (h * ratio);
        return newHeight;
    }

    private BufferedImage loadImage() throws IOException {
        var bimg = ImageIO.read(new File("caputmortuu.jpg"));
        return bimg;
    }

    private BufferedImage resizeImage(BufferedImage originImage, int width, int height, int type) {
        var resizeImage = new BufferedImage(width, height, type);
        var g = resizeImage.createGraphics();
        g.drawImage(originImage, 0, 0, width, height, null);
        g.dispose();
        return resizeImage;
    }

    private class ClickAction extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent e) {

            checkButton(e);
            checkSolution();
        }

        private void checkButton(ActionEvent e) {

            int lidx = 0;

            for (PuzzleMyButton button : buttons) {
                if (button.isLastButton()) {
                    lidx = buttons.indexOf(button);
                }
            }

            var button = (JButton) e.getSource();
            int bidx = buttons.indexOf(button);

            if ((bidx - 1 == lidx) || (bidx + 1 == lidx) || (bidx - 3 == lidx) || (bidx + 3 == lidx)) {
                Collections.swap(buttons, bidx, lidx);
                updateButtons();
            }
        }

        private void updateButtons() {
            panel.removeAll();

            for (JComponent bth : buttons) {
                panel.add(bth);
            }

            panel.validate();
        }
    }

    private void checkSolution() {
        var current = new ArrayList<Point>();

        for (JComponent bth : buttons) {
            current.add((Point) bth.getClientProperty("position"));
        }

        if (compareList(solution, current)) {
            JOptionPane.showMessageDialog(panel, "Finished", "Congratulation", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public static boolean compareList(List ls1, List ls2) {
        return ls1.toString().contentEquals(ls2.toString());
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            var puzzle = new Game();
            puzzle.setVisible(true);
        });
    }
}