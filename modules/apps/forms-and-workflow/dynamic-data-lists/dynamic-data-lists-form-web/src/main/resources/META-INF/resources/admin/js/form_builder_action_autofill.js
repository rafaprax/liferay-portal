AUI.add(
	'liferay-ddl-form-builder-action-autofill',
	function(A) {
		var Lang = A.Lang;

		var TPL_ACTION_FIELD_LABEL = '<label class="lfr-ddm-form-field-container-inline">{message}</label>';

		var FormBuilderActionAutofill = A.Component.create(
			{
				ATTRS: {
					action: {
						value: ''
					},

					fields: {
						value: []
					},

					getDataProviderInstancesURL: {
						value: ''
					},

					getDataProviderParametersSettingsURL: {
						value: ''
					},

					index: {
						value: ''
					},

					options: {
						value: []
					},

					portletNamespace: {
						value: ''
					},

					strings: {
						value: {
							fromDataProvider: Liferay.Language.get('from-data-provider'),
							requiredField: Liferay.Language.get('required-field'),
							dataProviderParameterInputDescription: Liferay.Language.get('data-provider-parameter-input-description'),
							dataProviderParameterOutputDescription: Liferay.Language.get('data-provider-parameter-output-description')
						}
					}
				},

				AUGMENTS: [],

				EXTENDS: Liferay.DDL.FormBuilderAction,

				NAME: 'liferay-ddl-form-builder-action-autofill',

				prototype: {
					initializer: function() {
						var instance = this;

						instance._inputParameters = [];

						instance._outputParameters = [];
					},

					getValue: function() {
						var instance = this;

						return {
							action: 'auto-fill',
							ddmDataProviderInstanceUUID: instance._getUUId(instance._dataProvidersList.getValue()),
							inputs: instance._getInputValue(),
							outputs: instance._getOutputValue()
						};
					},

					render: function() {
						var instance = this;

						var boundingBox = instance.get('boundingBox');

						var strings = instance.get('strings');
						
						var index = instance.get('index');

						var fieldsListContainer = instance.get('boundingBox').one('.target-' + index);

						instance._createDataProviderList().render(fieldsListContainer);
					},

					_afterDataProviderChange: function(event) {
						var instance = this;

						var ddmDataProviderInstanceId = event.newVal[0];

						A.io.request(
							instance.get('getDataProviderParametersSettingsURL'),
							{
								data: instance._getDataProviderPayload(ddmDataProviderInstanceId),
								method: 'GET',
								on: {
									success: function(event, id, xhr) {
										var result = xhr.responseText;

										if (result) {
											instance._createDataProviderParametersSettings(JSON.parse(result));
										}
									}
								}
							}
						);
					},

					_createDataProviderInputParametersSettings: function(inputParameters) {
						var instance = this;

						var index = instance.get('index');

						var boundingBox = instance.get('boundingBox');

						var inputParametersContainer = boundingBox.one('.additional-info-' + index).one('.data-provider-parameter-input-list');

						var inputParameter;

						for (var i = 0; i < inputParameters.length; i++) {
							var name = inputParameters[i].name;

							var value;

							var action = instance.get('action');

							inputParametersContainer.append(name);

							if (action && action.inputs && action.inputs[name]) {
								value = action.inputs[name];
							}

							inputParameter = new Liferay.DDM.Field.Select(
								{
									fieldName: instance.get('index') + '-action',
									options: instance.get('fields'),
									showLabel: false,
									visible: true
								}
							).render(inputParametersContainer);

							instance._inputParameters.push(
								{
									field: inputParameter,
									parameter: name
								}
							);
						}
					},

					_createDataProviderList: function() {
						var instance = this;

						instance._dataProvidersList = new Liferay.DDM.Field.Select(
							{
								fieldName: instance.get('index') + '-action',
								showLabel: false,
								visible: true
							}
						);

						instance._dataProvidersList.get('container').addClass('lfr-ddm-form-field-container-inline');

						instance._dataProvidersList.after('valueChange', A.bind(instance._afterDataProviderChange, instance));

						instance._fillDataProvidersSelectField();

						return instance._dataProvidersList;
					},

					_createDataProviderOutputParametersSettings: function(outputParameters) {
						var instance = this;

						var index = instance.get('index');

						var boundingBox = instance.get('boundingBox');

						var outputParametersContainer = boundingBox.one('.additional-info-' + index).one('.data-provider-parameter-output-list');

						var outputParameter;

						for (var i = 0; i < outputParameters.length; i++) {
							var name = outputParameters[i].name;

							var value;

							var action = instance.get('action');

							outputParametersContainer.append(name);

							if (action && action.outputs && action.outputs[name]) {
								value = action.outputs[name];
							}

							outputParameter = new Liferay.DDM.Field.Select(
								{
									fieldName: instance.get('index') + '-action',
									label: outputParameters[i],
									options: instance.get('fields'),
									showLabel: false,
									value: value,
									visible: true
								}
							).render(outputParametersContainer);

							instance._outputParameters.push(
								{
									field: outputParameter,
									parameter: name
								}
							);
						}
					},

					_createDataProviderParametersSettings: function(dataProviderParametersSettings) {
						var instance = this;

						var index = instance.get('index');

						var dataProviderParametersContainer = instance.get('boundingBox').one('.additional-info-' + index);

						dataProviderParametersContainer.setHTML(instance._getRuleContainerTemplate());

						instance._createDataProviderInputParametersSettings(dataProviderParametersSettings.inputs);

						instance._createDataProviderOutputParametersSettings(dataProviderParametersSettings.outputs);
					},

					_fillDataProvidersSelectField: function() {
						var instance = this;

						A.io.request(
							instance.get('getDataProviderInstancesURL'),
							{
								method: 'GET',
								on: {
									success: function(event, id, xhr) {
										var result = JSON.parse(xhr.responseText);

										instance._renderDataProvidersList(result);
									}
								}
							}
						);
					},

					_getDataProviderPayload: function(ddmDataProviderInstanceId) {
						var instance = this;

						var portletNamespace = instance.get('portletNamespace');

						var payload = Liferay.Util.ns(
							portletNamespace, 
							{
								ddmDataProviderInstanceId: ddmDataProviderInstanceId
							}
						);

						return payload;
					},

					_getFieldsByType: function(type) {
						var instance = this;

						var fields = instance.get('fields');

						var fieldsFiltered = [];

						for (var i = 0; i < fields.length; i++) {
							if (fields[i].type === type) {
								fieldsFiltered.push(fields[i]);
							}
						}

						return fieldsFiltered;
					},

					_getInputValue: function() {
						var instance = this;

						var inputParameters = instance._inputParameters;

						var inputParameterValues = {};

						for (var i = 0; i < inputParameters.length; i++) {
							inputParameterValues[inputParameters[i].parameter] = inputParameters[i].field.getValue();
						}

						return inputParameterValues;
					},

					_getOutputValue: function() {
						var instance = this;

						var outputParameters = instance._outputParameters;

						var outputParameterValues = {};

						for (var i = 0; i < outputParameters.length; i++) {
							outputParameterValues[outputParameters[i].parameter] = outputParameters[i].field.getValue();
						}

						return outputParameterValues;
					},

					_getRuleContainerTemplate: function() {
						var instance = this;

						var strings = instance.get('strings');

						var dataProviderParametersTemplateRenderer = Liferay.DDM.SoyTemplateUtil.getTemplateRenderer('ddl.data_provider_parameter.settings');

						return dataProviderParametersTemplateRenderer(
							{
								strings: strings
							}
						);
					},

					_getUUId: function(id) {
						var instance = this;

						var dataProviderList = instance._dataProvidersList.get('options');

						for (var i = 0; i < dataProviderList.length; i++) {
							if (dataProviderList[i].value === id) {
								return dataProviderList[i].uuid;
							}
						}
					},

					_renderDataProvidersList: function(result) {
						var instance = this;

						var dataProvidersList = [];

						var uuid;

						var value;

						var action = instance.get('action');

						if (action && action.ddmDataProviderInstanceUUID) {
							uuid = action.ddmDataProviderInstanceUUID;
						}

						for (var i = 0; i < result.length; i++) {
							if (result[i].uuid === uuid) {
								value = result[i].id;
							}

							dataProvidersList.push(
								{
									label: result[i].name,
									uuid: result[i].uuid,
									value: result[i].id
								}
							);
						}

						instance._dataProvidersList.set('options', dataProvidersList);

						instance._dataProvidersList.setValue(value);
					}
				}
			}
		);

		Liferay.namespace('DDL').FormBuilderActionAutofill = FormBuilderActionAutofill;
	},
	'',
	{
		requires: ['liferay-ddl-form-builder-action']
	}
);