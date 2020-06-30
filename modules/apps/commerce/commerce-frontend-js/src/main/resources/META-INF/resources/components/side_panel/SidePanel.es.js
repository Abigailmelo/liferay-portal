/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

import ClayButton from '@clayui/button';
import ClayIcon from '@clayui/icon';
import ClayLoadingIndicator from '@clayui/loading-indicator';
import classNames from 'classnames';
import PropTypes from 'prop-types';
import ReactDOM from 'react-dom';
import React from 'react';

import {
	OPEN_SIDE_PANEL,
	CLOSE_SIDE_PANEL,
	IFRAME_LOADED,
	SIDE_PANEL_CLOSED
} from '../../utilities/eventsDefinitions.es';
import {
	getIframeHandlerModalId,
	isPageInIframe
} from '../../utilities/iframes.es';
import {debounce} from '../../utilities/index.es';
import {exposeSidePanel} from '../../utilities/sidePanels.es';
import Modal from '../modal/Modal.es';
import SideMenu from './SideMenu.es';

const SIDE_PANEL_TITLE_HEIGHT = 65;
export default class SidePanel extends React.Component {
	static defaultSize = 'md';

	constructor(props) {
		super(props);
		this.state = {
			active: null,
			closeButtonStyle: null,
			currentUrl: props.url || null,
			loading: true,
			menuCoverTopDistance: 0,
			moving: false,
			onAfterSubmit: props.onAfterSubmit || null,
			size: props.size || this.defaultSize,
			topDistance: 0,
			visible: !!props.visible,
			wrapper:
				document.querySelector(this.props.wrapperSelector) ||
				document.querySelector('body')
		};
		this.iframeHandlerModalId = getIframeHandlerModalId();
		this.handleIframeClickOnSubmit = this.handleIframeClickOnSubmit.bind(
			this
		);
		this.handleIframeSubmit = this.handleIframeSubmit.bind(this);
		this.handleContentLoaded = this.handleContentLoaded.bind(this);
		this.close = this.close.bind(this);
		this.open = this.open.bind(this);
		this.handlePanelOpenEvent = this.handlePanelOpenEvent.bind(this);
		this.handlePanelCloseEvent = this.handlePanelCloseEvent.bind(this);
		this.updateTop = this.updateTop.bind(this);
		this.debouncedUpdateTop = debounce(this.updateTop, 250);
		this.panel = React.createRef();
		this.iframeRef = React.createRef();
	}

	componentDidMount() {
		if (this.props.topAnchorSelector) {
			window.addEventListener('resize', this.debouncedUpdateTop);
			this.updateTop();
		}

		if (this.props.containerSelector) {
			const container = document.querySelector(
				this.props.containerSelector
			);
			if (container) {
				container.classList.add('with-side-panel');
			} else {
				throw new Error(
					`Container: "${this.props.containerSelector}" not found!`
				);
			}
		}

		if (Liferay) {
			Liferay.on(OPEN_SIDE_PANEL, this.handlePanelOpenEvent);
			Liferay.on(CLOSE_SIDE_PANEL, this.handlePanelCloseEvent);
		}

		exposeSidePanel(this.props.id, () => ({
			activeMenuItem: this.state.active,
			size: this.state.size,
			url: this.state.currentUrl,
			visible: this.state.visible
		}));
	}

	handlePanelOpenEvent(e) {
		if (e.id !== this.props.id) {
			return this.close();
		}

		this.open(e.url, e.slug);

		this.setState({
			onAfterSubmit: e.onSubmit || null,
			size: e.size || this.defaultSize
		});
	}

	handlePanelCloseEvent(e) {
		e.preventDefault();

		return this.close();
	}

	componentWillUnmount() {
		if (this.props.topAnchorSelector) {
			window.removeEventListener('resize', this.debouncedUpdateTop);
		}

		if (Liferay) {
			Liferay.detach(OPEN_SIDE_PANEL, this.handlePanelOpenEvent);
		}
	}

	updateTop() {
		const topAnchor = document.querySelector(this.props.topAnchorSelector);

		if (topAnchor) {
			const {height, top} = topAnchor.getBoundingClientRect();
			this.setState({
				topDistance: top + height + 'px'
			});
		}

		const pageHeader = document.querySelector('.page-header');

		if (pageHeader) {
			const {top} = pageHeader.getBoundingClientRect();

			this.setState({
				menuCoverTopDistance: top + 'px'
			});
		} else if (isPageInIframe()) {
			this.setState({
				menuCoverTopDistance: SIDE_PANEL_TITLE_HEIGHT + 'px'
			});
		}
	}

	load(url, refreshPageAfterSubmit) {
		this.setState(
			{
				currentUrl: url,
				loading: true,
				onAfterSubmit: refreshPageAfterSubmit
					? () => window.location.reload()
					: null
			},
			() => {
				if (
					this.iframeRef.current &&
					this.iframeRef.current.contentWindow
				) {
					this.iframeRef.current.contentWindow.location = this.state.currentUrl;
				}
			}
		);
	}

	setSize(size) {
		if (!size) {
			new Error('Size parameter is mandatory');
		}
		this.setState({size});
	}

