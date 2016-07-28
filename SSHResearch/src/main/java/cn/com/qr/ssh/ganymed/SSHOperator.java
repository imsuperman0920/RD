package cn.com.qr.ssh.ganymed;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.SCPClient;
import ch.ethz.ssh2.SFTPv3Client;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;

/**
 * SSH 操作类
 * @author i.m.superman
 */
public class SSHOperator {
	/**
	 * 日志对象
	 */
	private Logger logger = LoggerFactory.getLogger(SSHOperator.class);
	/**
	 * SSH 连接对象
	 */
	private Connection conn = null;
	/**
	 * 会话
	 */
	private Session session = null;
	/**
	 * 执行对象
	 */
	private SFTPv3Client operatClient = null;
	/**
	 * scp 客户端对象
	 */
	private SCPClient scpClient = null;
	/**
	 * 使用默认接口（22）连接远程服务器
	 * @param host                 主机名/IP
	 * @param username        用户名
	 * @param password         连接密码
	 * @throws Exception
	 */
	public void connect(String host, String username, String password) throws Exception {
		connect(host, username, password, 22);
	}
	/**
	 * 连接远程服务器
	 * @param host                 主机名/IP
	 * @param username        用户名
	 * @param password         连接密码
	 * @param port                 端口
	 * @throws Exception
	 */
	public void connect(String host, String username, String password, int port) throws Exception {
		// 初始化 Connection
		conn = new Connection(host, port);
		// 连接目标服务器
		conn.connect();
		// 设置验证信息
		boolean isAuthenticated = conn.authenticateWithPassword(username, password);
		if(!isAuthenticated) {
			throw new Exception("Authentication failed!!");
		} else {
			System.out.println("连接成功~");
		}
		/*
		 *  创建 Session
		 *  执行多条命令时，使用 && 分隔
		 *  eg：mkdir -p /APP/test && cd /APP/test
		 */
//		session = conn.openSession();
		// 创建 Client
		operatClient = new SFTPv3Client(conn);
		// 创建 scp 客户端对象
		scpClient = conn.createSCPClient();
	}
	/**
	 * 关闭连接
	 */
	public void close() {
		operatClient.close();
		conn.close();
	}
	/**
	 * 远程执行命令
	 * @param cmd               命令
	 * @throws Exception
	 */
	public void executeCommand(String cmd) throws Exception {
		session = conn.openSession();
		session.execCommand(cmd);
		// 输出返回信息
		BufferedReader br = new BufferedReader(new InputStreamReader(new StreamGobbler(session.getStdout())));
		while(true) {
			String line = br.readLine();
			if(line == null) {break;}
			logger.debug(line);
		}
		br.close();
		session.close();
	}
	/**
	 * 删除文件夹
	 * @param dirPath   文件夹路径
	 */
	public void rmdir(String dirPath) throws Exception {
		// 只能单级删除，不能删除其子文件夹和文件
		operatClient.rmdir(dirPath);
		logger.info("删除文件夹 [" + dirPath + "]");
	}
	/**
	 * 删除文件或文件夹
	 * @param targetPath   文件或文件夹路径
	 */
	public void rm(String targetPath) throws Exception {
		executeCommand("rm -rf " + targetPath);
		logger.info("删除文件夹 [" + targetPath + "] 下的全部文件和文件夹");
	}
	/**
	 * 创建文件夹
	 * @param dirPath                 文件夹路径
	 * @param posixPermissions  文件夹权限
	 */
	public void 	mkdir(String dirPath, int posixPermissions) throws Exception {
		// 只能单级创建，不能直接创建其子文件夹
		operatClient.mkdir(dirPath, posixPermissions);
		logger.info("创建文件夹 [" + dirPath + "]");
	}
	/**
	 * 创建文件夹（可以多级创建）
	 * @param dirPath                 文件夹路径
	 * @param posixPermissions  文件夹权限
	 */
	public void mkMultiDir(String dirPath, int posixPermissions) throws Exception {
		executeCommand("mkdir -p -m " + posixPermissions + " " + dirPath);
		logger.info("创建文件夹 [" + dirPath + "]");
	}
	/**
	 * 将本地文件夹上传到远程服务器上
	 * @param localObj      本地文件或文件夹路径
	 * @param remoteDir   远程文件夹路径
	 * @throws Exception
	 */
	public void uploadDir(String localObj, String remoteDir) throws Exception {
		// 判断上传的是否为文件夹
		File uploadObj = new File(localObj);
		// 判断上传文件是否存在
		if(!uploadObj.exists()) {
			logger.error("上传文件 [" + uploadObj.getParent() + "] 不存在!");
			throw new Exception("上传文件不存在");
		}
		// 判断上传文件是否为文件
		if(uploadObj.isFile()) {
			// 删除远程服务器上的文件
			executeCommand("rm -rf " + localObj);
			// 上传文件至远程服务器
			scpClient.put(localObj, remoteDir);
			// 上传完成，退出方法
			return;
		} else {
			/*
			 *  上传文件对应表
			 *  Map<FilePath, FileParentDirPath>
			 */
			Map<String, String> uploadFileMap = new HashMap<>();
			Set<String> dirSet = new HashSet<>();
			getAllFilePath(uploadObj, uploadFileMap);
			dirSet.addAll(uploadFileMap.values());
			// 遍历列表
			for(String dirPath : dirSet) {
				executeCommand("mkdir -p " + dirPath);
			}
			for(String uploadFilePath : uploadFileMap.keySet()) {
				// 上传文件至远程服务器
				scpClient.put(uploadFilePath, uploadFileMap.get(uploadFilePath));
				// 为 sh 脚本文件赋予执行权限
				if(uploadFilePath.endsWith(".sh")) {
					executeCommand("chmod +x " + uploadFilePath);
				}
				logger.info("Upload File [" + uploadFilePath + "] Completed ~");
			}
		}
	}
	/**
	 * 递归遍历文件夹，获取该文件夹下所有的文件路径
	 * @param uploadDirObj    目标文件夹
	 * @param uploadPathList  文件路径列表
	 * 
	 */
	private void getAllFilePath(File uploadDirObj, Map<String, String> uploadFileMap) {
		// 遍历文件夹
		for(File file : uploadDirObj.listFiles()) {
			if(file.isFile()) {
				uploadFileMap.put(file.getPath(), file.getParent());
			} else {
				getAllFilePath(file, uploadFileMap);
			}
		}
	}
}
