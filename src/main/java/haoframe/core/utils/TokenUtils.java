package haoframe.core.utils;

import java.io.Serializable;
import java.util.Map;
import java.util.UUID;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

import haoframe.core.enumeration.Terminal;

public class TokenUtils {
	private static final String SECRET = "0123456789abcdef";// 秘钥

	public static final String dic_token_userid_key            = "token_userid";
	public static final String dic_token_terminal_key          = "token_terminal";
	public static final String dic_token_app_key               = "token_platformname";
	
	public static final String TOKEN_KEY = "Authorization";
	public static final String TOKEN_BASE_USER_INFO_KEY = "Authorization_BASE_USER_INFO";
	
	
	
	
	public static class TokenInfo implements Serializable{

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private String userId;
		private String appName;
		private Terminal terminal;
		
		public TokenInfo(String userId,String appName,Terminal terminal) {
			this.userId = userId;
			this.appName = appName;
			this.terminal = terminal;
		}
		
		public String getUserId() {
			return userId;
		}
		public void setUserId(String userId) {
			this.userId = userId;
		}
		public String getAppName() {
			return appName;
		}
		public void setAppName(String appName) {
			this.appName = appName;
		}

		public Terminal getTerminal() {
			return terminal;
		}

		public void setTerminal(Terminal terminal) {
			this.terminal = terminal;
		}
	}
	
	public static String  getRedisKey(TokenInfo tokenInfo) {
		return "jwt_token_"+tokenInfo.getUserId()+"_"+tokenInfo.getAppName()+"_"+tokenInfo.getTerminal();
	}
	
	
	/**
	 * 生成token
	 * 
	 * @param claims
	 * @return
	 */
	public static String genToken(TokenInfo tokenInfo) {
		String jwtId = UUID.randomUUID().toString();
		Algorithm algorithm = Algorithm.HMAC256(SECRET);
		JWTCreator.Builder builder = JWT.create().withJWTId(jwtId);
		builder.withClaim(dic_token_app_key, tokenInfo.getAppName());
		builder.withClaim(dic_token_terminal_key,tokenInfo.getTerminal().toString());
		builder.withClaim(dic_token_userid_key,tokenInfo.getUserId());
		return builder.sign(algorithm).toString();
	}
	
	/**
	 * 验证token
	 * 
	 * @param token
	 * @return
	 */
	public static TokenInfo verifyToken(String token) {
		Algorithm algorithm = null;
		try {
			algorithm = Algorithm.HMAC256(SECRET);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		}
		JWTVerifier verifier = JWT.require(algorithm).build();
		DecodedJWT jwt = verifier.verify(token);
		Map<String, Claim> map = jwt.getClaims();
		return new TokenInfo(
				map.get(dic_token_userid_key).asString(),
				map.get(dic_token_app_key).asString(),
				Terminal.getByName(map.get(dic_token_terminal_key).asString())
				);
	}

}
