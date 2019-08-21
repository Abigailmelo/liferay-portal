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

package com.liferay.portal.kernel.license.messaging;

import com.liferay.portal.kernel.messaging.Message;

import org.junit.Assert;

/**
 * @author Igor Beslic
 */
public class LicenseManagerMessageTypeTest {

	public void testCreateMessage() {
		LicenseManagerMessageType validateLCSLicenseManagerMessageType =
			LicenseManagerMessageType.VALIDATE_LCS;

		Message message = validateLCSLicenseManagerMessageType.createMessage();

		Object payload = message.getPayload();

		Assert.assertTrue(
			"message payload is string", payload instanceof String);

		Assert.assertEquals("{\"type\": \"liferay/lcs_status\"}", payload);

		message = validateLCSLicenseManagerMessageType.createMessage(
			LCSPortletState.NO_SUBSCRIPTION);

		payload = message.getPayload();

		Assert.assertTrue(
			"message payload is string", payload instanceof String);

		Assert.assertEquals(
			"{\"state\": 4, \"type\": \"liferay/lcs_status\"}", payload);
	}

}