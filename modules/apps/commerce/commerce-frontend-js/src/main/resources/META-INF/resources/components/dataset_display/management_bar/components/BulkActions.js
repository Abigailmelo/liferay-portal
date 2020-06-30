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

import ClayIcon from '@clayui/icon';
import ClayLink from '@clayui/link';
import classNames from 'classnames';
import PropTypes from 'prop-types';
import React, {useState, useEffect} from 'react';

import {OPEN_SIDE_PANEL} from '../../../../utilities/eventsDefinitions';
import {getOpenedSidePanel} from '../../../../utilities/sidePanels';
import DatasetDisplayContext from '../../DatasetDisplayContext';

function submit(action, method = 'get', formId, form) {
	let queriedForm;

	if (formId) {
		queriedForm = document.getElementById(formId);
	}
	if (form.current) {
		queriedForm = form.current;
	}
	if (!queriedForm) {
		throw new Error('Form not found');
	}

	queriedForm.action = action;
	queriedForm.method = method;
	queriedForm.submit();
}

function getQueryString(key, values = []) {
	return `?${key}=${values.join(',')}`;
}

function getRichPayload(payload, key, values = []) {
	const richPayload = {
		...payload,
		url: payload.baseUrl + getQueryString(key, values)
	};
	return richPayload;
}

function BulkActions(props) {
	const [
		currentSidePanelActionPayload,
		setCurrentSidePanelActionPayload
	] = useState(null);

	function handleActionClick(
		actionDefinition,
		formId,
		formRef,
		loadData,
		sidePanelId
	) {
		if (actionDefinition.target === 'sidePanel') {
			const sidePanelActionPayload = {
				baseUrl: actionDefinition.href,
				id: sidePanelId,
				onAfterSubmit: () => loadData(),
				slug: actionDefinition.slug || null
			};

			Liferay.fire(
				OPEN_SIDE_PANEL,
				getRichPayload(
					sidePanelActionPayload,
					props.selectedItemsKey,
					props.selectedItemsValue
				)
			);

			setCurrentSidePanelActionPayload(sidePanelActionPayload);
		} else {
			submit(
				actionDefinition.href,
				actionDefinition.method || 'post',
				formId,
				formRef
			);
		}
	}

	useEffect(
		() => {
			if (!currentSidePanelActionPayload) {
				return;
			}

			const currentOpenedSidePanel = getOpenedSidePanel();

			if (
				currentOpenedSidePanel &&
				currentOpenedSidePanel.id ===
					currentSidePanelActionPayload.id &&
				currentOpenedSidePanel.url.indexOf(
					currentSidePanelActionPayload.baseUrl
				) > -1
			) {
				Liferay.fire(
					OPEN_SIDE_PANEL,
					getRichPayload(
						currentSidePanelActionPayload,
						props.selectedItemsValue
					)
				);
			}
		},
		// eslint-disable-next-line react-hooks/exhaustive-deps
		[props.selectedItemsValue]
	);

	return props.selectedItemsValue.length ? (
		<DatasetDisplayContext.Consumer>
			{({formId, formRef, loadData, sidePanelId}) => (
				<nav className="management-bar management-bar-primary navbar navbar-expand-md pb-2 pt-2 subnav-tbar">
					<div
						className={classNames(
							'container-fluid container-fluid-max-xl py-1',
							!props.fluid && 'px-0'
						)}
					>
						<ul className="navbar-nav">
							<li className="nav-item">
								<span className="text-truncate">
									{props.selectedItemsValue.length}{' '}
									{Liferay.Language.get('of')}{' '}
									{props.totalItemsCount}{' '}
									{Liferay.Language.get('items-selected')}
								</span>
								<ClayLink
									className="ml-3"
									href="#"
									onClick={e => {
										e.preventDefault();
										props.selectAllItems();
									}}
								>
									{Liferay.Language.get('select-all')}
								</ClayLink>
							</li>
						</ul>
						<div className="bulk-actions">
							{props.bulkActions.map((actionDefinition, i) => (
								<button
									className={classNames(
										'btn btn-monospaced btn-link',
										i > 0 && 'ml-1'
									)}
									key={actionDefinition.label}
									onClick={() =>
										handleActionClick(
											actionDefinition,
											formId,
											formRef,
											loadData,
											sidePanelId
										)
									}
									type="button"
								>
									<ClayIcon symbol={actionDefinition.icon} />
								</button>
							))}
						</div>
					</div>
				</nav>
			)}
		</DatasetDisplayContext.Consumer>
	) : null;
}

BulkActions.propTypes = {
	bulkActions: PropTypes.arrayOf(
		PropTypes.shape({
			href: PropTypes.string.isRequired,
			icon: PropTypes.string.isRequired,
			label: PropTypes.string.isRequired,
			method: PropTypes.string,
			target: PropTypes.oneOf(['sidePanel', 'modal'])
		})
	),
	selectedItemsKey: PropTypes.string.isRequired,
	selectedItemsValue: PropTypes.array.isRequired,
	totalItemsCount: PropTypes.number.isRequired
};

export default BulkActions;
