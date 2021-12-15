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

package com.liferay.portal.metadata;

import com.liferay.document.library.kernel.util.AudioProcessorUtil;
import com.liferay.document.library.kernel.util.VideoProcessorUtil;
import com.liferay.exportimport.kernel.lar.PortletDataContext;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.xml.Element;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.XMPDM;

/**
 * @author Juan González
 * @author Alexander Chow
 */
public class FFMPEGRawMetadataProcessor extends BaseRawMetadataProcessor {

	@Override
	public void exportGeneratedFiles(
		PortletDataContext portletDataContext, FileEntry fileEntry,
		Element fileEntryElement) {
	}

	@Override
	public void importGeneratedFiles(
		PortletDataContext portletDataContext, FileEntry fileEntry,
		FileEntry importedFileEntry, Element fileEntryElement) {
	}

	protected Metadata extractMetadata(File file) throws Exception {
		Metadata metadata = new Metadata();

		_extractDurationMetadata(file, metadata);

		return metadata;
	}

	@Override
	protected Metadata extractMetadata(
		String extension, String mimeType, File file) {

		if (!isSupported(mimeType)) {
			return null;
		}

		try {
			return extractMetadata(file);
		}
		catch (Exception exception) {
			_log.error(exception, exception);
		}

		return null;
	}

	@Override
	protected Metadata extractMetadata(
		String extension, String mimeType, InputStream inputStream) {

		if (!isSupported(mimeType)) {
			return null;
		}

		File file = null;

		try {
			file = FileUtil.createTempFile(extension);

			FileUtil.write(file, inputStream);

			return extractMetadata(file);
		}
		catch (Exception exception) {
			_log.error(exception, exception);
		}
		finally {
			FileUtil.delete(file);
		}

		return null;
	}

	protected boolean isSupported(String mimeType) {
		if (AudioProcessorUtil.isAudioSupported(mimeType) ||
			VideoProcessorUtil.isVideoSupported(mimeType)) {

			return true;
		}

		return false;
	}

	private void _extractDurationMetadata(File file, Metadata metadata)
		throws Exception {

		List<String> ffmpegCommand = Arrays.asList(
			"ffmpeg", "-y", "-i", file.getAbsolutePath(), "-f null -");

		ProcessBuilder processBuilder = new ProcessBuilder(ffmpegCommand);

		processBuilder.redirectErrorStream(true);

		Process process = processBuilder.start();

		InputStream inputStream = process.getInputStream();

		while (true) {
			try (BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(inputStream))) {

				while (bufferedReader.ready()) {
					Matcher matcher = _timePattern.matcher(
						bufferedReader.readLine());

					if (matcher.find()) {
						metadata.set(XMPDM.DURATION, matcher.group());

						return;
					}
				}

				if (!process.waitFor(5, TimeUnit.SECONDS)) {
					continue;
				}

				if (process.exitValue() != 0) {
					throw new Exception(
						StringBundler.concat(
							"FFMPEG command ",
							StringUtil.merge(ffmpegCommand, StringPool.SPACE),
							" failed with exit status ", process.exitValue()));
				}

				return;
			}
			catch (InterruptedException interruptedException) {
				if (_log.isDebugEnabled()) {
					_log.debug(interruptedException, interruptedException);
				}
			}
		}
	}

	private static final Pattern _timePattern = Pattern.compile(
		"(\\d+):(\\d+):(\\d+(?:\\.\\d+)?)");

	private static final Log _log = LogFactoryUtil.getLog(
		FFMPEGRawMetadataProcessor.class);

}