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

package com.liferay.user.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Organization;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.OrganizationTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Brian Wing Shun Chan
 * @author José Manuel Navarro
 * @author Drew Brokke
 */
@RunWith(Arquillian.class)
public class UserServiceWhenGroupAdminUnsetsGroupUsersTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() throws Exception {
		_organization = OrganizationTestUtil.addOrganization(true);

		_group = GroupTestUtil.addGroup();

		_groupAdminUser = UserTestUtil.addGroupAdminUser(_group);
	}

	@Test
	public void testShouldUnsetGroupAdmin() throws Exception {
		User groupAdminUser = UserTestUtil.addGroupAdminUser(_group);

		try {
			UserServiceTestUtil.unsetGroupUsers(
				_group.getGroupId(), _groupAdminUser, groupAdminUser);

			Assert.assertTrue(
				_userLocalService.hasGroupUser(
					_group.getGroupId(), groupAdminUser.getUserId()));
		}
		finally {
			_userLocalService.deleteUser(groupAdminUser);
		}
	}

	@Test
	public void testShouldUnsetGroupOwner() throws Exception {
		User groupOwnerUser = UserTestUtil.addGroupOwnerUser(_group);

		try {
			UserServiceTestUtil.unsetGroupUsers(
				_group.getGroupId(), _groupAdminUser, groupOwnerUser);

			Assert.assertTrue(
				_userLocalService.hasGroupUser(
					_group.getGroupId(), groupOwnerUser.getUserId()));
		}
		finally {
			_userLocalService.deleteUser(groupOwnerUser);
		}
	}

	@Test
	public void testShouldUnsetOrganizationAdmin() throws Exception {
		User organizationAdminUser = UserTestUtil.addOrganizationAdminUser(
			_organization);

		try {
			UserServiceTestUtil.unsetOrganizationUsers(
				_organization.getOrganizationId(), _groupAdminUser,
				organizationAdminUser);

			Assert.assertTrue(
				_userLocalService.hasOrganizationUser(
					_organization.getOrganizationId(),
					organizationAdminUser.getUserId()));
		}
		finally {
			_userLocalService.deleteUser(organizationAdminUser);
		}
	}

	@Test
	public void testShouldUnsetOrganizationOwner() throws Exception {
		User organizationOwnerUser = UserTestUtil.addOrganizationOwnerUser(
			_organization);

		try {
			UserServiceTestUtil.unsetOrganizationUsers(
				_organization.getOrganizationId(), _groupAdminUser,
				organizationOwnerUser);

			Assert.assertTrue(
				_userLocalService.hasOrganizationUser(
					_organization.getOrganizationId(),
					organizationOwnerUser.getUserId()));
		}
		finally {
			_userLocalService.deleteUser(organizationOwnerUser);
		}
	}

	@DeleteAfterTestRun
	private Group _group;

	@DeleteAfterTestRun
	private User _groupAdminUser;

	@DeleteAfterTestRun
	private Organization _organization;

	@Inject
	private UserLocalService _userLocalService;

}