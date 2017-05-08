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

package com.liferay.vulcan.wiring.osgi;

import com.liferay.vulcan.error.VulcanDeveloperError;
import com.liferay.vulcan.representor.ModelRepresentorMapper;
import com.liferay.vulcan.wiring.osgi.internal.GenericUtil;
import com.liferay.vulcan.wiring.osgi.internal.ModelRepresentorMapperTuple;
import com.liferay.vulcan.wiring.osgi.internal.RelatedModel;
import com.liferay.vulcan.wiring.osgi.internal.RepresentorBuilderImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

/**
 * @author Alejandro Hernández
 * @author Carlos Sierra Andrés
 * @author Jorge Ferrer
 */
@Component(immediate = true, service = RepresentorManager.class)
public class RepresentorManager {

	public RepresentorManager() {
		Bundle bundle = FrameworkUtil.getBundle(RepresentorManager.class);

		_bundleContext = bundle.getBundleContext();
	}

	public <T, V> List<RelatedModel<T, V>> getEmbeddedRelatedModels(
		Class<T> modelClass) {

		return (List)_embeddedRelatedModelLists.get(modelClass.getName());
	}

	public <T> Map<String, Function<T, Object>> getFieldFunctions(
		Class<T> modelClass) {

		return (Map)_fieldFunctionMaps.get(modelClass.getName());
	}

	public <T> String getIdentifier(Class<T> modelClass, T model) {
		Function<T, String> identifierFunction =
			(Function<T, String>)_identifierFunctions.get(modelClass.getName());

		return identifierFunction.apply(model);
	}

	public <T, V> List<RelatedModel<T, V>> getLinkedRelatedModels(
		Class<T> modelClass) {

		return (List)_linkedRelatedModelLists.get(modelClass.getName());
	}

	public <T> Map<String, String> getLinks(Class<T> modelClass) {
		return _linkMaps.get(modelClass.getName());
	}

	public <T> Optional<ModelRepresentorMapper<T>>
		getModelRepresentorMapperOptional(Class<T> modelClass) {

		TreeSet<ModelRepresentorMapperTuple<?>> modelRepresentorMapperTuples =
			_modelRepresentorMapperSets.get(modelClass.getName());

		Optional<TreeSet<ModelRepresentorMapperTuple<?>>>
			modelRepresentorMapperTuplesOptional = Optional.ofNullable(
				modelRepresentorMapperTuples);

		Optional<ModelRepresentorMapperTuple<?>>
			firstModelRepresentorMapperTuple =
				modelRepresentorMapperTuplesOptional.map(TreeSet::first);

		return firstModelRepresentorMapperTuple.map(
			modelRepresentorMapperTuple ->
				(ModelRepresentorMapper<T>)modelRepresentorMapperTuple.
					getModelRepresentorMapper());
	}

	public <T> List<String> getTypes(Class<T> modelClass) {
		return _typeLists.get(modelClass.getName());
	}

	@Reference(
		cardinality = ReferenceCardinality.MULTIPLE,
		policy = ReferencePolicy.DYNAMIC,
		policyOption = ReferencePolicyOption.GREEDY
	)
	protected <T> void setServiceReference(
		ServiceReference<ModelRepresentorMapper<T>> serviceReference) {

		ModelRepresentorMapper<T> modelRepresentorMapper =
			_bundleContext.getService(serviceReference);

		Class<T> modelClass = _getModelClass(modelRepresentorMapper);

		_addModelRepresentorMapper(
			serviceReference, modelRepresentorMapper, modelClass);

		_addModelClassMaps(modelClass);
	}

	protected <T> void unsetServiceReference(
		ServiceReference<ModelRepresentorMapper<T>> serviceReference) {

		ModelRepresentorMapper<T> modelRepresentorMapper =
			_bundleContext.getService(serviceReference);

		Class<T> modelClass = _getModelClass(modelRepresentorMapper);

		_removeModelRepresentorMapper(modelRepresentorMapper, modelClass);

		_removeModelClassMaps(modelClass);

		Optional<ModelRepresentorMapper<T>> optional =
			getModelRepresentorMapperOptional(modelClass);

		optional.ifPresent(
			firstModelRepresentorMapper -> _addModelClassMaps(modelClass));
	}

