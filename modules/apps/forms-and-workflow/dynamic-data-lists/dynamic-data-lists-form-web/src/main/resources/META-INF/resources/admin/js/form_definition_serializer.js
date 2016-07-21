AUI.add(
	'liferay-ddl-form-builder-definition-serializer',
	function(A) {
		var DefinitionSerializer = A.Component.create(
			{
				ATTRS: {
					fieldHandler: {
						valueFn: '_valueFieldHandler'
					},

					fields: {
						validator: Array.isArray,
						value: []
					},

					fieldTypesDefinitions: {
						value: {}
					}
				},

				EXTENDS: Liferay.DDL.LayoutVisitor,

				NAME: 'liferay-ddl-form-builder-definition-serializer',

				prototype: {
					serialize: function() {
						var instance = this;

						instance.visit();

						var definition = A.JSON.stringify(
							{
								availableLanguageIds: ['en_US'],
								defaultLanguageId: 'en_US',
								fields: instance.get('fields')
							}
						);

						instance.set('fields', []);

						return definition;
					},

					_serializeField: function(field) {
						var instance = this;

						var config = {};

						var fieldTypesDefinitions = instance.get('fieldTypesDefinitions');

						var definitionFields = fieldTypesDefinitions[field.get('type')];

						var languageId = themeDisplay.getLanguageId();

						definitionFields.forEach(
							function(fieldSetting) {
								var name = fieldSetting.name;

								var value = field.get('context.' + name);

								if (name === 'name') {
									config[name] = field.get('fieldName');
								}
								else if (name === 'options') {
									config[name] = value.slice().map(
										function(option) {
											var label = {};

											label[languageId] = option.label;

											return {
												label: label,
												value: option.value
											};
										}
									);
								}
								else {
									if (fieldSetting.localizable) {
										config[name] = {};

										config[name][languageId] = value;
									}
									else {
										config[name] = value;
									}
								}
							}
						);

						instance._serializeFieldRules(field, config);

						instance.get('fields').push(
							A.merge(
								config,
								{
									dataType: field.get('dataType'),
									readOnly: false,
									type: field.get('type')
								}
							)
						);
					},

					_serializeFieldRules: function(field, config) {
						var instance = this;

						var validation = config.validation;

						var validationExpression = validation.expression;
						var validationErrorMessage = validation.errorMessage;

						delete config.validation;

						var visibilityExpression = config.visibilityExpression;

						delete config.visibilityExpression;

						config.rules = [
							{
								errorMessage: validationErrorMessage,
								expression: validationExpression,
								type: 'VALIDATION'
							},
							{
								expression: visibilityExpression,
								type: 'VISIBILITY'
							}
						];
					},

					_valueFieldHandler: function() {
						var instance = this;

						return instance._serializeField;
					}
				}
			}
		);

		Liferay.namespace('DDL').DefinitionSerializer = DefinitionSerializer;
	},
	'',
	{
		requires: ['json', 'liferay-ddl-form-builder-layout-visitor', 'liferay-ddm-form-field-types']
	}
);