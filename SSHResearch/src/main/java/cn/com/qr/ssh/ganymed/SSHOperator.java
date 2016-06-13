package cn.com.qr.ssh.ganymed;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.ethz.ssh2.Connection;
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
	 * 服务器 IP 或主机名
	 */
	private String host = "";
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
		// 记录主机 IP 或主机名
		this.host = host;
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
		session = conn.openSession();
		// 创建 Client
		operatClient = new SFTPv3Client(conn);
	}
	/**
	 * 关闭连接
	 */
	public void close() {
		session.close();
		operatClient.close();
		conn.close();
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
		session.execCommand("rm -rf " + targetPath);
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
		session.execCommand("mkdir -p -m " + posixPermissions + " " + dirPath);
		logger.info("创建文件夹 [" + dirPath + "]");
	}
	/**
	 * 将本地文件夹上传到远程服务器上
	 * @param localDir      本地文件夹路径
	 * @param remoteDir  远程文件夹路径
	 * @throws Exception
	 */
	public void uploadDir(String localDir, String remoteDir) throws Exception {
		// 构建 scp 命令
		StringBuffer command = new StringBuffer("");
		command.append("scp -r ").append(localDir).append(" ").append(host).append(":").append(remoteDir);
		logger.info("scp 命令 [" + command + "]");
		// 执行 scp 命令
		session.execCommand(command.toString());
		// 获取执行命令的反馈信息
		InputStream stdout = new StreamGobbler(session.getStdout());
		BufferedReader br = new BufferedReader(new InputStreamReader(stdout));
		String line = "";
		while((line = br.readLine()) != null) {
			logger.info(line);
		}
		br.close();
	}
}
