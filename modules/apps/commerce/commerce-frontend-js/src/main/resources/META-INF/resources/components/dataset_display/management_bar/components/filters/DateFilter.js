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

import ClayButton from '@clayui/button';
import classNames from 'classnames';
import PropTypes from 'prop-types';
import React, {useState, useEffect} from 'react';

import {
	formatDateObject,
	getDateFromDateString
} from '../../../utilities/dates';
import getAppContext from '../Context';

function DateFilter(props) {
	const {actions} = getAppContext();
	const [value, setValue] = useState(
		props.value ? formatDateObject(props.value) : ''
	);

	useEffect(() => {
		setValue(() => (props.value ? formatDateObject(props.value) : ''));
	}, [props.value]);

	return (
		<div className="form-group">
			<div className="input-group">
				<div
					className={classNames('input-group-item', {
						'input-group-prepend': props.inputText
					})}
				>
					<input
						className="form-control"
						max={props.max && formatDateObject(props.max)}
						min={props.min && formatDateObject(props.min)}
						onChange={e => setValue(e.target.value)}
						pattern="\d{4}-\d{2}-\d{2}"
						placeholder={props.placeholder || 'yyyy-mm-dd'}
						type="date"
						value={value}
					/>
				</div>
			</div>
			<div className="mt-3">
				<ClayButton
					className="btn-sm"
					disabled={
						value ==
						(props.value ? formatDateObject(props.value) : '')
					}
					onClick={() => {
						actions.updateFilterValue(
							props.id,
							value ? getDateFromDateString(value) : null
						);
					}}
				>
					{props.panelType === 'edit'
						? Liferay.Language.get('edit-filter')
						: Liferay.Language.get('add-filter')}
				</ClayButton>
			</div>
		</div>
	);
}

DateFilter.propTypes = {
	id: PropTypes.string.isRequired,
	invisible: PropTypes.bool,
	label: PropTypes.string.isRequired,
	max: PropTypes.shape({
		day: PropTypes.number,
		month: PropTypes.number,
		year: PropTypes.number
	}),
	min: PropTypes.shape({
		day: PropTypes.number,
		month: PropTypes.number,
		year: PropTypes.number
	}),
	placeholder: PropTypes.string,
	type: PropTypes.oneOf(['date']).isRequired,
	value: PropTypes.shape({
		day: PropTypes.number,
		month: PropTypes.number,
		year: PropTypes.number
	})
};

export default DateFilter;
