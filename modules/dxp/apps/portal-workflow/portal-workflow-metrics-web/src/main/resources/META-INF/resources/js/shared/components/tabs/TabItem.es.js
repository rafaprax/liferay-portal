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

import React from 'react';

import {getPathname} from '../router/routerUtil.es';
import {ChildLink} from '../router/routerWrapper.es';

export default class TabItem extends React.Component {
	render() {
		const {active, name, params, path} = this.props;
		const activeClassName = active ? 'active' : '';
		const activeLabel = active ? Liferay.Language.get('current-page') : '';

		return (
			<ChildLink
				aria-label={activeLabel}
				className={`${activeClassName} nav-link`}
				to={getPathname(params, path)}
			>
				<span className="navbar-text-truncate">{name}</span>
			</ChildLink>
		);
	}
}
