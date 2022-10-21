/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/***
 * Yige Li
 * CMPSC 221
 * Java 2D Application
 */

package java2ddrawingapplication;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import javax.swing.*;

/**
 * @author acv
 */
public class DrawingApplicationFrame extends JFrame {

    // Create the panels for the top of the application. One panel for each
    JPanel topPanel = new JPanel();
    JPanel firstLine = new JPanel();
    JPanel secondLine = new JPanel();

    // line and one to contain both of those panels.

    // create the widgets for the firstLine Panel.
    JLabel shapeLabel = new JLabel("Shape: ");
    JComboBox shapeComboBox = new JComboBox<>(new String[]{"Line", "Oval", "Rectangle"});

    JButton color1Button = new JButton("1st Color..");
    JButton color2Button = new JButton("2nd Color...");
    JButton undoButton = new JButton("Undo");
    JButton clearButton = new JButton("Clear");

    //create the widgets for the secondLine Panel.
    JLabel optionsLabel = new JLabel("Options: ");

    JCheckBox filledCheckBox = new JCheckBox("Filled");
    JCheckBox useGradientCheckBox = new JCheckBox("Use Gradient");
    JCheckBox dashedCheckBox = new JCheckBox("Dashed");

    JLabel lineWidthLabel = new JLabel("Line Width: ");
    JSpinner lineWidthField = new JSpinner(new SpinnerNumberModel(4, 3, 100, 1));

    JLabel dashLengthLabel = new JLabel("Dash Length: ");
    JSpinner dashLengthField = new JSpinner(new SpinnerNumberModel(15, 3, 100, 1));

    // Variables for drawPanel.
    DrawPanel drawPanel = new DrawPanel();
    ArrayList<MyShapes> allShapes = new ArrayList<MyShapes>();
    float dashLength = 15;
    int lineWidth = 4;
    Color color1 = Color.BLACK;
    Color color2 = Color.BLACK;

    // add status label
    JLabel statusLabel = new JLabel();


    // Constructor for DrawingApplicationFrame
    public DrawingApplicationFrame() {
        this.setLayout(new BorderLayout());
        topPanel.setLayout(new BorderLayout());

        // add widgets to panels
        // firstLine widgets
        firstLine.add(shapeLabel);
        firstLine.add(shapeComboBox);
        firstLine.add(color1Button);
        firstLine.add(color2Button);
        firstLine.add(undoButton);
        firstLine.add(clearButton);
        firstLine.setBackground(Color.decode("#99FFFF"));

        // secondLine widgets
        secondLine.add(optionsLabel);
        secondLine.add(filledCheckBox);
        secondLine.add(useGradientCheckBox);
        secondLine.add(dashedCheckBox);
        secondLine.add(lineWidthLabel);
        secondLine.add(lineWidthField);
        secondLine.add(dashLengthLabel);
        secondLine.add(dashLengthField);
        secondLine.setBackground(Color.decode("#99FFFF"));

        // add top panel of two panels
        topPanel.add(firstLine, BorderLayout.NORTH);
        topPanel.add(secondLine, BorderLayout.SOUTH);

        // add topPanel to North, drawPanel to Center, and statusLabel to South
        this.add(topPanel, BorderLayout.NORTH);
        this.add(drawPanel, BorderLayout.CENTER);
        this.add(statusLabel, BorderLayout.SOUTH);

        //add listeners and event handlers
        color1Button.addActionListener(listener -> {
            Color tp = color1;
            color1 = JColorChooser.showDialog(null, "Select Color 1", color1);
            if (color1 == null) {
                color1 = tp;
            }
        });

        color2Button.addActionListener(listener -> {
            Color tp = color2;
            color2 = JColorChooser.showDialog(null, "Select Color 2", color2);
            if (color2 == null) {
                color2 = tp;
            }
        });

        undoButton.addActionListener(listener -> {
            if (allShapes.size() > 0) {
                allShapes.remove(allShapes.size() - 1);
                drawPanel.repaint();
            }
        });

        clearButton.addActionListener(listener -> {
            allShapes.clear();
            drawPanel.repaint();
        });
    }

    // Create event handlers, if needed

    // Create a private inner class for the DrawPanel.
    private class DrawPanel extends JPanel {
        Point initialPoint;
        ArrayList<MyShapes> tpShapes = new ArrayList<MyShapes>();

        public DrawPanel() {
            setBackground(Color.decode("#FFFFFF"));
            addMouseListener(new MouseHandler());
            addMouseMotionListener(new MouseHandler());
        }

        private MyShapes buildShape(Point start, Point end) {
            BasicStroke stroke;
            if (dashedCheckBox.isSelected()) {
                stroke = new BasicStroke(lineWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10, new float[]{Float.parseFloat(dashLengthField.getValue().toString())}, 0);
            } else {
                stroke = new BasicStroke(lineWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
            }

            Paint paint = useGradientCheckBox.isSelected()
                    ? new GradientPaint(0, 0, color1, 50, 50, color2, true)
                    : new GradientPaint(0, 0, color1, 50, 50, color1, true);

            switch (shapeComboBox.getSelectedItem().toString()) {
                case "Line":
                    return new MyLine(start, end, paint, stroke);
                case "Rectangle":
                    return new MyRectangle(start, end, paint, stroke, filledCheckBox.isSelected());
                case "Oval":
                    return new MyOval(start, end, paint, stroke, filledCheckBox.isSelected());
                default:
                    return null;
            }
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;

            //loop through and draw each shape in the shapes arraylist
            for (MyShapes shape : allShapes) {
                shape.draw(g2d);
            }

            for (MyShapes shape : tpShapes) {
                shape.draw(g2d);
            }
            tpShapes.clear();
        }


        private class MouseHandler extends MouseAdapter implements MouseMotionListener {
            public void mousePressed(MouseEvent event) {
                initialPoint = event.getPoint();
            }

            public void mouseReleased(MouseEvent event) {
                MyShapes currentShape = buildShape(initialPoint, event.getPoint());
                if (currentShape != null) {
                    allShapes.add(currentShape);
                    drawPanel.repaint();
                }
            }

            @Override
            public void mouseDragged(MouseEvent event) {
                statusLabel.setText("(" + event.getX() + "," + event.getY() + ")");
                MyShapes currentShape = buildShape(initialPoint, event.getPoint());
                if (currentShape != null) {
                    tpShapes.add(currentShape);
                    drawPanel.repaint();
                }
            }

            @Override
            public void mouseMoved(MouseEvent event) {
                statusLabel.setText("(" + event.getX() + "," + event.getY() + ")");
            }
        }
    }
}
