import React from 'react';

import UploadFiles from './UploadFiles';
import TimePad from './TimePad';


class Maint extends React.Component {
	render() {
		return (
			<div className="bs-docs-section">
				<div class="col-lg-6">
					<UploadFiles />
				</div>
				<div class="col-lg-6">
					<TimePad />
				</div>
			</div>
		);
	}
}

export default Maint;