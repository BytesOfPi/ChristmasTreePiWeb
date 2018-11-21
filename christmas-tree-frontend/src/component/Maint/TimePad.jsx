import React from 'react';

class TimePad extends React.Component {
	constructor(props) {
		super(props);
		this.state = {
			milliPad: 0
		};
		//---------------------------------------------------------------------
		// Bind methods to this component
		this.handleSubmit = this.handleSubmit.bind(this);
	}

	//---------------------------------------------------------------------
	// Stop Current Song
	handleSubmit( event ) {		
		//---------------------------------------------------------------------
		// Set Millipad
		let milliPad = document.getElementById('milliPad').value;
		console.log(milliPad);
		fetch(`api/tree/maint/${milliPad}`)
			.then(res => ( res.ok ) ? res.json() : [{name: 'FAILED TO LOAD'}])
			.then(data => console.log(data))
			.catch( error => console.log(error) );
	}

	render() {
		return (
			<fieldset>
				<legend>Song Time Pad:</legend>
				<div class="form-group row">
					<div className="col-lg-3 col-md-3 col-sm-3">Time pad (in milliseconds)</div>
					<div className="col-lg-3 col-md-3 col-sm-3"><input id="milliPad" type="text"/></div>
					<div className="col-lg-3 col-md-3 col-sm-3"><button type="button" className="btn btn-primary" onClick={this.handleSubmit} >Add Pad</button></div>
				</div>
			</fieldset>
		);
	}
}

export default TimePad;