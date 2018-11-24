import React, { Component } from 'react';
import ReactDOM from 'react-dom';
import { Droppable } from 'react-beautiful-dnd';
import SongEntry from './SongEntry';


const grid = 4;

const getListStyle = isDraggingOver => ({
    background: isDraggingOver ? 'lightblue' : 'lightgrey',
    padding: grid,
    width: 250
});

class SongList extends Component {
	constructor(props) {
        super(props);
    }

    render() {
        const {id, list, getClass} = this.props;
        return (
            <Droppable droppableId={id}>
                {(provided, snapshot) => (
                    <div
                        ref={provided.innerRef}
                        style={getListStyle(snapshot.isDraggingOver)}>
                        {list.map((item, index) => (
                            <SongEntry 
                                item={item}
                                index={index}
                                getClass={getClass} />
                        ))}
                        {provided.placeholder}
                    </div>
                )}
            </Droppable>
        );
    }
}

export default SongList;