	private <T> void _addModelClassMaps(Class<T> modelClass) {
		Map<String, Function<?, Object>> fieldFunctions = new HashMap<>();

		_fieldFunctionMaps.put(modelClass.getName(), fieldFunctions);

		List<RelatedModel<?, ?>> embeddedRelatedModels = new ArrayList<>();

		_embeddedRelatedModelLists.put(
			modelClass.getName(), embeddedRelatedModels);

		Map<String, String> links = new HashMap<>();

		_linkMaps.put(modelClass.getName(), links);

		List<RelatedModel<?, ?>> linkedRelatedModels = new ArrayList<>();

		_linkedRelatedModelLists.put(modelClass.getName(), linkedRelatedModels);

		List<String> types = new ArrayList<>();

		_typeLists.put(modelClass.getName(), types);

		getModelRepresentorMapperOptional(modelClass).ifPresent(
			modelRepresentorMapper -> modelRepresentorMapper.buildRepresentor(
				new RepresentorBuilderImpl<>(
					modelClass, _identifierFunctions, fieldFunctions,
					embeddedRelatedModels, linkedRelatedModels, links, types)));
	}

	private <T> void _addModelRepresentorMapper(
		ServiceReference<ModelRepresentorMapper<T>> serviceReference,
		ModelRepresentorMapper<T> modelRepresentorMapper, Class<T> modelClass) {

		_modelRepresentorMapperSets.computeIfAbsent(
			modelClass.getName(), name -> new TreeSet<>());

		ModelRepresentorMapperTuple<T> modelRepresentorMapperTuple =
			new ModelRepresentorMapperTuple<>(
				serviceReference, modelRepresentorMapper);

		TreeSet<ModelRepresentorMapperTuple<?>> modelRepresentorMapperTuples =
			_modelRepresentorMapperSets.get(modelClass.getName());

		modelRepresentorMapperTuples.add(modelRepresentorMapperTuple);
	}

	private <T> Class<T> _getModelClass(
		ModelRepresentorMapper<T> modelRepresentorMapper) {

		Optional<Class<T>> genericClassOptional =
			GenericUtil.getGenericClassOptional(
				modelRepresentorMapper, ModelRepresentorMapper.class);

		return genericClassOptional.orElseThrow(() ->
			new VulcanDeveloperError.MustHaveValidGenericType(
				modelRepresentorMapper.getClass()));
	}

	private <T> void _removeModelClassMaps(Class<T> modelClass) {
		_embeddedRelatedModelLists.remove(modelClass.getName());
		_linkedRelatedModelLists.remove(modelClass.getName());
		_linkMaps.remove(modelClass.getName());
		_fieldFunctionMaps.remove(modelClass.getName());
		_identifierFunctions.remove(modelClass.getName());
		_typeLists.remove(modelClass.getName());
	}

	private <T> void _removeModelRepresentorMapper(
		ModelRepresentorMapper modelRepresentorMapper, Class<T> modelClass) {

		TreeSet<ModelRepresentorMapperTuple<?>> modelRepresentorMapperTuples =
			_modelRepresentorMapperSets.get(modelClass.getName());

		modelRepresentorMapperTuples.removeIf(
			modelRepresentorMapperTuple -> {
				if (modelRepresentorMapperTuple.getModelRepresentorMapper() ==
						modelRepresentorMapper) {

					return true;
				}

				return false;
			});
	}

	private final BundleContext _bundleContext;
	private final Map<String, List<RelatedModel<?, ?>>>
		_embeddedRelatedModelLists = new ConcurrentHashMap<>();
	private final Map<String, Map<String, Function<?, Object>>>
		_fieldFunctionMaps = new ConcurrentHashMap<>();
	private final Map<String, Function<?, String>> _identifierFunctions =
		new ConcurrentHashMap<>();
	private final Map<String, List<RelatedModel<?, ?>>>
		_linkedRelatedModelLists = new ConcurrentHashMap<>();
	private final Map<String, Map<String, String>> _linkMaps =
		new ConcurrentHashMap<>();
	private final Map<String, TreeSet<ModelRepresentorMapperTuple<?>>>
		_modelRepresentorMapperSets = new ConcurrentHashMap<>();
	private final Map<String, List<String>> _typeLists =
		new ConcurrentHashMap<>();

}