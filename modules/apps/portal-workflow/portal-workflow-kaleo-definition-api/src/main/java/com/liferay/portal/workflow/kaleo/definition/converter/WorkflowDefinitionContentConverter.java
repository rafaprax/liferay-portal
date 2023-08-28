/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.definition.converter;

import com.liferay.portal.kernel.workflow.WorkflowException;

/**
 * @author Rafael Praxedes
 */
public interface WorkflowDefinitionContentConverter {

	public String convert(String content) throws WorkflowException;

}