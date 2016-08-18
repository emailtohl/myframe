package com.github.emailtohl.frame.dao.preparedstatementfactory;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.github.emailtohl.frame.entities.GoodsDto;
import com.github.emailtohl.frame.entities.SupplierDto;

public class PoAnalyzerTest {
	OrmAnalyzer ormAnalyzer;
	
	@Before
	public void setUp() throws Exception {
		ormAnalyzer = new PoAnalyzer();
	}

	@After
	public void tearDown() throws Exception {
		ormAnalyzer = null;
	}

	@Test
	public void testParse() {
		GoodsDto dto = new GoodsDto();
		dto.setAmount(12);
		dto.setCreateTime(new java.sql.Timestamp(new java.util.Date().getTime()));
		dto.setCurrentPage(1L);
		dto.setDescription("test");
		dto.setEndTimeLong(new Date().getTime());
		dto.setGoodsId(1000L);
		dto.setGoodsName("testGoods");
		dto.setMaxRowNum(10);
		dto.setPrice(new BigDecimal(12.00));
		dto.setStartTimeLong(new Date().getTime());
		dto.setSupplierId(100L);
		dto.setSupplierName("testSupplierName");
		dto.setTotalRowNum(100L);
		
		SqlAndArgs sa = ormAnalyzer.parse(dto);
		_assert(sa);
		
		SupplierDto sd = new SupplierDto();
		sa = ormAnalyzer.parse(sd);
		Assert.assertTrue("mark_deleted".equals(sa.getMarkDeletedColumnLabel()));
	}
	
	private void _assert(SqlAndArgs sa) {
		Map<String, PropertyBean> map = sa.getColumnPropertyMap();
		for (Entry<String, PropertyBean> entry : map.entrySet()) {
			System.out.println(entry.toString());
		}
		Assert.assertTrue("t_goods".equals(sa.getTableName()));
		Assert.assertTrue("id".equals(sa.getKeyColumnLabel()));
		Assert.assertTrue(new Long(1000).equals(map.get(sa.getKeyColumnLabel()).getPropertyValue()));
		Assert.assertSame("testGoods", map.get("goods_name").getPropertyValue());
		Assert.assertNull(sa.getMarkDeletedColumnLabel());
	}

}
