package com.raven.chart;

import java.awt.Color;
import java.awt.Graphics;
import java.text.DecimalFormat;

public class ItemChartPie extends javax.swing.JPanel {

    private final ModelChartPie data;

    public ItemChartPie(ModelChartPie data) {
        this.data = data;
        initComponents();
        setOpaque(false);
        DecimalFormat df = new DecimalFormat("đ #,##0.##");
        lbName.setText(data.getName());
        lbValues.setText(df.format(data.getValue()));
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lbName = new javax.swing.JLabel();
        lbValues = new javax.swing.JLabel();

        lbName.setForeground(new java.awt.Color(69, 69, 69));
        lbName.setText("Name");

        lbValues.setForeground(new java.awt.Color(69, 69, 69));
        lbValues.setText("Values");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(lbName)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lbValues)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lbValues, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(lbName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    @Override
    protected void paintComponent(Graphics g) {
        //  Create line
        g.setColor(new Color(240, 240, 240));
        g.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
        int margin = 8;
        int size = getHeight() - margin * 2;
        g.setColor(data.getColor());
        g.fillRect(margin, margin, size, size);
        super.paintComponent(g);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel lbName;
    private javax.swing.JLabel lbValues;
    // End of variables declaration//GEN-END:variables
}
