package com.liferay.calendar.upgrade.v1_0_4.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.kernel.service.AssetCategoryLocalService;
import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.asset.kernel.service.AssetLinkLocalService;
import com.liferay.asset.kernel.service.AssetVocabularyLocalService;
import com.liferay.asset.kernel.service.persistence.AssetCategoryPersistence;
import com.liferay.asset.kernel.service.persistence.AssetLinkPersistence;
import com.liferay.asset.kernel.service.persistence.AssetVocabularyPersistence;
import com.liferay.calendar.service.CalendarResourceLocalService;
import com.liferay.calendar.service.persistence.CalendarBookingPersistence;
import com.liferay.calendar.upgrade.v1_0_4.UpgradeCalEvent;
import com.liferay.counter.kernel.service.CounterLocalService;
import com.liferay.message.boards.kernel.service.MBDiscussionLocalService;
import com.liferay.message.boards.kernel.service.MBThreadLocalService;
import com.liferay.message.boards.kernel.service.persistence.MBMessagePersistence;
import com.liferay.portal.kernel.dao.db.DB;
import com.liferay.portal.kernel.dao.db.DBManagerUtil;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ResourceBlockLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.SubscriptionLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.service.persistence.ResourceActionPersistence;
import com.liferay.portal.kernel.service.persistence.UserPersistence;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.ratings.kernel.service.persistence.RatingsEntryPersistence;
import com.liferay.ratings.kernel.service.persistence.RatingsStatsPersistence;
import com.liferay.registry.Registry;
import com.liferay.registry.RegistryUtil;
import com.liferay.social.kernel.service.persistence.SocialActivityPersistence;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class UpgradeCalEventTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUpBeforeClass() throws Exception {
		setUpServices();

		setUpCalEventTable();

		_upgrade = new UpgradeCalEvent(
			_assetCategoryLocalService, _assetCategoryPersistence,
			_assetEntryLocalService, _assetLinkLocalService,
			_assetLinkPersistence, _assetVocabularyLocalService,
			_assetVocabularyPersistence, _calendarBookingPersistence,
			_calendarResourceLocalService, _classNameLocalService,
			_counterLocalService, _groupLocalService, _mbDiscussionLocalService,
			_mbMessagePersistence, _mbThreadLocalService,
			_ratingsEntryPersistence, _ratingsStatsPersistence,
			_resourceActionPersistence, _resourceBlockLocalService,
			_resourcePermissionLocalService, _socialActivityPersistence,
			_subscriptionLocalService, _userPersistence, _userLocalService);

	}

	@Test
	public void testSTup() {
	}

	protected boolean doHasTable(String tableName) throws SQLException {
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			DatabaseMetaData metadata = _connection.getMetaData();

			rs = metadata.getTables(null, null, tableName, null);

			while (rs.next()) {
				return true;
			}
		}
		finally {
			DataAccess.cleanUp(ps, rs);
		}

		return false;
	}

	protected boolean hasTable(String tableName) throws SQLException {
		if (doHasTable(StringUtil.toLowerCase(tableName)) ||
			doHasTable(StringUtil.toUpperCase(tableName)) ||
			doHasTable(tableName)) {

			return true;
		}

		return false;
	}

	protected void setUpCalEventTable() throws IOException, SQLException {
		if (!hasTable("CalEvent")) {
			StringBuilder sb = new StringBuilder();

			sb.append(
				"create table CalEvent ( uuid_ varchar(75) default null, ");
			sb.append(
				"eventId bigint(20) NOT null, groupId bigint(20) default ");
			sb.append("null, companyId bigint(20) default null, userId ");
			sb.append("bigint(20) default null, userName varchar(75) default ");
			sb.append("null, createDate datetime default null, modifiedDate ");
			sb.append(
				"datetime default null, title varchar(75) default null, ");
			sb.append("description longtext, location longtext, startDate ");
			sb.append("datetime default null, endDate datetime default null, ");
			sb.append("durationHour int(11) default null, durationMinute ");
			sb.append("int(11) default null, allDay tinyint(4) default null, ");
			sb.append("timeZoneSensitive tinyint(4) default null, type_ ");
			sb.append(
				"varchar(75) default null, repeating tinyint(4) default ");
			sb.append("null, recurrence longtext, remindBy int(11) default ");
			sb.append(
				"null, firstReminder int(11) default null, secondReminder ");
			sb.append(
				"int(11) default null, primary key (eventId), unique key ");
			sb.append("IX_5CCE79C8 (uuid_,groupId), key IX_D6FD9496 ");
			sb.append(
				"(companyId), key IX_12EE4898 (groupId), key IX_4FDDD2BF ");
			sb.append("(groupId,repeating), key IX_FCD7C63D (groupId,type_), ");
			sb.append("key IX_FD93CBFA (groupId,type_,repeating), key ");
			sb.append("IX_F6006202 (remindBy), key IX_C1AD2122 ");
			sb.append("(uuid_));");

			_db.runSQL(sb.toString());
		}
	}

	protected void setUpServices() throws SQLException {
		Registry registry = RegistryUtil.getRegistry();

		_assetCategoryLocalService = registry.getService(
			AssetCategoryLocalService.class);
		_assetCategoryPersistence = registry.getService(
			AssetCategoryPersistence.class);
		_assetEntryLocalService = registry.getService(
			AssetEntryLocalService.class);
		_assetLinkLocalService = registry.getService(
			AssetLinkLocalService.class);
		_assetLinkPersistence = registry.getService(AssetLinkPersistence.class);
		_assetVocabularyLocalService = registry.getService(
			AssetVocabularyLocalService.class);
		_assetVocabularyPersistence = registry.getService(
			AssetVocabularyPersistence.class);
		_calendarBookingPersistence = registry.getService(
			CalendarBookingPersistence.class);
		_calendarResourceLocalService = registry.getService(
			CalendarResourceLocalService.class);
		_classNameLocalService = registry.getService(
			ClassNameLocalService.class);
		_counterLocalService = registry.getService(CounterLocalService.class);
		_groupLocalService = registry.getService(GroupLocalService.class);
		_mbDiscussionLocalService = registry.getService(
			MBDiscussionLocalService.class);
		_mbMessagePersistence = registry.getService(MBMessagePersistence.class);
		_mbThreadLocalService = registry.getService(MBThreadLocalService.class);
		_ratingsEntryPersistence = registry.getService(
			RatingsEntryPersistence.class);
		_ratingsStatsPersistence = registry.getService(
			RatingsStatsPersistence.class);
		_resourceActionPersistence = registry.getService(
			ResourceActionPersistence.class);
		_resourceBlockLocalService = registry.getService(
			ResourceBlockLocalService.class);
		_resourcePermissionLocalService = registry.getService(
			ResourcePermissionLocalService.class);
		_socialActivityPersistence = registry.getService(
			SocialActivityPersistence.class);
		_subscriptionLocalService = registry.getService(
			SubscriptionLocalService.class);
		_userLocalService = registry.getService(UserLocalService.class);
		_userPersistence = registry.getService(UserPersistence.class);

		_connection = DataAccess.getUpgradeOptimizedConnection();
		_db = DBManagerUtil.getDB();
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
	private Connection _connection;
	private CounterLocalService _counterLocalService;
	private DB _db;
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
	private UpgradeCalEvent _upgrade;
	private UserLocalService _userLocalService;
	private UserPersistence _userPersistence;

}