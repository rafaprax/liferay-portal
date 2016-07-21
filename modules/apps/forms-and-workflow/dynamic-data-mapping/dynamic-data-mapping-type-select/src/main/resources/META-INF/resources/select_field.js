AUI.add(
	'liferay-ddm-form-field-select',
	function(A) {
		var Lang = A.Lang;

		var TPL_OPTION = '<option>{label}</option>';

		var SelectField = A.Component.create(
			{
				ATTRS: {
					dataSourceOptions: {
						value: []
					},

					dataSourceType: {
						value: 'manual'
					},

					dataProviderURL: {
						valueFn: '_valueDataProviderURL'
					},

					ddmDataProviderInstanceId: {
						value: 0
					},

					multiple: {
						value: false
					},

					options: {
						validator: Array.isArray,
						value: []
					},

					strings: {
						repaint: false,
						value: {
							chooseAnOption: Liferay.Language.get('choose-an-option'),
							dynamicallyLoadedData: Liferay.Language.get('dynamically-loaded-data')
						}
					},

					type: {
						value: 'select'
					},

					value: {
						repaint: false,
						value: []
					}
				},

				EXTENDS: Liferay.DDM.Renderer.Field,

				NAME: 'liferay-ddm-form-field-select',

				prototype: {
					render: function() {
						var instance = this;

						var dataSourceType = instance.get('dataSourceType');

						SelectField.superclass.render.apply(instance, arguments);

						if (dataSourceType !== 'manual') {
							if (instance.get('builder')) {
								var inputNode = instance.getInputNode();

								var strings = instance.get('strings');

								inputNode.attr('disabled', true);

								inputNode.html(
									Lang.sub(
										TPL_OPTION,
										{
											label: strings.dynamicallyLoadedData
										}
									)
								);
							}
							else {
								var container = instance.get('container');

								instance._getDataSourceData(
									function(options) {
										instance.set('dataSourceOptions', options);

										container.html(instance.getTemplate());
									}
								);
							}
						}

						return instance;
					},

					setValue: function(value) {
						var instance = this;

						var inputNode = instance.getInputNode();

						var options = inputNode.all('option');

						options.attr('selected', false);

						options.filter(
							function(option) {
								return value.indexOf(option.val()) > -1;
							}
						).attr('selected', true);
					},

					showErrorMessage: function() {
						var instance = this;

						SelectField.superclass.showErrorMessage.apply(instance, arguments);

						var container = instance.get('container');

						var inputGroup = container.one('.input-select-wrapper');

						inputGroup.insert(container.one('.help-block'), 'after');
					},

					_getDataSourceData: function(callback) {
						var instance = this;

						A.io.request(
							instance.get('dataProviderURL'),
							{
								data: {
									ddmDataProviderInstanceId: instance.get('ddmDataProviderInstanceId')
								},
								dataType: 'JSON',
								method: 'GET',
								on: {
									failure: function() {
										callback.call(instance, null);
									},
									success: function() {
										var result = this.get('responseData');

										callback.call(instance, result);
									}
								}
							}
						);
					},

					_valueDataProviderURL: function() {
						var instance = this;

						var dataProviderURL;

						var form = instance.getRoot();

						if (form) {
							dataProviderURL = form.get('dataProviderURL');
						}

						return dataProviderURL;
					}
				}
			}
		);

		Liferay.namespace('DDM.Field').Select = SelectField;
	},
	'',
	{
		requires: ['liferay-ddm-form-renderer-field']
	}
);