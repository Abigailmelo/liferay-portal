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

package com.liferay.portal.vulcan.internal.jaxrs.writer.interceptor;

import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.vulcan.fields.NestedField;
import com.liferay.portal.vulcan.internal.fields.NestedFieldsContext;
import com.liferay.portal.vulcan.internal.fields.NestedFieldsContextThreadLocal;
import com.liferay.portal.vulcan.internal.fields.servlet.NestedFieldsHttpServletRequestWrapperTest;
import com.liferay.portal.vulcan.internal.jaxrs.context.provider.PaginationContextProvider;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import java.io.IOException;

import java.lang.reflect.Type;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.validation.constraints.NotNull;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.ext.WriterInterceptorContext;

import org.apache.cxf.Bus;
import org.apache.cxf.jaxrs.ext.ContextProvider;
import org.apache.cxf.jaxrs.provider.ProviderFactory;
import org.apache.cxf.message.Message;
import org.apache.cxf.message.MessageImpl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.mockito.Mockito;

import org.osgi.framework.BundleContext;

/**
 * @author Ivica Cardic
 */
public class NestedFieldsWriterInterceptorTest {

	@Before
	public void setUp() throws Exception {
		_nestedFieldsWriterInterceptor = Mockito.spy(
			new NestedFieldsWriterInterceptor(
				Mockito.mock(BundleContext.class)));

		Mockito.doReturn(
			new MockProviderFactory()
		).when(
			_nestedFieldsWriterInterceptor
		).getProviderFactory(
			Mockito.any(Message.class)
		);

		_productResourceImpl = new ProductResourceImpl();

		Mockito.doReturn(
			Collections.singletonList(_productResourceImpl)
		).when(
			_nestedFieldsWriterInterceptor
		).getResources();

		_writerInterceptorContext = Mockito.mock(
			WriterInterceptorContext.class);
	}

	@Test
	public void testGetNestedFieldsForMultipleItems() throws Exception {
		Product product1 = _toProduct(1L);
		Product product2 = _toProduct(2L);

		Mockito.when(
			_writerInterceptorContext.getEntity()
		).thenReturn(
			Arrays.asList(product1, product2)
		);

		Mockito.doReturn(
			new NestedFieldsHttpServletRequestWrapperTest.
				MockHttpServletRequest()
		).when(
			_nestedFieldsWriterInterceptor
		).getHttpServletRequest(
			Mockito.any(Message.class)
		);

		NestedFieldsContextThreadLocal.setNestedFieldsContext(
			new NestedFieldsContext(
				Arrays.asList("productOptions", "skus"), new MessageImpl(),
				new MultivaluedHashMap<>(), new MultivaluedHashMap<>()));

		_nestedFieldsWriterInterceptor.aroundWriteTo(_writerInterceptorContext);

		Sku[] skus = product1.getSkus();

		Assert.assertEquals(Arrays.toString(skus), 4, skus.length);

		ProductOption[] productOptionsDTOs = product1.getProductOptions();

		Assert.assertEquals(
			Arrays.toString(productOptionsDTOs), 3, productOptionsDTOs.length);
	}

	@Test
	public void testGetNestedFieldsForSingleItem() throws Exception {
		Product product = _toProduct(1L);

		Mockito.when(
			_writerInterceptorContext.getEntity()
		).thenReturn(
			product
		);

		Mockito.doReturn(
			new NestedFieldsHttpServletRequestWrapperTest.
				MockHttpServletRequest()
		).when(
			_nestedFieldsWriterInterceptor
		).getHttpServletRequest(
			Mockito.any(Message.class)
		);

		NestedFieldsContextThreadLocal.setNestedFieldsContext(
			new NestedFieldsContext(
				Arrays.asList("productOptions", "skus"), new MessageImpl(),
				_getPathParameters(), new MultivaluedHashMap<>()));

		_nestedFieldsWriterInterceptor.aroundWriteTo(_writerInterceptorContext);

		Sku[] skus = product.getSkus();

		Assert.assertEquals(Arrays.toString(skus), 4, skus.length);

		ProductOption[] productOptions = product.getProductOptions();

		Assert.assertEquals(
			Arrays.toString(productOptions), 3, productOptions.length);
	}

