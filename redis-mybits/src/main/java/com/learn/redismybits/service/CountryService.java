/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2016 abel533@gmail.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.learn.redismybits.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.PageHelper;
import com.learn.redismybits.common.MyMapper;
import com.learn.redismybits.dto.City;
import com.learn.redismybits.dto.Country;
import com.learn.redismybits.mapper.CountryMapper;

/**
 * 
 * @author Administrator
 *
 */
@Service
public class CountryService {

    @Autowired
    private CountryMapper countryMapper;
    @Resource
    private MyMapper<City> cityMapper2;
    
    @Transactional
    public Object transactionalTest() {
    	Country country = new Country();
    	country.setCountrycode("countrycodeTest");
    	countryMapper.insert(country);
    	
    	City city = new City();
    	city.setName("cityTest");
    	cityMapper2.insert(city);
    	String a = null;
		if(a.equals("ccc")){
			
		}
    	Country obj = countryMapper.selectCountryByIdTest(209);
    				//sqlSession.selectOne(CountryMapper.class.getName()+".selectCountryById", 295);
    	return obj;
    }
    
    public List<Country> getAll(Country country) {
        if (country.getPage() != null && country.getRows() != null) {
            PageHelper.startPage(country.getPage(), country.getRows());
        }
        if(null != country.getCountryname() && "".equals(country.getCountryname())){
        	country.setCountryname(null);
        }
        if(null != country.getCountrycode() && "".equals(country.getCountrycode())){
        	country.setCountrycode(null);
        }
        //return countryMapper.select(country);
        Map<String,String> countryMap = new HashMap<String,String>();
        countryMap.put("countryname", country.getCountryname());
        countryMap.put("countrycode", country.getCountrycode());
        return countryMapper.queryCountry(countryMap);
    }

    public Country getById(Integer id) {
        return countryMapper.selectByPrimaryKey(id);
    }

    public void deleteById(Integer id) {
        countryMapper.deleteByPrimaryKey(id);
    }

    public void save(Country country) {
        if (country.getId() != null) {
            countryMapper.updateByPrimaryKey(country);
        } else {
            countryMapper.insert(country);
        }
    }
    
    
}
