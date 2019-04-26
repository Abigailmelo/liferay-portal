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

package com.liferay.fragment.internal.renderer;

import com.liferay.asset.display.page.constants.AssetDisplayPageWebKeys;
import com.liferay.fragment.renderer.FragmentRenderer;
import com.liferay.fragment.renderer.FragmentRendererContext;
import com.liferay.info.display.contributor.InfoDisplayObjectProvider;
import com.liferay.info.renderer.InfoItemRenderer;
import com.liferay.info.renderer.InfoItemRendererTracker;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;

import java.io.IOException;
import java.io.PrintWriter;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jorge Ferrer
 */
@Component(service = FragmentRenderer.class)
public class LayoutDisplayObjectFragmentRenderer implements FragmentRenderer {

	@Override
	public String getLabel(Locale locale) {
		return "Display Page Content";
	}

	@Override
	public boolean isSelectable(HttpServletRequest httpServletRequest) {
		Layout layout = (Layout)httpServletRequest.getAttribute(WebKeys.LAYOUT);

		if (Objects.equals(
				layout.getType(), LayoutConstants.TYPE_ASSET_DISPLAY)) {

			return true;
		}

		return false;
	}

	@Override
	public void render(
		FragmentRendererContext fragmentRendererContext,
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		Object displayObject = _getDisplayObject(httpServletRequest);

		if (displayObject == null) {
			ThemeDisplay themeDisplay =
				(ThemeDisplay)httpServletRequest.getAttribute(
					WebKeys.THEME_DISPLAY);

			try {
				PrintWriter printWriter = httpServletResponse.getWriter();

				printWriter.write(
					LanguageUtil.get(
						themeDisplay.getLocale(),
						"the-rendered-content-will-be-shown-here"));
			}
			catch (IOException ioe) {
				if (_log.isDebugEnabled()) {
					_log.debug(ioe, ioe);
				}
			}
			finally {
				return;
			}
		}

		Class<?> displayObjectClass = displayObject.getClass();

		List<InfoItemRenderer> infoItemRenderers = _getInfoItemRenderer(
			displayObjectClass);

		if (infoItemRenderers == null) {
			return;
		}

		InfoItemRenderer infoItemRenderer = infoItemRenderers.get(0);

		infoItemRenderer.render(
			displayObject, httpServletRequest, httpServletResponse);
	}

	private Object _getDisplayObject(HttpServletRequest httpServletRequest) {
		InfoDisplayObjectProvider infoDisplayObjectProvider =
			(InfoDisplayObjectProvider)httpServletRequest.getAttribute(
				AssetDisplayPageWebKeys.INFO_DISPLAY_OBJECT_PROVIDER);

		if (infoDisplayObjectProvider == null) {
			return null;
		}

		return infoDisplayObjectProvider.getDisplayObject();
	}

	private List<InfoItemRenderer> _getInfoItemRenderer(Class<?> clazz) {
		Class<?>[] interfaces = clazz.getInterfaces();

		if (interfaces.length != 0) {
			for (Class<?> anInterface : interfaces) {
				List<InfoItemRenderer> infoItemRenderers =
					_infoItemRendererTracker.getInfoItemRenderers(
						anInterface.getName());

				if (!infoItemRenderers.isEmpty()) {
					return infoItemRenderers;
				}
			}
		}

		Class<?> superclass = clazz.getSuperclass();

		if (superclass != null) {
			return _getInfoItemRenderer(superclass);
		}

		return null;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LayoutDisplayObjectFragmentRenderer.class);

	@Reference
	private InfoItemRendererTracker _infoItemRendererTracker;

}