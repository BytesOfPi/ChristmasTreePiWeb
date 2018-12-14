import React, { Component } from 'react';
import ReactDOM from 'react-dom';
import PropTypes from 'prop-types';
import { DragDropContext } from 'react-beautiful-dnd';
import SongList from '../../component/PlayList/SongList';
import SongSelections from '../../component/PlayList/SongSelections';
import { Object } from 'es6-shim';

// a little function to help us with reordering the result
const reorder = (list, startIndex, endIndex) => {
    const result = Array.from(list);
    const [removed] = result.splice(startIndex, 1);
    result.splice(endIndex, 0, removed);

    return result;
};

/**
 * Moves an item from one list to another list.
 */
const move = (source, destination, droppableSource, droppableDestination) => {
    const sourceClone = Array.from(source);
    const destClone = Array.from(destination);
    const [removed] = sourceClone.splice(droppableSource.index, 1);

    destClone.splice(droppableDestination.index, 0, removed);

    const result = {};
    result[droppableSource.droppableId] = sourceClone;
    result[droppableDestination.droppableId] = destClone;

    return result;
};

/**
 * DND dynamic classes
 * These methods dynamically change the dragged item's class to show it's being
 * dragged or if it's still
 */
const getItemClass = (isDragging) => (
	isDragging ? 'card text-white bg-primary mb-3' : 'card text-white bg-success mb-3'
);
const getPlaylistClass = (isDragging) => (
	isDragging ? 'card text-white bg-success mb-3' : 'card text-white bg-primary mb-3'
);

/**
 * PlayList Container
 * 
 * This container houses all elements in the "Play List" tab.  It utilizes Drag and Drop
 * components to build / order / manage the current playlist.
 */
class PlayList extends Component {
	constructor(props) {
		super(props);
		this.state = {
			playlist: [],
			categories: {}
		};
		//---------------------------------------------------------------------
		// Bind methods to this component
		this.getList = this.getList.bind(this);
		this.onDragEnd = this.onDragEnd.bind(this);
		this.loadSongs = this.loadSongs.bind(this);
		this.buildState = this.buildState.bind(this);
		
		this.handleStopSong = this.handleStopSong.bind(this);
		this.handleRefresh = this.handleRefresh.bind(this);
		this.handlePlay = this.handlePlay.bind(this);
	}

	/**
	 * Component Did Mount (On load)
	 * This method initializes the component when it is first drawn
	 */
	componentDidMount() {
		this.loadSongs();
	}

	/**
	 * Get List.
	 * Quick utility function to map the droppable DOM object to the state values
	 */
    getList(id) {
		return (id === 'playlist') ? this.state[id] : this.state.categories[id];
	}

	/**
	 * Load Songs
	 * This method will call the backend to pull up a list of songs.  It will then
	 * redraw the playlist.
	 */
	loadSongs() {
		//---------------------------------------------------------------------
		// Pull back list of songs and then draw list
		fetch('api/tree/song/get')
			.then(res => ( res.ok ) ? res.json() : [{name: 'FAILED TO LOAD'}])
			.then(data => {
				//---------------------------------------------------------------------
				// Lowercase categories, just for consistency
				let categories = {};
				Object.entries(data.categories).forEach( entry => {
					categories[entry[0].toLowerCase()]=entry[1];
				});
		
				//---------------------------------------------------------------------
				// 0 out playlist and set categories
				this.setState({ playlist: [], categories });
			})
			.catch( error => console.log(error) );
	}

	/**
	 * Build State
	 * This method streamlines recreating this component's state based on the drag n drop changes.
	 */
	buildState( id, values ) {
		// Override target playlist if playlist was reordered
		if (id === 'playlist') return { playlist: values };
		// Otherwise, clone categories and override only the list modified
		let { categories } = this.state;
		categories[id] = values;
		return { categories };
	}

