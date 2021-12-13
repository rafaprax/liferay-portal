package com.acme.f2m9.search;

import com.acme.f2m9.model.Todo;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.util.LocalizationUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.search.spi.model.index.contributor.ModelDocumentContributor;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;

@Component(
	property = "indexer.class.name=com.acme.f2m9.model.Todo",
	service = ModelDocumentContributor.class
)
public class TodoModelDocumentContributor
	implements ModelDocumentContributor<Todo> {

	@Override
	public void contribute(Document document, Todo todo) {
		try {
			document.addDate(Field.MODIFIED_DATE, todo.getModifiedDate());

			Locale defaultLocale = PortalUtil.getSiteDefaultLocale(
				todo.getGroupId());

			String localizedTitle = LocalizationUtil.getLocalizedName(
				Field.TITLE, defaultLocale.toString());

			document.addText(localizedTitle, todo.getName());
		}
		catch (PortalException portalException) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to index todo " + todo.getTodoId(),
					portalException);
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		TodoModelDocumentContributor.class);

}