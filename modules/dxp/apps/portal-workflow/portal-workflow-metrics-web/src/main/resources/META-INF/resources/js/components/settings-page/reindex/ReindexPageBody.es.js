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

import ClayButton from '@clayui/button';
import ClayProgressBar from '@clayui/progress-bar';
import React from 'react';

import PromisesResolver from '../../../shared/components/promises-resolver/PromisesResolver.es';
import {GroupActions} from './ReindexPageBodyActions.es';
import {useReindexActions} from './hooks/useReindexActions.es';

const Body = ({items = []}) => {
	const {
		handleReindex,
		reindexStatuses,
		reindexingAll,
		setReindexingAll,
	} = useReindexActions();

	const handleReindexAll = () => {
		setReindexingAll(true);
		handleReindex('All');
	};

	const reindexStatusAll = reindexStatuses.find(({key}) => key === 'All');
	const {completionPercentage = 0} = reindexStatusAll || {};

	return (
		<>
			<div className="mb-4 p-3 sheet">
				<div className="autofit-row autofit-row-center">
					<div className="autofit-col autofit-col-expand">
						<span className="font-weight-semi-bold">
							{Liferay.Language.get('workflow-indexes')}
						</span>
					</div>

					<div className="autofit-col">
						{completionPercentage || reindexingAll ? (
							<ClayProgressBar value={completionPercentage} />
						) : (
							<ClayButton onClick={handleReindexAll} small>
								{Liferay.Language.get('reindex-all')}
							</ClayButton>
						)}
					</div>
				</div>
			</div>

			<PromisesResolver.Resolved>
				{items.map((item, index) => (
					<Body.GroupActions
						disabled={reindexingAll}
						handleAction={handleReindex}
						key={index}
						statuses={reindexStatuses}
						{...item}
					/>
				))}
			</PromisesResolver.Resolved>
		</>
	);
};

Body.GroupActions = GroupActions;

export {Body};
