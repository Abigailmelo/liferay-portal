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

package com.liferay.portal.template.react.renderer.internal;

import com.liferay.frontend.js.loader.modules.extender.npm.NPMResolvedPackageNameUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.template.react.renderer.ComponentDescriptor;
import com.liferay.portal.template.react.renderer.ReactRenderer;

import java.io.IOException;
import java.io.Writer;

import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Chema Balsas
 */
@Component(service = ReactRenderer.class)
public class ReactRendererImpl implements ReactRenderer {

	@Override
	public void renderReact(
			HttpServletRequest httpServletRequest, Writer writer,
			ComponentDescriptor componentDescriptor, Map<String, Object> data)
		throws IOException {

		ReactRendererUtil.renderReact(
			httpServletRequest, writer, componentDescriptor, data, _portal,
			NPMResolvedPackageNameUtil.get(_servletContext));
	}

	@Reference
	private Portal _portal;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.portal.template.react.renderer.impl)",
		unbind = "-"
	)
	private ServletContext _servletContext;

}