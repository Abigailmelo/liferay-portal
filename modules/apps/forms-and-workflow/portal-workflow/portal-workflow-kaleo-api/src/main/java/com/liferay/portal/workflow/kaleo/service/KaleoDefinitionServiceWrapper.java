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

package com.liferay.portal.workflow.kaleo.service;

import com.liferay.portal.kernel.service.ServiceWrapper;

/**
 * Provides a wrapper for {@link KaleoDefinitionService}.
 *
 * @author Brian Wing Shun Chan
 * @see KaleoDefinitionService
 * @generated
 */
public class KaleoDefinitionServiceWrapper
	implements KaleoDefinitionService, ServiceWrapper<KaleoDefinitionService> {

	public KaleoDefinitionServiceWrapper(
		KaleoDefinitionService kaleoDefinitionService) {

		_kaleoDefinitionService = kaleoDefinitionService;
	}

	/**
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify or reference this interface directly. Always use {@link KaleoDefinitionServiceUtil} to access the kaleo definition remote service. Add custom service methods to <code>com.liferay.portal.workflow.kaleo.service.impl.KaleoDefinitionServiceImpl</code> and rerun ServiceBuilder to automatically copy the method declarations to this interface.
	 */
	@Override
	public java.util.List
		<com.liferay.portal.workflow.kaleo.model.KaleoDefinition>
			getKaleoDefinitions(int start, int end) {

		return _kaleoDefinitionService.getKaleoDefinitions(start, end);
	}

	@Override
	public java.util.List
		<com.liferay.portal.workflow.kaleo.model.KaleoDefinition>
			getKaleoDefinitions(long companyId, int start, int end) {

		return _kaleoDefinitionService.getKaleoDefinitions(
			companyId, start, end);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	@Override
	public String getOSGiServiceIdentifier() {
		return _kaleoDefinitionService.getOSGiServiceIdentifier();
	}

	@Override
	public KaleoDefinitionService getWrappedService() {
		return _kaleoDefinitionService;
	}

	@Override
	public void setWrappedService(
		KaleoDefinitionService kaleoDefinitionService) {

		_kaleoDefinitionService = kaleoDefinitionService;
	}

	private KaleoDefinitionService _kaleoDefinitionService;

}