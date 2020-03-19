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

import React, {createContext, useMemo, useState} from 'react';

const InstanceListContext = createContext(null);

const InstanceListPageProvider = ({children}) => {
	const [instanceId, setInstanceId] = useState();
	const [selectAll, setSelectAll] = useState(false);
	const [selectedItem, setSelectedItem] = useState({});
	const [selectedItems, setSelectedItems] = useState([]);

	const selectedInstance = useMemo(
		() => (selectedItems.length === 1 ? selectedItems[0] : selectedItem),
		[selectedItem, selectedItems]
	);

	const value = {
		instanceId,
		selectAll,
		selectedInstance,
		selectedItem,
		selectedItems,
		setInstanceId,
		setSelectAll,
		setSelectedItem,
		setSelectedItems,
	};

	return (
		<InstanceListContext.Provider value={value}>
			{children}
		</InstanceListContext.Provider>
	);
};

export {InstanceListContext};
export default InstanceListPageProvider;
