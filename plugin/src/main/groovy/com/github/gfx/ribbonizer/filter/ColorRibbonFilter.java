package com.github.gfx.ribbonizer.filter;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.function.Consumer;

public class ColorRibbonFilter implements Consumer<BufferedImage> {

    static final boolean debug = Boolean.parseBoolean(System.getenv("RIBBONIZER_DEBUG"));

    final Color ribbonColor;

    final Color labelColor;

    String label;

    String fontName = "Default";

    int fontStyle = Font.PLAIN;

    public ColorRibbonFilter(String label, Color ribbonColor, Color labelColor) {
        this.label = label;
        this.ribbonColor = ribbonColor;
        this.labelColor = labelColor;
    }

    public ColorRibbonFilter(String label, Color ribbonColor) {
        this(label, ribbonColor, Color.WHITE);
    }

    static int calculateMaxLabelWidth(int y) {
        return (int) Math.sqrt(Math.pow(y, 2) * 2);
    }

    static void drawString(Graphics2D g, String str, int x, int y) {
        g.drawString(str, x, y);

        if (debug) {
            FontMetrics fm = g.getFontMetrics();
            Rectangle2D bounds = g.getFont().getStringBounds(str,
                    new FontRenderContext(g.getTransform(), true, true));

            g.drawRect(x, y - fm.getAscent(), (int) bounds.getWidth(), fm.getAscent());
        }
    }

    @Override
    public void accept(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();

        Graphics2D g = (Graphics2D) image.getGraphics();

//        g.setTransform(AffineTransform.getRotateInstance(Math.toRadians(-45)));

        // calculate the rectangle where the label is rendered
        FontRenderContext frc = new FontRenderContext(g.getTransform(), true, true);
        int maxLabelWidth = calculateMaxLabelWidth(0);
        g.setFont(getFont(maxLabelWidth, frc));
        Rectangle2D labelBounds = g.getFont().getStringBounds(label, frc);

        // draw the ribbon
        g.setColor(ribbonColor);
        g.fillRect(0, 0, width, (int) (labelBounds.getHeight()));

        // draw the label
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(labelColor);

        FontMetrics fm = g.getFontMetrics();

        drawString(g, label, 0, 0);

        g.dispose();
    }

    Font getFont(int maxLabelWidth, FontRenderContext frc) {
        int max = 32;
        int min = 0;
        int x = max;

        for (int i = 0; i < 10; i++) {
            int m = ((max + min) / 2);
            if (m == x) {
                break;
            }

            Font font = new Font(fontName, fontStyle, m);
            Rectangle2D labelBounds = font.getStringBounds(label, frc);
            int px = (int) labelBounds.getWidth();

            if (px > maxLabelWidth) {
                max = m;
            } else {
                min = m;
            }
            x = m;
        }
        return new Font(fontName, fontStyle, x);
    }
}