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
import React from 'react';

import PromisesResolver from '../../../shared/components/promises-resolver/PromisesResolver.es';
import {Actions} from './ReindexActions.es';

const Body = ({items}) => {
	return (
		<>
			<Body.ReindexAll />

			<PromisesResolver.Resolved>
				{items.map((item, index) => (
					<Body.Actions key={index} {...item} />
				))}
			</PromisesResolver.Resolved>
		</>
	);
};

const ReindexAll = () => {
	return (
		<div className="mb-4 pb-4 sheet">
			<div className="autofit-row autofit-row-center">
				<div className="autofit-col autofit-col-expand">
					<span className="font-weight-bold">
						{Liferay.Language.get('workflow-indexes')}
					</span>
				</div>

				<div className="autofit-col">
					<ClayButton>
						{Liferay.Language.get('reindex-all')}
					</ClayButton>
				</div>
			</div>
		</div>
	);
};

Body.Actions = Actions;
Body.ReindexAll = ReindexAll;

export {Body};
