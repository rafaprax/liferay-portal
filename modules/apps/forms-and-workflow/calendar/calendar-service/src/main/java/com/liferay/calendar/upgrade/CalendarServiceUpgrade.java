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

package com.liferay.calendar.upgrade;

import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.asset.kernel.service.AssetLinkLocalService;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.asset.kernel.service.persistence.AssetCategoryPersistence;
import com.liferay.asset.kernel.service.persistence.AssetLinkPersistence;
import com.liferay.asset.kernel.service.persistence.AssetVocabularyPersistence;
import com.liferay.calendar.service.CalendarResourceLocalService;
import com.liferay.calendar.service.persistence.CalendarBookingPersistence;
import com.liferay.calendar.upgrade.v1_0_1.UpgradeCalendar;
import com.liferay.calendar.upgrade.v1_0_3.UpgradeCalendarResource;
import com.liferay.calendar.upgrade.v1_0_3.UpgradeClassNames;
import com.liferay.calendar.upgrade.v1_0_3.UpgradeCompanyId;
import com.liferay.calendar.upgrade.v1_0_3.UpgradeLastPublishDate;
import com.liferay.calendar.upgrade.v1_0_4.UpgradeCalEvent;
import com.liferay.counter.kernel.service.CounterLocalService;
import com.liferay.message.boards.kernel.service.MBDiscussionLocalService;
import com.liferay.message.boards.kernel.service.MBThreadLocalService;
import com.liferay.message.boards.kernel.service.persistence.MBMessagePersistence;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ResourceBlockLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.SubscriptionLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.persistence.ResourceActionPersistence;
import com.liferay.portal.kernel.service.persistence.UserPersistence;
import com.liferay.portal.kernel.upgrade.DummyUpgradeStep;
import com.liferay.portal.upgrade.registry.UpgradeStepRegistrator;
import com.liferay.ratings.kernel.service.persistence.RatingsEntryPersistence;
import com.liferay.ratings.kernel.service.persistence.RatingsStatsPersistence;
import com.liferay.social.kernel.service.persistence.SocialActivityPersistence;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Iván Zaera
 * @author Manuel de la Peña
 */
@Component(immediate = true, service = UpgradeStepRegistrator.class)
public class CalendarServiceUpgrade implements UpgradeStepRegistrator {

	@Override
	public void register(Registry registry) {
		registry.register(
			"com.liferay.calendar.service", "0.0.1", "1.0.0",
			new com.liferay.calendar.upgrade.v1_0_0.UpgradeCalendarBooking());

		registry.register(
			"com.liferay.calendar.service", "1.0.0", "1.0.1",
			new com.liferay.calendar.upgrade.v1_0_1.UpgradeCalendarBooking(),
			new UpgradeCalendar());

		registry.register(
			"com.liferay.calendar.service", "1.0.1", "1.0.2",
			new DummyUpgradeStep());

		registry.register(
			"com.liferay.calendar.service", "1.0.2", "1.0.3",
			new UpgradeClassNames(),
			new UpgradeCalendarResource(
				_classNameLocalService, _companyLocalService,
				_userLocalService),
			new UpgradeCompanyId(), new UpgradeLastPublishDate());

		registry.register(
			"com.liferay.calendar.service", "1.0.3", "1.0.4",
			new UpgradeCalEvent(
				_assetCategoryLocalService, _assetCategoryPersistence,
				_assetEntryLocalService, _assetLinkLocalService,
				_assetLinkPersistence, _assetVocabularyLocalService,
				_assetVocabularyPersistence, _calendarBookingPersistence,
				_calendarResourceLocalService, _classNameLocalService,
				_counterLocalService, _groupLocalService,
				_mbDiscussionLocalService, _mbMessagePersistence,
				_mbThreadLocalService, _ratingsEntryPersistence,
				_ratingsStatsPersistence, _resourceActionPersistence,
				_resourceBlockLocalService, _resourcePermissionLocalService,
				_socialActivityPersistence, _subscriptionLocalService,
				_userPersistence, _userLocalService));
	}

	@Reference(unbind = "-")
	protected void setAssetCategoryLocalService(
		AssetCategoryLocalService assetCategoryLocalService) {

		_assetCategoryLocalService = assetCategoryLocalService;
	}

	@Reference(unbind = "-")
	protected void setAssetCategoryPersistence(
		AssetCategoryPersistence assetCategoryPersistence) {

		_assetCategoryPersistence = assetCategoryPersistence;
	}

	@Reference(unbind = "-")
	protected void setAssetEntryLocalService(
		AssetEntryLocalService assetEntryLocalService) {

		_assetEntryLocalService = assetEntryLocalService;
	}

	@Reference(unbind = "-")
	protected void setAssetLinkLocalService(
		AssetLinkLocalService assetLinkLocalService) {

		_assetLinkLocalService = assetLinkLocalService;
	}

	@Reference(unbind = "-")
	protected void setAssetLinkPersistence(
		AssetLinkPersistence assetLinkPersistence) {

		_assetLinkPersistence = assetLinkPersistence;
	}

	@Reference(unbind = "-")
	protected void setAssetVocabularyLocalService(
		AssetVocabularyLocalService assetVocabularyLocalService) {

		_assetVocabularyLocalService = assetVocabularyLocalService;
	}

