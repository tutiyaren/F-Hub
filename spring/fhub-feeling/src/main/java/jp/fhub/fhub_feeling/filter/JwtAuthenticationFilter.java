package jp.fhub.fhub_feeling.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jp.fhub.fhub_feeling.service.CustomUserDetailsService;
import jp.fhub.fhub_feeling.util.JwtUtil;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auth0.jwt.exceptions.JWTVerificationException;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, CustomUserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // トークン抽出
        String token = authHeader.substring(7);
        String email;
        try {
            // トークンが無効な場合、フィルターを次に進める
            email = jwtUtil.validateTokenAndRetrieveSubject(token);
        } catch (JWTVerificationException e) {
            logger.error("認証に失敗しました。再度ログインしてください。", e);
            response.setCharacterEncoding("UTF-8");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            String jsonResponse = "{\"result\": 401, \"message\": \"認証に失敗しました。再度ログインしてください。\"}";
            response.getWriter().write(jsonResponse);
            return;
        }

        // トークンが有効で、認証情報がまだセットされていない場合
        if (email == null || SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }

        // ユーザー情報の取得と認証トークンの設定
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        if (userDetails == null) {
            filterChain.doFilter(request, response);
            return;
        }

        // 認証情報をセキュリティコンテキストに設定
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities());
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        // フィルターを次に進める
        filterChain.doFilter(request, response);
    }

}