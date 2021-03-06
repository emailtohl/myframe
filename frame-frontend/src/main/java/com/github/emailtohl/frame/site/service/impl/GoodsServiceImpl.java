package com.github.emailtohl.frame.site.service.impl;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import javax.inject.Inject;

import com.github.emailtohl.frame.cdi.Component;
import com.github.emailtohl.frame.dao.Page;
import com.github.emailtohl.frame.site.dao.GoodsDao;
import com.github.emailtohl.frame.site.dto.GoodsDto;
import com.github.emailtohl.frame.site.filter.AuthenticationFilter;
import com.github.emailtohl.frame.site.service.GoodsService;
import com.github.emailtohl.frame.util.BeanUtil;
import com.github.emailtohl.frame.util.CommonUtil;

@Component
public class GoodsServiceImpl implements GoodsService {
	private static final Logger logger = Logger.getLogger(GoodsServiceImpl.class.getName());
	
	@Inject
	private GoodsDao goodsDao;
	
	public GoodsDao getGoodsDao() {
		return goodsDao;
	}

	public void setGoodsDao(GoodsDao goodsDao) {
		this.goodsDao = goodsDao;
	}

	@Override
	public GoodsDto getGoodsById(Long id) {
		GoodsDto dto = new GoodsDto();
		dto.setGoodsId(id);
		List<GoodsDto> ls = queryGoods(dto);
		if (ls != null && !ls.isEmpty()) {
			return ls.get(0);
		} else {
			return null;
		}
	}
	
	@Override
	public List<GoodsDto> queryGoods(GoodsDto goodsDto) {
		return goodsDao.queryGoods(goodsDto);
	}

	@Override
	public Page<GoodsDto> queryPage(GoodsDto goodsDto) {
		return goodsDao.queryPage(goodsDto);
	}

	@Override
	public synchronized int updateGoods(GoodsDto goodsDto) {
		setGoodsType(goodsDto);
		GoodsDto before = getGoodsById(goodsDto.getGoodsId());
		int row = goodsDao.updateGoods(goodsDto);
		if (row > 0) {
			updateRecord(before, goodsDto);
		}
		return row;
	}

	@Override
	public synchronized int deleteGoods(GoodsDto goodsDto) {
		int row = goodsDao.deleteGoods(goodsDto);
		if (row > 0) {
			deleteRecord(goodsDto);
		}
		return row;
	}

	@Override
	public long addGoods(GoodsDto goodsDto) {
		goodsDto.setCreateTime(new java.sql.Timestamp(new java.util.Date().getTime()));
		setGoodsType(goodsDto);
		long id = goodsDao.addGoods(goodsDto);
		if (id > 0) {
			addRecord(goodsDto);
		}
		return id;
	}

	private void addRecord(GoodsDto afterDto) {
		logger.info("user: " + AuthenticationFilter.CURRENT_USER.get() +  "  新增项：" + afterDto);
	}

	private synchronized void updateRecord(GoodsDto preDto, GoodsDto afterDto) {
		Map<String, Object> map = BeanUtil.getModifiedField(preDto, afterDto);
		Iterator<Map.Entry<String, Object>> it = map.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, Object> entry = it.next();
			if (entry.getValue() == null || "types".equals(entry.getKey())) {
				it.remove();
			}
		}
		logger.info("user: " + AuthenticationFilter.CURRENT_USER.get() +  "  修改项：" + map);
	}

	private void deleteRecord(GoodsDto afterDto) {
		logger.info("user: " + AuthenticationFilter.CURRENT_USER.get() +  "  删除id：" + afterDto.getGoodsId());
	}

	private void setGoodsType(GoodsDto goodsDto) {
		String[] types = goodsDto.getTypes();
		if (!CommonUtil.isEmpty(types)) {
			String s = CommonUtil.join(types, ",");
			goodsDto.setType(s);
		}
	}

}
