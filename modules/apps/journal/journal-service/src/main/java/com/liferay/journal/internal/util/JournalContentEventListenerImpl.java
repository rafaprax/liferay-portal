/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.internal.util;

import com.liferay.change.tracking.spi.listener.CTEventListener;

import org.osgi.service.component.annotations.Component;

/**
 * @author Roberto Cassio Silva do Nascimento Junior
 */
@Component(service = CTEventListener.class)
public class JournalContentEventListenerImpl implements CTEventListener {

	@Override
	public void onAfterPublish(long ctCollectionId) {
		JournalContentImpl.portalCache.removeAll();
	}

}