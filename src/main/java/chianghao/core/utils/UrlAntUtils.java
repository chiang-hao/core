package chianghao.core.utils;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

public class UrlAntUtils {

	
	private static final boolean useSuffixPatternMatch=true;

	private static final boolean useTrailingSlashMatch=true;
	
	private static final List<String> fileExtensions=new ArrayList<String>();
	
	
	public static String getMatchingPattern(String pattern, String lookupPath) {
		if (pattern.equals(lookupPath)) {
			return pattern;
		}
		PathMatcher   pathMatcher   = new AntPathMatcher();//路径匹配器
		if (useSuffixPatternMatch) {
			if (!fileExtensions.isEmpty() && lookupPath.indexOf('.') != -1) {
				for (String extension : fileExtensions) {
					if (pathMatcher.match(pattern + extension, lookupPath)) {
						return pattern + extension;
					}
				}
			}
			else {
				boolean hasSuffix = pattern.indexOf('.') != -1;
				if (!hasSuffix && pathMatcher.match(pattern + ".*", lookupPath)) {
					return pattern + ".*";
				}
			}
		}
		if (pathMatcher.match(pattern, lookupPath)) {
			return pattern;
		}
		if (useTrailingSlashMatch) {
			if (!pattern.endsWith("/") && pathMatcher.match(pattern + "/", lookupPath)) {
				return pattern +"/";
			}
		}
		return null;
	}

	
	
}
