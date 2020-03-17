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
import React from 'react';

const Actions = ({actions = [], title, handleAction}) => {
	return (
		<ClayList>
			<ClayList.Header>{title}</ClayList.Header>

			{actions.map(({id, label, main, status}, index) => (
				<ClayList.Item
					className="autofit-row-center"
					flex
					key={index}
					style={{minHeight: '85px'}}
				>
					<ClayList.ItemField
						className={`${main ? 'font-weight-bold' : ''}`}
						expand
					>
						{label}
					</ClayList.ItemField>

					<ClayList.ItemField>
						{status ? (
							<ClayProgressBar value={status} />
						) : (
							<ClayButton
								displayType="secondary"
								// onClick={() => handleAction(id)}
							>
								{main
									? Liferay.Language.get('execute-all')
									: Liferay.Language.get('execute')}
							</ClayButton>
						)}
					</ClayList.ItemField>
				</ClayList.Item>
			))}
		</ClayList>
	);
};

export {Actions};
