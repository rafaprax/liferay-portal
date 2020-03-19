/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Liferay Enterprise
 * Subscription License ("License"). You may not use this file except in
 * compliance with the License. You can obtain a copy of the License by
 * contacting Liferay, Inc. See the License for the specific language governing
 * permissions and limitations under the License, including but not limited to
 * distribution rights of the Software.
 */

import ClayAlert from '@clayui/alert';
import ClayButton from '@clayui/button';
import ClayModal, {useModal} from '@clayui/modal';
import React, {useCallback, useContext, useMemo, useState} from 'react';

import StepBar from '../../../../../shared/components/step-bar/StepBar.es';
import {useToaster} from '../../../../../shared/components/toaster/hooks/useToaster.es';
import {useFilter} from '../../../../../shared/hooks/useFilter.es';
import {usePatch} from '../../../../../shared/hooks/usePatch.es';
import {usePost} from '../../../../../shared/hooks/usePost.es';
import {sub} from '../../../../../shared/util/lang.es';
import {InstanceListContext} from '../../../InstanceListPageProvider.es';
import {ModalContext} from '../../ModalProvider.es';
import {SelectTasksStep} from '../../shared/select-tasks-step/SelectTasksStep.es';
import {BulkReassignSelectAssigneesStep} from './select-assignees-step/BulkReassignSelectAssigneesStep.es';

