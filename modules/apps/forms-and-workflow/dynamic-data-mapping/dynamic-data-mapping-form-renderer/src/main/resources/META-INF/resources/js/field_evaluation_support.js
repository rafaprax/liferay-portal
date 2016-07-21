AUI.add(
	'liferay-ddm-form-renderer-field-evaluation',
	function(A) {
		var Lang = A.Lang;

		var Renderer = Liferay.DDM.Renderer;

		var Util = Renderer.Util;

		var FieldEvaluationSupport = function() {
		};

		FieldEvaluationSupport.ATTRS = {
			enableEvaluations: {
				value: true
			},

			evaluator: {
				getter: '_getEvaluator'
			}
		};

		FieldEvaluationSupport.prototype = {
			initializer: function() {
				var instance = this;

				instance._eventHandlers.push(
					instance.after('valueChanged', instance._afterValueChanged),
					instance.after('visibleChange', instance._afterVisibleChange)
				);
			},

			evaluate: function() {
				var instance = this;

				var evaluator = instance.get('evaluator');

				if (evaluator) {
					evaluator.evaluate(instance);
				}
			},

			_afterValueChanged: function() {
				var instance = this;

				instance.evaluate();
			},

			_afterVisibleChange: function(event) {
				var instance = this;

				var container = instance.get('container');

				container.toggleClass('hide', !event.newVal);
			},

			_getEvaluator: function() {
				var instance = this;

				var evaluator;

				var root = instance.getRoot();

				if (root) {
					evaluator = root.get('evaluator');
				}

				return evaluator;
			}
		};

		Liferay.namespace('DDM.Renderer').FieldEvaluationSupport = FieldEvaluationSupport;
	},
	'',
	{
		requires: ['liferay-ddm-form-renderer-expressions-evaluator', 'liferay-ddm-form-renderer-util']
	}
);