import React from 'react';


class UploadFiles extends React.Component {
	constructor(props) {
		super(props);
		this.state = {
			alerts: []
		};
		//---------------------------------------------------------------------
		// Bind methods to this component
		this.handleSubmit = this.handleSubmit.bind(this);
		this.dismissAlert = this.dismissAlert.bind(this);
		this.generateAlert = this.generateAlert.bind(this);
		this.generateAlerts = this.generateAlerts.bind(this);
	}

	handleSubmit(event) {
		event.preventDefault();
		const data = new FormData(event.target);
		
		fetch('api/upload/file', {
			method: 'POST',
			body: data,
		})
		.then(res => ( res.ok ) ? res.json() : [{name: 'FAILED TO LOAD'}])
		.then(data => {
			var alerts = [];
			//---------------------------------------------------------------------
			// Load messages into array
			var id = 0;
			for (var prop in data) {
				if ( prop !== "" ) {
					alerts.push( {id, msg: `${prop}: ${data[prop]}` });
					id++;
				}
			}
			//---------------------------------------------------------------------
			// Set the alerts array
			this.setState({alerts});
		})
		.catch( error => console.log(error) );
	 }

	dismissAlert(event) {
		var alerts = this.state.alerts.filter(function(value, index, arr){
			return value.id !== parseInt(event.target.id);
		});
		//---------------------------------------------------------------------
		// Set the alerts array
		this.setState({alerts});
	}
	
	generateAlert(alert) {
		return (<div class="alert alert-dismissible alert-info">
		<button type="button" class="close" id={alert.id} data-dismiss="alert" onClick={this.dismissAlert}>&times;</button>{alert.msg}
	  </div>);
	}

	generateAlerts(data) {
		//---------------------------------------------------------------------
		// Return mapped alerts
		return data.map(alert => this.generateAlert(alert));
	}



	render() {
		return (
		<fieldset>
			<legend>File Upload:</legend>
			<div class="form-group row">
				{this.generateAlerts(this.state.alerts)}
			</div>
			<div class="form-group row">
				
				<form onSubmit={this.handleSubmit}>
					Select files to upload:<br />
					<input type="file" name="fileLoad" id="fileToUpload" /><br />
					<input type="file" name="fileLoad" id="fileToUpload" /><br />
					<input type="file" name="fileLoad" id="fileToUpload" /><br />
					<input type="submit" value="Upload files" name="submit" />
				</form>
			</div>
		</fieldset>
		);
	}
}

export default UploadFiles;