/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.shipment.web.internal.display.context;

import com.liferay.commerce.constants.CommerceWebKeys;
import com.liferay.commerce.model.CommerceOrderItem;
import com.liferay.commerce.model.CommerceShipment;
import com.liferay.commerce.model.CommerceShipmentItem;
import com.liferay.commerce.service.CommerceOrderItemService;
import com.liferay.commerce.service.CommerceShipmentItemLocalService;
import com.liferay.commerce.service.CommerceShipmentItemService;
import com.liferay.commerce.service.CommerceShipmentLocalService;
import com.liferay.commerce.util.CommerceQuantityFormatter;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.util.ParamUtil;

import java.math.BigDecimal;

import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Alessio Antonio Rendina
 */
public class CommerceShipmentItemDisplayContext
	extends BaseCommerceShipmentDisplayContext<CommerceShipmentItem> {

	public CommerceShipmentItemDisplayContext(
		HttpServletRequest httpServletRequest,
		CommerceOrderItemService commerceOrderItemService,
		CommerceQuantityFormatter commerceQuantityFormatter,
		CommerceShipmentItemService commerceShipmentItemService,
		CommerceShipmentItemLocalService commerceShipmentItemLocalService,
		CommerceShipmentLocalService commerceShipmentLocalService,
		PortletResourcePermission portletResourcePermission) {

		super(
			httpServletRequest, commerceShipmentItemLocalService,
			commerceShipmentLocalService, portletResourcePermission);

		_commerceOrderItemService = commerceOrderItemService;
		_commerceQuantityFormatter = commerceQuantityFormatter;
		_commerceShipmentItemService = commerceShipmentItemService;
		_commerceShipmentItemLocalService = commerceShipmentItemLocalService;
		_commerceShipmentLocalService = commerceShipmentLocalService;
	}

	public CommerceOrderItem getCommerceOrderItem() throws Exception {
		CommerceShipmentItem commerceShipmentItem = getCommerceShipmentItem();

		if (commerceShipmentItem == null) {
			return null;
		}

		return _commerceOrderItemService.getCommerceOrderItem(
			commerceShipmentItem.getCommerceOrderItemId());
	}

	@Override
	public CommerceShipment getCommerceShipment() throws Exception {
		CommerceShipmentItem commerceShipmentItem = getCommerceShipmentItem();

		if (commerceShipmentItem == null) {
			return null;
		}

		return commerceShipmentItem.getCommerceShipment();
	}

	public CommerceShipmentItem getCommerceShipmentItem() throws Exception {
		if (_commerceShipmentItem != null) {
			return _commerceShipmentItem;
		}

		_commerceShipmentItem = _getCommerceShipmentItem(
			cpRequestHelper.getRenderRequest());

		return _commerceShipmentItem;
	}

	public String getOutstandingQuantity() throws Exception {
		CommerceOrderItem commerceOrderItem = getCommerceOrderItem();

		BigDecimal quantity = commerceOrderItem.getQuantity();

		BigDecimal outstandingQuantity = _commerceQuantityFormatter.format(
			commerceOrderItem.getCPInstanceId(),
			quantity.subtract(commerceOrderItem.getShippedQuantity()),
			commerceOrderItem.getUnitOfMeasureKey());

		return outstandingQuantity.toString();
	}

	@Override
	public PortletURL getPortletURL() throws Exception {
		return PortletURLBuilder.create(
			super.getPortletURL()
		).setMVCRenderCommandName(
			"/commerce_shipment/edit_commerce_shipment"
		).buildPortletURL();
	}

	public String getToSendQuantity() throws Exception {
		CommerceOrderItem commerceOrderItem = getCommerceOrderItem();

		BigDecimal commerceShipmentOrderItemsQuantity =
			_commerceQuantityFormatter.format(
				commerceOrderItem.getCPInstanceId(),
				_commerceShipmentItemService.
					getCommerceShipmentOrderItemsQuantity(
						getCommerceShipmentId(),
						commerceOrderItem.getCommerceOrderItemId()),
				commerceOrderItem.getUnitOfMeasureKey());

		return commerceShipmentOrderItemsQuantity.toString();
	}

	private CommerceShipmentItem _getCommerceShipmentItem(
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
				_commerceShipmentItemLocalService.fetchCommerceShipmentItem(
					commerceShipmentItemId);
		}

		if (commerceShipmentItem != null) {
			renderRequest.setAttribute(
				CommerceWebKeys.COMMERCE_SHIPMENT_ITEM, commerceShipmentItem);
		}

		return commerceShipmentItem;
	}

	private final CommerceOrderItemService _commerceOrderItemService;
	private final CommerceQuantityFormatter _commerceQuantityFormatter;
	private CommerceShipmentItem _commerceShipmentItem;
	private final CommerceShipmentItemLocalService
		_commerceShipmentItemLocalService;
	private final CommerceShipmentItemService _commerceShipmentItemService;
	private final CommerceShipmentLocalService _commerceShipmentLocalService;

}