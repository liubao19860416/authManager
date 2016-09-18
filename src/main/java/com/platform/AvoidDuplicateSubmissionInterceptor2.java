package com.platform;
import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * <p>
 * 防止重复提交过滤器
 * </p>
 */
public class AvoidDuplicateSubmissionInterceptor2 extends
		HandlerInterceptorAdapter {
	private static final Logger log = LoggerFactory
			.getLogger(AvoidDuplicateSubmissionInterceptor2.class);

	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {

		// User user = UserUtil.getUser();
		// if (user != null) {
		HandlerMethod handlerMethod = (HandlerMethod) handler;
		Method method = handlerMethod.getMethod();

		AvoidDuplicateSubmission annotation = method
				.getAnnotation(AvoidDuplicateSubmission.class);
		if (annotation != null) {
			boolean needSaveSession = annotation.needSaveToken();
			if (needSaveSession) {
//				request.getSession(false).setAttribute("token",
//						(new UUID(false)).nextID());
			}

			boolean needRemoveSession = annotation.needRemoveToken();
			if (needRemoveSession) {
				if (isRepeatSubmit(request)) {
					log.warn("please don't repeat submit,[user:" + /*
																	 * user.
																	 * getUsername
																	 * () +
																	 */",url:"
							+ request.getServletPath() + "]");
					return false;
				}
				request.getSession(false).removeAttribute("token");
			}
		}
		// }
		return true;
	}

	private boolean isRepeatSubmit(HttpServletRequest request) {
		String serverToken = (String) request.getSession(false).getAttribute(
				"token");
		if (serverToken == null) {
			return true;
		}
		String clinetToken = request.getParameter("token");
		if (clinetToken == null) {
			return true;
		}
		if (!serverToken.equals(clinetToken)) {
			return true;
		}
		return false;
	}

}
    