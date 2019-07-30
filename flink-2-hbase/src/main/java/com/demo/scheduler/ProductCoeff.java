package com.demo.scheduler;

import com.demo.client.HbaseClient;
import com.demo.client.MysqlClient;
import com.demo.client.RedisClient;
import com.demo.domain.ProductPortraitEntity;
import com.demo.util.Constants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 基于产品标签的产品相关度计算
 *      * 策略2 ： 基于产品标签 计算产品的余弦相似度
 *      *
 *      *     w = sqrt( pow((tag{i,a} - tag{j,a}),2)  + pow((tag{i,b} - tag{j,b}),2) )
 * @author XINZE
 */
public class ProductCoeff {

	private RedisClient redis = new RedisClient();
	private MysqlClient mysql = new MysqlClient();


	/**
	 * 计算一个产品和其他相关产品的评分,并将计算结果放入Hbase
	 * @param id 产品id
	 * @param others 其他产品的id
	 */
	public void getSingelProductCoeff(String id, List<String> others) throws Exception {
		ProductPortraitEntity product = sigleProduct(id);
		for (String proId : others) {
			if (id.equals(proId))
				continue;
			ProductPortraitEntity entity = sigleProduct(proId);
			Double score = getScore(product, entity);
			HbaseClient.putData("ps", id, "p", proId, score.toString());
		}
	}

	/**
	 * 获取一个产品的所有标签数据
	 * @param proId 产品id
	 * @return 产品标签entity
	 * @throws IOException
	 */
	private ProductPortraitEntity sigleProduct(String proId) {
		ProductPortraitEntity entity = new ProductPortraitEntity();
		try {
			String woman = HbaseClient.getData("prod", proId, "sex", Constants.SEX_WOMAN);
			String man = HbaseClient.getData("prod", proId, "sex", Constants.SEX_MAN);
			String age_10 = HbaseClient.getData("prod", proId, "age", Constants.AGE_10);
			String age_20 = HbaseClient.getData("prod", proId, "age", Constants.AGE_20);
			String age_30 = HbaseClient.getData("prod", proId, "age", Constants.AGE_30);
			String age_40 = HbaseClient.getData("prod", proId, "age", Constants.AGE_40);
			String age_50 = HbaseClient.getData("prod", proId, "age", Constants.AGE_50);
			String age_60 = HbaseClient.getData("prod", proId, "age", Constants.AGE_60);
			entity.setMan(Integer.valueOf(man));
			entity.setWoman(Integer.valueOf(woman));
			entity.setAge_10(Integer.valueOf(age_10));
			entity.setAge_20(Integer.valueOf(age_20));
			entity.setAge_30(Integer.valueOf(age_30));
			entity.setAge_40(Integer.valueOf(age_40));
			entity.setAge_50(Integer.valueOf(age_50));
			entity.setAge_60(Integer.valueOf(age_60));
		} catch (Exception e) {
			System.err.println("proId: " + proId);
			e.printStackTrace();
		}
		return entity;

	}

	/**
	 * 根据标签计算两个产品之间的相关度
	 * @param product 产品
	 * @param target 相关产品
	 * @return
	 */
	private Double getScore(ProductPortraitEntity product, ProductPortraitEntity target) {
		double sqrt = Math.sqrt(product.getTotal() + target.getTotal());
		if (sqrt == 0) {
			return 0.0;
		}
		int total = product.getMan() * target.getMan() + product.getWoman() * target.getWoman()
				+ product.getAge_10() * target.getAge_10() + product.getAge_20() * target.getAge_20()
				+ product.getAge_30() * target.getAge_30() + product.getAge_40() * target.getAge_40()
				+ product.getAge_50() * target.getAge_50() + product.getAge_60() * target.getAge_60();
		return Math.sqrt(total) / sqrt;
	}

	public static void main(String[] args) throws IOException {
		String data = HbaseClient.getData("prod", "2", "sex", "2");
		System.out.println(data);
	}


}
