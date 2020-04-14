/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Liferay Enterprise
 * Subscription License ("License"). You may not use this file except in
 * compliance with the License. You can obtain a copy of the License by
 * contacting Liferay, Inc. See the License for the specific language governing
 * permissions and limitations under the License, including but not limited to
 * distribution rights of the Software.
 */

const REINDEX_ALL_KEY = 'All';
const REINDEX_METRICS_KEY = 'Metrics';
const REINDEX_SLA_KEY = 'Sla';

const REINDEX_GROUPS = [
	{
		actions: [
			{
				key: REINDEX_METRICS_KEY,
				label: Liferay.Language.get('workflow-metrics-indexes'),
			},
		],
		key: REINDEX_METRICS_KEY,
		label: Liferay.Language.get('metrics'),
	},
	{
		actions: [
			{
				key: REINDEX_METRICS_KEY,
				label: Liferay.Language.get('workflow-sla-indexes'),
			},
		],
		key: REINDEX_SLA_KEY,
		label: Liferay.Language.get('slas'),
	},
];

const REINDEX_GROUP_KEYS = [
	REINDEX_ALL_KEY,
	REINDEX_METRICS_KEY,
	REINDEX_SLA_KEY,
];

const SUCCESS_MESSAGES = {
	ALL: Liferay.Language.get('all-x-have-reindexed-successfully'),
	SINGLE: Liferay.Language.get('x-has-reindexed-successfully'),
};

export {
	REINDEX_ALL_KEY,
	REINDEX_METRICS_KEY,
	REINDEX_SLA_KEY,
	REINDEX_GROUPS,
	REINDEX_GROUP_KEYS,
	SUCCESS_MESSAGES,
};
