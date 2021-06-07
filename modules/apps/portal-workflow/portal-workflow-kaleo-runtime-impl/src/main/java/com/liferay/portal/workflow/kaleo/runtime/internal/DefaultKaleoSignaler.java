/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.portal.workflow.kaleo.runtime.internal;

import com.liferay.portal.aop.AopService;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.TransactionCommitCallbackUtil;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.workflow.kaleo.model.KaleoInstanceToken;
import com.liferay.portal.workflow.kaleo.model.KaleoNode;
import com.liferay.portal.workflow.kaleo.runtime.ExecutionContext;
import com.liferay.portal.workflow.kaleo.runtime.KaleoSignaler;
import com.liferay.portal.workflow.kaleo.runtime.graph.PathElement;
import com.liferay.portal.workflow.kaleo.runtime.internal.graph.petra.executor.GraphWalkerPortalExecutor;
import com.liferay.portal.workflow.kaleo.runtime.internal.node.NodeExecutorFactory;
import com.liferay.portal.workflow.kaleo.runtime.node.NodeExecutor;
import com.liferay.portal.workflow.kaleo.runtime.util.ExecutionContextHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Michael C. Han
 */
@Component(immediate = true, service = AopService.class)
@Transactional(
	isolation = Isolation.PORTAL, propagation = Propagation.SUPPORTS,
	rollbackFor = Exception.class
)
public class DefaultKaleoSignaler
	extends BaseKaleoBean implements AopService, KaleoSignaler {

	@Override
	public void signalEntry(
			String transitionName, ExecutionContext executionContext,
			boolean waitForCompletion)
		throws PortalException {

		KaleoInstanceToken kaleoInstanceToken =
			executionContext.getKaleoInstanceToken();

		executionContext.setTransitionName(transitionName);

		_doSignal(
			new PathElement(
				null, kaleoInstanceToken.getCurrentKaleoNode(),
				executionContext),
			waitForCompletion);
	}

	@Override
	@Transactional(
		isolation = Isolation.PORTAL, propagation = Propagation.REQUIRED,
		rollbackFor = Exception.class
	)
	public void signalExecute(
			KaleoNode currentKaleoNode, ExecutionContext executionContext,
			boolean waitForCompletion)
		throws PortalException {

		NodeExecutor nodeExecutor = _nodeExecutorFactory.getNodeExecutor(
			currentKaleoNode.getType());

		List<PathElement> remainingPathElements = new ArrayList<>();

		nodeExecutor.execute(
			currentKaleoNode, executionContext, remainingPathElements);

		_executionContextHelper.checkKaleoInstanceComplete(executionContext);

		for (PathElement remainingPathElement : remainingPathElements) {
			_doSignal(remainingPathElement, waitForCompletion);
		}
	}

	@Override
	public void signalExit(
			String transitionName, ExecutionContext executionContext,
			boolean waitForCompletion)
		throws PortalException {

		KaleoInstanceToken kaleoInstanceToken =
			executionContext.getKaleoInstanceToken();

		executionContext.setTransitionName(transitionName);

		PathElement pathElement = new PathElement(
			kaleoInstanceToken.getCurrentKaleoNode(), null, executionContext);

		_doSignal(pathElement, waitForCompletion);
	}

	private void _doSignal(PathElement pathElement, boolean waitForCompletion) {
		final CountDownLatch countDownLatch = new CountDownLatch(1);

		TransactionCommitCallbackUtil.registerCallback(
			() -> {
				_graphWalkerPortalExecutor.execute(countDownLatch, pathElement);

				return null;
			});

		if (waitForCompletion) {
			try {
				countDownLatch.await();
			}
			catch (InterruptedException interruptedException) {
				_log.error(interruptedException, interruptedException);
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DefaultKaleoSignaler.class);

	@Reference
	private ExecutionContextHelper _executionContextHelper;

	@Reference
	private GraphWalkerPortalExecutor _graphWalkerPortalExecutor;

	@Reference
	private NodeExecutorFactory _nodeExecutorFactory;

}