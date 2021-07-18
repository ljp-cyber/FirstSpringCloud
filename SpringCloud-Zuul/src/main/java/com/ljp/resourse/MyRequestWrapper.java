package com.ljp.resourse;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.springframework.security.web.firewall.FirewalledRequest;

import java.io.IOException;
import java.util.*;

/**
 * 这个类来自spring的一个final类RequestWrapper，直接把代发复制过来让其可以被继承
 * @author LiJunPeng
 *
 */
public class MyRequestWrapper extends FirewalledRequest {
	private final String strippedServletPath;
	private final String strippedPathInfo;
	private boolean stripPaths = true;

	public MyRequestWrapper(HttpServletRequest request) {
		super(request);
		strippedServletPath = strip(request.getServletPath());
		String pathInfo = strip(request.getPathInfo());
		if (pathInfo != null && pathInfo.length() == 0) {
			pathInfo = null;
		}
		strippedPathInfo = pathInfo;
	}

	private String strip(String path) {
		if (path == null) {
			return null;
		}

		int scIndex = path.indexOf(';');

		if (scIndex < 0) {
			int doubleSlashIndex = path.indexOf("//");
			if (doubleSlashIndex < 0) {
				// Most likely case, no parameters in any segment and no '//', so no
				// stripping required
				return path;
			}
		}

		StringTokenizer st = new StringTokenizer(path, "/");
		StringBuilder stripped = new StringBuilder(path.length());

		if (path.charAt(0) == '/') {
			stripped.append('/');
		}

		while (st.hasMoreTokens()) {
			String segment = st.nextToken();
			scIndex = segment.indexOf(';');

			if (scIndex >= 0) {
				segment = segment.substring(0, scIndex);
			}
			stripped.append(segment).append('/');
		}

		// Remove the trailing slash if the original path didn't have one
		if (path.charAt(path.length() - 1) != '/') {
			stripped.deleteCharAt(stripped.length() - 1);
		}

		return stripped.toString();
	}

	@Override
	public String getPathInfo() {
		return stripPaths ? strippedPathInfo : super.getPathInfo();
	}

	@Override
	public String getServletPath() {
		return stripPaths ? strippedServletPath : super.getServletPath();
	}

	@Override
	public RequestDispatcher getRequestDispatcher(String path) {
		return this.stripPaths ? new FirewalledRequestAwareRequestDispatcher(path)
				: super.getRequestDispatcher(path);
	}

	public void reset() {
		this.stripPaths = false;
	}

	/**
	 * Ensures {@link FirewalledRequest#reset()} is called prior to performing a forward.
	 * It then delegates work to the {@link RequestDispatcher} from the original
	 * {@link HttpServletRequest}.
	 *
	 * @author Rob Winch
	 */
	private class FirewalledRequestAwareRequestDispatcher implements RequestDispatcher {
		private final String path;

		/**
		 *
		 * @param path the {@code path} that will be used to obtain the delegate
		 * {@link RequestDispatcher} from the original {@link HttpServletRequest}.
		 */
		FirewalledRequestAwareRequestDispatcher(String path) {
			this.path = path;
		}

		public void forward(ServletRequest request, ServletResponse response)
				throws ServletException, IOException {
			reset();
			getDelegateDispatcher().forward(request, response);
		}

		public void include(ServletRequest request, ServletResponse response)
				throws ServletException, IOException {
			getDelegateDispatcher().include(request, response);
		}

		private RequestDispatcher getDelegateDispatcher() {
			return MyRequestWrapper.super.getRequestDispatcher(path);
		}
	}
}
