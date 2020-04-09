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

import React, {useMemo} from 'react';

import PromisesResolver from '../../../shared/components/promises-resolver/PromisesResolver.es';
import {useFetch} from '../../../shared/hooks/useFetch.es';
import {Body} from './ReindexPageBody.es';

const ReindexPage = () => {
	// const {data, fetchData} = useFetch({url: '/reindex-action-groups'});
	// const promises = useMemo(() => [fetchData()], [fetchData]);

	const promises = [];
	const data = {
		items: [
			{
				label: 'Metrics',
				reindexActions: [
					{
						key: 'AllMetrics',
						label: 'Workflow Metrics Indexes',
					},
					{
						key: 'Instances',
						label: 'Workflow Metrics Instances',
					},
					{
						key: 'Nodes',
						label: 'Workflow Metrics Nodes',
					},
					{
						key: 'Processes',
						label: 'Workflow Metrics Processes',
					},
					{
						key: 'Tasks',
						label: 'Workflow Metrics Tasks',
					},
					{
						key: 'Transitions',
						label: 'Workflow Metrics Transitions',
					},
				],
			},
			{
				label: 'SLAs',
				reindexActions: [
					{
						key: 'AllSlas',
						label: 'Workflow Metrics Indexes',
					},
					{
						key: 'SlaInstances',
						label: 'SLA Instance Results',
					},
					{
						key: 'SlaTasks',
						label: 'SLA Process Results',
					},
				],
			},
		],
	};

	return (
		<div className="container-fluid-1280">
			<h3 className="my-4">
				{Liferay.Language.get('workflow-index-actions')}
			</h3>

			<PromisesResolver promises={promises}>
				<ReindexPage.Body {...data} />
			</PromisesResolver>
		</div>
	);
};

ReindexPage.Body = Body;

export default ReindexPage;
