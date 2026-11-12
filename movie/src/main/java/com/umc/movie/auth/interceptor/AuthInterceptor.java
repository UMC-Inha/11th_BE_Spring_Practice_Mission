package com.umc.movie.auth.interceptor;

import com.umc.movie.common.exception.ApiException;
import com.umc.movie.common.exception.ErrorCode;
import com.umc.movie.common.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
// DispatcherServlet이 컨트롤러를 호출하기 전후에 intercept
public class AuthInterceptor implements HandlerInterceptor {

    public static final String MEMBER_ID = "memberId"; // 컨트롤러에서 꺼낼 때 쓸 이름
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtUtil jwtUtil;

    /*
    * 컨트롤러가 실행되기 전에 호출
    * true를 반환하면 요청이 컨트롤러까지
    * exception을 던지면 여기서 끝남
    * */
    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler
    ){
        //preflight에는 토큰 없음
        if(HttpMethod.OPTIONS.matches(request.getMethod())){
            return true;
        }

        String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        // "Bearer {token}" 형식이 아니면 토큰을 보내지 않은 것으로 간주
        if(header == null || !header.startsWith(BEARER_PREFIX)){
            throw new ApiException(ErrorCode.TOKEN_MISSING);
        }

        //검증에 실패하면 JwtUtil이 예외를 던지므로 여기까지 왔으면 유효한 토큰
        Long memberId = jwtUtil.getMemberId(header.substring(BEARER_PREFIX.length()));

        //컨트롤러가 다시 꺼내 쓸 수 있도록 요청에 담아둠
        request.setAttribute(MEMBER_ID, memberId);

        return true;
    }

}
