package com.kenny.common.utils;

import java.io.IOException;
import java.net.URLEncoder;

import java.util.*;

import org.apache.commons.lang.StringUtils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.kenny.vo.output.ResponseEntity;

/** 
 * 音乐工具类
 * @author Kenny.zheng
 * @date 2014-11-4 下午1:12:15
 */
public class SongUtil {
	/*
	百度music web版全接口
	http://tingapi.ting.baidu.com/v1/restserver/ting
	获取方式：GET
	参数：
	format:  json|xml
	callback: 
	from:  webapp_music
	method: 
	baidu.ting.billboard.billList  {type:1,size:10, offset:0} 
	    1、新歌榜，2、热歌榜，
	    11、摇滚榜，12、爵士，16、流行
	    21、欧美金曲榜，22、经典老歌榜，23、情歌对唱榜，24、影视金曲榜，25、网络歌曲榜
	baidu.ting.adv.showlist  {_:+(new Date)}
	baidu.ting.search.catalogSug  {query:e}
	baidu.ting.song.play  {songid:t}
	baidu.ting.song.playAAC  {songid:t}
	baidu.ting.song.lry  {songid:e}
	baidu.ting.song.getRecommandSongList  {song_id:t.id, num:5}
	baidu.ting.song.downWeb  {songid:e, bit:"24, 64, 128, 192, 256, 320, flac",_t:+(new Date)}
	baidu.ting.artist.getInfo  {tinguid:t}
	baidu.ting.artist.getSongList  {tinguid:t.author.id,limits:6,use_cluster:1,order:2}
	*/
	
	private static final String HIGH_SONG_FLAG = "flac";//无损歌曲标识
	
	//基础接口
	private static final String SONG_BAIDU_API_URL = "http://tingapi.ting.baidu.com/v1/restserver/ting" +
			"?from=webapp_music"
			;
	
	//无损音乐的下载地址(中转)
	//http://music.baidu.com/data/music/fmlink?songIds=124169979&rate=320&type=flac
	private static final String SONG_BAIDU_DOWNLOAD_TEMP_API_URL = "http://music.baidu.com/data/music/fmlink";
	
	
	
	/**
	 * 
    {
       "song":
       [
           {
               "songid": "51158747",
               "songname": "青春万岁",
               "encrypted_songid": "",
               "artistname": "信乐团"
           }
       ],
       "artist":
       [
       ],
       "album":
       [
           {
               "albumid": "51409248",
               "albumname": "青春万岁",
               "artistname": "信乐团",
               "artistpic": "http://b.hiphotos.baidu.com/ting/pic/item/dc54564e9258d109e33dcfa5d358ccbf6d814dce.jpg"
           }
       ],
       "order": "song,album",
       "error_code": 22000
    }
	 */
	@SuppressWarnings("unchecked")
	public static String querySongId(String songName) throws JsonParseException, JsonMappingException, IOException{
		String songid = null;
		String queryURL = SONG_BAIDU_API_URL
				.concat("&format=json")
				.concat("&callback")
				.concat("&method=baidu.ting.search.catalogSug")
				.concat("&query=" + URLEncoder.encode(songName,"UTF-8"))
				.concat("&_=1413017198449")
				;
		ResponseEntity entity = HttpUtil.get(queryURL);
		if(entity.isValid()){
			Map<String, Object> map = (Map<String, Object>) JacksonMapper.readValue(entity.getBody().toString(), Object.class);
			if(map != null && map.get("error_code")!=null
					&& "22000".equals(map.get("error_code").toString()) ){
				List<Object> songList = (List<Object>) map.get("song");
				if(songList.size() > 0){
					LinkedHashMap<String, Object> o = (LinkedHashMap<String, Object>) songList.get(0);
					System.out.println(songName + "的查询结果：" + o.toString());
					songid = (String) o.get("songid");
				}
			}
		}
		return songid;
	}
	
