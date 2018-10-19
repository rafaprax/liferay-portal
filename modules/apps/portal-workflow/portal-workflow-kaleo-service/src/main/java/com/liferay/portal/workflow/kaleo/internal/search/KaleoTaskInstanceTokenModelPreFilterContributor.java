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

package com.liferay.portal.workflow.kaleo.internal.search;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.model.UserGroupGroupRole;
import com.liferay.portal.kernel.model.UserGroupRole;
import com.liferay.portal.kernel.search.BooleanClauseOccur;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.search.filter.BooleanFilter;
import com.liferay.portal.kernel.search.filter.TermFilter;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserGroupGroupRoleLocalService;
import com.liferay.portal.kernel.service.UserGroupLocalService;
import com.liferay.portal.kernel.service.UserGroupRoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.filter.DateRangeFilterBuilder;
import com.liferay.portal.search.filter.FilterBuilders;
import com.liferay.portal.search.spi.model.query.contributor.ModelPreFilterContributor;
import com.liferay.portal.search.spi.model.registrar.ModelSearchSettings;
import com.liferay.portal.workflow.kaleo.service.persistence.KaleoTaskInstanceTokenQuery;

import java.text.Format;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Rafael Praxedes
 */
@Component(
	immediate = true,
	property = "indexer.class.name=com.liferay.portal.workflow.kaleo.model.KaleoTaskInstanceToken",
	service = ModelPreFilterContributor.class
)
public class KaleoTaskInstanceTokenModelPreFilterContributor
	implements ModelPreFilterContributor {

	@Override
	public void contribute(
		BooleanFilter booleanFilter, ModelSearchSettings modelSearchSettings,
		SearchContext searchContext) {

		KaleoTaskInstanceTokenQuery kaleoTaskInstanceTokenQuery =
			(KaleoTaskInstanceTokenQuery)searchContext.getAttribute(
				"kaleoTaskInstanceTokenQuery");

		appendAssigneeClassNameTerm(booleanFilter, kaleoTaskInstanceTokenQuery);
		appendAssigneeClassPKTerm(booleanFilter, kaleoTaskInstanceTokenQuery);
		appendCompletedTerm(booleanFilter, kaleoTaskInstanceTokenQuery);
		appendKaleoInstanceIdTerm(booleanFilter, kaleoTaskInstanceTokenQuery);
		appendRoleIdsTerm(booleanFilter, kaleoTaskInstanceTokenQuery);

		//not fully translated
		appendSearchByUserRolesTerm(booleanFilter, kaleoTaskInstanceTokenQuery);

		if (appendSearchCriteria(kaleoTaskInstanceTokenQuery)) {
			BooleanFilter booleanFilter1 = new BooleanFilter();

			appendAssetPrimaryKeyTerm(
				booleanFilter1, kaleoTaskInstanceTokenQuery);
			appendAssetTypeTerm(booleanFilter1, kaleoTaskInstanceTokenQuery);
			appendDueDateRangeTerm(booleanFilter1, kaleoTaskInstanceTokenQuery);

			appendTaskNameTerm(booleanFilter, kaleoTaskInstanceTokenQuery);
			appendAssetTitleTerm(booleanFilter, kaleoTaskInstanceTokenQuery);

			booleanFilter.add(booleanFilter1, BooleanClauseOccur.MUST);
		}
	}

	protected void appendAssetPrimaryKeyTerm(
		BooleanFilter booleanFilter,
		KaleoTaskInstanceTokenQuery kaleoTaskInstanceTokenQuery) {

		Long[] assetPrimaryKeys =
			kaleoTaskInstanceTokenQuery.getAssetPrimaryKeys();

		if (ArrayUtil.isEmpty(assetPrimaryKeys)) {
			return;
		}

		for (Long assetPrimaryKey : assetPrimaryKeys) {
			booleanFilter.addTerm(Field.CLASS_PK, assetPrimaryKey);
		}
	}

	protected void appendAssetTitleTerm(
		BooleanFilter booleanFilter,
		KaleoTaskInstanceTokenQuery kaleoTaskInstanceTokenQuery) {

		String assetTitle = kaleoTaskInstanceTokenQuery.getAssetTitle();

		if (Validator.isNull(assetTitle)) {
			return;
		}

		booleanFilter.addTerm(
			KaleoTaskInstanceTokenField.ASSET_TITLE, assetTitle);
	}

	protected void appendAssetTypeTerm(
		BooleanFilter booleanFilter,
		KaleoTaskInstanceTokenQuery kaleoTaskInstanceTokenQuery) {

		String[] assetTypes = kaleoTaskInstanceTokenQuery.getAssetTypes();

		if (ArrayUtil.isEmpty(assetTypes)) {
			return;
		}

		for (String assetType : assetTypes) {
			booleanFilter.addTerm(
				KaleoTaskInstanceTokenField.CLASS_NAME, assetType);
		}
	}

	protected void appendAssigneeClassNameTerm(
		BooleanFilter contextBooleanFilter,
		KaleoTaskInstanceTokenQuery kaleoTaskInstanceTokenQuery) {

		String assigneeClassName =
			kaleoTaskInstanceTokenQuery.getAssigneeClassName();

		if (Validator.isNull(assigneeClassName)) {
			return;
		}

		TermFilter parentCategoryTermsFilter = new TermFilter(
			KaleoTaskInstanceTokenField.ASSIGNEE_CLASS_NAME_IDS,
			String.valueOf(portal.getClassNameId(assigneeClassName)));

		contextBooleanFilter.add(
			parentCategoryTermsFilter, BooleanClauseOccur.MUST);
	}

	protected void appendAssigneeClassPKTerm(
		BooleanFilter contextBooleanFilter,
		KaleoTaskInstanceTokenQuery kaleoTaskInstanceTokenQuery) {

		Long assigneeClassPK = kaleoTaskInstanceTokenQuery.getAssigneeClassPK();

		if (assigneeClassPK == null) {
			return;
		}

		TermFilter parentCategoryTermsFilter = new TermFilter(
			KaleoTaskInstanceTokenField.ASSIGNEE_CLASS_PKS,
			String.valueOf(assigneeClassPK));

		contextBooleanFilter.add(
			parentCategoryTermsFilter, BooleanClauseOccur.MUST);
	}

	protected void appendCompletedTerm(
		BooleanFilter contextBooleanFilter,
		KaleoTaskInstanceTokenQuery kaleoTaskInstanceTokenQuery) {

		Boolean completed = kaleoTaskInstanceTokenQuery.isCompleted();

		if (completed == null) {
			return;
		}

		contextBooleanFilter.addRequiredTerm(
			KaleoTaskInstanceTokenField.COMPLETED, completed);
	}

	protected void appendDueDateRangeTerm(
		BooleanFilter booleanFilter,
		KaleoTaskInstanceTokenQuery kaleoTaskInstanceTokenQuery) {

		Date dueDateGT = kaleoTaskInstanceTokenQuery.getDueDateGT();
		Date dueDateLT = kaleoTaskInstanceTokenQuery.getDueDateGT();

		if ((dueDateGT == null) && (dueDateLT == null)) {
			return;
		}

		String formatPattern = PropsUtil.get(
			PropsKeys.INDEX_DATE_FORMAT_PATTERN);

		Format dateFormat = FastDateFormatFactoryUtil.getSimpleDateFormat(
			formatPattern);

		DateRangeFilterBuilder dateRangeFilterBuilder =
			_filterBuilders.dateRangeFilterBuilder();

		dateRangeFilterBuilder.setFieldName(
			KaleoTaskInstanceTokenField.DUE_DATE);

		if (dueDateGT != null) {
			dateRangeFilterBuilder.setFrom(dateFormat.format(dueDateGT));
		}

		if (dueDateLT != null) {
			dateRangeFilterBuilder.setTo(dateFormat.format(dueDateLT));
		}

		booleanFilter.add(
			dateRangeFilterBuilder.build(), BooleanClauseOccur.MUST);
	}

	protected void appendKaleoInstanceIdTerm(
		BooleanFilter contextBooleanFilter,
		KaleoTaskInstanceTokenQuery kaleoTaskInstanceTokenQuery) {

		Long kaleoInstanceId = kaleoTaskInstanceTokenQuery.getKaleoInstanceId();

		if (kaleoInstanceId == null) {
			return;
		}

		contextBooleanFilter.addRequiredTerm(
			KaleoTaskInstanceTokenField.KALEO_INSTANCE_ID, kaleoInstanceId);
	}

	protected void appendRoleIdsTerm(
		BooleanFilter contextBooleanFilter,
		KaleoTaskInstanceTokenQuery kaleoTaskInstanceTokenQuery) {

		Boolean searchByUserRoles =
			kaleoTaskInstanceTokenQuery.isSearchByUserRoles();

		if (searchByUserRoles != null) {
			return;
		}

		List<Long> roleIds = kaleoTaskInstanceTokenQuery.getRoleIds();

		if (ListUtil.isEmpty(roleIds)) {
			return;
		}

		for (Long roleId : roleIds) {
			contextBooleanFilter.add(
				new TermFilter(
					KaleoTaskInstanceTokenField.ASSIGNEE_CLASS_PKS,
					roleId.toString()),
				BooleanClauseOccur.SHOULD);
		}
	}

	protected void appendSearchByUserRolesTerm(
		BooleanFilter contextBooleanFilter,
		KaleoTaskInstanceTokenQuery kaleoTaskInstanceTokenQuery)
			{

		Boolean searchByUserRoles =
			kaleoTaskInstanceTokenQuery.isSearchByUserRoles();

		if (searchByUserRoles == null) {
			return;
		}

		if (searchByUserRoles) {
			List<Long> roleIds = getSearchByUserRoleIds(
				kaleoTaskInstanceTokenQuery);

			Map<Long, Set<Long>> roleIdGroupIdsMap = new HashMap<>();

			List<UserGroupRole> userGroupRoles =
				userGroupRoleLocalService.getUserGroupRoles(
					kaleoTaskInstanceTokenQuery.getUserId());

			for (UserGroupRole userGroupRole : userGroupRoles) {
				mapRoleIdGroupId(
					userGroupRole.getRoleId(), userGroupRole.getGroupId(),
					roleIdGroupIdsMap);
			}

			List<UserGroupGroupRole> userGroupGroupRoles =
				getUserGroupGroupRoles(kaleoTaskInstanceTokenQuery.getUserId());

			for (UserGroupGroupRole userGroupGroupRole : userGroupGroupRoles) {
				mapRoleIdGroupId(
					userGroupGroupRole.getRoleId(),
					userGroupGroupRole.getGroupId(), roleIdGroupIdsMap);
			}

			if (roleIds.isEmpty() && roleIdGroupIdsMap.isEmpty()) {
				return;
			}

			BooleanFilter searchByRolesBooleanFilter = new BooleanFilter();

			TermFilter rolesClassNameTermFilter = new TermFilter(
				KaleoTaskInstanceTokenField.ASSIGNEE_CLASS_NAME_IDS,
				String.valueOf(portal.getClassNameId(Role.class)));

			searchByRolesBooleanFilter.add(
				rolesClassNameTermFilter, BooleanClauseOccur.MUST);

			for (Long roleId : roleIds) {
				searchByRolesBooleanFilter.add(
					new TermFilter(
						KaleoTaskInstanceTokenField.ASSIGNEE_CLASS_PKS,
						String.valueOf(roleId)));
			}

			if (!roleIdGroupIdsMap.isEmpty()) {
				BooleanFilter parentRoleIdGroupIdsBooleanFilter =
					new BooleanFilter();

				for (Map.Entry<Long, Set<Long>> entry :
						roleIdGroupIdsMap.entrySet()) {

					BooleanFilter roleIdGroupIdsBooleanFilter =
						new BooleanFilter();

					roleIdGroupIdsBooleanFilter.add(
						new TermFilter(
							KaleoTaskInstanceTokenField.ASSIGNEE_CLASS_PKS,
							String.valueOf(entry.getKey())),
						BooleanClauseOccur.MUST);

					BooleanFilter assigneeGroupIdsBooleanFilter =
						new BooleanFilter();

					for (Long assigneeGroupId : entry.getValue()) {
						assigneeGroupIdsBooleanFilter.add(
							new TermFilter(
								KaleoTaskInstanceTokenField.ASSIGNEE_GROUP_IDS,
								String.valueOf(assigneeGroupId)));
					}

					roleIdGroupIdsBooleanFilter.add(
						assigneeGroupIdsBooleanFilter, BooleanClauseOccur.MUST);

					parentRoleIdGroupIdsBooleanFilter.add(
						roleIdGroupIdsBooleanFilter);

					BooleanClauseOccur parentRoleIdGroupIdsBooleanClauseOccur =
						null;

					if (!roleIds.isEmpty()) {
						parentRoleIdGroupIdsBooleanClauseOccur =
							BooleanClauseOccur.SHOULD;
					}
					else {
						parentRoleIdGroupIdsBooleanClauseOccur =
							BooleanClauseOccur.MUST;
					}

					contextBooleanFilter.add(
						parentRoleIdGroupIdsBooleanFilter,
						parentRoleIdGroupIdsBooleanClauseOccur);
				}
			}

			contextBooleanFilter.add(
				searchByRolesBooleanFilter, BooleanClauseOccur.MUST);
		}
		else {
			TermFilter assigneeClassNameTermFilter = new TermFilter(
				KaleoTaskInstanceTokenField.ASSIGNEE_CLASS_NAME_IDS,
				String.valueOf(portal.getClassNameId(User.class)));

			contextBooleanFilter.add(
				assigneeClassNameTermFilter, BooleanClauseOccur.MUST);

			TermFilter assigneeClassPKTermFilter = new TermFilter(
				KaleoTaskInstanceTokenField.ASSIGNEE_CLASS_PKS,
				String.valueOf(kaleoTaskInstanceTokenQuery.getUserId()));

			contextBooleanFilter.add(
				assigneeClassPKTermFilter, BooleanClauseOccur.MUST);
		}
	}

	protected boolean appendSearchCriteria(
		KaleoTaskInstanceTokenQuery kaleoTaskInstanceTokenQuery) {

		if (ArrayUtil.isNotEmpty(
				kaleoTaskInstanceTokenQuery.getAssetPrimaryKeys())) {

			return true;
		}

		if (ArrayUtil.isNotEmpty(kaleoTaskInstanceTokenQuery.getAssetTypes())) {
			return true;
		}

		if (kaleoTaskInstanceTokenQuery.getDueDateGT() != null) {
			return true;
		}

		if (kaleoTaskInstanceTokenQuery.getDueDateLT() != null) {
			return true;
		}

		if (Validator.isNotNull(kaleoTaskInstanceTokenQuery.getTaskName())) {
			return true;
		}

		if (Validator.isNotNull(kaleoTaskInstanceTokenQuery.getAssetTitle())) {
			return true;
		}

		return false;
	}

	protected void appendTaskNameTerm(
		BooleanFilter booleanFilter,
		KaleoTaskInstanceTokenQuery kaleoTaskInstanceTokenQuery) {

		String taskName = kaleoTaskInstanceTokenQuery.getTaskName();

		if (Validator.isNull(taskName)) {
			return;
		}

		booleanFilter.addTerm(
			KaleoTaskInstanceTokenField.KALEO_TASK_NAME, taskName);
	}

	protected List<Long> getSearchByUserRoleIds(
		KaleoTaskInstanceTokenQuery kaleoTaskInstanceTokenQuery) {

		try {
			List<Role> roles = roleLocalService.getUserRoles(
				kaleoTaskInstanceTokenQuery.getUserId());

			User user = userLocalService.getUserById(
				kaleoTaskInstanceTokenQuery.getUserId());

			List<Group> groups = new ArrayList<>();

			groups.addAll(user.getGroups());
			groups.addAll(
				groupLocalService.getOrganizationsGroups(
					user.getOrganizations()));
			groups.addAll(
				groupLocalService.getOrganizationsRelatedGroups(
					user.getOrganizations()));
			groups.addAll(
				groupLocalService.getUserGroupsGroups(user.getUserGroups()));
			groups.addAll(
				groupLocalService.getUserGroupsRelatedGroups(
					user.getUserGroups()));

			for (Group group : groups) {
				roles.addAll(
					roleLocalService.getGroupRoles(group.getGroupId()));
			}

			Stream<Role> stream = roles.parallelStream();

			return stream.map(
				Role::getRoleId
			).collect(
				Collectors.toList()
			);
		}
		catch (Exception e) {
			if (_log.isDebugEnabled()) {
				_log.debug(e, e);
			}
		}

		return Collections.emptyList();
	}

	protected List<UserGroupGroupRole> getUserGroupGroupRoles(long userId) {
		List<UserGroupGroupRole> userGroupGroupRoles = new ArrayList<>();

		List<UserGroup> userGroups = userGroupLocalService.getUserUserGroups(
			userId);

		for (UserGroup userGroup : userGroups) {
			userGroupGroupRoles.addAll(
				userGroupGroupRoleLocalService.getUserGroupGroupRoles(
					userGroup.getUserGroupId()));
		}

		return userGroupGroupRoles;
	}

	protected void mapRoleIdGroupId(
		long roleId, long groupId, Map<Long, Set<Long>> roleIdGroupIdsMap) {

		Set<Long> groupIds = roleIdGroupIdsMap.get(roleId);

		if (groupIds == null) {
			groupIds = new TreeSet<>();

			roleIdGroupIdsMap.put(roleId, groupIds);
		}

		groupIds.add(groupId);
	}

	@Reference
	protected GroupLocalService groupLocalService;

	@Reference
	protected Portal portal;

	@Reference
	protected RoleLocalService roleLocalService;

	@Reference
	protected UserGroupGroupRoleLocalService userGroupGroupRoleLocalService;

	@Reference
	protected UserGroupLocalService userGroupLocalService;

	@Reference
	protected UserGroupRoleLocalService userGroupRoleLocalService;

	@Reference
	protected UserLocalService userLocalService;

	private static final Log _log = LogFactoryUtil.getLog(
		KaleoTaskInstanceTokenModelPreFilterContributor.class);

	@Reference
	private FilterBuilders _filterBuilders;

}