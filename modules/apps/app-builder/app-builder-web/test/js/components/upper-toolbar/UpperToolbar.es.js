/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

import {cleanup, fireEvent, render} from '@testing-library/react';
import React from 'react';

import UpperToolbar from '../../../../src/main/resources/META-INF/resources/js/components/upper-toolbar/UpperToolbar.es';

describe('UpperToolbar', () => {
	afterEach(() => {
		cleanup();
		jest.restoreAllMocks();
	});

	it('renders UpperToolbar with children', () => {
		const onClickCallback = jest.fn();

		const {container, queryByPlaceholderText, queryByRole} = render(
			<UpperToolbar className="upperToolbarClassName">
				<UpperToolbar.Input placeholder="placeholder1" />

				<UpperToolbar.Input
					onChange={() => {}}
					placeholder="placeholder2"
				/>

				<UpperToolbar.Group>
					<UpperToolbar.Button onClick={onClickCallback}>
						Button
					</UpperToolbar.Button>
				</UpperToolbar.Group>
			</UpperToolbar>
		);

		const input1 = queryByPlaceholderText('placeholder1');
		const input2 = queryByPlaceholderText('placeholder2');
		const button = queryByRole('button');

		expect(container.querySelector('.upperToolbarClassName')).toBeTruthy();
		expect(button).toBeTruthy();
		expect(input1.value).toBe('');
		expect(input2.value).toBe('');

		fireEvent.change(input1, {target: {value: 'value1'}});
		fireEvent.change(input2, {target: {value: 'value2'}});

		expect(input1.value).toBe('value1');
		expect(input2.value).toBe('value2');

		fireEvent.click(button);

		expect(onClickCallback.mock.calls.length).toBe(1);
	});
});
