/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.portal.image;

import com.liferay.portal.kernel.util.ArrayUtil;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;

import java.util.Hashtable;

/**
 * @author Rafael Praxedes
 */
public class ImageToolHelper {

	public static ImageToolHelper getInstance() {
		return _imageToolHelper;
	}

	public BufferedImage getBufferedImage(RenderedImage renderedImage) {
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

	public RenderedImage scale(RenderedImage renderedImage, int width) {
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

	public RenderedImage scale(
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

	private ImageToolHelper() {
	}

	private RenderedImage _scale(
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

	private static final ImageToolHelper _imageToolHelper =
		new ImageToolHelper();

}