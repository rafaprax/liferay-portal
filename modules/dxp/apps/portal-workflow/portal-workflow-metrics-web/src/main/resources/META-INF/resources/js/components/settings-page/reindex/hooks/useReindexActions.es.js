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

import {useContext} from 'react';

import {useToaster} from '../../../../shared/components/toaster/hooks/useToaster.es';
import {useFetch} from '../../../../shared/hooks/useFetch.es';
import {usePost} from '../../../../shared/hooks/usePost.es';
import {AppContext} from '../../../AppContext.es';

const useReindexActions = () => {
	const {reindexStatuses, setReindexStatuses} = useContext(AppContext);
	const toaster = useToaster();

	const {fetchData} = useFetch({url: '/reindex-statuses'});
	const {postData} = usePost({
		config: {headers: {'Content-Type': 'application/json'}},
		url: '/reindex-action',
	});

	const getStatuses = () => {
		const interval = setInterval(() => {
			fetchData()
				.then(({items, totalCount}) => {
					setReindexStatuses(items);

					if (!totalCount) {
						if (reindexStatuses.length > 0) {
							toaster.success();
						}
						clearInterval(interval);
					}
				})
				.catch(() => {
					clearInterval(interval);
					setReindexStatuses([]);
					sendError();
				});
		}, 2000);
	};

	const handleReindex = reindexKey => {
		postData(JSON.stringify(reindexKey))
			.then(getStatuses)
			.catch(sendError);
	};

	const sendError = () => {
		toaster.danger(Liferay.Language.get('please-check-the-server-log'));
	};

	return {getStatuses, handleReindex, reindexStatuses};
};

export {useReindexActions};