	open(url = this.state.currentUrl, active = null) {
		this.setState({active, closeButtonStyle: null});
		switch (true) {
			case !this.state.visible:
				return this.toggle(true).then(() => {
					this.load(url);
				});
			case url !== this.state.currentUrl:
				return this.load(url);
			default:
				break;
		}
	}

	close() {
		this.toggle(false).then(() => {
			this.setState({
				active: null,
				closeButtonStyle: null,
				currentUrl: null,
				loading: true
			});
			Liferay.fire(SIDE_PANEL_CLOSED, {
				id: this.props.id
			});
		});
	}

	toggle(status = !this.state.visible) {
		return new Promise(resolve => {
			this.setState({moving: true, visible: status});

			if (!this.panel.current) return;

			this.panel.current.addEventListener(
				'transitionend',
				() => {
					this.setState({moving: false}, () => resolve(status));
				},
				{
					once: true
				}
			);
		});
	}

	handleIframeSubmit(e) {
		if (e.id !== this.props.id) {
			return;
		}

		Liferay.detach(IFRAME_LOADED, this.handleIframeSubmit);

		if (this.props.onAfterSubmit) {
			this.props.onAfterSubmit();
		}
	}

	handleIframeClickOnSubmit() {
		Liferay.on(IFRAME_LOADED, this.handleIframeSubmit);

		setTimeout(() => {
			Liferay.detach(IFRAME_LOADED, this.handleIframeSubmit);
		}, 3000);
	}

	handleContentLoaded() {
		Liferay.fire(IFRAME_LOADED, {
			id: this.props.id
		});

		this.setState({
			loading: false
		});

		try {
			const iframeDocument = this.iframeRef.current.contentDocument;
			const iframeWindow = this.iframeRef.current.contentWindow;

			if (iframeWindow.Liferay) {
				iframeWindow.Liferay.on('endNavigate', () =>
					this.handleIframeSubmit({id: this.props.id})
				);
			}

			const submitButton = iframeDocument.querySelector(
				'[type="submit"]'
			);

			if (submitButton) {
				submitButton.addEventListener(
					'click',
					this.handleIframeClickOnSubmit
				);
			}

			this.setState({
				closeButtonStyle: iframeDocument.querySelector(
					'.side-panel-iframe-menu-wrapper'
				)
					? 'menu'
					: 'simple'
			});
		} catch (error) {
			throw new Error(
				`Cannot access to iframe body. Url: "${this.state.currentUrl}"`
			);
		}
	}

	render() {
		const visibility = this.state.visible ? 'is-visible' : 'is-hidden';
		const loading =
			this.state.loading || (this.state.moving && this.state.visible)
				? 'is-loading'
				: '';

		return ReactDOM.createPortal(
			<>
				<Modal id={this.iframeHandlerModalId} />
				<div
					className={classNames(
						'side-panel-nav-cover border-bottom',
						visibility
					)}
					style={{top: this.state.menuCoverTopDistance}}
				>
					<div
						className={classNames(!isPageInIframe() && 'container')}
					>
						<ul className="nav nav-underline">
							<li className="nav-item">
								<button
									className="btn btn-unstyled nav-link"
									onClick={() => this.close()}
								>
									<ClayIcon symbol="angle-left" />
								</button>
							</li>
						</ul>
					</div>
				</div>
				<div
					className={classNames(
						'side-panel',
						`side-panel-${this.state.size}`,
						visibility,
						loading
					)}
					ref={this.panel}
					style={{top: this.state.topDistance}}
				>
					{this.props.items && this.props.items.length && (
						<SideMenu
							active={this.state.active}
							items={this.props.items}
							open={this.open}
						/>
					)}

					<ClayButton
						className={classNames(
							'side-panel-close',
							this.state.closeButtonStyle === 'simple' &&
								'side-panel-close-simple',
							this.state.closeButtonStyle === 'menu' &&
								'side-panel-close-menu'
						)}
						displayType="monospaced"
						onClick={() => this.close()}
					>
						<ClayIcon
							spritemap={this.props.spritemap}
							symbol="times"
						/>
					</ClayButton>

					<div className="tab-content">
						<div className="loader">
							<ClayLoadingIndicator />
						</div>
						<div
							className="active fade show tab-pane"
							role="tabpanel"
						>
							{!(this.state.moving && this.state.visible) && (
								<iframe
									frameBorder="0"
									onLoad={this.handleContentLoaded}
									ref={this.iframeRef}
									src={this.state.currentUrl}
								></iframe>
							)}
						</div>
					</div>
				</div>
			</>,
			this.state.wrapper
		);
	}
}

SidePanel.propTypes = {
	id: PropTypes.string,
	items: PropTypes.any,
	size: PropTypes.oneOf(['xs', 'sm', 'md', 'lg', 'xl', 'full']),
	spritemap: PropTypes.string,
	topAnchorSelector: PropTypes.any,
	wrapperSelector: PropTypes.string
};

SidePanel.defaultProps = {
	size: 'lg',
	topAnchorSelector: '.control-menu',
	wrapperSelector: '.side-panel-wrapper'
};
