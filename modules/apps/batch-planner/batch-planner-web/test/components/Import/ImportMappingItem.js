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

import '@testing-library/jest-dom/extend-expect';
import {act, cleanup, fireEvent, render, within} from '@testing-library/react';
import React from 'react';

import ImportMappingItem from '../../../src/main/resources/META-INF/resources/js/import/ImportMappingItem';

const field = 'currencyCode';

const selectableFields = [
	{
		label: 'test',
		required: true,
		value: 'test',
	},
	{
		label: 'testSelect',
		value: 'testSelect',
	},
];

const BASE_PROPS = {
	field,
	portletNamespace: 'test',
	selectableFields,
};

describe('ImportMappingItem', () => {
	afterEach(cleanup);

	it('must render', () => {
		render(<ImportMappingItem {...BASE_PROPS} />);
	});

	it('must show dropdown items on select click', () => {
		const {getByLabelText, getByText} = render(
			<ImportMappingItem {...BASE_PROPS} />
		);

		act(() => {
			fireEvent.click(getByLabelText(BASE_PROPS.field));
		});

		expect(
			getByText(BASE_PROPS.selectableFields[0].label)
		).toBeInTheDocument();
	});

	it('must call the onChange method on user click dropdown item not selected', () => {
		const onChangeMock = jest.fn();

		const {getByLabelText, getByText} = render(
			<ImportMappingItem {...BASE_PROPS} onChange={onChangeMock} />
		);

		act(() => {
			fireEvent.click(getByLabelText(BASE_PROPS.field));
			fireEvent.click(getByText(BASE_PROPS.selectableFields[0].label));
		});

		expect(onChangeMock).toBeCalledTimes(1);
	});

	it('must not call the onChange method on user click dropdown item selected', () => {
		const onChangeMock = jest.fn();

		const {getByLabelText, getByRole} = render(
			<ImportMappingItem
				field={field}
				onChange={onChangeMock}
				portletNamespace={BASE_PROPS.portletNamespace}
				selectableFields={selectableFields.filter(
					(field) => field.label !== selectableFields[0].label
				)}
				selectedField={selectableFields[0]}
			/>
		);

		act(() => {
			fireEvent.click(getByLabelText(BASE_PROPS.field));
			fireEvent.click(
				within(getByRole('list')).getByText(selectableFields[0].label)
			);
		});

		expect(onChangeMock).not.toBeCalled();
	});

	it('must show in the dropdown menu the item selected', () => {
		const {getByLabelText, getByRole} = render(
			<ImportMappingItem
				field={field}
				portletNamespace={BASE_PROPS.portletNamespace}
				selectableFields={selectableFields.filter(
					(fields) => fields.label !== selectableFields[0].label
				)}
				selectedField={selectableFields[0]}
			/>
		);

		act(() => {
			fireEvent.click(getByLabelText(field));
		});

		expect(
			within(getByRole('list')).getByText(selectableFields[0].label)
		).toBeInTheDocument();
	});

	it('must filter elements when search text is provided', () => {
		const {
			getByLabelText,
			getByPlaceholderText,
			getByText,
			queryByText,
		} = render(<ImportMappingItem {...BASE_PROPS} />);

		act(() => {
			fireEvent.click(getByLabelText(BASE_PROPS.field));
			fireEvent.change(
				getByPlaceholderText(Liferay.Language.get('search')),
				{
					target: {value: BASE_PROPS.selectableFields[1].label},
				}
			);
		});

		expect(
			getByText(BASE_PROPS.selectableFields[1].label)
		).toBeInTheDocument();
		expect(queryByText(BASE_PROPS.selectableFields[0].label)).toBeNull();
	});
});
