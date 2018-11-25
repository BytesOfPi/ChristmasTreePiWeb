import React, { Component } from 'react';
import ReactDOM from 'react-dom';
import PropTypes from 'prop-types';
import { Droppable } from 'react-beautiful-dnd';
import SongEntry from './SongEntry';

const grid = 4;

const getListStyle = isDraggingOver => ({
    background: isDraggingOver ? 'lightblue' : 'lightgrey',
    padding: grid,
    width: 250
});

/**
 * SongList Component
 * 
 * This component is a single list of songs.  It is the grouping of songs that allows for
 * Drag and Drop source/target.  You can drag songs in the component to reorder them or
 * drag from other lists to add / remove songs.
 */
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

SongList.propTypes = {
    id: PropTypes.string.isRequired,
    list: PropTypes.array.isRequired,
    getClass: PropTypes.func.isRequired
};

export default SongList;