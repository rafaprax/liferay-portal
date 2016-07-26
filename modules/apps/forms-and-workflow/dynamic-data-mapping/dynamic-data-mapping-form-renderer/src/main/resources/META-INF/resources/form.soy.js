// This file was automatically generated from form.soy.
// Please don't edit this file by hand.

/**
 * @fileoverview Templates in namespace ddm.
 * @hassoydeltemplate {ddm.field}
 * @hassoydelcall {ddm.field}
 * @public
 */

if (typeof ddm == 'undefined') { var ddm = {}; }


ddm.__deltemplate_s2_86478705 = function(opt_data, opt_ignored) {
  return '';
};
if (goog.DEBUG) {
  ddm.__deltemplate_s2_86478705.soyTemplateName = 'ddm.__deltemplate_s2_86478705';
}
soy.$$registerDelegateFn(soy.$$getDelTemplateId('ddm.field'), '', 0, ddm.__deltemplate_s2_86478705);


ddm.fields = function(opt_data, opt_ignored) {
  var output = '';
  var fieldList12 = opt_data.fields;
  var fieldListLen12 = fieldList12.length;
  for (var fieldIndex12 = 0; fieldIndex12 < fieldListLen12; fieldIndex12++) {
    var fieldData12 = fieldList12[fieldIndex12];
    var variant__soy4 = fieldData12.type;
    output += '<div class="clearfix ' + ((! fieldData12.visible) ? 'hide' : '') + ' lfr-ddm-form-field-container">' + soy.$$getDelegateFn(soy.$$getDelTemplateId('ddm.field'), variant__soy4, true)(fieldData12) + '</div>';
  }
  return output;
};
if (goog.DEBUG) {
  ddm.fields.soyTemplateName = 'ddm.fields';
}


ddm.form_renderer_js = function(opt_data, opt_ignored) {
  return '<script type="text/javascript">AUI().use( \'liferay-ddm-form-renderer\', \'liferay-ddm-form-renderer-field\', function(A) {Liferay.DDM.Renderer.FieldTypes.register(' + soy.$$filterNoAutoescape(opt_data.fieldTypes) + '); Liferay.component( \'' + soy.$$escapeJsString(opt_data.containerId) + 'DDMForm\', new Liferay.DDM.Renderer.Form({container: \'#' + soy.$$escapeJsString(opt_data.containerId) + '\', context: ' + soy.$$filterNoAutoescape(opt_data.context) + ', definition: ' + soy.$$filterNoAutoescape(opt_data.definition) + ', evaluatorURL: \'' + soy.$$escapeJsString(opt_data.evaluatorURL) + '\', layout: ' + soy.$$filterNoAutoescape(opt_data.layout) + ', portletNamespace: \'' + soy.$$escapeJsString(opt_data.portletNamespace) + '\', readOnly: ' + soy.$$escapeJsValue(opt_data.readOnly) + '}).render() );});<\/script>';
};
if (goog.DEBUG) {
  ddm.form_renderer_js.soyTemplateName = 'ddm.form_renderer_js';
}


ddm.form_rows = function(opt_data, opt_ignored) {
  var output = '';
  var rowList43 = opt_data.rows;
  var rowListLen43 = rowList43.length;
  for (var rowIndex43 = 0; rowIndex43 < rowListLen43; rowIndex43++) {
    var rowData43 = rowList43[rowIndex43];
    output += '<div class="row">' + ddm.form_row_columns(soy.$$augmentMap(opt_data, {columns: rowData43.columns})) + '</div>';
  }
  return output;
};
if (goog.DEBUG) {
  ddm.form_rows.soyTemplateName = 'ddm.form_rows';
}


ddm.form_row_column = function(opt_data, opt_ignored) {
  return '<div class="col-md-' + soy.$$escapeHtmlAttribute(opt_data.column.size) + '">' + ddm.fields(soy.$$augmentMap(opt_data, {fields: opt_data.column.fields})) + '</div>';
};
if (goog.DEBUG) {
  ddm.form_row_column.soyTemplateName = 'ddm.form_row_column';
}


