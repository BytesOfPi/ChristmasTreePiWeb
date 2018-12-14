import React, { Component } from 'react';
import ReactDOM from 'react-dom';
import PropTypes from 'prop-types';
import { Draggable } from 'react-beautiful-dnd';

/**
 * SongEntry Component
 * 
 * This component is a single song.  This item implements the Draggable tag so it
 * can be dragged within a SongList to reorder or dragged between SongLists.
 */
class SongEntry extends Component {
	constructor(props) {
        super(props);
    }
    //  provided, snapshot,
    render() {
        const { item, index, getClass } = this.props;
        return (
            <Draggable
                key={item.id}
                draggableId={item.id}
                index={index}>
                {(provided, snapshot) => (
                    <div
                        ref={provided.innerRef}
                        {...provided.draggableProps}
                        {...provided.dragHandleProps}
                        className={getClass(snapshot.isDragging)}
                        style={provided.draggableProps.style}>
                        {item.title}
                    </div>
                )}
            </Draggable>
        );
    }
}

SongEntry.propTypes = {
    item: PropTypes.object.isRequired,
    index: PropTypes.number.isRequired,
    getClass: PropTypes.func.isRequired
};

export default SongEntry;
