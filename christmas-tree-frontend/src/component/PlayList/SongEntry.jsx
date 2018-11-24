import React, { Component } from 'react';
import ReactDOM from 'react-dom';
import { Draggable } from 'react-beautiful-dnd';


class SongList extends Component {
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
export default SongList;
