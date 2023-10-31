/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.internal.buffer;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

/**
 * @author Michael C. Han
 */
public class IndexerRequestBufferOverflowHandler {

	public IndexerRequestBufferOverflowHandler(
		IndexerRequestBufferExecutor indexerRequestBufferExecutor) {

		_indexerRequestBufferExecutor = indexerRequestBufferExecutor;
	}

	public void bufferOverflowed(
		IndexerRequestBuffer indexerRequestBuffer, int maxBufferSize) {

		int currentBufferSize = indexerRequestBuffer.size();

		if (currentBufferSize < maxBufferSize) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Buffer size is less than maximum: " + maxBufferSize);
			}

			return;
		}

		int numRequests = Math.round(
			currentBufferSize -
				Math.abs(maxBufferSize * _minimumBufferAvailabilityPercentage));

		if (numRequests > 0) {
			try {
				BufferOverflowThreadLocal.setOverflowMode(true);

				_indexerRequestBufferExecutor.execute(
					indexerRequestBuffer, numRequests);
			}
			finally {
				BufferOverflowThreadLocal.setOverflowMode(false);
			}
		}
	}

	public void setMinimumBufferAvailabilityPercentage(
		float minimumBufferAvailabilityPercentage) {

		_minimumBufferAvailabilityPercentage =
			minimumBufferAvailabilityPercentage;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		IndexerRequestBufferOverflowHandler.class);

	private final IndexerRequestBufferExecutor _indexerRequestBufferExecutor;
	private volatile float _minimumBufferAvailabilityPercentage;

}