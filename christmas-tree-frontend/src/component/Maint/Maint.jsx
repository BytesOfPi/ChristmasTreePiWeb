import React from 'react';


class Maint extends React.Component {
	render() {
		return (
			<div className="bs-docs-section">
				<div class="col-lg-6">
					<div class="form-group row">
						<form action="api/upload/file" method="post" enctype="multipart/form-data">
							Select files to upload:<br />
							<input type="file" name="fileLoad" id="fileToUpload" /><br />
							<input type="file" name="fileLoad" id="fileToUpload" /><br />
							<input type="file" name="fileLoad" id="fileToUpload" /><br />
							<input type="submit" value="Upload files" name="submit" />
						</form>
					</div>
				</div>
			</div>
		);
	}
}

export default Maint;