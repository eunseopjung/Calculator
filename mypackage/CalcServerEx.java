package mypackage;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CalcServerEx {
	public static String readFile() throws IOException{ // 파일 읽기
		BufferedReader reader = new BufferedReader(new FileReader("./server_info.txt"));
		String str = reader.readLine();
		reader.close();
		return str;
	}
	
	
    public static String calc(String exp) throws Exception{ // 사칙연산 수행 
    	StringTokenizer st = new StringTokenizer(exp, " ");
        if (st.countTokens() != 3)
            throw new ArgumentsException();
        String res = "";
        String opcode = st.nextToken();
        int op1 = Integer.parseInt(st.nextToken());
        int op2 = Integer.parseInt(st.nextToken());
        switch (opcode) {
            case "ADD":
                res = Integer.toString(op1 + op2);
                break;
            case "SUB":
                res = Integer.toString(op1 - op2);
                break;
            case "MUL":
                res = Integer.toString(op1 * op2);
                break;
            case "DIV":
            	if (op2 == 0) throw new DivideException();
                res = Integer.toString(op1 / op2);
                break;
            default:
                res = "Arithmetic error";
        }
        return res;
    }
    
    private static class Capitalizer implements Runnable {
    	private Socket socket;
    	
    	Capitalizer(Socket socket) {
    		this.socket = socket;
    	}
    	
    	@Override
    	public void run() {
    		System.out.println("연결되었습니다. " + socket);
            try {
            	BufferedReader in = new BufferedReader(
	                    new InputStreamReader(socket.getInputStream()));
            	BufferedWriter out = new BufferedWriter(
	                    new OutputStreamWriter(socket.getOutputStream()));
	            while (true) {
	                String inputMessage = in.readLine();
	                if (inputMessage.equalsIgnoreCase("bye")) {
	                    System.out.println(socket + " -> 이 클라이언트에서 연결을 종료하였음");
	                    break; // "bye"를 받으면 연결 종료
	                }
	                System.out.println(inputMessage + " 클라이언트: "+ socket ); // 받은 메시지를 화면에 출력
	                String res = calc(inputMessage); // 계산. 계산 결과는 res
	                out.write(res + "\n"); // 계산 결과 문자열 전송
	                out.flush();
	            }
            } catch (IOException e) {
	            System.out.println(e.getMessage());
            } catch (ArgumentsException e) {
	            System.out.println(e.getMessage());
	            try {
	            	BufferedWriter out = new BufferedWriter(
		                    new OutputStreamWriter(socket.getOutputStream()));
		            out.write(e.getMessage());
		            out.flush();
	            } catch (IOException er) {
		            System.out.println(er.getMessage());	   
	            }
	        } catch (DivideException e) {
	            System.out.println(e.getMessage());
	            try {
	            	BufferedWriter out = new BufferedWriter(
		                    new OutputStreamWriter(socket.getOutputStream()));
		            out.write(e.getMessage());
		            out.flush();
	            } catch (IOException er) {
		            System.out.println(er.getMessage());	   
	            }
	        } catch (Exception e) {
	            System.out.println(e.getMessage());
	        } finally {
	            try {
	                if (socket != null)
	                    socket.close(); // 통신용 소켓 닫기
	            } catch (IOException e) {
	                System.out.println("클라이언트와 채팅 중 오류가 발생했습니다.");
	            }
	        }
    	}
    }

    public static void main(String[] args) throws Exception{
    	String str = readFile();
    	StringTokenizer st = new StringTokenizer(str, " ");
        String serverIP = st.nextToken();
        int portN = Integer.parseInt(st.nextToken());
        
    	ServerSocket listener = new ServerSocket(portN);
    	System.out.println("The capitalization server is running... ");
    	ExecutorService pool = Executors.newFixedThreadPool(20);
    	while (true) {
    		Socket sock = listener.accept();
    		pool.execute(new Capitalizer(sock));
    	}
            
    }
}
    
