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

package com.liferay.document.library.kernel.service;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.repository.model.FileVersion;

/**
 * @author Roberto Díaz
 * @author Adolfo Pérez
 */
public interface FileVersionPreviewEventListener {

	public void deleteDLFileVersionPreviews(long fileEntryId)
		throws PortalException;

	public void onFailure(FileVersion fileVersion);

	public void onSuccess(FileVersion fileVersion);

}