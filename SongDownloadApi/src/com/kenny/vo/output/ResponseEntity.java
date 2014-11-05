package com.kenny.vo.output;

import java.io.IOException;

import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import com.kenny.common.utils.JacksonMapper;

/**
 * VO对象，用于接口访问json串.</br>
 * @author Kenny.zheng
 * @date 2014-11-4 下午13:03:14
 *
 */
public class ResponseEntity implements java.io.Serializable{

	/**
	 * @Fields serialVersionUID : TODO(添加描述)
	 */
	private static final long serialVersionUID = 74778671084697794L;

	/*状态码**/
	private int code;
	
	/*body信息**/
	private Object body;

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public Object getBody() {
		return body;
	}

	public void setBody(Object body) {
		this.body = body;
	}
	
	//验证环信token是否合法（状态码是401，并且且error值是unauthorized）
	public boolean isValid() {
		try {
			//状态码401
			if(code==401){
				Map<String, Object> detailMap = JacksonMapper.getInstance().readValue(body.toString(), Map.class);
				String errorMessage = (String)detailMap.get("error");
				//error值为unauthorized
				if("unauthorized".equalsIgnoreCase(errorMessage)){
					return false;
				}
			}
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}//body信息转化为map
		if(code == 200){
			return true;
		}
		return false;
	}
	
}
