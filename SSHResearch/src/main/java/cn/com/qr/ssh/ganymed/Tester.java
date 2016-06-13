package cn.com.qr.ssh.ganymed;

public class Tester {
	public static void main(String[] args) throws Exception {
		SSHOperator oper = new SSHOperator();
		oper.connect(args[0], args[1], args[2]);
	}
}
