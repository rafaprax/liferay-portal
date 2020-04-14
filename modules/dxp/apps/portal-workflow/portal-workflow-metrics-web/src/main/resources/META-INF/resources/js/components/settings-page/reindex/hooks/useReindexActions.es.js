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

import {usePrevious} from 'frontend-js-react-web';
import {useContext, useState} from 'react';

import {useToaster} from '../../../../shared/components/toaster/hooks/useToaster.es';
import {useFetch} from '../../../../shared/hooks/useFetch.es';
import {usePost} from '../../../../shared/hooks/usePost.es';
import {sub} from '../../../../shared/util/lang.es';
import {AppContext} from '../../../AppContext.es';
import {REINDEX_GROUP_KEYS, SUCCESS_MESSAGES} from '../ReindexConstants.es';

const useReindexActions = () => {
	const {reindexStatuses, setReindexStatuses} = useContext(AppContext);
	const previousStatuses = usePrevious(reindexStatuses);
	const [reindexingAll, setReindexingAll] = useState(false);
	const toaster = useToaster();

	const {fetchData} = useFetch({url: '/reindex-status'});
	const {postData} = usePost({url: '/reindex-action'});

	const getStatuses = (
		key,
		label = Liferay.Language.get('workflow-indexes')
	) => {
		const interval = setInterval(() => {
			fetchData()
				.then(({items, totalCount}) => {
					if (!totalCount) {
						if (previousStatuses) {
							toaster.success(getSuccessMessage(key, label));
						}

						clearInterval(interval);
						setReindexingAll(false);
					}

					setReindexStatuses(items);
				})
				.catch(() => {
					clearInterval(interval);
					sendError();
					setReindexStatuses([]);
					setReindexingAll(false);
				});
		}, 1500);
	};

	const getSuccessMessage = (key, label) => {
		const message = REINDEX_GROUP_KEYS.includes(key)
			? SUCCESS_MESSAGES.ALL
			: SUCCESS_MESSAGES.SINGLE;

		return sub(message, [label]);
	};

	const handleReindex = (key, label) => {
		postData({key})
			.then(() => getStatuses(key, label))
			.catch(() => {
				sendError();
				setReindexingAll(false);
			});
	};

	const sendError = () => {
		toaster.danger(Liferay.Language.get('please-check-the-server-log'));
	};

	return {
		getStatuses,
		handleReindex,
		reindexStatuses,
		reindexingAll,
		setReindexingAll,
	};
};

export {useReindexActions};
