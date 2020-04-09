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

import {useInterval, usePrevious} from 'frontend-js-react-web';
import {useContext, useEffect, useState} from 'react';

import {useToaster} from '../../../../shared/components/toaster/hooks/useToaster.es';
import {useFetch} from '../../../../shared/hooks/useFetch.es';
import {usePost} from '../../../../shared/hooks/usePost.es';
import {AppContext} from '../../../AppContext.es';

const INTERVAL = 2000;

const useReindexActions = () => {
	const [cancel, setCancel] = useState();
	const {reindexStatuses, setReindexStatuses} = useContext(AppContext);

	const previousStatuses = usePrevious(reindexStatuses);
	const schedule = useInterval();
	const toaster = useToaster();

	const {postData} = usePost({url: '/reindex-action'});
	const {fetchData} = useFetch({url: '/reindex-statuses'});

	const getStatuses = () => {
		const callback = () =>
			fetchData()
				.then(setReindexStatuses)
				.catch(sendError);

		setCancel(schedule(callback, INTERVAL));
	};

	const handleReindex = reindexKey => {
		return postData({reindexKey})
			.then(getStatuses)
			.catch(sendError);
	};

	const sendError = () => {
		toaster.danger(Liferay.Language.get('please-check-the-server-log'));
	};

	useEffect(() => {
		if (
			cancel &&
			!reindexStatuses.length &&
			previousStatuses &&
			!previousStatuses.length
		) {
			toaster.success(
				Liferay.Language.get('please-check-the-server-log')
			);

			cancel();
		}
		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, [cancel, previousStatuses, reindexStatuses]);

	return {getStatuses, handleReindex, reindexStatuses};
};

export {useReindexActions};
