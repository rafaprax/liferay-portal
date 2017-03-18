AUI.add(
	'liferay-ddl-form-builder-field-types-modal',
	function(A) {
		var Lang = A.Lang;

		var CSS_FIELD_SETS_LIST = A.getClassName('form', 'builder', 'field', 'sets', 'list');

		var CSS_FIELD_TYPE = A.getClassName('field', 'type');

		var CSS_FIELD_SET_TYPE = A.getClassName('field', 'set', 'type');

		var TPL_COLUMN = '<div class="col col-md-{size}"></div>';

		var TPL_ROW = '<div class="row"></div>';

		var FormBuilderFieldTypesModal = A.Component.create(
			{
				ATTRS: {
					fieldSets: {
						value: []
					},

					fieldSetsGroupNode: {
						value: ''
					},

					fieldTypesGroupNode: {
						value: ''
					}
				},

				CSS_PREFIX: 'lfr-ddl-form-builder-field-types-modal',

				AUGMENTS: [Liferay.DDL.FormBuilderModalSupport],

				EXTENDS: A.FormBuilderFieldTypesModal,

				NAME: 'form-builder-field-types-modal',

				prototype: {
					TPL_TYPES_PARENT: '<div class="clearfix" role="main"></div>',

					TPL_FIELD_SETS_GROUP: '<div class="list-group" role="main"><div class="list-group-heading">'+ Liferay.Language.get('field-sets') +'</div></div>',

					TPL_FIELD_TYPES_GROUP: '<div class="list-group" role="main"><div class="list-group-heading">'+ Liferay.Language.get('field-types') +'</div></div>',

					TPL_FIELD_SETS_LIST: '<div class="clearfix ' + CSS_FIELD_SETS_LIST + '" role="main"></div>',

					bindUI: function() {
						var instance = this;

						var bodyNode = instance.getStdModNode('body');

						FormBuilderFieldTypesModal.superclass.bindUI.apply(instance, arguments);

						instance._eventHandles.push(
							bodyNode.delegate('click', instance._onClickFieldSet, '.' + CSS_FIELD_SET_TYPE, this)
						);
					},

					renderUI: function() {
						var instance = this;

						var typesParentNode = A.Node.create(instance.TPL_TYPES_PARENT);

						typesParentNode.empty();

						var fieldTypesGroupNode = A.Node.create(instance.TPL_FIELD_TYPES_GROUP);

						instance.set('fieldTypesGroupNode', fieldTypesGroupNode);

						typesParentNode.append(fieldTypesGroupNode);

						var fieldSetsGroupNode = A.Node.create(instance.TPL_FIELD_SETS_GROUP);

						instance.set('fieldSetsGroupNode', fieldSetsGroupNode);

						typesParentNode.append(fieldSetsGroupNode);

						instance.set('bodyContent', typesParentNode);

						FormBuilderFieldTypesModal.superclass.renderUI.apply(this, arguments);

						instance._uiSetFieldSets(instance.get('fieldSets'));
					},

					_addRow: function(nodeList, index, node) {
						var instance = this;

						var size = 4;

						if (index % 3 === 0) {
							rowNode = instance._createRow();

							nodeList.append(rowNode);

							if (index === length - 1) {
								size = 12;
							}
						}

						if (length % 3 === 2 && index > length - 3) {
							size = 6;
						}

						var columnNode = instance._createColumn(size);

						columnNode.append(node);

						rowNode.append(columnNode);
					},

					_createColumn: function(size) {
						var instance = this;

						return A.Node.create(
							Lang.sub(
								TPL_COLUMN,
								{
									size: size
								}
							)
						);
					},

					_createRow: function() {
						var instance = this;

						return A.Node.create(TPL_ROW);
					},

					_onClickFieldSet: function(event) {
						var instance = this;

						event.preventDefault();

						var fieldType = event.currentTarget.getData('fieldType');

						instance.fire('selectFieldSet', {
							fieldType: fieldType
						});

						instance.hide();
					},

					_onClickFieldType: function(event) {
						var instance = this;

						event.preventDefault();

						FormBuilderFieldTypesModal.superclass._onClickFieldType.apply(instance, arguments);
					},

					_uiSetFieldSets: function(fieldSets) {
						var instance = this;

						var fieldSetsListNode = A.Node.create(instance.TPL_FIELD_SETS_LIST);

						fieldSetsListNode.empty();

						var length = fieldSets.length;

						var rowNode;

						fieldSets.forEach(
							function(fieldSet, index) {
								var node = fieldSet.get('node');

								node.removeClass(CSS_FIELD_TYPE);

								node.addClass(CSS_FIELD_SET_TYPE);

								instance._addRow(fieldSetsListNode, index, node);
							}
						);

						var fieldSetsGroupNode = instance.get('fieldSetsGroupNode');

						fieldSetsGroupNode.append(fieldSetsListNode);
					},

					_uiSetFieldTypes: function(fieldTypes) {
						var instance = this;

						var fieldTypesListNode = A.Node.create(instance.TPL_TYPES_LIST);

						fieldTypesListNode.empty();

						var length = fieldTypes.length;

						var rowNode;

						fieldTypes.forEach(
							function(fieldType, index) {
								instance._addRow(fieldTypesListNode, index, fieldType.get('node'));
							}
						);

						var fieldTypesGroupNode = instance.get('fieldTypesGroupNode');

						fieldTypesGroupNode.append(fieldTypesListNode);
					},

					_valueToolbars: function() {
						return {
							header: [
								{
									cssClass: 'close',
									discardDefaultButtonCssClasses: true,
									labelHTML: Liferay.Util.getLexiconIconTpl('times'),
									on: {
										click: A.bind(this._onFieldTypesModalCloseClick, this)
									}
								}
							]
						};
					}
				}
			}
		);

		Liferay.namespace('DDL').FormBuilderFieldTypesModal = FormBuilderFieldTypesModal;
	},
	'',
	{
		requires: ['aui-form-builder-field-types-modal', 'liferay-ddl-form-builder-modal-support']
	}
);