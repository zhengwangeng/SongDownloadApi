package com.kenny.common.utils;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 添加描述
 * 
 * @author Kenny.zheng
 * @date 2014-11-4 下午12:18:59
 */
public class FileUtil {

	public static List<File> startMathch(String musicPath) {
		List<File> fileList = new ArrayList<File>();//需要匹配的文件对象
		File file = new File(musicPath);
		File[] files = file.listFiles();
		readMusicDirFile(files,fileList);
		return fileList;
	}

	/**
	 * 读取文件.</br>
	 * @param @param files
	 * @param @param isIncludeChildDir
	 * @return void    返回类型
	 * @throws
	 */
	public static List<File> readMusicDirFile(File[] files,List<File> fileList) {
		for (File f : files){
			if (f.isHidden()) {
				System.out.println("隐藏文件："+f.getName());
				f.delete();//删除隐藏的文件，db，略缩图等
			} else {
				if (f.isDirectory()) {
					readDirFile(f,fileList);
				}
				if (f.isFile()){
					//FIXME 这里可以判断是否是音乐文件
					fileList.add(f);
				}
				
			}
		}
		return fileList;
	}

	/**
	 * 
	 * 读取目录下的文件.</br>
	 * @param @param f
	 * @param @param isIncludeChildDir
	 * @return void    返回类型
	 * @throws
	 */
	public static void readDirFile(File f,List<File> fileList) {
		File[] dirFiles = f.listFiles();
		readMusicDirFile(dirFiles,fileList);
	}
}