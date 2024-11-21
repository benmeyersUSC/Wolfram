import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class CellularAutomata {
    private static final int ROW_LENGTH = 927;
    private static final int ITERATIONS = 729;
    private static final int PIXEL_SIZE = 1; // Size of each cell in pixels
    private static final int IMAGE_HEIGHT = 927;

    private int RULE_NUM;
    private boolean DOUBLE = false;
    private boolean RAN = false;
    private final String bin_rule;
    private String binRule;
    private final ArrayList<char[]> output;

    public CellularAutomata(int rule) {
        int rule1 = Math.min(rule, 256);
        RULE_NUM = rule1;
        this.bin_rule = int_to_byte(rule1);
        this.binRule = this.bin_rule;
        char[] origin = new char[ROW_LENGTH];
        this.output = new ArrayList<>(ITERATIONS);


        Arrays.fill(origin, ' ');

        if ((rule1 % 8 == 4) && ((rule1 / 8 - 3) % 4 == 0)) {
            origin[0] = '*';
            DOUBLE = true;
        }
        else if (rule1 == 110){origin[ROW_LENGTH-1] = '*'; DOUBLE = true;}
        else {
            origin[ROW_LENGTH / 2] = '*';
        }

        // Add origin to output
        this.output.add(origin);
    }

    public void run_switch() {
        int iter = DOUBLE ? ITERATIONS * 2 : ITERATIONS;
        int curr_rule = RULE_NUM;

        Random random = new Random();
        for (int i = 1; i < iter; i++) {
            char[] new_line = new char[ROW_LENGTH];
            char[] prev_line = output.get(i - 1);

            // Generate a new random rule for each iteration
            int next_rule = random.nextInt((curr_rule+9) - (curr_rule-9)) + (curr_rule-9);
            binRule = int_to_byte(random.nextInt(next_rule));
            if (binRule.length() < 8){binRule = "00000000";}

            for (int j = 0; j < ROW_LENGTH; j++) {
                char left = (j > 0) ? prev_line[j - 1] : ' ';
                char me = prev_line[j];
                char right = (j < ROW_LENGTH - 1) ? prev_line[j + 1] : ' ';

                new_line[j] = ' ';
                for (int caseNum = 0; caseNum < 8; caseNum++) {
                    if (binRule.charAt(caseNum) == '1' && checkCase(caseNum, left, me, right)) {
                        new_line[j] = '*';
                        break;
                    }
                }
            }

            output.add(new_line);
        }
        RAN = true;
    }
    public void run() {
        int iter = DOUBLE ? ITERATIONS * 2 : ITERATIONS;

        for (int i = 1; i < iter; i++) {
            char[] new_line = new char[ROW_LENGTH];
            char[] prev_line = output.get(i - 1);

            for (int j = 0; j < ROW_LENGTH; j++) {
                char left = (j > 0) ? prev_line[j - 1] : ' ';
                char me = prev_line[j];
                char right = (j < ROW_LENGTH - 1) ? prev_line[j + 1] : ' ';

                new_line[j] = ' ';
                for (int caseNum = 0; caseNum < 8; caseNum++) {
                    if (bin_rule.charAt(caseNum) == '1' && checkCase(caseNum, left, me, right)) {
                        new_line[j] = '*';
                        break;
                    }
                }
            }

            output.add(new_line);
        }
        RAN = true;
    }
    public void display() {
        run();
        for (char[] row : output) {
            System.out.println(new String(row));
        }
    }
    public void save() {
        String file_out = "../rule" + RULE_NUM + ".txt";
        if (!RAN) {
            run();
        }

        try (PrintWriter pw = new PrintWriter(new FileOutputStream(file_out))) {
            StringBuilder sb = new StringBuilder(ROW_LENGTH);
            for (char[] line : output) {
                sb.setLength(0);  // Clear the StringBuilder
                for (char c : line) {
                    sb.append(c);
                }
                pw.println(sb);
            }
        } catch (Exception e) {
            System.err.println("Error writing to file: " + file_out);
            e.printStackTrace();
        }
    }
    public static String int_to_byte(int num){
        if (num == 0){return "0";}

        StringBuilder res = new StringBuilder();
        while (num > 0){
            int new_digit = num % 2;
            res.insert(0, new_digit);
            num /= 2;
        }
        while (res.length() < 8){
            res.insert(0, "0");
        }
        return res.toString();
    }
    public static boolean checkCase(int caseNumber, char l, char m, char r) {
        switch (caseNumber) {
            case 0: return l == '*' && m == '*' && r == '*';
            case 1: return r == ' ' && l == m && l == '*';
            case 2: return m == ' ' && l == r && l == '*';
            case 3: return l == '*' && m == r && m == ' ';
            case 4: return l == ' ' && m == r && m == '*';
            case 5: return m == '*' && l == r && l == ' ';
            case 6: return r == '*' && l == m && l == ' ';
            case 7: return l == ' ' && m == ' ' && r == ' ';
            default: throw new IllegalArgumentException("Invalid case number: " + caseNumber);
        }
    }
    public void saveAsImage() {
        if (!RAN) {
            run();
        }

        int width = ROW_LENGTH * PIXEL_SIZE;
        int height = Math.min(output.size(), IMAGE_HEIGHT) * PIXEL_SIZE;

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();

        // Set background to white
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, width, height);

        // Draw cells
        g2d.setColor(Color.BLACK);
        for (int y = 0; y < height / PIXEL_SIZE; y++) {
            char[] row = output.get(y);
            for (int x = 0; x < ROW_LENGTH; x++) {
                if (row[x] == '*') {
                    g2d.fillRect(x * PIXEL_SIZE, y * PIXEL_SIZE, PIXEL_SIZE, PIXEL_SIZE);
                }
            }
        }

        g2d.dispose();

        // Save the image
        try {
            File outputfile = new File("../rule" + RULE_NUM + ".png");
            ImageIO.write(image, "png", outputfile);
            System.out.println("Image saved successfully: " + outputfile.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Error saving image: " + e.getMessage());
        }
    }

}