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

import PropTypes from 'prop-types';
import React, {useState} from 'react';

import CommentForm from './CommentForm.es';
import {editFragmentEntryLinkComment} from '../../../utils/FragmentsEditorFetchUtils.es';

const EditCommentForm = props => {
	const [editingComment, setEditingComment] = useState(false);
	const [textareaContent, setTextareaContent] = useState(props.body);

	const _handleCommentButtonClick = () => {
		setEditingComment(true);

		editFragmentEntryLinkComment(props.commentId, textareaContent)
			.then(response => response.json())
			.then(comment => {
				setEditingComment(false);

				props.onEdit(comment);
				props.onCloseForm();
			});
	};

	return (
		<CommentForm
			autoFocus
			loading={editingComment}
			onCancelButtonClick={() => props.onCloseForm()}
			onSubmitButtonClick={_handleCommentButtonClick}
			onTextareaChange={content => setTextareaContent(content)}
			showButtons
			submitButtonLabel={Liferay.Language.get('update')}
			textareaContent={textareaContent}
		/>
	);
};

EditCommentForm.defaultProps = {
	onEdit: () => {}
};

EditCommentForm.propTypes = {
	body: PropTypes.string.isRequired,
	commentId: PropTypes.string.isRequired,
	onCloseForm: PropTypes.func.isRequired,
	onEdit: PropTypes.func
};

export {EditCommentForm};
export default EditCommentForm;
