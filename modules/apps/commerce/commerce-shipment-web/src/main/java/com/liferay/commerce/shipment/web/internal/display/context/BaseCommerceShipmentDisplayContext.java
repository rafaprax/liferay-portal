/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.shipment.web.internal.display.context;

import com.liferay.commerce.constants.CommerceActionKeys;
import com.liferay.commerce.constants.CommerceWebKeys;
import com.liferay.commerce.model.CommerceShipment;
import com.liferay.commerce.model.CommerceShipmentItem;
import com.liferay.commerce.product.display.context.helper.CPRequestHelper;
import com.liferay.commerce.service.CommerceShipmentItemLocalServiceUtil;
import com.liferay.commerce.service.CommerceShipmentLocalServiceUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;

import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Alessio Antonio Rendina
 */
public class BaseCommerceShipmentDisplayContext<T> {

	public BaseCommerceShipmentDisplayContext(
		HttpServletRequest httpServletRequest,
		PortletResourcePermission portletResourcePermission) {

		this.httpServletRequest = httpServletRequest;

		_portletResourcePermission = portletResourcePermission;

		cpRequestHelper = new CPRequestHelper(httpServletRequest);

		liferayPortletRequest = cpRequestHelper.getLiferayPortletRequest();
		liferayPortletResponse = cpRequestHelper.getLiferayPortletResponse();
	}

	public CommerceShipment getCommerceShipment() throws Exception {
		if (_commerceShipment != null) {
			return _commerceShipment;
		}

		_commerceShipment = _getCommerceShipment(
			cpRequestHelper.getRenderRequest());

		return _commerceShipment;
	}

	public long getCommerceShipmentId() throws Exception {
		CommerceShipment commerceShipment = getCommerceShipment();

		if (commerceShipment == null) {
			return 0;
		}

		return commerceShipment.getCommerceShipmentId();
	}

	public CommerceShipmentItem getCommerceShipmentItem(
			RenderRequest renderRequest)
		throws Exception {

		CommerceShipmentItem commerceShipmentItem =
			(CommerceShipmentItem)renderRequest.getAttribute(
				CommerceWebKeys.COMMERCE_SHIPMENT_ITEM);

		if (commerceShipmentItem != null) {
			return commerceShipmentItem;
		}

		long commerceShipmentItemId = ParamUtil.getLong(
			renderRequest, "commerceShipmentItemId");

		if (commerceShipmentItemId > 0) {
			commerceShipmentItem =
				CommerceShipmentItemLocalServiceUtil.fetchCommerceShipmentItem(
					commerceShipmentItemId);
		}

		if (commerceShipmentItem != null) {
			renderRequest.setAttribute(
				CommerceWebKeys.COMMERCE_SHIPMENT_ITEM, commerceShipmentItem);
		}

		return commerceShipmentItem;
	}

	public String getKeywords() {
		return ParamUtil.getString(httpServletRequest, "keywords");
	}

	public PortletURL getPortletURL() throws Exception {
		PortletURL portletURL = liferayPortletResponse.createRenderURL();

		String redirect = ParamUtil.getString(httpServletRequest, "redirect");

		if (Validator.isNotNull(redirect)) {
			portletURL.setParameter("redirect", redirect);
		}

		CommerceShipment commerceShipment = getCommerceShipment();

		if (commerceShipment != null) {
			portletURL.setParameter(
				"commerceShipmentId", String.valueOf(getCommerceShipmentId()));
		}

		String delta = ParamUtil.getString(httpServletRequest, "delta");

		if (Validator.isNotNull(delta)) {
			portletURL.setParameter("delta", delta);
		}

		String deltaEntry = ParamUtil.getString(
			httpServletRequest, "deltaEntry");

		if (Validator.isNotNull(deltaEntry)) {
			portletURL.setParameter("deltaEntry", deltaEntry);
		}

		String keywords = getKeywords();

		if (Validator.isNotNull(keywords)) {
			portletURL.setParameter("keywords", keywords);
		}

		return portletURL;
	}

	public boolean hasManageCommerceShipmentsPermission() {
		return _portletResourcePermission.contains(
			cpRequestHelper.getPermissionChecker(), null,
			CommerceActionKeys.MANAGE_COMMERCE_SHIPMENTS);
	}

	protected final CPRequestHelper cpRequestHelper;
	protected final HttpServletRequest httpServletRequest;
	protected final LiferayPortletRequest liferayPortletRequest;
	protected final LiferayPortletResponse liferayPortletResponse;

	private CommerceShipment _getCommerceShipment(RenderRequest renderRequest)
		throws Exception {

		CommerceShipment commerceShipment =
			(CommerceShipment)renderRequest.getAttribute(
				CommerceWebKeys.COMMERCE_SHIPMENT);

		if (commerceShipment != null) {
			return commerceShipment;
		}

		long commerceShipmentId = ParamUtil.getLong(
			renderRequest, "commerceShipmentId");

		if (commerceShipmentId > 0) {
			commerceShipment =
				CommerceShipmentLocalServiceUtil.fetchCommerceShipment(
					commerceShipmentId);
		}

		if (commerceShipment != null) {
			renderRequest.setAttribute(
				CommerceWebKeys.COMMERCE_SHIPMENT, commerceShipment);
		}

		return commerceShipment;
	}

	private CommerceShipment _commerceShipment;
	private final PortletResourcePermission _portletResourcePermission;

}