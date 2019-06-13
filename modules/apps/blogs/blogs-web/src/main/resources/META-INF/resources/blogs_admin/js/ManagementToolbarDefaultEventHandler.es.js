import {DefaultEventHandler} from 'frontend-js-web';
import {Config} from 'metal-state';

class ManagementToolbarDefaultEventHandler extends DefaultEventHandler {
	deleteEntries() {
		if (
			this.trashEnabled ||
			confirm(
				Liferay.Language.get('are-you-sure-you-want-to-delete-this')
			)
		) {
			const form = this.one('#fm');

			const searchContainer = Liferay.SearchContainer.get(
				this.ns('blogEntries')
			);

			const bulkSelection =
				searchContainer.select &&
				searchContainer.select.get('bulkSelection');

			Liferay.Util.postForm(form, {
				data: {
					cmd: this.deleteEntriesCmd,
					deleteEntryIds: Liferay.Util.listCheckedExcept(
						form,
						this.ns('allRowIds')
					),
					selectAll: bulkSelection
				},
				url: this.deleteEntriesURL
			});
		}
	}
}

ManagementToolbarDefaultEventHandler.STATE = {
	deleteEntriesCmd: Config.string(),
	deleteEntriesURL: Config.string(),
	trashEnabled: Config.bool()
};

export default ManagementToolbarDefaultEventHandler;
