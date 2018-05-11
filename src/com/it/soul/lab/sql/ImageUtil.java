package com.it.soul.lab.sql;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageUtil {
	
	private static final int IMG_WIDTH = 100;
	private static final int IMG_HEIGHT = 120;
	
	public enum ImageEncodeType{
		ImageEncodeTypePNG,
		ImageEncodeTypeJPEG,
		ImageEncodeTypeJPG
	}
	
	public static BufferedImage convertFromByteArray(byte[] imageBytes)
	throws IOException{
		
		BufferedImage result = null;
		
		if(imageBytes.length > 0){
			ByteArrayInputStream inStream = new ByteArrayInputStream(imageBytes);
			result = ImageIO.read(inStream);
		}
		
		return result;
	}
	
	public static byte[] convertFromBufferdImage(BufferedImage image, ImageEncodeType encode)
	throws IOException{
		
		byte[] result = null;
		
		if(image != null){
			ByteArrayOutputStream orignStream = new ByteArrayOutputStream();
			
			switch (encode) {
			case ImageEncodeTypeJPEG:
			case ImageEncodeTypeJPG:
				ImageIO.write(image, "jpeg", orignStream);
				break;
			case ImageEncodeTypePNG:
				ImageIO.write(image, "png", orignStream);
			default:
				break;
			}
			
			orignStream.flush();
			result = orignStream.toByteArray();
			orignStream.close();
		}
		
		return result;
	}
	
	public static BufferedImage getBufferedImage(String imagePath)
	throws IOException{

		byte[] imageByte = null;
		BufferedImage orignImage = ImageIO.read(new File(imagePath));
		ByteArrayOutputStream orignStream = new ByteArrayOutputStream();
		
		if(imagePath.trim().toLowerCase().equals("jpeg") || imagePath.trim().toLowerCase().equals("jpg")){
			ImageIO.write(orignImage, "jpeg", orignStream);
		}else if(imagePath.trim().toLowerCase().equals("png")){
			ImageIO.write(orignImage, "png", orignStream);
		}
		
		orignStream.flush();
		imageByte = orignStream.toByteArray();
		orignStream.close();
		
		BufferedImage result = null;
		if(imageByte.length > 0){
			ByteArrayInputStream inStream = new ByteArrayInputStream(imageByte);
			result = ImageIO.read(inStream);
		}
		
		return result;
	}
	
	public static BufferedImage resizeImage(BufferedImage originalImage){

		int type = originalImage.getType() == 0? BufferedImage.TYPE_INT_ARGB : originalImage.getType();
		
		BufferedImage resizedImage = new BufferedImage(IMG_WIDTH, IMG_HEIGHT, type);
		Graphics2D g = resizedImage.createGraphics();
		g.drawImage(originalImage, 0, 0, IMG_WIDTH, IMG_HEIGHT, null);
		g.dispose();

		return resizedImage;
	}
	
	public static BufferedImage resizeImage(BufferedImage originalImage, int width, int height){

		int type = originalImage.getType() == 0? BufferedImage.TYPE_INT_ARGB : originalImage.getType();
		
		BufferedImage resizedImage = new BufferedImage(width, height, type);
		Graphics2D g = resizedImage.createGraphics();
		g.drawImage(originalImage, 0, 0, width, height, null);
		g.dispose();

		return resizedImage;
	}
	
	public static BufferedImage resizeImageWithHint(BufferedImage originalImage){

		int type = originalImage.getType() == 0? BufferedImage.TYPE_INT_ARGB : originalImage.getType();
		
		BufferedImage resizedImage = new BufferedImage(IMG_WIDTH, IMG_HEIGHT, type);
		Graphics2D g = resizedImage.createGraphics();
		g.drawImage(originalImage, 0, 0, IMG_WIDTH, IMG_HEIGHT, null);
		g.dispose();	
		g.setComposite(AlphaComposite.Src);

		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g.setRenderingHint(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_QUALITY);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		return resizedImage;
	}

	public static BufferedImage getScaledImage(BufferedImage img,
			int targetWidth,
			int targetHeight,
			Object hint,
			boolean higherQuality)
	{
		int type = (img.getTransparency() == Transparency.OPAQUE) ?
				BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
		BufferedImage ret = (BufferedImage)img;
		int w, h;
		if (higherQuality) {
			// Use multi-step technique: start with original size, then
			// scale down in multiple passes with drawImage()
			// until the target size is reached
			w = img.getWidth();
			h = img.getHeight();
		} else {
			// Use one-step technique: scale directly from original
			// size to target size with a single drawImage() call
			w = targetWidth;
			h = targetHeight;
		}

		do {
			if (higherQuality && w > targetWidth) {
				w /= 2;
				if (w < targetWidth) {
					w = targetWidth;
				}
			}

			if (higherQuality && h > targetHeight) {
				h /= 2;
				if (h < targetHeight) {
					h = targetHeight;
				}
			}

			BufferedImage tmp = new BufferedImage(w, h, type);
			Graphics2D g2 = tmp.createGraphics();
			g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, hint);
			g2.drawImage(ret, 0, 0, w, h, null);
			g2.dispose();

			ret = tmp;
		} while (w != targetWidth || h != targetHeight);

		return ret;
	}
}
