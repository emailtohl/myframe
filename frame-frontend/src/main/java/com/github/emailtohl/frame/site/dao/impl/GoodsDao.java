package com.github.emailtohl.frame.site.dao.impl;

import java.sql.Date;
import java.util.List;

import javax.sql.DataSource;

import com.github.emailtohl.frame.dao.BaseDao;
import com.github.emailtohl.frame.dao.Pager;
import com.github.emailtohl.frame.dao.myjdbctemplate.BeanAnnotationRowMapper;
import com.github.emailtohl.frame.dao.myjdbctemplate.RowMapper;
import com.github.emailtohl.frame.dao.preparedstatementfactory.SqlAndArgs;
import com.github.emailtohl.frame.site.dao.IGoodsDao;
import com.github.emailtohl.frame.site.dto.GoodsDto;
import com.github.emailtohl.frame.util.CommonUtils;

public final class GoodsDao extends BaseDao implements IGoodsDao {
	private static GoodsDao goodsDao;
	private static final String SELECT;

	static {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT ").append("	t_goods.*, t_supplier.supplier_name ").append("FROM ").append("	t_goods ")
				.append("INNER JOIN t_supplier ON t_goods.supplier_id = t_supplier.id ");
		SELECT = sql.toString();
	}

	private GoodsDao(DataSource ds) {
		super(ds);
	}

	public synchronized static GoodsDao getGoodsDaoInstance() {
		if (goodsDao == null) {
			String configFilePath = Thread.currentThread().getContextClassLoader()
					.getResource("database.properties").getPath().substring(1);
			DataSource ds = BaseDao.getDataSourceByPropertyFile(configFilePath);
			goodsDao = new GoodsDao(ds);
		}
		return goodsDao;
	}

	@Override
	public List<GoodsDto> queryGoods(GoodsDto goodsDto) {
		SqlAndArgs sqlAndArgs = sqlBuilder.getSelectCondition(goodsDto);
		String sql = SELECT + sqlAndArgs.getPreparedSQL() + " ORDER BY id DESC";
		RowMapper<GoodsDto> rowMapper = new BeanAnnotationRowMapper<GoodsDto>(GoodsDto.class);
		return simpleJdbcTemplate.query(sql.toString(), sqlAndArgs.getParamValues(), rowMapper);
	}

	@Override
	public long addGoods(GoodsDto goodsDto) {
		return insert(goodsDto);
	}

	@Override
	public int updateGoods(GoodsDto goodsDto) {
		GoodsDto condition = new GoodsDto();
		condition.setGoodsId(goodsDto.getGoodsId());
		return update(goodsDto, condition);
	}

	@Override
	public int deleteGoods(GoodsDto condition) {
		return delete(condition);
	}

//	private SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
	
	@Override
	public Pager<GoodsDto> queryPage(GoodsDto goodsDto) {
		// 第一步，获取条件语句（WHERE 1=1 ...）以及对应的参数
		SqlAndArgs sa = sqlBuilder.getSelectCondition(goodsDto);
		// 第二步，获取时间范围的语句和参数
		Date startTime = null, endTime = null;
		String startDateStr = goodsDto.getStartDate(), endDateStr = goodsDto.getEndDate();
		if (startDateStr != null && startDateStr.length() > 0) {
			startTime = Date.valueOf(startDateStr);
		}
		if (endDateStr != null && endDateStr.length() > 0) {
			endTime = Date.valueOf(endDateStr);
		}
		SqlAndArgs timeScopeSa = sqlBuilder.getSelectTimeRangeCondition("t_goods.create_time", startTime, endTime);
		// 第三步，合并sql
		String sql = SELECT + sa.getPreparedSQL() + timeScopeSa.getPreparedSQL() + " ORDER BY t_goods.id DESC";
		// 第四步，合并对应的参数
		Object[] params = CommonUtils.mergeArray(sa.getParamValues(), timeScopeSa.getParamValues());
		// 第五步，新建属性列映射对象
		RowMapper<GoodsDto> rowMapper = new BeanAnnotationRowMapper<GoodsDto>(GoodsDto.class);
		// 第六步，执行
		return pageForMySqlOrPostgresql(sql.toString(), params, rowMapper, goodsDto.getPageNum(), 5);
	}

}