	@Test
	public void testGetNestedFieldsWithNonexistendFieldName() throws Exception {
		Product product = _toProduct(1L);

		Mockito.when(
			_writerInterceptorContext.getEntity()
		).thenReturn(
			product
		);

		NestedFieldsContextThreadLocal.setNestedFieldsContext(
			new NestedFieldsContext(
				Collections.emptyList(), new MessageImpl(),
				_getPathParameters(), new MultivaluedHashMap<>()));

		_nestedFieldsWriterInterceptor.aroundWriteTo(_writerInterceptorContext);

		Sku[] skus = product.getSkus();

		Assert.assertNull(skus);

		NestedFieldsContextThreadLocal.setNestedFieldsContext(
			new NestedFieldsContext(
				Collections.singletonList("nonexistent"), new MessageImpl(),
				_getPathParameters(), new MultivaluedHashMap<>()));

		_nestedFieldsWriterInterceptor.aroundWriteTo(_writerInterceptorContext);

		skus = product.getSkus();

		Assert.assertNull(skus);
	}

	@Test
	public void testGetNestedFieldsWithPagination() throws Exception {
		Product product = _toProduct(1L);

		Mockito.when(
			_writerInterceptorContext.getEntity()
		).thenReturn(
			product
		);

		Mockito.doReturn(
			new NestedFieldsHttpServletRequestWrapperTest.
				MockHttpServletRequest(
					"skus", "page", String.valueOf(1), "pageSize",
					String.valueOf(2))
		).when(
			_nestedFieldsWriterInterceptor
		).getHttpServletRequest(
			Mockito.any(Message.class)
		);

		NestedFieldsContextThreadLocal.setNestedFieldsContext(
			new NestedFieldsContext(
				Collections.singletonList("skus"), new MessageImpl(),
				_getPathParameters(), new MultivaluedHashMap<>()));

		_nestedFieldsWriterInterceptor.aroundWriteTo(_writerInterceptorContext);

		Sku[] skus = product.getSkus();

		Assert.assertEquals(Arrays.toString(skus), 2, skus.length);
	}

	@Test
	public void testGetNestedFieldsWithQueryParameter() throws IOException {
		Product product = _toProduct(1L);

		Mockito.when(
			_writerInterceptorContext.getEntity()
		).thenReturn(
			product
		);

		Mockito.doReturn(
			new NestedFieldsHttpServletRequestWrapperTest.
				MockHttpServletRequest("productOptions")
		).when(
			_nestedFieldsWriterInterceptor
		).getHttpServletRequest(
			Mockito.any(Message.class)
		);

		MultivaluedHashMap<String, String> queryParameters =
			new MultivaluedHashMap<String, String>() {
				{
					putSingle(
						"productOptions.createDate",
						"2019-02-19T08:03:11.763Z");
					putSingle("productOptions.name", "test2");
				}
			};

		NestedFieldsContextThreadLocal.setNestedFieldsContext(
			new NestedFieldsContext(
				Collections.singletonList("productOptions"), new MessageImpl(),
				_getPathParameters(), queryParameters));

		_nestedFieldsWriterInterceptor.aroundWriteTo(_writerInterceptorContext);

		ProductOption[] productOptions = product.getProductOptions();

		Assert.assertEquals(
			Arrays.toString(productOptions), 1, productOptions.length);

		ProductOption productOption = productOptions[0];

		Assert.assertEquals("test2", productOption.getName());
	}

	@Test
	public void testInjectResourceContexts() throws Exception {
		Product product = _toProduct(1L);

		Mockito.when(
			_writerInterceptorContext.getEntity()
		).thenReturn(
			product
		);

		Mockito.doReturn(
			new NestedFieldsHttpServletRequestWrapperTest.
				MockHttpServletRequest("skus")
		).when(
			_nestedFieldsWriterInterceptor
		).getHttpServletRequest(
			Mockito.any(Message.class)
		);

		NestedFieldsContextThreadLocal.setNestedFieldsContext(
			new NestedFieldsContext(
				Arrays.asList("productOptions", "skus"), new MessageImpl(),
				_getPathParameters(), new MultivaluedHashMap<>()));

		Assert.assertNull(_productResourceImpl.themeDisplay);

		_nestedFieldsWriterInterceptor.aroundWriteTo(_writerInterceptorContext);

		Assert.assertNotNull(_productResourceImpl.themeDisplay);
	}

	private static Product _toProduct(long id) {
		Product product = new Product();

		product.setId(id);

		return product;
	}

	private static Sku _toSku(long id) {
		Sku sku = new Sku();

		sku.setId(id);

		return sku;
	}

	private MultivaluedHashMap<String, String> _getPathParameters() {
		return new MultivaluedHashMap<String, String>() {
			{
				putSingle("id", "1");
			}
		};
	}

