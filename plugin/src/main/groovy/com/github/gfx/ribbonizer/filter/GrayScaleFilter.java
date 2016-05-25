package com.github.gfx.ribbonizer.filter;

import java.awt.image.BufferedImage;
import java.util.function.Consumer;

public class GrayScaleFilter implements Consumer<BufferedImage> {

    static int toGray(int color) {
        int a = (color & 0xFF000000);
        int r = (color & 0x00FF0000) >> 16;
        int g = (color & 0x0000FF00) >> 8;
        int b = (color & 0x000000FF);

        int c = (int) ((2.0 * r + 4.0 * g + b) / 7.0);
        return a | (c << 16) | (c << 8) | c;
    }

    @Override
    public void accept(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int color = image.getRGB(x, y);
                image.setRGB(x, y, toGray(color));
            }
        }
    }

}
