import React from 'react';


class MusicSelection extends React.Component {
	constructor(props) {
		super(props);
		this.state = {
			songs: [{name: 'bing', title: 'bing', id: 1}],
			adminPath: false,
			playGame: false
		};
		//---------------------------------------------------------------------
		// Bind methods to this component
		this.loadSongs = this.loadSongs.bind(this);
		this.genSongOptions = this.genSongOptions.bind(this);
		this.genSongSelect = this.genSongSelect.bind(this);
		this.handleSongSubmit = this.handleSongSubmit.bind(this);
	}
	//---------------------------------------------------------------------
	// When you reach the page...
	componentWillMount() {
		this.loadSongs();
	}
	//---------------------------------------------------------------------
	// Load teams from backend
	loadSongs() {
		fetch('api/tree/song/get')
			.then(res => ( res.ok ) ? res.json() : [{name: 'FAILED TO LOAD'}])
			.then(data => this.setState({songs: data.songs}))
			.catch( error => console.log(error) );
	}
	onKeyPress(e) {
		if(e.key === 'Enter'){
			this.handleSongSubmit();
		}
	}
	
	//---------------------------------------------------------------------
	// Generate Music Selection dropdown
	handleSongSubmit() {
		const channel = this.refs.songs.value;
		console.log(channel);
		
		//---------------------------------------------------------------------
		// Play song
		fetch(`api/tree/song/play/${channel}`)
			.then(res => ( res.ok ) ? res.json() : [{name: 'FAILED TO LOAD'}])
			.then(data => console.log(data))
			.catch( error => console.log(error) );
	}
	//---------------------------------------------------------------------
	// Generate Music Selection dropdown
	genSongOptions(songs) {
		return songs.map(entry => <option value={entry.id}>{entry.name}</option>);
	}
	genSongSelect() {
		return (<select class="form-control" id="songs" ref="songs">{this.genSongOptions(this.state.songs)}</select>);
	}
	render() {
		return (
			<div className="bs-docs-section">
				<div class="col-lg-6">
					<div class="form-group row">
                    Music: {this.genSongSelect()}
					</div>
					<div class="form-group row">
						<button type="button" className="btn btn-primary" onClick={this.handleSongSubmit} >Submit</button>
					</div>
				</div>
			</div>
		);
	}
}

export default MusicSelection;