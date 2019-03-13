package ysq.fall.util;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

/**
 * *****************************************************************************
 * Description: 图片水印工具类 Copyright: Copyright (c) 2011 Company: Founder Project:
 * CMS
 *
 * @Author taoxg
 * @version 1.0
 * @See HISTORY 2011-9-27 taoxg create
 *****************************************************************************
 */
public class ImageMarkLogoUtil {

    // 水印透明度 
    private float alpha = 0.5f;
    // 水印横向位置
    private int positionWidth = 150;
    // 水印纵向位置
    private int positionHeight = 300;
    // 水印文字字体
    private Font font = new Font("宋体", Font.BOLD, 30);
    // 水印文字颜色
    private Color color = Color.red;

    /**
     * 给图片添加水印图片
     *
     * @param iconPath 水印图片路径
     * @param srcImgPath 源图片路径
     * @param targerPath 目标图片路径
     */
    public void markImageByIcon(String iconPath, String srcImgPath,
            String targerPath) {
        markImageByIcon(iconPath, srcImgPath, targerPath, null);
    }
    
    
    

    /**
     * 给图片添加水印图片、可设置水印图片旋转角度
     *
     * @param iconPath 水印图片路径
     * @param srcImgPath 源图片路径
     * @param targerPath 目标图片路径
     * @param degree 水印图片旋转角度
     */
    public void markImageByIcon(String iconPath, String srcImgPath,
            String targerPath, Integer degree) {
        OutputStream os = null;
        try {

            Image srcImg = ImageIO.read(new File(srcImgPath));

            BufferedImage buffImg = new BufferedImage(srcImg.getWidth(null),
                    srcImg.getHeight(null), BufferedImage.TYPE_INT_RGB);

            // 1、得到画笔对象
            Graphics2D g = buffImg.createGraphics();

            // 2、设置对线段的锯齿状边缘处理
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.drawImage(srcImg.getScaledInstance(srcImg.getWidth(null), srcImg.getHeight(null), Image.SCALE_SMOOTH), 0, 0, null);
            // 3、设置水印旋转
            if (null != degree) {
                g.rotate(Math.toRadians(degree), (double) buffImg.getWidth() / 2, (double) buffImg.getHeight() / 2);
            }

            // 4、水印图片的路径 水印图片一般为gif或者png的，这样可设置透明度
            ImageIcon imgIcon = new ImageIcon(iconPath);

            // 5、得到Image对象。
            Image img = imgIcon.getImage();

            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, getAlpha()));

            // 6、水印图片的位置
            g.drawImage(img, getPositionWidth(), getPositionHeight(), null);
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
            // 7、释放资源
            g.dispose();

            // 8、生成图片
            os = new FileOutputStream(targerPath);
            ImageIO.write(buffImg, "JPG", os);

            System.out.println("图片完成添加水印图片");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != os) {
                    os.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 给图片添加水印文字
     *
     * @param logoText 水印文字
     * @param srcImgPath 源图片路径
     * @param targerPath 目标图片路径
     */
    public BufferedImage markImageByText(String logoText, Image srcImg) {
        return markImageByText(logoText, srcImg, null);
    }

    /**
     * 给图片添加水印文字、可设置水印文字的旋转角度
     *
     * @param logoText
     * @param srcImgPath
     * @param targerPath
     * @param degree
     */
    public BufferedImage markImageByText(String logoText, Image srcImg, Integer degree) {

        try {
            // 1、源图片
            
            BufferedImage buffImg = new BufferedImage(srcImg.getWidth(null), srcImg.getHeight(null), BufferedImage.TYPE_INT_RGB);

            // 2、得到画笔对象
            Graphics2D g = buffImg.createGraphics();
            // 3、设置对线段的锯齿状边缘处理
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.drawImage(srcImg.getScaledInstance(srcImg.getWidth(null), srcImg.getHeight(null), Image.SCALE_SMOOTH), 0, 0, null);
            // 4、设置水印旋转
            if (null != degree) {
                g.rotate(Math.toRadians(degree), (double) buffImg.getWidth() / 2, (double) buffImg.getHeight() / 2);
            }
            // 5、设置水印文字颜色
            g.setColor(getColor());
            // 6、设置水印文字Font
            g.setFont(getFont());
            // 7、设置水印文字透明度
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, getAlpha()));
            // 8、第一参数->设置的内容，后面两个参数->文字在图片上的坐标位置(x,y)
            g.drawString(logoText, getPositionWidth(), getPositionHeight());
            // 9、释放资源
            g.dispose();

            return buffImg;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
        return null;
    }
    
    public static boolean isPicture(String fName,
            String pImgeFlag) throws Exception {
        // 文件名称为空的场合
        if (fName == null) {
            // 返回不和合法
            return false;
        }
        // 获得文件后缀名
        String tmpName = fName.substring(fName.lastIndexOf(".") + 1,
                fName.length());
        if (pImgeFlag != null) {
            return tmpName.toLowerCase().equals(pImgeFlag);
        }
        // 声明图片后缀名数组
        String imgeArray[] = {
            "bmp", "dib", "gif", "jfif", "jpe", "jpeg", "jpg", "png", "tif", "tiff", "ico"
        };
        // 遍历名称数组
        for (int i = 0; i < imgeArray.length; i++) {
            // 判断单个类型文件的场合
            if (imgeArray[i].equals(tmpName.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    public float getAlpha() {
        return alpha;
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }

    public int getPositionWidth() {
        return positionWidth;
    }

    public void setPositionWidth(int positionWidth) {
        this.positionWidth = positionWidth;
    }

    public int getPositionHeight() {
        return positionHeight;
    }

    public void setPositionHeight(int positionHeight) {
        this.positionHeight = positionHeight;
    }

    public Font getFont() {
        return font;
    }

    public void setFont(Font font) {
        this.font = font;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

}
