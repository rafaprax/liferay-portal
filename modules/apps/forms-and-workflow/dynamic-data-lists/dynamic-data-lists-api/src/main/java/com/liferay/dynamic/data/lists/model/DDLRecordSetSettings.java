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

package com.liferay.dynamic.data.lists.model;

import com.liferay.dynamic.data.mapping.annotations.DDMForm;
import com.liferay.dynamic.data.mapping.annotations.DDMFormField;
import com.liferay.dynamic.data.mapping.annotations.DDMFormFieldRule;
import com.liferay.dynamic.data.mapping.annotations.DDMFormLayout;
import com.liferay.dynamic.data.mapping.annotations.DDMFormLayoutColumn;
import com.liferay.dynamic.data.mapping.annotations.DDMFormLayoutPage;
import com.liferay.dynamic.data.mapping.annotations.DDMFormLayoutRow;
import com.liferay.dynamic.data.mapping.model.DDMFormFieldRuleType;

/**
 * @author Bruno Basto
 */
@DDMForm
@DDMFormLayout(
	{
		@DDMFormLayoutPage(
			title = "%form-options",
			value = {
				@DDMFormLayoutRow(
					{
						@DDMFormLayoutColumn(
							size = 12,
							value = {
								"requireCaptcha", "redirectURL", "storageType",
								"workflowDefinition"
							}
						)
					}
				)
			}
		),
		@DDMFormLayoutPage(
			title = "%email-notifications",
			value = {
				@DDMFormLayoutRow(
					{
						@DDMFormLayoutColumn(
							size = 12,
							value = {
								"sendEmailNotification", "emailFromName",
								"emailFromAddress", "emailToAddress",
								"emailSubject", "published"
							}
						)
					}
				)
			}
		)
	}
)
public interface DDLRecordSetSettings {

	@DDMFormField(
		label = "%from-address",
		rules = {
			@DDMFormFieldRule(
				errorMessage = "%please-enter-a-valid-email-address",
				expression = "isEmailAddress(emailFromAddress)",
				type = DDMFormFieldRuleType.VALIDATION
			),
			@DDMFormFieldRule(
				expression = "sendEmailNotification == TRUE",
				type = DDMFormFieldRuleType.VISIBILITY
			)
		}
	)
	public String emailFromAddress();

	@DDMFormField(
		label = "%from-name",
		rules = {
			@DDMFormFieldRule(
				expression = "sendEmailNotification == TRUE",
				type = DDMFormFieldRuleType.VISIBILITY
			)
		}
	)
	public String emailFromName();

	@DDMFormField(
		label = "%subject",
		rules = {
			@DDMFormFieldRule(
				expression = "sendEmailNotification == TRUE",
				type = DDMFormFieldRuleType.VISIBILITY
			)
		}
	)
	public String emailSubject();

	@DDMFormField(
		label = "%to-address",
		rules = {
			@DDMFormFieldRule(
				errorMessage = "%please-enter-a-valid-email-address",
				expression = "isEmailAddress(emailToAddress)",
				type = DDMFormFieldRuleType.VALIDATION
			),
			@DDMFormFieldRule(
				expression = "sendEmailNotification == TRUE",
				type = DDMFormFieldRuleType.VISIBILITY
			)
		}
	)
	public String emailToAddress();

	@DDMFormField(visible = false)
	public boolean published();

	@DDMFormField(
		label = "%redirect-url-on-success",
		properties = {"placeholder=%enter-a-valid-url"},
		rules = {
			@DDMFormFieldRule(
				errorMessage = "%please-enter-a-valid-url",
				expression = "isURL(redirectURL)",
				type = DDMFormFieldRuleType.VALIDATION
			)
		}
	)
	public String redirectURL();

	@DDMFormField(
		label = "%require-captcha", properties = {"showAsSwitcher=true"},
		type = "checkbox"
	)
	public boolean requireCaptcha();

	@DDMFormField(
		label = "%send-an-email-notification-for-each-entry",
		properties = {"showAsSwitcher=true"}, type = "checkbox"
	)
	public boolean sendEmailNotification();

	@DDMFormField(
		label = "%select-a-storage-type",
		properties = {"dataSourceType=manual"}, type = "select"
	)
	public String storageType();

	@DDMFormField(
		label = "%select-a-workflow", properties = {"dataSourceType=manual"},
		type = "select"
	)
	public String workflowDefinition();

}