ddm.form_row_columns = function(opt_data, opt_ignored) {
  var output = '';
  var columnList55 = opt_data.columns;
  var columnListLen55 = columnList55.length;
  for (var columnIndex55 = 0; columnIndex55 < columnListLen55; columnIndex55++) {
    var columnData55 = columnList55[columnIndex55];
    output += ddm.form_row_column(soy.$$augmentMap(opt_data, {column: columnData55}));
  }
  return output;
};
if (goog.DEBUG) {
  ddm.form_row_columns.soyTemplateName = 'ddm.form_row_columns';
}


ddm.required_warning_message = function(opt_data, opt_ignored) {
  return '' + ((opt_data.showRequiredFieldsWarning) ? soy.$$filterNoAutoescape(opt_data.requiredFieldsWarningMessageHTML) : '');
};
if (goog.DEBUG) {
  ddm.required_warning_message.soyTemplateName = 'ddm.required_warning_message';
}


ddm.paginated_form = function(opt_data, opt_ignored) {
  var output = '<div class="lfr-ddm-form-container" id="' + soy.$$escapeHtmlAttribute(opt_data.containerId) + '"><div class="lfr-ddm-form-content">';
  if (opt_data.pages.length > 1) {
    output += '<div class="lfr-ddm-form-wizard"><ul class="multi-step-progress-bar">';
    var pageList79 = opt_data.pages;
    var pageListLen79 = pageList79.length;
    for (var pageIndex79 = 0; pageIndex79 < pageListLen79; pageIndex79++) {
      var pageData79 = pageList79[pageIndex79];
      output += '<li ' + ((pageIndex79 == 0) ? 'class="active"' : '') + '><div class="progress-bar-title">' + soy.$$filterNoAutoescape(pageData79.title) + '</div><div class="divider"></div><div class="progress-bar-step">' + soy.$$escapeHtml(pageIndex79 + 1) + '</div></li>';
    }
    output += '</ul></div>';
  }
  output += '<div class="lfr-ddm-form-pages">';
  var pageList106 = opt_data.pages;
  var pageListLen106 = pageList106.length;
  for (var pageIndex106 = 0; pageIndex106 < pageListLen106; pageIndex106++) {
    var pageData106 = pageList106[pageIndex106];
    output += '<div class="' + ((pageIndex106 == 0) ? 'active' : '') + ' lfr-ddm-form-page">' + ((pageData106.title) ? '<h3 class="lfr-ddm-form-page-title">' + soy.$$filterNoAutoescape(pageData106.title) + '</h3>' : '') + ((pageData106.description) ? '<h4 class="lfr-ddm-form-page-description">' + soy.$$filterNoAutoescape(pageData106.description) + '</h4>' : '') + ddm.required_warning_message(soy.$$augmentMap(opt_data, {showRequiredFieldsWarning: pageData106.showRequiredFieldsWarning, requiredFieldsWarningMessageHTML: opt_data.requiredFieldsWarningMessageHTML})) + ddm.form_rows(soy.$$augmentMap(opt_data, {rows: pageData106.rows})) + '</div>';
  }
  output += '</div></div><div class="lfr-ddm-form-pagination-controls"><button class="btn btn-lg btn-primary hide lfr-ddm-form-pagination-prev" type="button"><i class="icon-angle-left"></i> ' + soy.$$escapeHtml(opt_data.strings.previous) + '</button><button class="btn btn-lg btn-primary' + ((opt_data.pages.length == 1) ? ' hide' : '') + ' lfr-ddm-form-pagination-next pull-right" type="button">' + soy.$$escapeHtml(opt_data.strings.next) + ' <i class="icon-angle-right"></i></button>' + ((opt_data.showSubmitButton) ? '<button class="btn btn-lg btn-primary' + ((opt_data.pages.length > 1) ? ' hide' : '') + ' lfr-ddm-form-submit pull-right" disabled type="submit">' + soy.$$escapeHtml(opt_data.submitLabel) + '</button>' : '') + '</div></div>';
  return output;
};
if (goog.DEBUG) {
  ddm.paginated_form.soyTemplateName = 'ddm.paginated_form';
}


