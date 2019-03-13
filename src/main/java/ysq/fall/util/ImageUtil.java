package ysq.fall.util;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.imageio.ImageIO;

public class ImageUtil {

    public static void save(InputStream is, OutputStream os, String ext, float w, float h) throws IOException {

        BufferedImage oldImage = ImageIO.read(is);
        int old_w = oldImage.getWidth();
        int old_h = oldImage.getHeight();
        int new_w = 0;
        int new_h = 0;
        float temp;
        if (old_w / old_h > w / h) {
            temp = old_w / w;
        } else {
            temp = old_h / h;
        }
        new_w = Math.round(old_w / temp);
        new_h = Math.round(old_h / temp);

        BufferedImage newImage;
        String type;
        if (ext.toUpperCase().equals("PNG")) {
            newImage = new BufferedImage(new_w, new_h, BufferedImage.TYPE_INT_ARGB_PRE);
            type = "PNG";
        } else {
            newImage = new BufferedImage(new_w, new_h, BufferedImage.TYPE_INT_RGB);
            type = "JPEG";
        }
        Graphics2D g2d = newImage.createGraphics();
        g2d.drawImage(oldImage, 0, 0, new_w, new_h, null);
        g2d.dispose();
        ImageIO.write(newImage, type, os);
    }

    public static void delete(String url, String oldPhoto){
        if (oldPhoto != null && !oldPhoto.equals("")) {
            File file = new File(url, oldPhoto);
            if (file.exists()) {
                file.delete();
            }
        }
    }

    public static String save(InputStream is, String url, String ext, float w, float h)
            throws IOException {
        if (is != null) {
            String name = String.valueOf(System.currentTimeMillis());
            String photo = name + "." + ext;
            String path = url + "/" + photo;
            try {
                save(is, new FileOutputStream(path), ext, w, h);
            } finally {
                try {
                    is.close();
                } catch (IOException ex) {
                    throw ex;
                }
            }
            return photo;
        }
        return null;
    }
}
