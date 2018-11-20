import React from 'react';
import { Switch, Route } from 'react-router-dom';

// import { WrappedLink } from '../util/controls';
import Admin from '../../container/Admin/Admin';
import Light from '../../container/Light/Light';
import Tree from '../../container/Tree/Tree';

import '../../resources/css/w3-theme-green.css';
import '../../resources/css/w3.css';
import './routes.css';
import logo from '../../resources/images/BytesOfPi2.png';

//  class="w3-container w3-theme-d4 w3-card"
export const Header = () => (
	<div className="page-header w3-theme-d4">
		<div className="row">
			<div className="col-lg-1 col-md-1 col-sm-1"><img class="resize" src={logo} /></div>
			<div className="col-lg-10 col-md-5 col-sm-6" >
				<h2>BytesOfPi Christmas Tree</h2>
			</div>
			{
				// <div className="col-lg-2 col-md-5 col-sm-6" ><WrappedLink to='/' style={{ textDecoration: 'none' }} >Home</WrappedLink></div>
				// <div className="col-lg-2 col-md-5 col-sm-6" ><WrappedLink to='/game' style={{ textDecoration: 'none' }}>Game</WrappedLink></div>
			}
		</div>
	</div>
);

export const Main = () => (
	<main>
		<Switch>
			<Route exact path='/' component={Tree}/>
			<Route path='/light' component={Light}/>
			<Route path='/admin' component={Admin}/>
		</Switch>
	</main>
);