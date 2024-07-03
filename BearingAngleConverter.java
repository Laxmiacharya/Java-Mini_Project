import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

public class BearingAngleConverter extends JFrame {

    private JTextField inputField;
    private JTextField outputField;
    private JRadioButton wcbToQbRadioButton;
    private JRadioButton qbToWcbRadioButton;
    private int inputAngle;
    private int convertedAngle;
    private boolean isWcbToQb;

    public BearingAngleConverter() {
        setTitle("Angle and Bearing Converter");
        setSize(600, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        // Load the image file for icon
        ImageIcon icon = new ImageIcon("C:\\Users\\USER\\Desktop\\Internal\\java.jpg");

        // Set the icon for the JFrame
        setIconImage(icon.getImage());

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Angle Converter", new AngleConverterPanel());
        tabbedPane.addTab("Bearing Converter", new BearingConverterPanel());
        tabbedPane.addTab("Angle Figure", new AngleFigurePanel());

        add(tabbedPane);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BearingAngleConverter().setVisible(true));
    }

    class AngleConverterPanel extends JPanel {
        private JLabel degreeLabel, minuteLabel, secondLabel, resultLabel;
        private JTextField degreeField, minuteField, secondField, resultField;
        private JButton convertButton;

        public AngleConverterPanel() {
            degreeLabel = new JLabel("Degrees:");
            minuteLabel = new JLabel("Minutes:");
            secondLabel = new JLabel("Seconds:");
            resultLabel = new JLabel("Decimal Degrees:");

            degreeField = new JTextField(10);
            minuteField = new JTextField(10);
            secondField = new JTextField(10);
            resultField = new JTextField(15);
            resultField.setEditable(false); // Output field is read-only

            convertButton = new JButton("Convert");

            setLayout(new GridLayout(5, 2));

            add(degreeLabel);
            add(degreeField);
            add(minuteLabel);
            add(minuteField);
            add(secondLabel);
            add(secondField);
            add(resultLabel);
            add(resultField);
            add(new JLabel()); // Empty label for spacing
            add(convertButton);

            convertButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    convertAngle();
                }
            });
        }

        private void convertAngle() {
            try {
                int degrees = Integer.parseInt(degreeField.getText());
                int minutes = Integer.parseInt(minuteField.getText());
                int seconds = Integer.parseInt(secondField.getText());

                double decimalDegrees = degrees + (minutes / 60.0) + (seconds / 3600.0);

                resultField.setText(String.format("%.6f", decimalDegrees));
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid input. Please enter valid numbers.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    class BearingConverterPanel extends JPanel {
        private JLabel inputLabel, outputLabel;
        private JButton convertButton;

        public BearingConverterPanel() {
            inputLabel = new JLabel("Enter Angle:");
            outputLabel = new JLabel("Converted Angle:");
            inputField = new JTextField(10);
            outputField = new JTextField(15);
            outputField.setEditable(false); // Output field is read-only
            convertButton = new JButton("Convert");

            // Radio buttons for conversion type
            wcbToQbRadioButton = new JRadioButton("WCB to QB");
            qbToWcbRadioButton = new JRadioButton("QB to WCB");
            ButtonGroup conversionTypeGroup = new ButtonGroup();
            conversionTypeGroup.add(wcbToQbRadioButton);
            conversionTypeGroup.add(qbToWcbRadioButton);

            // Set default conversion type
            wcbToQbRadioButton.setSelected(true);

            // Set layout
            setLayout(new GridLayout(4, 2));

            // Add components to the panel
            add(inputLabel);
            add(inputField);
            add(wcbToQbRadioButton);
            add(qbToWcbRadioButton);
            add(outputLabel);
            add(outputField);
            add(convertButton);

            // Add action listener to the convert button
            convertButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    convertAngle();
                    repaint();
                }
            });
        }

        private void convertAngle() {
            try {
                // Get input angle
                double angle;
                String input = inputField.getText();
                String convertedAngleText = "";

                if (wcbToQbRadioButton.isSelected()) {
                    // WCB to QB Conversion
                    angle = Double.parseDouble(input);
                    inputAngle = (int) angle; // Use input angle for plotting
                    isWcbToQb = true;

                    if (angle >= 0 && angle <= 90) {
                        convertedAngleText = String.format("N%.6fE", angle); // Quadrant I: QB = N(angle)E
                        convertedAngle = (int) angle;
                    } else if (angle > 90 && angle <= 180) {
                        convertedAngleText = String.format("S%.6fE", 180 - angle); // Quadrant II: QB = S(angle)E
                        convertedAngle = (int) (180 - angle);
                    } else if (angle > 180 && angle <= 270) {
                        convertedAngleText = String.format("S%.6fW", angle - 180); // Quadrant III: QB = S(angle)W
                        convertedAngle = (int) (angle - 180);
                    } else if (angle > 270 && angle <= 360) {
                        convertedAngleText = String.format("N%.6fW", 360 - angle); // Quadrant IV: QB = N(angle)W
                        convertedAngle = (int) (360 - angle);
                    } else {
                        throw new IllegalArgumentException("WCB must be between 0° and 360°");
                    }

                } else if (qbToWcbRadioButton.isSelected()) {
                    // QB to WCB Conversion
                    String qbInput = input.trim().toUpperCase();
                    char direction1 = qbInput.charAt(0);
                    char direction2 = qbInput.charAt(qbInput.length() - 1);
                    double angleValue = Double.parseDouble(qbInput.substring(1, qbInput.length() - 1));
                    isWcbToQb = false;

                    if (angleValue < 0 || angleValue > 90) {
                        throw new IllegalArgumentException("Angle must be between 0° and 90°");
                    }

                    if (direction1 == 'N' && direction2 == 'E') {
                        convertedAngleText = String.format("%.6f°", angleValue); // Quadrant I
                        convertedAngle = (int) angleValue;
                    } else if (direction1 == 'S' && direction2 == 'E') {
                        convertedAngleText = String.format("%.6f°", 180 - angleValue); // Quadrant II
                        convertedAngle = (int) (180 - angleValue);
                    } else if (direction1 == 'S' && direction2 == 'W') {
                        convertedAngleText = String.format("%.6f°", 180 + angleValue); // Quadrant III
                        convertedAngle = (int) (180 + angleValue);
                    } else if (direction1 == 'N' && direction2 == 'W') {
                        convertedAngleText = String.format("%.6f°", 360 - angleValue); // Quadrant IV
                        convertedAngle = (int) (360 - angleValue);
                    } else {
                        throw new IllegalArgumentException("Invalid QB format. Use N(angle)E or S(angle)W.");
                    }
                }

                // Display the converted angle
                outputField.setText(convertedAngleText);

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid input. Please enter a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    class AngleFigurePanel extends JPanel {

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;

            int centerX = getWidth() / 2;
            int centerY = getHeight() / 2;
            int radius = 150;

            // Draw the coordinate axes
            g2d.setColor(Color.BLACK);
            g2d.drawLine(centerX, 0, centerX, getHeight()); // Y-axis
            g2d.drawLine(0, centerY, getWidth(), centerY);  // X-axis

            // Draw cardinal directions
            g2d.setFont(new Font("Arial", Font.BOLD, 12));
            g2d.drawString("N (0°)", centerX - 15, centerY - radius - 10);
            g2d.drawString("E (90°)", centerX + radius + 5, centerY + 5);
            g2d.drawString("S (180°)", centerX - 20, centerY + radius + 20);
            g2d.drawString("W (270°)", centerX - radius - 45, centerY + 5);

            int angleToDraw = isWcbToQb ? inputAngle : convertedAngle;

            // Draw a line from origin to the endpoint of the angle arc
            double endX = centerX + radius * Math.cos(Math.toRadians(90 - angleToDraw));
            double endY = centerY - radius * Math.sin(Math.toRadians(90 - angleToDraw));
            g2d.drawLine(centerX, centerY, (int) endX, (int) endY);

            // Draw the angle arc
            int startAngle = 90;  // Start from the north (90 degrees)
            int arcAngle = angleToDraw; // Draw anticlockwise (positive angle)

            g2d.setStroke(new BasicStroke(2));
            g2d.setColor(Color.BLUE);
            g2d.draw(new Arc2D.Double(centerX - radius, centerY - radius, 2 * radius, 2 * radius, startAngle, -arcAngle, Arc2D.OPEN));

            // Display the angle measure
            g2d.setColor(Color.RED);
            g2d.setFont(new Font("Arial", Font.BOLD, 16));
            String angleText = "Angle: " + angleToDraw + "°";
            FontMetrics fontMetrics = g2d.getFontMetrics();
            int textWidth = fontMetrics.stringWidth(angleText);
            g2d.drawString(angleText, centerX - textWidth / 2, centerY + radius + 40);
        }
    }
}