	/**
	 * DND Action when drag ends
	 * This method will handle what happens when a DND drag is complete
	 */
    onDragEnd(result){
        const { source, destination } = result;

				//---------------------------------------------------------------------
        // dropped outside the list
        if (!destination) {
            return;
        }

        // If dropped in same list
        if (source.droppableId === destination.droppableId) {
            const items = reorder(
                this.getList(source.droppableId),
                source.index,
                destination.index
            );

			this.setState( this.buildState(source.droppableId, items) );
		} 
		// If dropped in the other list, move items
		else {
            const result = move(
                this.getList(source.droppableId),
                this.getList(destination.droppableId),
                source,
                destination
			);
			
			let sourceState = this.buildState(source.droppableId, result[source.droppableId]);
			let destinationState = this.buildState(destination.droppableId, result[destination.droppableId]);
			// Spread not working... not ES6 :( )
			// this.setState( {...sourceState, ...destinationState} );
			this.setState( Object.assign(sourceState, destinationState) );
        }
    };

	/**
	 * Handle Stop Songs (Event Handler)
	 * This method will stop the current playing song
	 */
	handleStopSong() {		
		//---------------------------------------------------------------------
		// Stop song
		fetch(`api/tree/song/stop`)
			.then(res => ( res.ok ) ? res.json() : [{name: 'FAILED TO LOAD'}])
			.then(data => console.log(data))
			.catch( error => console.log(error) );
	}

	/**
	 * Handle Refresh (Event Handler)
	 * This method will signal to re-read and pull in new songs / categories
	 */
	handleRefresh() {
		//---------------------------------------------------------------------
		// Send message to refresh list data then redraw list
		fetch(`api/tree/maint/refresh`)
			.then(res => ( res.ok ) ? res.json() : [{name: 'FAILED TO LOAD'}])
			.then(data => { 
				//---------------------------------------------------------------------
				// Refresh list
				this.loadSongs();
			})
			.catch( error => console.log(error) );
	}

	/**
	 * Handle Play (Event Handler)
	 * This method will trigger the start of a new playlist sequence.  It will stop
	 * the existing sequence (if any is playing)
	 */
	handlePlay() {
		let pList = [];
		this.state.playlist.map( song => {
			pList.push(song.id);
		}); 

		//---------------------------------------------------------------------
		// Refresh list
		fetch('api/tree/song/playlist', {
			method: 'POST',
			mode: 'cors',
			headers: { 'Content-Type': 'application/json; charset=utf-8' },
			body: JSON.stringify(pList)
		})
		.then(data => console.log(data))
		.catch( error => console.log(error) );
	}

	/**
	 * Render
	 * This method will render the component.  The component is composed of:
	 *		1) A list of song selection categories on the left
	 *		2) The play list on the right
	 *		3) Event buttons on the bottom to start, stop and refresh
	 */
    render() {
        return (
		<DragDropContext onDragEnd={this.onDragEnd}>
			<table class="table table-hover">
			<thead>
				<tr>
					<th scope="col">Options</th>
					<th scope="col">Play List</th>
				</tr>
			</thead>
			<tbody>
				<tr class="table-secondary">
					<td>
						<SongSelections 
							categories={this.state.categories}
							getClass={getItemClass} />
					</td>
					<td>
						<SongList
							id="playlist" //droppable2
							list={this.state.playlist}
							getClass={getPlaylistClass} />
					</td>
				</tr>
			</tbody>
			</table>
			<div class="form-group row">
				<div className="col-lg-3 col-md-3 col-sm-3"><button type="button" className="btn btn-primary" onClick={this.handleStopSong} >Stop Song</button></div>
				<div className="col-lg-3 col-md-3 col-sm-3"><button type="button" className="btn btn-primary" onClick={this.handleRefresh} >Refresh</button></div>
				<div className="col-lg-3 col-md-3 col-sm-3"><button type="button" className="btn btn-primary" onClick={this.handlePlay} >Play</button></div>
			</div>
		</DragDropContext>
        );
    }
}

PlayList.propTypes = {
};

export default PlayList;