package com.yun.zhe;



import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

import com.yun.zhe.entity.BeanInfo;
import com.yun.zhe.util.XmlHandler;
/**
 * 
 * @author 刘云哲
 * 中心转发器
 */
public class DisplatcherServilet extends HttpServlet {
	private static final long serialVersionUID = -5773993998850471835L;
	private String path=null;
	private Map<String,Object> controllerMap=null;
	@Override
	public void init(ServletConfig config) throws ServletException {
		/**
		 * 在初始化displatcherServlet的 
		 * 
		 * 1 从xml中获取到controller
		 * 2 加载所得controller到
		 */
		this.path=config.getInitParameter("controllerPath");
		controllerMap=new HashMap<>();
		/**
		 * 必须放到类路经下
		 */
		if(path.startsWith("classPath")) {
	    InputStream in=this.getClass().getClassLoader().getResourceAsStream("controller.xml");
	    SAXParserFactory factory=SAXParserFactory.newInstance();
	    SAXParser paser=null;
	    try {
	    	paser= factory.newSAXParser();
	    	/**
	    	 * 解析文件
	    	 */
	    	XmlHandler handler=new XmlHandler();
	    	paser.parse(in, handler);
	    	Map<String,BeanInfo> beanMap =handler.getBeanMap();
	    	beanMap.entrySet().stream()
	    	.forEach(item->{
	    		BeanInfo info=item.getValue();
	    		//加载class对象
	    		try {
					Class<?> clazz=Class.forName(info.getClassName());
					/**
					 * key是 请求path
					 */
					controllerMap.put(item.getKey(), clazz);
				} catch (ClassNotFoundException  e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    		;
	    		
	    	});
	    	
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
	}
	
	
	@Override
	public void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	 /**
	  *1  可以获取到请求路径 /MvcApp/test
	  *2  可以获取到要请求的方法  ?method=add
	  *3 根据路径获取到对应的controller 执行对应的方法
	  *4 根据方法的返回值 来判断 转发还是重定向 f： r:
	  */
		String url=req.getRequestURI(); // 请求的路径包含了 /mvcapp/app/test.do
		String path=req.getContextPath(); //MVCapp
		String method=req.getParameter("method");
		String newPath=url.replace(path, "");
		/**
		 * 获取controller		
		 */
		
		Class<?> clazz=(Class)controllerMap.get(newPath);
		if(clazz==null) {
			throw new RuntimeException("未加载到bean");
		}
		Object obj=null;
		try {
			obj = clazz.newInstance();
		} catch (InstantiationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Method method1=null;
		try {
			method1=clazz.getMethod(method, HttpServletRequest.class,HttpServletResponse.class);
			String result=(String)method1.invoke(obj,req,resp);
			if(result != null && !result.trim().isEmpty()) {//如果请求处理方法返回不为空
				int index = result.indexOf(":");//获取第一个冒号的位置
				if(index == -1) {//如果没有冒号，使用转发
					req.getRequestDispatcher(result).forward(req, resp);
				} else {//如果存在冒号
					String start = result.substring(0, index);//分割出前缀
					String path2 = result.substring(index + 1);//分割出路径
					if(start.equals("f")) {//前缀为f表示转发
						req.getRequestDispatcher(path2).forward(req, resp);
					} else if(start.equals("r")) {//前缀为r表示重定向
						resp.sendRedirect(req.getContextPath() + path2);
					}
				}
			}
			
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException  e) {
			e.printStackTrace();
		}
		
		
	
	}




	
	
}
