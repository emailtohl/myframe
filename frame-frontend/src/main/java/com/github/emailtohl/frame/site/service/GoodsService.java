package com.github.emailtohl.frame.site.service;

import java.util.List;

import javax.transaction.Transactional;

import com.github.emailtohl.frame.dao.Page;
import com.github.emailtohl.frame.site.dto.GoodsDto;

@Transactional
public interface GoodsService {
	List<GoodsDto> queryGoods(GoodsDto goodsDto);
	
	GoodsDto getGoodsById(Long id);

	Page<GoodsDto> queryPage(GoodsDto goodsDto);

	int updateGoods(GoodsDto goodsDto);

	int deleteGoods(GoodsDto goodsDto);

	long addGoods(GoodsDto goodsDto);
}
