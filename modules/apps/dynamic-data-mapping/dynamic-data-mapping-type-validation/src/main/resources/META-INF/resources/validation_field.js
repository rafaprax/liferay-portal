AUI.add(
	'liferay-ddm-form-field-validation',
	function(A) {
		var Lang = A.Lang;

		var Renderer = Liferay.DDM.Renderer;

		var Util = Renderer.Util;

		var ValidationField = A.Component.create(
			{
				ATTRS: {
					errorMessageValue: {
						value: ''
					},

					parameterValue: {
						value: ''
					},

					selectedValidation: {
						getter: '_getSelectedValidation',
						value: 'notEmpty'
					},

					strings: {
						value: {
							email: Liferay.Language.get('email'),
							errorMessageGoesHere: Liferay.Language.get('error-message-goes-here'),
							url: Liferay.Language.get('url'),
							validation: Liferay.Language.get('validation')
						}
					},

					type: {
						value: 'validation'
					},

					validations: {
						getter: '_getValidations'
					},

					value: {
						setter: '_setValue',
						state: true,
						valueFn: '_valueValidation'
					}
				},

				EXTENDS: Liferay.DDM.Renderer.Field,

				NAME: 'liferay-ddm-form-field-validation',

				prototype: {
					initializer: function() {
						var instance = this;

						var evaluator = instance.get('evaluator');

						instance._eventHandlers.push(
							evaluator.after('evaluationEnded', A.bind('_loadValidationFieldType', instance)),
							instance.after('valueChange', A.bind('_afterValueChange', instance)),
							instance.after('render', instance._loadValidationFieldType, instance),
							instance.bindContainerEvent('change', A.bind('_setErrorMessage', instance), '.message-input'),
							instance.bindContainerEvent('change', A.bind('_setParameterValue', instance), '.parameter-input'),
							instance.bindContainerEvent('change', A.bind('_syncValidationUI', instance), '.enable-validation'),
							instance.bindContainerEvent('change', A.bind('_syncValidationUI', instance), 'select')
						);
					},

					createDecimalField: function(context) {
						var instance = this;

						var config = A.merge(
							context,
							{
								bubbleTargets: [instance],
								context: A.clone(context),
								cssClass: 'validation-input',
								dataType: 'double'
							}
						);

						return new Liferay.DDM.Field.Numeric(config);
					},

					createIntegerField: function(context) {
						var instance = this;

						var config = A.merge(
							context,
							{

								bubbleTargets: [instance],
								context: A.clone(context),
								cssClass: 'validation-input'
							}
						);

						return new Liferay.DDM.Field.Numeric(config);
					},

					createTextField: function(context) {
						var instance = this;

						var config = A.merge(
							context,
							{
								bubbleTargets: [instance],
								context: A.clone(context),
								cssClass: 'validation-input'
							}
						);

						return new Liferay.DDM.Field.Text(config);
					},

					extractParameterValue: function(regex, expression) {
						var instance = this;

						regex.lastIndex = 0;

						var matches = regex.exec(expression);

						return matches && matches[2] || '';
					},

					getTemplateContext: function() {
						var instance = this;

						var strings = instance.get('strings');

						var value = instance.get('value');

						return A.merge(
							ValidationField.superclass.getTemplateContext.apply(instance, arguments),
							{
								enableValidationValue: !!(value && value.expression),
								errorMessagePlaceholder: strings.errorMessageGoesHere,
								errorMessageValue: instance.get('errorMessageValue'),
								parameterValue: instance.get('parameterValue'),
								validationMessage: strings.validation,
								validationsOptions: instance._getValidationsOptions()
							}
						);
					},

					getValue: function() {
						var instance = this;

						var expression = '';

						var selectedValidation = instance.get('selectedValidation');

						var validationEnabled = instance._getEnableValidationValue();

						if (selectedValidation && validationEnabled) {
							var root = instance.getRoot();

							var nameField = root.getField('name');

							expression = Lang.sub(
								selectedValidation.template,
								{
									name: nameField && nameField.get('value') || '',
									parameter: instance._getParameterValue()
								}
							);
						}

						return {
							errorMessage: instance._getMessageValue(),
							expression: expression
						};
					},

					_afterValueChange: function() {
						var instance = this;

						instance.evaluate();
					},

					_createField: function(dataType) {
						var instance = this;

						var parameterMessage = '';

						var selectedValidation = instance.get('selectedValidation');

						if (selectedValidation) {
							parameterMessage = selectedValidation.parameterMessage;
						}

						var field;
						var fieldConfig = {
							fieldName: '',
							options: [],
							placeholder: parameterMessage,
							readOnly: false,
							showLabel: false,
							strings: {},
							value: instance.get('parameterValue'),
							visible: true
						};

						if (dataType == 'integer') {
							field = instance.createIntegerField(fieldConfig);
						}
						else if (dataType == 'double') {
							field = instance.createDecimalField(fieldConfig);
						}
						else {
							field = instance.createTextField(fieldConfig);
						}

						return field;
					},

					_getEnableValidationValue: function() {
						var instance = this;

						var container = instance.get('container');

						var enableValidationNode = container.one('.enable-validation');

						return !!enableValidationNode.attr('checked');
					},

					_getMessageValue: function() {
						var instance = this;

						var container = instance.get('container');

						var messageNode = container.one('.message-input');

						return messageNode.val();
					},

					_getParameterValue: function() {
						var instance = this;

						var container = instance.get('container');

						var parameterNode = container.one('.validation-input input');

						return parameterNode.val();
					},

					_getSelectedValidation: function(val) {
						var instance = this;

						var validations = instance.get('validations');

						var selectedValidation = A.Array.find(
							validations,
							function(validation) {
								return validation.name === val;
							}
						);

						if (!selectedValidation) {
							selectedValidation = validations[0];
						}

						return selectedValidation;
					},

					_getValidations: function() {
						var instance = this;

						return Util.getValidations(instance.get('dataType')) || [];
					},

					_getValidationsOptions: function() {
						var instance = this;

						var selectedValidation = instance.get('selectedValidation');

						var validations = instance.get('validations');

						return validations.map(
							function(validation) {
								var status = '';

								if (selectedValidation && selectedValidation.name === validation.name) {
									status = 'selected';
								}

								return {
									label: validation.label,
									status: status,
									value: validation.name
								};
							}
						);
					},

					_loadValidationFieldType: function() {
						var instance = this;

						var container = instance.get('container');

						var fieldSettingsForm = instance.get('parent');

						var currentField = fieldSettingsForm.get('field');

						var dataType = currentField.get('dataType');

						if (instance._validationField) {
							instance._validationField.destroy();
						}

						instance._validationField = instance._createField(dataType);

						instance._validationField.render(container.one('.validation-input'));
					},

					_setErrorMessage: function(event) {
						var instance = this;

						var input = event.target;

						instance.set('errorMessageValue', input.val());
						instance.set('value', instance.getValue());
					},

					_setParameterValue: function(event) {
						var instance = this;

						var input = event.target;

						instance.set('parameterValue', input.val());
						instance.set('value', instance.getValue());
					},

					_setValue: function(validation) {
						var instance = this;

						if (validation) {
							var errorMessage = validation.errorMessage;

							var expression = validation.expression;

							A.each(
								instance.get('validations'),
								function(item, type) {
									var regex = item.regex;

									if (regex.test(expression)) {
										instance.set('errorMessageValue', errorMessage);
										instance.set('selectedValidation', item.name);

										instance.set(
											'parameterValue',
											instance.extractParameterValue(regex, expression)
										);
									}
								}
							);
						}

						return validation;
					},

					_syncValidationUI: function(event) {
						var instance = this;

						var currentTarget = event.currentTarget;

						var newVal = currentTarget.val();

						var selectedValidation = newVal;

						if (currentTarget.hasClass('types-select')) {
							var validations = instance.get('validations');

							selectedValidation = validations[0].name;
						}

						instance.set('selectedValidation', selectedValidation);

						instance.set('value', instance.getValue());
					},

					_valueValidation: function() {
						var instance = this;

						return {
							errorMessage: Liferay.Language.get('is-empty'),
							expression: 'NOT(equals({name}, ""))'
						};
					}
				}
			}
		);

		Liferay.namespace('DDM.Field').Validation = ValidationField;
	},
	'',
	{
		requires: ['aui-dropdown', 'liferay-ddm-form-renderer-field']
	}
);