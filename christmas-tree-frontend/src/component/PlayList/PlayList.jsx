import React, { Component } from 'react';
import ReactDOM from 'react-dom';
import { DragDropContext, Droppable, Draggable } from 'react-beautiful-dnd';

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

const grid = 8;

/**
 * DND dynamic class
 * These methods dynamically change the dragged item's class to show it's being
 * dragged or if it's still
 */
const getItemClass = (isDragging) => (
	isDragging ? 'card text-white bg-primary mb-3' : 'card text-white bg-success mb-3'
);
const getPlaylistClass = (isDragging) => (
	isDragging ? 'card text-white bg-success mb-3' : 'card text-white bg-primary mb-3'
);

const getListStyle = isDraggingOver => ({
    background: isDraggingOver ? 'lightblue' : 'lightgrey',
    padding: grid,
    width: 250
});


class PlayList extends Component {
	constructor(props) {
		super(props);
		this.state = {
			items: [],
			playlist: [],
			/**
			 * A semi-generic way to handle multiple lists. Matches
			 * the IDs of the droppable container to the names of the
			 * source arrays stored in the state.
			 */
			id2List: {
				droppable: 'items',
				droppable2: 'playlist'
			}
		};
		//---------------------------------------------------------------------
		// Bind methods to this component
		this.getList = this.getList.bind(this);
		this.onDragEnd = this.onDragEnd.bind(this);
		this.loadSongs = this.loadSongs.bind(this);
		
		this.handleDump = this.handleDump.bind(this);
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
		return this.state[this.state.id2List[id]];
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
				let items = [];
				data.songs.map( song => {
					items.push({id: song.id, content: song.name});
				}); 
		
				this.setState({items, playlist:[] });
			})
			.catch( error => console.log(error) );
	}

	/**
	 * DND Action when drag ends
	 * This method will handle what happens when a DND drag is complete
	 */
    onDragEnd(result){
        const { source, destination } = result;

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

            let state = { items };

            if (source.droppableId === 'droppable2') {
                state = { playlist: items };
            }

            this.setState(state);
		} 
		// If dropped in the other list
		else {
            const result = move(
                this.getList(source.droppableId),
                this.getList(destination.droppableId),
                source,
                destination
            );

            this.setState({
                items: result.droppable,
                playlist: result.droppable2
            });
        }
    };

	handleDump() {
		console.log(this.state, this.state.items, this.state.playlist);
	}

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

	handlePlay() {
		console.log('TODO');
		let pList = [];
		this.state.playlist.map( song => {
			pList.push(song.id);
		}); 
		
		console.log(pList);
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

    // Normally you would want to split things out into separate components.
    // But in this example everything is just done in one place for simplicity
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
						<Droppable droppableId="droppable">
						{(provided, snapshot) => (
							<div
								ref={provided.innerRef}
								style={getListStyle(snapshot.isDraggingOver)}>
								{this.state.items.map((item, index) => (
									<Draggable
										key={item.id}
										draggableId={item.id}
										index={index}>
										{(provided, snapshot) => (
											<div
												ref={provided.innerRef}
												{...provided.draggableProps}
												{...provided.dragHandleProps}
												className={getItemClass(snapshot.isDragging)}
												style={provided.draggableProps.style}>
												{item.content}
											</div>
										)}
									</Draggable>
								))}
								{provided.placeholder}
							</div>
						)}
						</Droppable></td>
					<td>
						<Droppable droppableId="droppable2">
							{(provided, snapshot) => (
								<div
									ref={provided.innerRef}
									style={getListStyle(snapshot.isDraggingOver)}>
									{this.state.playlist.map((item, index) => (
										<Draggable
											key={item.id}
											draggableId={item.id}
											index={index}>
											{(provided, snapshot) => (
												<div
													ref={provided.innerRef}
													{...provided.draggableProps}
													{...provided.dragHandleProps}
													className={getPlaylistClass(snapshot.isDragging)}
													style={provided.draggableProps.style}>
													{item.content}
												</div>
											)}
										</Draggable>
									))}
									{provided.placeholder}
								</div>
							)}
						</Droppable>
					</td>
				</tr>
			</tbody>
			</table>
			<div class="form-group row">
				<div className="col-lg-3 col-md-3 col-sm-3"><button type="button" className="btn btn-primary" onClick={this.handleDump} >Dump</button></div>
				<div className="col-lg-3 col-md-3 col-sm-3"><button type="button" className="btn btn-primary" onClick={this.handleRefresh} >Refresh</button></div>
				<div className="col-lg-3 col-md-3 col-sm-3"><button type="button" className="btn btn-primary" onClick={this.handlePlay} >Play</button></div>
			</div>
		</DragDropContext>
        );
    }
}

export default PlayList;