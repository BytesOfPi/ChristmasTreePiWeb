import React from 'react';
import LightSelection from '../../component/LightSelection/LightSelection';
import MusicSelection from '../../component/MusicSelection/MusicSelection';
import Maint from '../../component/Maint/Maint';
import PlayList from '../../component/PlayList/PlayList';

/* openTab()
 * This function handles showing / hiding tab content on the admin page based on
 * click events.
 *
 */
function openTab(event) {
	const tabName = event.target.id;
	const tabCName = tabName.replace('tab', 'tabC');
	const classTabShow = 'nav-link active';
	const classTabHide = 'nav-link';
	const classTabCShow = 'tab-pane fade show active';
	const classTabCHide = 'tab-pane fade';
	var i;
	var x = document.getElementsByClassName('nav-link');
	var y = document.getElementsByClassName('tab-pane');
	console.log(`[${tabName}][${tabCName}]`);
	for (i = 0; i < x.length; i++) {
		x[i].className = (x[i].id === tabName) ? classTabShow: classTabHide;
	}
	for (i = 0; i < y.length; i++) {
		y[i].className = (y[i].id === tabCName) ? classTabCShow: classTabCHide;
	}
}

/* Music
 * This component is a simple tab framework allowing the admin to tab between administrative
 * functions.
 *
 */
const Tree = () => (
	<div className="bs-docs-section">
		<div className="row">
			<div className="col-lg-12">
				<div className="bs-component">
					<ul class="nav nav-pills">
						<li class="nav-item">
							<a id="tabHome" class="nav-link active" data-toggle="tab" href="#home" onClick={openTab}>Light Selection</a>
						</li>
						<li class="nav-item">
							<a id="tabMusic" class="nav-link" data-toggle="tab" href="#profile" onClick={openTab}>Music Selection</a>
						</li>
						<li class="nav-item">
							<a id="tabPlayList" class="nav-link" data-toggle="tab" href="#profile" onClick={openTab}>Play List</a>
						</li>
						<li class="nav-item">
							<a id="tabMaint" class="nav-link" href="#" onClick={openTab}>Maint</a>
						</li>
					</ul>
					<div id="myTabContent" class="tab-content">
						<div id="tabCHome" class="tab-pane fade show active">
							<LightSelection />
						</div>
						<div id="tabCMusic" class="tab-pane fade">
							<MusicSelection />
						</div>
						<div id="tabCPlayList" class="tab-pane fade">
							<PlayList />
						</div>
						<div id="tabCMaint" class="tab-pane fade">
							<Maint />
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	

);

export default Tree;