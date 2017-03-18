AUI.add(
	'liferay-ddl-form-builder-field-sets',
	function(A) {
		var AArray = A.Array;

		var _fieldSets = [];

		var FieldSets = {
			get: function(type) {
				var instance = this;

				return AArray.find(
					_fieldSets,
					function(item, index) {
						return item.get('name') === type;
					}
				);
			},

			getAll: function() {
				var instance = this;

				return _fieldSets;
			},

			register: function(fieldSets) {
				var instance = this;

				_fieldSets = AArray(fieldSets).map(instance._getFieldSet);
			},

			_getFieldSet: function(config) {
				var instance = this;

				return new Liferay.DDM.FormRendererFieldType(
					{
						defaultConfig: {
							fields: config.fields,
							page: config.layout.pages[0]
						},
						fieldClass: Liferay.DDM.Renderer.Field,
						icon: config.icon,
						label: config.name
					}
				);
			}
		};

		Liferay.namespace('DDL').FieldSets = FieldSets;
	},
	'',
	{
		requires: ['array-extras', 'liferay-ddm-form-renderer-type']
	}
);