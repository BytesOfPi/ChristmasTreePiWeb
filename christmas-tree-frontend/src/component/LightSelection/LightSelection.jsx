import React from 'react';


class LightSelection extends React.Component {
	constructor(props) {
		super(props);
		this.state = {
			songs: [{name: 'bing'}, {name: 'COTB'}],
			adminPath: false,
			playGame: false
		};
		//---------------------------------------------------------------------
		// Bind methods to this component
		this.handleToggleLight = this.handleToggleLight.bind(this);
	}

	//---------------------------------------------------------------------
	// Generate Music Selection dropdown
	handleToggleLight(event) {
		const channel = event.target.id.replace('light_', '');

		//---------------------------------------------------------------------
		// Toggle light
		fetch(`api/tree/toggle/${channel}`)
			.then(res => ( res.ok ) ? res.json() : [{name: 'FAILED TO LOAD'}])
			.then(data => { console.log(data);  } )
			.catch( error => console.log(error) );
		//---------------------------------------------------------------------
		// Log channel
		console.log(channel);
	}

	render() {
		return (
			<div className="bs-docs-section">
				<div class="col-lg-6">
					<div class="form-group row">
						<h3>Toggle Lights</h3>
					</div>
					<div class="form-group row">
						<div className="col-lg-4 col-md-4 col-sm-4"><button id="light_1" type="button" className="btn btn-primary" onClick={this.handleToggleLight} >Toggle Light 1</button></div>
						<div className="col-lg-4 col-md-4 col-sm-4"><button id="light_2" type="button" className="btn btn-primary" onClick={this.handleToggleLight} >Toggle Light 2</button></div>
						<div className="col-lg-4 col-md-4 col-sm-4"><button id="light_3" type="button" className="btn btn-primary" onClick={this.handleToggleLight} >Toggle Light 3</button></div>
					</div>
					<div class="form-group row">
						<div className="col-lg-4 col-md-4 col-sm-4"><button id="light_4" type="button" className="btn btn-primary" onClick={this.handleToggleLight} >Toggle Light 4</button></div>
						<div className="col-lg-4 col-md-4 col-sm-4"><button id="light_5" type="button" className="btn btn-primary" onClick={this.handleToggleLight} >Toggle Light 5</button></div>
						<div className="col-lg-4 col-md-4 col-sm-4"><button id="light_6" type="button" className="btn btn-primary" onClick={this.handleToggleLight} >Toggle Light 6</button></div>
					</div>
					<div class="form-group row">
						<div className="col-lg-4 col-md-4 col-sm-4"><button id="light_7" type="button" className="btn btn-primary" onClick={this.handleToggleLight} >Toggle Light 7</button></div>
						<div className="col-lg-4 col-md-4 col-sm-4"><button id="light_8" type="button" className="btn btn-primary" onClick={this.handleToggleLight} >Toggle Light 8</button></div>
						<div className="col-lg-4 col-md-4 col-sm-4"><button id="light_0" type="button" className="btn btn-success" onClick={this.handleToggleLight} >Toggle All</button></div>
					</div>
				</div>
			</div>
		);
	}
}

export default LightSelection;