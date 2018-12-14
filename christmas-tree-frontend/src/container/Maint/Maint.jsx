import React from 'react';

import UploadFiles from '../../component/Maint/UploadFiles';
import TimePad from '../../component/Maint/TimePad';

/**
 * Maint Container
 * 
 * This container houses all maintainence elements.  It allows for uploading new instructions / music
 * as well as allows to add milliseconds plus/minus to synch music with lights.
 */
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