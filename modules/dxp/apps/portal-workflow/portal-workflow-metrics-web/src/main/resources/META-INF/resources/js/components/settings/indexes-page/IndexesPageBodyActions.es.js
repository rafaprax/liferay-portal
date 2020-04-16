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
import ClayList from '@clayui/list';
import ClayProgressBar from '@clayui/progress-bar';
import React, {useMemo} from 'react';

const Action = ({
	action: {key, label, status},
	disabled,
	handleAction,
	index,
}) => {
	const buttonTxt =
		index === 0
			? Liferay.Language.get('reindex-all')
			: Liferay.Language.get('reindex');

	return (
		<ClayList.Item
			className="autofit-row-center reindex-action"
			flex
			key={key}
		>
			<ClayList.ItemField expand>{label}</ClayList.ItemField>

			<ClayList.ItemField>
				{status ? (
					<ClayProgressBar value={status.completionPercentage} />
				) : (
					<ClayButton
						disabled={disabled}
						displayType="secondary"
						onClick={() => handleAction(key, label)}
						small
					>
						{buttonTxt}
					</ClayButton>
				)}
			</ClayList.ItemField>
		</ClayList.Item>
	);
};

const GroupActions = ({
	actions = [],
	disabled,
	handleAction,
	label,
	statuses,
}) => {
	const groupActions = useMemo(() => {
		return actions.map(item => {
			const status = statuses.find(({key}) => key === item.key);

			return {...item, status};
		});
	}, [actions, statuses]);

	return (
		<ClayList>
			<ClayList.Header>{label}</ClayList.Header>

			{groupActions.map((action, index) => (
				<Action
					action={action}
					disabled={disabled}
					handleAction={handleAction}
					index={index}
					key={index}
				/>
			))}
		</ClayList>
	);
};

export {GroupActions};
