package com.kenny.common.utils;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 饿汉式单例模式构建ObjectMapper.</br>
 * @author Kenny.zheng
 * @date 2014-3-8 下午5:04:25
 *
 */
public class JacksonMapper {

	/**
	 * 饿汉式单例模式构建ObjectMapper
	 */
	private static final ObjectMapper mapper = new ObjectMapper();

	/**
	 * 私有化构造函数
	 */
	private JacksonMapper() {	}

	/**
	 * 获得实例
	 * 
	 * @return
	 */
	public static ObjectMapper getInstance() {
		return mapper;
	}
	
	/**
	 * 转换为json串.</br>
	 * @param @param obj 需要转换的对象
	 * @param @return
	 * @param @throws JsonProcessingException
	 * @return String    返回类型
	 * @throws
	 */
	public static String toJsonString(Object obj) throws JsonProcessingException {
		return mapper.writeValueAsString(obj);
	}
	
	/**
	 * 根据json字符串转换为对象.</br>
	 * @param @param content
	 * @param @param clazz
	 * @param @return
	 * @param @throws JsonParseException
	 * @param @throws JsonMappingException
	 * @param @throws IOException
	 * @return Object    返回类型
	 * @throws
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Object readValue(String content,Class clazz) throws JsonParseException, JsonMappingException, IOException{
		Object o = null;
		if(content == null || "".equals(content)){
			return null;
		}
		o =  mapper.readValue(content, clazz);
		return o;
	 }
	
	private static JavaType getCollectionType(Class<?> collectionClass, Class<?>... elementClasses) {   
         return mapper.getTypeFactory().constructParametricType(collectionClass, elementClasses);   
    }
	 
	@SuppressWarnings("rawtypes")
	public static Object readValues(String content,Class CollectionType,Class clazz) throws JsonParseException, JsonMappingException, IOException{
		Object o = null;
		if(content == null || "".equals(content)){
			return null;
		}
		o = mapper.readValue(content, getCollectionType(CollectionType, clazz));
		return o;
	 }
}