	private NestedFieldsWriterInterceptor _nestedFieldsWriterInterceptor;
	private ProductResourceImpl _productResourceImpl;
	private WriterInterceptorContext _writerInterceptorContext;

	private static class BaseProductResourceImpl implements ProductResource {

		@GET
		@Path("/{id}/productOption")
		@Produces("application/*")
		public List<ProductOption> getProductOptions(
			@NotNull @PathParam("id") Long id,
			@QueryParam("name") String name) {

			return Collections.emptyList();
		}

		@GET
		@Path("/products")
		@Produces("application/*")
		public List<Product> getProducts() {
			return Collections.emptyList();
		}

		@GET
		@Path("/{id}/sku")
		@Produces("application/*")
		public Page<Sku> getSkus(
			@NotNull @PathParam("id") Long id,
			@Context @NotNull Pagination pagination) {

			return Page.of(Collections.emptyList());
		}

	}

	@SuppressWarnings("unchecked")
	private static class MockProviderFactory extends ProviderFactory {

		@Override
		public <T> ContextProvider<T> createContextProvider(
			Type contextType, Message message) {

			if (Objects.equals(
					contextType.getTypeName(), Pagination.class.getName())) {

				return (ContextProvider<T>)new PaginationContextProvider();
			}

			return (ContextProvider<T>)new ThemeDisplayContextProvider();
		}

		@Override
		public Configuration getConfiguration(Message message) {
			return null;
		}

		@Override
		protected void setProviders(
			boolean custom, boolean busGlobal, Object... providers) {
		}

		private MockProviderFactory() {
			super(Mockito.mock(Bus.class));
		}

	}

	private static class Product {

		public Long getId() {
			return id;
		}

		public ProductOption[] getProductOptions() {
			return productOptions;
		}

		public Sku[] getSkus() {
			return skus;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public void setProductOptions(ProductOption[] productOptions) {
			this.productOptions = productOptions;
		}

		public void setSkus(Sku[] skus) {
			this.skus = skus;
		}

		protected Long id;
		protected ProductOption[] productOptions;
		protected Sku[] skus;

	}

	private static class ProductOption {

		public Long getId() {
			return _id;
		}

		public String getName() {
			return _name;
		}

		public void setId(Long id) {
			_id = id;
		}

		public void setName(String name) {
			_name = name;
		}

		private Long _id;
		private String _name;

	}

	private static class ProductResourceImpl extends BaseProductResourceImpl {

		@NestedField("productOptions")
		@Override
		public List<ProductOption> getProductOptions(Long id, String name) {
			if (id != 1) {
				return Collections.emptyList();
			}

			List<ProductOption> productOptions = Arrays.asList(
				_toProductOption(1L, "test1"), _toProductOption(2L, "test2"),
				_toProductOption(3L, "test3"));

			if (name != null) {
				Stream<ProductOption> productOptionDTOStream =
					productOptions.stream();

				productOptions = productOptionDTOStream.filter(
					productOptionDTO -> Objects.equals(
						productOptionDTO.getName(), name)
				).collect(
					Collectors.toList()
				);
			}

			return productOptions;
		}

		@GET
		@Path("/products")
		@Produces("application/*")
		public List<Product> getProducts() {
			return Arrays.asList(_toProduct(1), _toProduct(2));
		}

		@NestedField("skus")
		@Override
		public Page<Sku> getSkus(Long id, Pagination pagination) {
			if (!Objects.equals(id, 1L)) {
				return Page.of(Collections.emptyList());
			}

			List<Sku> skus = Arrays.asList(
				_toSku(1L), _toSku(2L), _toSku(3L), _toSku(4L));

			skus = skus.subList(
				pagination.getStartPosition(),
				Math.min(pagination.getEndPosition(), skus.size()));

			return Page.of(skus);
		}

		@Context
		public ThemeDisplay themeDisplay;

		private ProductOption _toProductOption(long id, String name) {
			ProductOption productOption = new ProductOption();

			productOption.setId(id);
			productOption.setName(name);

			return productOption;
		}

	}

	private static class Sku {

		public Long getId() {
			return _id;
		}

		public void setId(Long id) {
			_id = id;
		}

		private Long _id;

	}

	private static class ThemeDisplayContextProvider
		implements ContextProvider<ThemeDisplay> {

		@Override
		public ThemeDisplay createContext(Message message) {
			return new ThemeDisplay();
		}

	}

	private interface ProductResource {

		public List<ProductOption> getProductOptions(Long id, String name);

		public List<Product> getProducts();

		public Page<Sku> getSkus(Long id, Pagination pagination);

	}

}