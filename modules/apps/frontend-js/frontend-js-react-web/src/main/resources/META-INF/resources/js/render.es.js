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

import React from 'react';
import ReactDOM from 'react-dom';

import {ClayIconSpriteContext} from '@clayui/icon';

/**
 * Wrapper for ReactDOM render that automatically:
 *
 * - Provides commonly-needed context (for example, the Clay spritemap).
 * - Unmounts when portlets are destroyed.
 *
 * The React docs advise not to rely on the render return value, so we
 * don't propagate it.
 *
 * @see https://reactjs.org/docs/react-dom.html#render
 */
export default function render(element, container, callback) {
	const spritemap =
		Liferay.ThemeDisplay.getPathThemeImages() + '/lexicon/icons.svg';

	// eslint-disable-next-line liferay-portal/no-react-dom-render
	ReactDOM.render(
		<ClayIconSpriteContext.Provider value={spritemap}>
			{element}
		</ClayIconSpriteContext.Provider>,
		container,
		callback
	);

	Liferay.once('destroyPortlet', () =>
		ReactDOM.unmountComponentAtNode(container)
	);
}
