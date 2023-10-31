/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.internal.buffer;

import com.liferay.petra.lang.CentralizedThreadLocal;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.search.internal.buffer.util.IndexerRequestBufferExecutorUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author André de Oliveira
 * @author Michael C. Han
 */
public class IndexerRequestBuffer {

	public static IndexerRequestBuffer create() {
		List<IndexerRequestBuffer> indexerRequestBuffers =
			_indexerRequestBuffersThreadLocal.get();

		IndexerRequestBuffer indexerRequestBuffer = new IndexerRequestBuffer();

		indexerRequestBuffers.add(indexerRequestBuffer);

		return indexerRequestBuffer;
	}

	public static IndexerRequestBuffer get() {
		List<IndexerRequestBuffer> indexerRequestBuffers =
			_indexerRequestBuffersThreadLocal.get();

		if (indexerRequestBuffers.isEmpty()) {
			return null;
		}

		return indexerRequestBuffers.get(indexerRequestBuffers.size() - 1);
	}

	public static IndexerRequestBuffer remove() {
		List<IndexerRequestBuffer> indexerRequestBuffers =
			_indexerRequestBuffersThreadLocal.get();

		if (indexerRequestBuffers.isEmpty()) {
			return null;
		}

		return indexerRequestBuffers.remove(indexerRequestBuffers.size() - 1);
	}

	public void add(
		IndexerRequest indexerRequest, float minimumBufferSize,
		int maxBufferSize) {

		_indexerRequests.put(indexerRequest, indexerRequest);

		int currentBufferSize = size();

		if (currentBufferSize < maxBufferSize) {
			if (_log.isDebugEnabled()) {
				_log.debug(
					"Buffer size is less than maximum: " + maxBufferSize);
			}

			return;
		}

		int numRequests = Math.round(
			currentBufferSize - Math.abs(maxBufferSize * minimumBufferSize));

		if (numRequests > 0) {
			try {
				BufferOverflowThreadLocal.setOverflowMode(true);

				IndexerRequestBufferExecutorUtil.execute(this, numRequests);
			}
			finally {
				BufferOverflowThreadLocal.setOverflowMode(false);
			}
		}
	}

	public void clear() {
		_indexerRequests.clear();
	}

	public Collection<IndexerRequest> getIndexerRequests() {
		return _indexerRequests.values();
	}

	public boolean isEmpty() {
		return _indexerRequests.isEmpty();
	}

	public void remove(IndexerRequest indexerRequest) {
		_indexerRequests.remove(indexerRequest);
	}

	public int size() {
		return _indexerRequests.size();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		IndexerRequestBuffer.class);

	private static final ThreadLocal<List<IndexerRequestBuffer>>
		_indexerRequestBuffersThreadLocal = new CentralizedThreadLocal<>(
			IndexerRequestBuffer.class + "._indexerRequestBuffersThreadLocal",
			ArrayList::new);

	private final LinkedHashMap<IndexerRequest, IndexerRequest>
		_indexerRequests = new LinkedHashMap<>();

}