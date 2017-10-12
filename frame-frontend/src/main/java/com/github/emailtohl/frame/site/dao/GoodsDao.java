package com.github.emailtohl.frame.site.dao;

import java.util.List;

import com.github.emailtohl.frame.dao.Page;
import com.github.emailtohl.frame.site.dto.GoodsDto;

public interface GoodsDao {
	List<GoodsDto> queryGoods(GoodsDto goodsDto);

	Page<GoodsDto> queryPage(GoodsDto goodsDto);

	long addGoods(GoodsDto goodsDto);

	int updateGoods(GoodsDto goodsDto);

	int deleteGoods(GoodsDto goodsDto);
}
