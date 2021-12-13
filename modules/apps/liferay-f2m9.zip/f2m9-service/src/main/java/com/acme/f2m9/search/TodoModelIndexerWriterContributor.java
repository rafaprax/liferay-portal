package com.acme.f2m9.search;

import com.acme.f2m9.model.Todo;
import com.acme.f2m9.service.TodoLocalService;

import com.liferay.portal.search.batch.BatchIndexingActionable;
import com.liferay.portal.search.batch.DynamicQueryBatchIndexingActionableFactory;
import com.liferay.portal.search.spi.model.index.contributor.ModelIndexerWriterContributor;
import com.liferay.portal.search.spi.model.index.contributor.helper.ModelIndexerWriterDocumentHelper;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(
	property = "indexer.class.name=com.acme.f2m9.model.Todo",
	service = ModelIndexerWriterContributor.class
)
public class TodoModelIndexerWriterContributor
	implements ModelIndexerWriterContributor<Todo> {

	@Override
	public void customize(
		BatchIndexingActionable batchIndexingActionable,
		ModelIndexerWriterDocumentHelper modelIndexerWriterDocumentHelper) {

		batchIndexingActionable.setPerformActionMethod(
			(Todo todo) -> batchIndexingActionable.addDocuments(
				modelIndexerWriterDocumentHelper.getDocument(todo)));
	}

	@Override
	public BatchIndexingActionable getBatchIndexingActionable() {
		return dynamicQueryBatchIndexingActionableFactory.
			getBatchIndexingActionable(
				todoLocalService.getIndexableActionableDynamicQuery());
	}

	@Override
	public long getCompanyId(Todo todo) {
		return todo.getCompanyId();
	}

	@Reference
	protected DynamicQueryBatchIndexingActionableFactory
		dynamicQueryBatchIndexingActionableFactory;

	@Reference
	protected TodoLocalService todoLocalService;

}