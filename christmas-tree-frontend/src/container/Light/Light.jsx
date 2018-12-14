import React from 'react';


class Light extends React.Component {
	render() {
		return (

			<div className="bs-docs-section">
				<div class="col-lg-6">
					<div class="form-group row">
						<button type="button" className="btn btn-primary" onClick={this.handleSongSubmit} >Light 1</button>
					</div>
				</div>
			</div>
		);
	}
}

export default Light;