	/**
	 * 根据歌曲id获得歌曲列表信息.</br>
	 * @param @param songid
	 * @param @return
	 * @param @throws JsonParseException
	 * @param @throws JsonMappingException
	 * @param @throws IOException
	 * @return LinkedHashMap<String,Object>    返回类型
	 * @throws
	 */
	@SuppressWarnings("unchecked")
	public static LinkedHashMap<String, Object> querySongListInfo(String songid) throws JsonParseException, JsonMappingException, IOException{
		LinkedHashMap<String, Object> result = null;
		String queryURL = SONG_BAIDU_API_URL
				.concat("&songid=" + songid)
				.concat("&method=baidu.ting.song.downWeb")
				.concat("&format=json")
				.concat("&bit=24,64,128,192,256,320,flac")
				.concat("&_t=1413017198449")
				;
		ResponseEntity entity = HttpUtil.get(queryURL);
		if(entity.isValid()){
			Map<String, Object> map = (Map<String, Object>) JacksonMapper.readValue(entity.getBody().toString(), Object.class);
			if(map != null && map.get("error_code")!=null
					&& "22000".equals(map.get("error_code").toString()) ){
				
				List<Object> bitrateList = (List<Object>) map.get("bitrate");
				if(bitrateList !=null && bitrateList.size() > 0){
					boolean isSearch = false;
					for (Object object : bitrateList) {
//						System.out.println(object);
						LinkedHashMap<String, Object> o = (LinkedHashMap<String, Object>)object;
						if(HIGH_SONG_FLAG.equalsIgnoreCase(o.get("file_extension").toString())){
							//找到了高清无损歌曲
							isSearch = true;
							result = o;
							break;
						}
					}
					if(isSearch){
						System.out.println("已经找到songid为："+songid +" 的高清歌曲.");
					}else{
						System.err.println("----未找到songid为："+songid +" 的高清歌曲.----");
					}
				}
			}
		}
		return result;
	}
	
	/**
	 * 根据歌曲id获得歌曲的下载地址（中转的下载地址）.</br>
	 * @param @param songid
	 * @param @return
	 * @param @throws JsonParseException
	 * @param @throws JsonMappingException
	 * @param @throws IOException
	 * @return LinkedHashMap<String,Object>    返回类型
	 * @throws
	 */
	@SuppressWarnings("unchecked")
	public static LinkedHashMap<String, Object> querySongDownLoadInfo(String songid) throws JsonParseException, JsonMappingException, IOException{
		LinkedHashMap<String, Object> result = null;
		//?songIds=124169979&rate=320&type=flac
		String queryURL = SONG_BAIDU_DOWNLOAD_TEMP_API_URL
				.concat("?songIds=" + songid)
				.concat("&rate=320")
				.concat("&type=flac")
				;
		ResponseEntity entity = HttpUtil.get(queryURL);
		if(entity.isValid()){
			Map<String, Object> map = (Map<String, Object>) JacksonMapper.readValue(entity.getBody().toString(), Object.class);
			if(map != null && map.get("errorCode")!=null
					&& "22000".equals(map.get("errorCode").toString()) ){
				
				LinkedHashMap<String, Object> dataMap = (LinkedHashMap<String, Object>) map.get("data");
				if(dataMap !=null && dataMap.size() > 0){
					List<Object> songList = (List<Object>) dataMap.get("songList");
					if(songList !=null && songList.size() > 0){
						result = (LinkedHashMap<String, Object>) songList.get(0);
					}
				}
			}
		}
		return result;
	}
	
	/**
	 * 下载歌曲.</br>
	 * @param @param result
	 * @param @param songid
	 * @param @return
	 * @param @throws JsonParseException
	 * @param @throws JsonMappingException
	 * @param @throws IOException
	 * @return LinkedHashMap<String,Object>    返回类型
	 * @throws
	 */
	public static boolean downloadSong(LinkedHashMap<String, Object> songInfo,String baseFilePath,String singer,String saveFileName) throws Exception{
		String url = (String) songInfo.get("songLink");
		if(StringUtils.isEmpty(url)){
			return false;
		}
		System.out.println(saveFileName + " 的下载链接："+url);
		return HttpUtil.download(url, baseFilePath, singer,saveFileName);
	}
	
	public static void main(String[] args) throws Exception {
		String singer = "伍佰";
		String songName = "突然的自我";
		String fileName = singer+" - "+songName;
		String querySongId = SongUtil.querySongId(singer + " " + songName);//2127472
		if(querySongId == null){
			System.err.println("歌曲："+fileName+" 下载失败-找不到对应的无损音乐.");
			return;
		}
		LinkedHashMap<String, Object> resutl = SongUtil.querySongDownLoadInfo(querySongId);
		if(resutl == null){
			System.err.println("歌曲："+fileName+" 下载失败-找不到对应的无损音乐.");
			return;
		}
//		System.out.println(JacksonMapper.toJsonString(resutl));
		boolean isDownload = SongUtil.downloadSong(resutl,"G:\\Music_flac",singer,fileName);
		if(isDownload){
			System.out.println("歌曲："+fileName+" 下载完成.");
		}else{
			System.err.println("歌曲："+fileName+" 下载失败.");
		}
	}
	
}