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

import ClayButton from '@clayui/button';
import ClayIcon from '@clayui/icon';
import PropTypes from 'prop-types';
import React from 'react';

import AddCommentForm from './AddCommentForm.es';
import {deleteFragmentEntryLinkCommentAction} from '../../../actions/deleteFragmentEntryLinkComment.es';
import {FRAGMENTS_EDITOR_ITEM_TYPES} from '../../../utils/constants';
import {updateFragmentEntryLinkCommentAction} from '../../../actions/updateFragmentEntryLinkComment.es';
import {updateFragmentEntryLinkCommentReplyAction} from '../../../actions/updateFragmentEntryLinkCommentReply.es';
import FragmentComment from './FragmentComment.es';
import useSelector from '../../../store/hooks/useSelector.es';
import useDispatch from '../../../store/hooks/useDispatch.es';
import {CLEAR_ACTIVE_ITEM} from '../../../actions/actions.es';
import SidebarHeader from '../SidebarHeader.es';
import ShowResolvedCommentsToggle from './ShowResolvedCommentsToggle.es';
import useGetComments from '../../../store/hooks/useGetComments.es';

const FragmentComments = props => {
	const fragmentEntryLink = useSelector(
		state => state.fragmentEntryLinks[props.fragmentEntryLinkId]
	);
	const getComments = useGetComments();
	const fragmentEntryLinkComments = getComments(fragmentEntryLink);
	const dispatch = useDispatch();

	const {
		clearActiveItem,
		deleteComment,
		editComment,
		editCommentReply
	} = getActions(dispatch, props);

	return (
		<>
			<SidebarHeader>
				<ClayButton
					borderless
					className="position-absolute text-dark"
					onClick={clearActiveItem}
					small
				>
					<ClayIcon symbol="angle-left" />
				</ClayButton>

				<span className="ml-5">{fragmentEntryLink.name}</span>
			</SidebarHeader>

			<div
				data-fragments-editor-item-id={props.fragmentEntryLinkId}
				data-fragments-editor-item-type={
					FRAGMENTS_EDITOR_ITEM_TYPES.fragment
				}
			>
				<ShowResolvedCommentsToggle />

				<AddCommentForm
					fragmentEntryLinkId={props.fragmentEntryLinkId}
				/>

				{[...fragmentEntryLinkComments].reverse().map(comment => (
					<FragmentComment
						comment={comment}
						fragmentEntryLinkId={props.fragmentEntryLinkId}
						key={comment.commentId}
						onDelete={deleteComment}
						onEdit={editComment}
						onEditReply={editCommentReply}
					/>
				))}
			</div>
		</>
	);
};

FragmentComments.propTypes = {
	fragmentEntryLinkId: PropTypes.string.isRequired
};

const getActions = (dispatch, ownProps) => ({
	clearActiveItem: () =>
		dispatch({
			type: CLEAR_ACTIVE_ITEM
		}),
	deleteComment: comment =>
		dispatch(
			deleteFragmentEntryLinkCommentAction(
				ownProps.fragmentEntryLinkId,
				comment
			)
		),
	editComment: comment =>
		dispatch(
			updateFragmentEntryLinkCommentAction(
				ownProps.fragmentEntryLinkId,
				comment
			)
		),
	editCommentReply: parentCommentId => comment =>
		dispatch(
			updateFragmentEntryLinkCommentReplyAction(
				ownProps.fragmentEntryLinkId,
				parentCommentId,
				comment
			)
		)
});

export {FragmentComments};
export default FragmentComments;
