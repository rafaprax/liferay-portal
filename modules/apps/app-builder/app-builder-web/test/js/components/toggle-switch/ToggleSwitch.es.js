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

import {act, cleanup, render} from '@testing-library/react';
import React from 'react';

import ToggleSwitch from '../../../../src/main/resources/META-INF/resources/js/components/toggle-switch/ToggleSwitch.es';

describe('ToggleSwitch', () => {
	afterEach(() => {
		cleanup();
		jest.restoreAllMocks();
	});

	it('onChange is called when clicked', async () => {
		const onChangeCallback = jest.fn();

		const {container} = render(
			<ToggleSwitch onChange={onChangeCallback} />
		);

		const input = container.querySelector('input[type=checkbox]');

		expect(input.checked).toBe(false);
		expect(input.title).toBe('turn-on');

		act(() => {
			input.dispatchEvent(new MouseEvent('click', {bubbles: true}));
		});

		expect(input.checked).toBe(true);
		expect(input.title).toBe('turn-off');

		act(() => {
			input.dispatchEvent(new MouseEvent('click', {bubbles: true}));
		});

		expect(input.checked).toBe(false);
		expect(onChangeCallback.mock.calls.length).toBe(2);
		expect(onChangeCallback.mock.calls[0][0]).toBe(true);
		expect(onChangeCallback.mock.calls[1][0]).toBe(false);
	});
});
