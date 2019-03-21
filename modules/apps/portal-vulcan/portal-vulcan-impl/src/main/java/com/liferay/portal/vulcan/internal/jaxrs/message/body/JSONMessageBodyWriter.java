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

package com.liferay.portal.vulcan.internal.jaxrs.message.body;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.PropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

import com.liferay.portal.vulcan.fields.FieldsQueryParam;
import com.liferay.portal.vulcan.internal.jackson.databind.ser.VulcanPropertyFilter;

import java.io.IOException;
import java.io.OutputStream;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import java.util.Optional;
import java.util.Set;

import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.Providers;

/**
 * @author Alejandro Hernández
 */
@Produces(MediaType.APPLICATION_JSON)
@Provider
public class JSONMessageBodyWriter implements MessageBodyWriter<Object> {

	@Override
	public boolean isWriteable(
		Class<?> clazz, Type genericType, Annotation[] annotations,
		MediaType mediaType) {

		ObjectMapper objectMapper = _getObjectMapper(clazz);

		return objectMapper.canSerialize(clazz);
	}

	@Override
	public void writeTo(
			Object object, Class<?> clazz, Type genericType,
			Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, Object> multivaluedMap,
			OutputStream outputStream)
		throws IOException, WebApplicationException {

		ObjectMapper objectMapper = _getObjectMapper(clazz);

		objectMapper.writeValue(outputStream, object);

		outputStream.flush();
	}

	private ObjectMapper _addFilter(ObjectMapper objectMapper) {
		objectMapper.setFilterProvider(
			new SimpleFilterProvider() {
				{
					Set<String> fieldNames = _fieldsQueryParam.getFieldNames();

					PropertyFilter propertyFilter = null;

					if (fieldNames == null) {
						propertyFilter =
							SimpleBeanPropertyFilter.serializeAll();
					}
					else {
						propertyFilter = VulcanPropertyFilter.of(fieldNames);
					}

					addFilter("Liferay.Vulcan", propertyFilter);
				}
			});

		return objectMapper;
	}

	private ObjectMapper _getObjectMapper(Class<?> clazz) {
		return Optional.ofNullable(
			_providers.getContextResolver(
				ObjectMapper.class, MediaType.APPLICATION_JSON_TYPE)
		).map(
			contextResolver -> contextResolver.getContext(clazz)
		).map(
			ObjectMapper::copy
		).map(
			this::_addFilter
		).orElseThrow(
			() -> new InternalServerErrorException(
				"Unable to generate object mapper for class " + clazz)
		);
	}

	@Context
	private FieldsQueryParam _fieldsQueryParam;

	@Context
	private Providers _providers;

}