	@Reference(unbind = "-")
	protected void setAssetVocabularyPersistence(
		AssetVocabularyPersistence assetVocabularyPersistence) {

		_assetVocabularyPersistence = assetVocabularyPersistence;
	}

	@Reference(unbind = "-")
	protected void setCalendarBookingPersistence(
		CalendarBookingPersistence calendarBookingPersistence) {

		_calendarBookingPersistence = calendarBookingPersistence;
	}

	@Reference(unbind = "-")
	protected void setCalendarResourceLocalService(
		CalendarResourceLocalService calendarResourceLocalService) {

		_calendarResourceLocalService = calendarResourceLocalService;
	}

	@Reference(unbind = "-")
	protected void setClassNameLocalService(
		ClassNameLocalService classNameLocalService) {

		_classNameLocalService = classNameLocalService;
	}

	@Reference(unbind = "-")
	protected void setCompanyLocalService(
		CompanyLocalService companyLocalService) {

		_companyLocalService = companyLocalService;
	}

	@Reference(unbind = "-")
	protected void setCounterLocalService(
		CounterLocalService counterLocalService) {

		_counterLocalService = counterLocalService;
	}

	@Reference(unbind = "-")
	protected void setGroupLocalService(GroupLocalService groupLocalService) {
		_groupLocalService = groupLocalService;
	}

	@Reference(unbind = "-")
	protected void setMBDiscussionLocalService(
		MBDiscussionLocalService mbDiscussionLocalService) {

		_mbDiscussionLocalService = mbDiscussionLocalService;
	}

	@Reference(unbind = "-")
	protected void setMBMessagePersistence(
		MBMessagePersistence mbMessagePersistence) {

		_mbMessagePersistence = mbMessagePersistence;
	}

	@Reference(unbind = "-")
	protected void setMBThreadLocalService(
		MBThreadLocalService mbThreadLocalService) {

		_mbThreadLocalService = mbThreadLocalService;
	}

	@Reference(unbind = "-")
	protected void setRatingsEntryPersistence(
		RatingsEntryPersistence ratingsEntryPersistence) {

		_ratingsEntryPersistence = ratingsEntryPersistence;
	}

	@Reference(unbind = "-")
	protected void setRatingsStatsPersistence(
		RatingsStatsPersistence ratingsStatsPersistence) {

		_ratingsStatsPersistence = ratingsStatsPersistence;
	}

	@Reference(unbind = "-")
	protected void setResourceActionPersistence(
		ResourceActionPersistence resourceActionPersistence) {

		_resourceActionPersistence = resourceActionPersistence;
	}

	@Reference(unbind = "-")
	protected void setResourceBlockLocalService(
		ResourceBlockLocalService resourceBlockLocalService) {

		_resourceBlockLocalService = resourceBlockLocalService;
	}

	@Reference(unbind = "-")
	protected void setResourcePermissionLocalService(
		ResourcePermissionLocalService resourcePermissionLocalService) {

		_resourcePermissionLocalService = resourcePermissionLocalService;
	}

	@Reference(unbind = "-")
	protected void setSocialActivityPersistence(
		SocialActivityPersistence socialActivityPersistence) {

		_socialActivityPersistence = socialActivityPersistence;
	}

	@Reference(unbind = "-")
	protected void setSubscriptionLocalService(
		SubscriptionLocalService subscriptionLocalService) {

		_subscriptionLocalService = subscriptionLocalService;
	}

	@Reference(unbind = "-")
	protected void setUserLocalService(UserLocalService userLocalService) {
		_userLocalService = userLocalService;
	}

	@Reference(unbind = "-")
	protected void setUserPersistence(UserPersistence userPersistence) {
		_userPersistence = userPersistence;
	}

	private AssetCategoryLocalService _assetCategoryLocalService;
	private AssetCategoryPersistence _assetCategoryPersistence;
	private AssetEntryLocalService _assetEntryLocalService;
	private AssetLinkLocalService _assetLinkLocalService;
	private AssetLinkPersistence _assetLinkPersistence;
	private AssetVocabularyLocalService _assetVocabularyLocalService;
	private AssetVocabularyPersistence _assetVocabularyPersistence;
	private CalendarBookingPersistence _calendarBookingPersistence;
	private CalendarResourceLocalService _calendarResourceLocalService;
	private ClassNameLocalService _classNameLocalService;
	private CompanyLocalService _companyLocalService;
	private CounterLocalService _counterLocalService;
	private GroupLocalService _groupLocalService;
	private MBDiscussionLocalService _mbDiscussionLocalService;
	private MBMessagePersistence _mbMessagePersistence;
	private MBThreadLocalService _mbThreadLocalService;
	private RatingsEntryPersistence _ratingsEntryPersistence;
	private RatingsStatsPersistence _ratingsStatsPersistence;
	private ResourceActionPersistence _resourceActionPersistence;
	private ResourceBlockLocalService _resourceBlockLocalService;
	private ResourcePermissionLocalService _resourcePermissionLocalService;
	private SocialActivityPersistence _socialActivityPersistence;
	private SubscriptionLocalService _subscriptionLocalService;
	private UserLocalService _userLocalService;
	private UserPersistence _userPersistence;

}