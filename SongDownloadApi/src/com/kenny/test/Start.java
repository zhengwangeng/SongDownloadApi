package com.kenny.test;

import java.io.File;
import java.util.*;

import com.kenny.common.utils.*;


/** 
 * 添加描述
 * @author Kenny.zheng
 * @date 2014-11-4 下午12:50:02
 */
public class Start {
	
	//下载完成是否删除原来的文件
	private final static boolean isExistOldFile = true;//如果匹配到新的无损音乐，是否删除旧的文件
	private final static String SONGPATH = "G:\\Music";//需要更新音乐的本地路径
	private final static String SONGPATH_BASE = "G:\\Music";//需要更新音乐的本地路径(父路径),删除时验证用
	
	private final static String SONGPATH_FLAC = "G:\\Music_flac";//保存无损音乐的路径
	
	/**
	 * 添加描述.</br>
	 * @param @param args
	 * @return void    返回类型
	 * @throws  
	 */
	public static void main(String[] args) {
		try {
			List<File> fileList = new ArrayList<File>();//需要匹配的文件对象
			fileList = FileUtil.startMathch(SONGPATH);
			
			for (File lsFile : fileList) {
				String fileName = lsFile.getName();
				int index = fileName.lastIndexOf("-");
				int point_index = fileName.lastIndexOf(".");
				if (index != -1) {
					System.out.println("现在开始匹配下载的歌曲是："+lsFile.getPath());
					//信乐团 - 青春万岁.mp3
					String singer = fileName.substring(0, index).trim();//得到演唱者名字
					String songName = fileName.substring(index + 1, point_index).trim();//得到歌曲名字
					//也可以直接查询
					
					String querySongId = SongUtil.querySongId(singer + " " + songName);//2127472
					if(querySongId == null){
						System.err.println("歌曲："+fileName+" id查询失败-找不到对应的歌曲.");
						continue;
					}
					LinkedHashMap<String, Object> resutl = SongUtil.querySongDownLoadInfo(querySongId);
					if(resutl == null){
						System.err.println("歌曲："+fileName+" 下载失败-找不到对应的无损音乐.");
						continue;
					}
//					System.out.println(JacksonMapper.toJsonString(resutl));
					boolean isDownload = SongUtil.downloadSong(resutl,SONGPATH_FLAC,singer,fileName);
					if(isDownload){
						System.out.println("歌曲："+fileName+" 下载完成.");
						if(isExistOldFile){
							lsFile.delete();
							System.out.println(lsFile.getParent());
							//判断父级目录下是否还有文件，如果没有，则把父级目录删除
							String parentPath = lsFile.getParent();
							if(!parentPath.equalsIgnoreCase(SONGPATH_BASE)){
								List<File> fileListParent = FileUtil.startMathch(parentPath);
								if(fileListParent.size() == 0){
									File fileParent = new File(parentPath);
									fileParent.delete();
								}
							}
						}
					}else{
						System.err.println("歌曲："+fileName+" 下载失败.");
					}
				}
			}
			System.err.println("当前路径下的音乐文件个数：" + fileList.size());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