const BulkReassignModal = () => {
	const {
		bulkReassign,
		processId,
		selectTasks,
		setBulkReassign,
		setSelectTasks,
		setVisibleModal,
		visibleModal,
	} = useContext(ModalContext);
	const {
		selectAll: selectAllInstances,
		selectedItems,
		setSelectAll,
		setSelectedItems,
	} = useContext(InstanceListContext);

	const {
		dispatch,
		filterState,
		filterValues: {
			bulkAssigneeUserIds: userIds,
			bulkTaskKeys: workflowTaskNames,
		},
	} = useFilter({withoutRouteParams: true});

	const {reassignedTasks, reassigning} = bulkReassign;

	const [currentStep, setCurrentStep] = useState('selectTasks');
	const [errorToast, setErrorToast] = useState(null);
	const [fetching, setFetching] = useState(false);
	const {selectAll, tasks} = selectTasks;
	const toaster = useToaster();

	const {observer, onClose} = useModal({
		onClose: () => {
			setBulkReassign({
				...bulkReassign,
				reassigning: false,
				selectedAssignee: null,
				useSameAssignee: false,
			});

			setSelectTasks({selectAll: false, tasks: []});

			setVisibleModal('');

			dispatch({
				...filterState,
				bulkAssigneeUserIds: [],
				bulkTaskKeys: [],
			});

			setCurrentStep('selectTasks');

			setErrorToast(false);
		},
	});

	const {patchData} = usePatch({
		admin: true,
		body: reassignedTasks,
		url: 'workflow-tasks/assign-to-user',
	});

	const body = useMemo(() => {
		const filterByUser = userIds && userIds.length;

		const assigneeIds = filterByUser
			? userIds.filter(id => id !== '-1')
			: undefined;

		const searchByRoles = filterByUser && userIds.includes('-1');

		const params = {
			assigneeIds,
			completed: false,
			searchByRoles,
			workflowTaskNames,
		};

		if (selectAllInstances) {
			params.workflowDefinitionId = processId;
		}
		else {
			params.workflowInstanceIds = selectedItems.map(({id}) => id);
		}

		return params;
		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, [selectedItems, userIds, workflowTaskNames]);

	const {postData} = usePost({
		admin: true,
		body,
		url: '/workflow-tasks?page=1&pageSize=-1&sort=workflowInstanceId:asc',
	});

	const handleNext = useCallback(() => {
		if (selectAll) {
			setFetching(true);
			postData()
				.then(({items}) => {
					setSelectTasks({selectAll, tasks: items});
					setCurrentStep('selectAssignees');
					setFetching(false);
				})
				.catch(() => {
					setErrorToast(
						Liferay.Language.get('your-request-has-failed')
					);
					setFetching(false);
				});
		}
		else {
			setCurrentStep('selectAssignees');
		}
		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, [bulkReassign, postData, selectAll]);

	const handlePrevious = () => {
		setBulkReassign({
			...bulkReassign,
			reassignedTasks: [],
			reassigning: false,
			selectedAssignee: null,
			useSameAssignee: false,
		});
		setCurrentStep('selectTasks');
		setErrorToast(false);
	};

	const handleReassign = useCallback(() => {
		if (
			reassignedTasks.length > 0 &&
			reassignedTasks.length === tasks.length
		) {
			setBulkReassign({
				...bulkReassign,
				reassigning: true,
			});

			setErrorToast(false);

			patchData()
				.then(() => {
					onClose();

					toaster.success(
						reassignedTasks.length > 1
							? sub(
									Liferay.Language.get(
										'x-tasks-have-been-reassigned'
									),
									[reassignedTasks.length]
							  )
							: Liferay.Language.get(
									'this-task-has-been-reassigned'
							  )
					);

					setSelectedItems([]);
					setSelectAll(false);
				})
				.catch(() => {
					const error = `${Liferay.Language.get(
						'your-request-has-failed'
					)} ${Liferay.Language.get('select-reassign-to-retry')}`;

					setBulkReassign({
						...bulkReassign,
						reassigning: false,
					});

					setErrorToast(error);
				});
		}
		// eslint-disable-next-line react-hooks/exhaustive-deps
	}, [patchData]);

	const getStep = useCallback(
		step => {
			const steps = {
				selectAssignees: {
					component: BulkReassignSelectAssigneesStep,
					nextBtn: {
						disabled: reassignedTasks.length !== tasks.length,
						handle: handleReassign,
						text: Liferay.Language.get('reassign'),
					},
					order: 2,
					previousBtn: true,
					subtitle: Liferay.Language.get('select-assignees'),
					title: Liferay.Language.get('select-new-assignees'),
				},
				selectTasks: {
					component: SelectTasksStep,
					nextBtn: {
						disabled: tasks.length === 0 || fetching,
						handle: handleNext,
						text: Liferay.Language.get('next'),
					},
					order: 1,
					previousBtn: false,
					subtitle: Liferay.Language.get('select-tasks'),
					title: Liferay.Language.get('select-tasks-to-reassign'),
				},
			};

			return steps[step];
		},
		[
			fetching,
			handleNext,
			handleReassign,
			reassignedTasks.length,
			tasks.length,
		]
	);

	const step = useMemo(() => getStep(currentStep), [currentStep, getStep]);

	return (
		<>
			{visibleModal === 'bulkReassign' && (
				<ClayModal
					data-testid="bulkReassignModal"
					observer={observer}
					size="lg"
				>
					<ClayModal.Header>{step.title}</ClayModal.Header>

					{errorToast && (
						<ClayAlert
							className="mb-0"
							data-testid="alertError"
							displayType="danger"
							title={Liferay.Language.get('error')}
						>
							{errorToast}
						</ClayAlert>
					)}

					<StepBar
						current={step.order}
						title={step.subtitle}
						total={2}
					/>

					<div className="fixed-height modal-metrics-content">
						<step.component
							processId={processId}
							setErrorToast={setErrorToast}
						/>
					</div>

					<ClayModal.Footer
						first={
							<ClayButton
								data-testid="cancelButton"
								disabled={reassigning}
								displayType="secondary"
								onClick={onClose}
							>
								{Liferay.Language.get('cancel')}
							</ClayButton>
						}
						last={
							<>
								{step.previousBtn && (
									<ClayButton
										className="mr-3"
										data-testid="previousButton"
										disabled={reassigning}
										displayType="secondary"
										onClick={handlePrevious}
									>
										{Liferay.Language.get('previous')}
									</ClayButton>
								)}

								<ClayButton
									data-testid="nextButton"
									disabled={
										reassigning || step.nextBtn.disabled
									}
									onClick={step.nextBtn.handle}
								>
									{step.nextBtn.text}
								</ClayButton>
							</>
						}
					/>
				</ClayModal>
			)}
		</>
	);
};

export default BulkReassignModal;