ddm.simple_form = function(opt_data, opt_ignored) {
  var output = '<div class="lfr-ddm-form-container" id="' + soy.$$escapeHtmlAttribute(opt_data.containerId) + '"><div class="lfr-ddm-form-fields">';
  var pageList136 = opt_data.pages;
  var pageListLen136 = pageList136.length;
  for (var pageIndex136 = 0; pageIndex136 < pageListLen136; pageIndex136++) {
    var pageData136 = pageList136[pageIndex136];
    output += ddm.required_warning_message(soy.$$augmentMap(opt_data, {showRequiredFieldsWarning: pageData136.showRequiredFieldsWarning, requiredFieldsWarningMessageHTML: opt_data.requiredFieldsWarningMessageHTML})) + ddm.form_rows(soy.$$augmentMap(opt_data, {rows: pageData136.rows}));
  }
  output += '</div></div>';
  return output;
};
if (goog.DEBUG) {
  ddm.simple_form.soyTemplateName = 'ddm.simple_form';
}


ddm.tabbed_form = function(opt_data, opt_ignored) {
  var output = '<div class="lfr-ddm-form-container" id="' + soy.$$escapeHtmlAttribute(opt_data.containerId) + '"><div class="lfr-ddm-form-tabs"><ul class="nav nav-tabs nav-tabs-default">';
  var pageList146 = opt_data.pages;
  var pageListLen146 = pageList146.length;
  for (var pageIndex146 = 0; pageIndex146 < pageListLen146; pageIndex146++) {
    var pageData146 = pageList146[pageIndex146];
    output += '<li><a href="javascript:;">' + soy.$$escapeHtml(pageData146.title) + '</a></li>';
  }
  output += '</ul><div class="tab-content">';
  var pageList160 = opt_data.pages;
  var pageListLen160 = pageList160.length;
  for (var pageIndex160 = 0; pageIndex160 < pageListLen160; pageIndex160++) {
    var pageData160 = pageList160[pageIndex160];
    output += ddm.required_warning_message(soy.$$augmentMap(opt_data, {showRequiredFieldsWarning: pageData160.showRequiredFieldsWarning, requiredFieldsWarningMessageHTML: opt_data.requiredFieldsWarningMessageHTML})) + '<div class="tab-pane ' + ((pageIndex160 == 0) ? 'active' : '') + '">' + ddm.form_rows(soy.$$augmentMap(opt_data, {rows: pageData160.rows})) + '</div>';
  }
  output += '</div></div></div>';
  return output;
};
if (goog.DEBUG) {
  ddm.tabbed_form.soyTemplateName = 'ddm.tabbed_form';
}


ddm.settings_form = function(opt_data, opt_ignored) {
  var output = '<div class="lfr-ddm-form-container" id="' + soy.$$escapeHtmlAttribute(opt_data.containerId) + '"><div class="lfr-ddm-settings-form">';
  var pageList178 = opt_data.pages;
  var pageListLen178 = pageList178.length;
  for (var pageIndex178 = 0; pageIndex178 < pageListLen178; pageIndex178++) {
    var pageData178 = pageList178[pageIndex178];
    output += '<div class="lfr-ddm-form-page' + ((pageIndex178 == 0) ? ' active basic' : '') + ((pageIndex178 == pageListLen178 - 1) ? ' advanced' : '') + '">' + ddm.form_rows(soy.$$augmentMap(opt_data, {rows: pageData178.rows})) + '</div>';
  }
  output += '</div></div>';
  return output;
};
if (goog.DEBUG) {
  ddm.settings_form.soyTemplateName = 'ddm.settings_form';
}
