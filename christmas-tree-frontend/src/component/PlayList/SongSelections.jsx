import React, { Component } from 'react';
import PropTypes from 'prop-types';
import SongList from './SongList';

const styleShow = {display: 'block'};
const styleHide = {display: 'none'};

/**
 * SongSelections Component
 * 
 * This component allows the user to select a song list by category.  When there is a lot of
 * songs to choose from, it allows for an easier visual experience to hide all categories of
 * songs except the category of songs you want to select.
 */
class SongSelections extends Component {
	constructor(props) {
        super(props);
		//---------------------------------------------------------------------
		// Bind methods to this component
		this.genCategoryLists = this.genCategoryLists.bind(this);
		this.genCategoryOptions = this.genCategoryOptions.bind(this);
        this.genCategorySelect = this.genCategorySelect.bind(this);
        
		this.handleShowList = this.handleShowList.bind(this);
    }

	//---------------------------------------------------------------------
    // Handle hiding / showing lists
    handleShowList(event) {
        let id = event.target.value;
        //---------------------------------------------------------------------
        // Loop through category names
        const categoryKeys = Object.keys(this.props.categories);
        categoryKeys.forEach( categoryKey => {
            var x = document.getElementById(`list_${categoryKey.toLowerCase()}`);
            //---------------------------------------------------------------------
            // Show the selected
            console.log("Before", id, categoryKey.toLowerCase(), x.style.display);
            x.style.display = (id === categoryKey.toLowerCase()) ? 'block' : 'none';
            console.log("After", id, categoryKey.toLowerCase(), x.style.display);
        });
    }
    
	//---------------------------------------------------------------------
	// Generate Category Lists
	genCategoryLists(categories){
        var isVisble = true;
        return Object.entries(categories).map(entry => {
            //---------------------------------------------------------------------
            // Only show first list when drawn
            const divStyle = (isVisble) ? styleShow : styleHide;
            isVisble = false;

            //---------------------------------------------------------------------
            // Generate Song List
            return <div id={`list_${entry[0].toLowerCase()}`} style={divStyle}><SongList
            id={entry[0].toLowerCase()} //droppable2
            list={entry[1]}
            getClass={this.props.getClass} /></div>;
        });
    }

	//---------------------------------------------------------------------
	// Generate Category Selection dropdown
	genCategoryOptions(entries) {
		return entries.map(entry => <option value={entry[0].toLowerCase()}>{entry[0]}</option>);
	}
	genCategorySelect(categories) {
		return (<select onChange={this.handleShowList} id="categories" ref="categories">{this.genCategoryOptions(Object.entries(categories))}</select>);
	}

    render() {
        const { categories } = this.props;
        return (
            <div>
                {this.genCategorySelect(categories)}
                {this.genCategoryLists(categories)}
            </div>
        );
    }
}

SongSelections.propTypes = {
    categories: PropTypes.object.isRequired,
    getClass: PropTypes.func.isRequired
};

export default SongSelections;