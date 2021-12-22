package com.liferay.portal.kernel.image;

import com.liferay.portal.kernel.util.ArrayUtil;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.util.Hashtable;

public class ImageScaleUtil {

	public static BufferedImage getBufferedImage(RenderedImage renderedImage) {
		if (renderedImage instanceof BufferedImage) {
			return (BufferedImage)renderedImage;
		}

		ColorModel colorModel = renderedImage.getColorModel();

		WritableRaster writableRaster =
			colorModel.createCompatibleWritableRaster(
				renderedImage.getWidth(), renderedImage.getHeight());

		Hashtable<String, Object> properties = new Hashtable<>();

		String[] keys = renderedImage.getPropertyNames();

		if (!ArrayUtil.isEmpty(keys)) {
			for (String key : keys) {
				properties.put(key, renderedImage.getProperty(key));
			}
		}

		BufferedImage bufferedImage = new BufferedImage(
			colorModel, writableRaster, colorModel.isAlphaPremultiplied(),
			properties);

		renderedImage.copyData(writableRaster);

		return bufferedImage;
	}

	public static RenderedImage scale(RenderedImage renderedImage, int width) {
		if (width <= 0) {
			return renderedImage;
		}

		int imageHeight = renderedImage.getHeight();

		int imageWidth = renderedImage.getWidth();

		double factor = (double)width / imageWidth;

		int scaledHeight = (int)Math.round(factor * imageHeight);

		int scaledWidth = width;

		return _scale(renderedImage, scaledHeight, scaledWidth);
	}

	public static RenderedImage scale(
		RenderedImage renderedImage, int maxHeight, int maxWidth) {

		int imageHeight = renderedImage.getHeight();
		int imageWidth = renderedImage.getWidth();

		if (maxHeight == 0) {
			maxHeight = imageHeight;
		}

		if (maxWidth == 0) {
			maxWidth = imageWidth;
		}

		if ((imageHeight <= maxHeight) && (imageWidth <= maxWidth)) {
			return renderedImage;
		}

		double factor = Math.min(
			(double)maxHeight / imageHeight, (double)maxWidth / imageWidth);

		int scaledHeight = Math.max(1, (int)Math.round(factor * imageHeight));
		int scaledWidth = Math.max(1, (int)Math.round(factor * imageWidth));

		return _scale(renderedImage, scaledHeight, scaledWidth);
	}

	private static RenderedImage _scale(
		RenderedImage renderedImage, int scaledHeight, int scaledWidth) {

		// See http://www.oracle.com/technetwork/java/index-137037.html

		BufferedImage originalBufferedImage = getBufferedImage(renderedImage);

		int type = originalBufferedImage.getType();

		if (type == BufferedImage.TYPE_CUSTOM) {
			type = BufferedImage.TYPE_INT_ARGB;
		}

		BufferedImage scaledBufferedImage = new BufferedImage(
			scaledWidth, scaledHeight, type);

		int originalHeight = originalBufferedImage.getHeight();
		int originalWidth = originalBufferedImage.getWidth();

		if (((scaledHeight * 2) >= originalHeight) &&
			((scaledWidth * 2) >= originalWidth)) {

			Graphics2D scaledGraphics2D = scaledBufferedImage.createGraphics();

			scaledGraphics2D.setRenderingHint(
				RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
			scaledGraphics2D.setRenderingHint(
				RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			scaledGraphics2D.setRenderingHint(
				RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_QUALITY);

			scaledGraphics2D.drawImage(
				originalBufferedImage, 0, 0, scaledWidth, scaledHeight, null);

			scaledGraphics2D.dispose();

			return scaledBufferedImage;
		}

		BufferedImage tempBufferedImage = new BufferedImage(
			originalWidth, originalHeight, scaledBufferedImage.getType());

		Graphics2D tempGraphics2D = tempBufferedImage.createGraphics();

		RenderingHints renderingHints = new RenderingHints(
			RenderingHints.KEY_INTERPOLATION,
			RenderingHints.VALUE_INTERPOLATION_BILINEAR);

		tempGraphics2D.setRenderingHints(renderingHints);

		ColorModel originalColorModel = originalBufferedImage.getColorModel();

		if (originalColorModel.hasAlpha()) {
			tempGraphics2D.setComposite(AlphaComposite.Src);
		}

		int startHeight = scaledHeight;
		int startWidth = scaledWidth;

		while ((startHeight < originalHeight) && (startWidth < originalWidth)) {
			startHeight *= 2;
			startWidth *= 2;
		}

		originalHeight = startHeight / 2;
		originalWidth = startWidth / 2;

		tempGraphics2D.drawImage(
			originalBufferedImage, 0, 0, originalWidth, originalHeight, null);

		while ((originalHeight >= (scaledHeight * 2)) &&
			   (originalWidth >= (scaledWidth * 2))) {

			originalHeight /= 2;

			if (originalHeight < scaledHeight) {
				originalHeight = scaledHeight;
			}

			originalWidth /= 2;

			if (originalWidth < scaledWidth) {
				originalWidth = scaledWidth;
			}

			tempGraphics2D.drawImage(
				tempBufferedImage, 0, 0, originalWidth, originalHeight, 0, 0,
				originalWidth * 2, originalHeight * 2, null);
		}

		tempGraphics2D.dispose();

		Graphics2D scaledGraphics2D = scaledBufferedImage.createGraphics();

		scaledGraphics2D.drawImage(
			tempBufferedImage, 0, 0, scaledWidth, scaledHeight, 0, 0,
			originalWidth, originalHeight, null);

		scaledGraphics2D.dispose();

		return scaledBufferedImage;
	}
}
